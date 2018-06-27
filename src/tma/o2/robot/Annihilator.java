package tma.o2.robot;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.util.concurrent.ThreadLocalRandom;

import robocode.*;
import robocode.tma.*;
import tma.o2.action.ActionPool;
import tma.o2.misc.*;

public class Annihilator extends TTeamMemberRobot {

    private ActionPool action = new ActionPool();

    public void run() {
        ahead(200);
        while (true) {
            // action.moveRandom(this);
            int x = getRandom(10, 900);
            int y = getRandom(10, 900);
            goTo(x, y);
        }
    }

    public void onMessageReceived(MessageEvent e) {

        if (e.getMessage() instanceof Target) {
            Target target = constructTarget((Target) e.getMessage());
            action.fireNear(this, target);
        }

        if (e.getMessage() instanceof RobotColors) {
            RobotColors c = (RobotColors) e.getMessage();

            setBodyColor(c.bodyColor);
            setGunColor(c.gunColor);
            setRadarColor(c.radarColor);
            setScanColor(c.scanColor);
            setBulletColor(c.bulletColor);
        }
    }

    private Target constructTarget(Target raw) {
        Target target = new Target(raw);
        // Calculate distance

        double x = Math.abs(target.getX() - this.getX());
        double y = Math.abs(target.getY() - this.getY());
        target.setDistance(distanceTo(x, y));
        return target;
    }

    public void onHitWall(HitWallEvent event) {
        turnRight(60);
    }

    private int getRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private void goTo(double x, double y) {
        
        double dx = x - this.getX();
        double dy = y - this.getY();
        
        double theta = Math.toDegrees(Math.atan2(dx, dy));
        double degree = normalRelativeAngleDegrees(theta - getHeading());
        turnRight(degree);
        
        this.ahead(Math.sqrt(dx * dx + dy * dy));
        
    }
    
    private double distanceTo(double x, double y) {
        double dx = x - this.getX();
        double dy = x - this.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
