package tma.o2.robot;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.util.concurrent.ThreadLocalRandom;

import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.tma.TTeamMemberRobot;
import tma.o2.action.ActionPool;
import tma.o2.misc.RobotColors;
import tma.o2.misc.Strategy.FIRE_STRATEGY;
import tma.o2.misc.Strategy.MOVE_STRATEGY;
import tma.o2.misc.Target;

public class Annihilator extends TTeamMemberRobot {

    private MOVE_STRATEGY moveStrategy = MOVE_STRATEGY.RANDOMIZE;
    private FIRE_STRATEGY fireStrategy = FIRE_STRATEGY.FIRE_NEAR;

    private ActionPool action = new ActionPool();
    private double battleFieldHeight;
    private double battleFieldWidth;
    private String trackingTarget = "";

    public void run() {
        battleFieldHeight = getBattleFieldHeight();
        battleFieldWidth = getBattleFieldWidth();
        ahead(200);
        while (true) {
            switch (moveStrategy) {
            case RANDOMIZE:
                moveRandom();
                break;
            case TRACKER:
                ahead(5);
                break;
            }
        }
    }

    // Strategy function

    private void moveRandom() {
        int x = getRandom(10, (int) battleFieldHeight - 10);
        int y = getRandom(10, (int) battleFieldWidth - 10);
        goTo(x, y);
    }

    private void moveTracking(Target target) {
        goTo(target.getX(), target.getY());
    }

    // React function

    public void onMessageReceived(MessageEvent e) {

        if (e.getMessage() instanceof Target) {
            Target target = constructTarget((Target) e.getMessage());

            switch (moveStrategy) {
            case RANDOMIZE:
                action.fireNear(this, target);
                break;
            case TRACKER:
                if (target.getName().equals(trackingTarget)) {
                    moveTracking(target);
                }
                action.fireNear(this, target);
                break;
            }
        }

        if (e.getMessage() instanceof RobotColors) {
            setColorForRobot(e);
        }
    }

    public void onRobotDeath(RobotDeathEvent event) {
        if (event.getName().equals(trackingTarget)) {
            moveStrategy = MOVE_STRATEGY.RANDOMIZE;
        }
    }

    public void onHitRobot(HitRobotEvent event) {
        if (isTeammate(event.getName())) {
            back(100);
        } else {
            turnGunRight(event.getBearing());
            fire(3);
            // trackingTarget = event.getName();
            // moveStrategy = MOVE_STRATEGY.TRACKER;
        }
    }

    // Helper function

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
        turnRight(degree);

        this.ahead(Math.sqrt(dx * dx + dy * dy));
    }

    private double distanceTo(double x, double y) {
        double dx = x - this.getX();
        double dy = x - this.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
