package tma.o2.action;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.util.concurrent.ThreadLocalRandom;

import robocode.TeamRobot;
import tma.o2.misc.Target;

public class ActionPool {
    
    
    
    public void moveRandom(TeamRobot robot) {
        robot.ahead(getRandom(150, 350));
        if (getRandom(1, 2) == 1) {
            robot.turnRight(getRandom(0, 90));
        } else {
            robot.turnLeft(getRandom(0, 90));
        }
        robot.back(getRandom(20, 60));
//        double width = robot.getBattleFieldWidth();
//        double height = robot.getBattleFieldHeight();
//        goTo(robot, getRandom(1, 1024), 100);
    }
    
    
    
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
        
        double distance = distanceTo(robot, enemy.getX(), enemy.getY());
        
        if (distance > 600 && distance <= 800) {
            robot.fire(0.5);
        }
        else if (distance > 200 && distance <= 600) {
            robot.fire(1);
        }
        else if (distance <= 200) {
            robot.fire(3);
		}
    	goNear(robot, enemy.getX(), enemy.getY());
    }
    
	public void fireMelee(TeamRobot robot, Target enemy) {
        double dx = enemy.getX() - robot.getX();
        double dy = enemy.getY() - robot.getY();
        
        double theta = Math.toDegrees(Math.atan2(dx, dy));
        
        double distance = enemy.getDistance();
        
        if (distance <= 200) {
            robot.turnGunRight(normalRelativeAngleDegrees(theta - robot.getGunHeading()));
            robot.fire(3);
        }
    }
    
    public void fireNormal(TeamRobot robot, Target enemy) {
        double dx = enemy.getX() - robot.getX();
        double dy = enemy.getY() - robot.getY();

        double theta = Math.toDegrees(Math.atan2(dx, dy));
        robot.turnGunRight(normalRelativeAngleDegrees(theta - robot.getGunHeading()));

        robot.fire(1.5);
    }
    
    private int getRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    
    private double getRandom(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max + 1);
    }

    private void goTo(TeamRobot robot, int x, int y) {
        double a;
        robot.setTurnRightRadians(Math.tan(a = Math.atan2(x -= (int) robot.getX(), y -= (int) robot.getY()) - robot.getHeadingRadians()));
        robot.setAhead(Math.hypot(x, y) * Math.cos(a));
    }
    
    private void goTo(TeamRobot robot, double x, double y) {
    	double a;
        robot.setTurnRightRadians(Math.tan(a = Math.atan2(x -= (int) robot.getX(), y -= (int) robot.getY()) - robot.getHeadingRadians()));
        robot.setAhead(Math.hypot(x, y) * Math.cos(a));
	}
    private void goNear(TeamRobot robot, double x, double y) {
    	double a;
    	robot.setTurnRightRadians(Math.tan(a = Math.atan2(x -= (int) robot.getX(), y -= (int) robot.getY()) - robot.getHeadingRadians()));
    	double distance = Math.hypot(x, y) * Math.cos(a);
    	if (distance > 80) {
    		robot.setAhead(distance / 2);
    	}
    }
    
    private double distanceTo(TeamRobot robot, double x, double y) {
		double dx = Math.abs(x - robot.getX());
		double dy = Math.abs(x - robot.getY());
		return Math.sqrt(dx * dx + dy * dy);
	}
}
