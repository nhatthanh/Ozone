package tma.o2.robot;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.util.concurrent.ThreadLocalRandom;

import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.TeamRobot;
import robocode.tma.TTeamMemberRobot;
import tma.o2.misc.RobotColors;
import tma.o2.misc.Strategy.FIRE_STRATEGY;
import tma.o2.misc.Strategy.MOVE_STRATEGY;
import tma.o2.misc.Target;

public class Annihilator extends TTeamMemberRobot {

	private double battleFieldHeight;
	private double battleFieldWidth;

	public void run() {
		battleFieldHeight = getBattleFieldHeight();
		battleFieldWidth = getBattleFieldWidth();
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAhead(100);
	}

	// -----------------
	// -----------------
	// Strategy function
	// -----------------
	// -----------------

	public void fireNear(TeamRobot robot, Target enemy) {
    	// don't shoot while running low health
    	if (robot.getEnergy() < 10) {
    		if (getRandom(1, 100) > 10) {
    			return;
    		}
    	}
        double dx = enemy.getX() - robot.getX();
        double dy = enemy.getY() - robot.getY();
        
        double theta = Math.toDegrees(Math.atan2(dx, dy));
        robot.setTurnGunRight(normalRelativeAngleDegrees(theta - robot.getGunHeading()));
        
        double distance = distanceTo(enemy.getX(), enemy.getY());
        
        if (distance > 600) {
            robot.setFire(0.5);
        }
        else if (distance > 200 && distance <= 600) {
            robot.setFire(1.5);
        }
        else if (distance <= 200) {
            robot.setFire(3);
		}
    	
    	
    }

	// -----------------
	// -----------------
	// React function
	// -----------------
	// -----------------

	public void onMessageReceived(MessageEvent e) {
		if (e.getMessage() instanceof Target) {
			Target target = constructTarget((Target) e.getMessage());
			if (getGunHeat() == 0) {
				fireNear(this, target);
			}
			goNear(target.getX(), target.getY());
			execute();
		}

		if (e.getMessage() instanceof RobotColors) {
			setColorForRobot(e);
		}
	}

	public void onHitRobot(HitRobotEvent event) {
		if (isTeammate(event.getName())) {
			if (event.isMyFault()) {
				setBack(100);
			}
		} else {
			turnGunRight(event.getBearing());
			setFire(3);
		}
	}

	public void onBulletHit(BulletHitEvent event) {
		if (isTeammate(event.getName())) {
			setTurnRight(getGunHeading() + 70);
			setAhead(150);
		}
	}
	
	public void onHitByBullet(HitByBulletEvent event) {
		double bearing = event.getBearing();
		if (Math.abs(bearing) > 45 && Math.abs(bearing) < 135) {
			return;
		}
		boolean front = Math.abs(bearing) < 45 ? true : false;
		boolean right = bearing > 0 ? true : false;
		if (bearing > 135) {
			bearing = 180 - bearing;
		}
		if (front && right || !front && !right) {
			setTurnLeft(60 - bearing);
			setAhead(150);
		}
		if (front && !right || !front && right) {
			setTurnRight(bearing + 60);
			setAhead(150);
		}
	}

	// -----------------
	// -----------------
	// Helper function
	// -----------------
	// -----------------

	private void init() {
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
	}

	private void setColorForRobot(MessageEvent e) {
		RobotColors c = (RobotColors) e.getMessage();

		setBodyColor(c.bodyColor);
		setGunColor(c.gunColor);
		setRadarColor(c.radarColor);
		setScanColor(c.scanColor);
		setBulletColor(c.bulletColor);
	}

	private Target constructTarget(Target raw) {
		Target target = new Target(raw);
		// Calculate distance

		double x = Math.abs(target.getX() - this.getX());
		double y = Math.abs(target.getY() - this.getY());
		target.setDistance(distanceTo(x, y));
		return target;
	}

	private int getRandom(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	private void goTo(double x, double y) {

		double dx = x - this.getX();
		double dy = y - this.getY();

		double theta = Math.toDegrees(Math.atan2(dx, dy));
		double degree = normalRelativeAngleDegrees(theta - getHeading());
		setTurnRight(degree);

		this.setAhead(Math.sqrt(dx * dx + dy * dy));
	}
	
    private void goNear(double x, double y) {
    	double a;
    	setTurnRightRadians(Math.tan(a = Math.atan2(x -= (int) getX(), y -= (int) getY()) - getHeadingRadians()));
    	double distance = Math.hypot(x - getX(), y - getY());
    	if (distance > 100) {
    		setAhead(distance / 2);
    	}
    }

	private double distanceTo(double x, double y) {
		double dx = x - this.getX();
		double dy = x - this.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}
}
