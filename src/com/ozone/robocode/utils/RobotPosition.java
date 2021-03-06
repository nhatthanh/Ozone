package com.ozone.robocode.utils;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.io.Serializable;

import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class RobotPosition implements Serializable {
    private static final long serialVersionUID = 1L;
    public double x = 0.0D;
    public double y = 0.0D;
    private double energy;
    private double velocity;
    private String name;
    private double power = 0.0D;
    private boolean melee = false;
    private boolean soloTeam = false;
    private double bearingRadians;
    private double headingRadians;
    private double distance;

    public RobotPosition(double var1, double var3) {
        this.x = var1;
        this.y = var3;
    }

    public RobotPosition(RobotPosition enemyPoint) {
        x = enemyPoint.x;
        y = enemyPoint.y;
        energy = enemyPoint.energy;
        velocity = enemyPoint.velocity;
        name = enemyPoint.name;
        power = enemyPoint.power;
        melee = enemyPoint.melee;
        soloTeam = enemyPoint.soloTeam;
        bearingRadians = enemyPoint.bearingRadians;
        headingRadians = enemyPoint.headingRadians;
        distance = enemyPoint.distance;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double verlocity) {
        this.velocity = verlocity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getBearingRadians() {
        return bearingRadians;
    }

    public void setBearingRadians(double bearingRadians) {
        this.bearingRadians = bearingRadians;
    }

    public double getHeadingRadians() {
        return headingRadians;
    }

    public void setHeadingRadians(double headingRadians) {
        this.headingRadians = headingRadians;
    }
    
    public double getHeading() {
        return this.headingRadians * 180.0D / 3.141592653589793D;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistanceToEnemey(Robot robot) {
        return getDistance(new RobotPosition(robot.getX(), robot.getY()), this);
    }

    public double getDistance(RobotPosition point1, RobotPosition point2) {
        return Math.sqrt(Math.pow((point1.x - point2.x), 2) + Math.pow((point1.y - point2.y), 2));
    }

    public boolean isMelee() {
        return melee;
    }

    public void setMelee(boolean melee) {
        this.melee = melee;
    }

    public boolean isSoloTeam() {
        return soloTeam;
    }

    public void setSoloTeam(boolean soloTeam) {
        this.soloTeam = soloTeam;
    }

    /**
     * Using in Lead onScanEvent
     * 
     * @param e
     * @param myRobot
     * @return
     */
    public static RobotPosition getPoint(ScannedRobotEvent e, Robot myRobot) {
        double enemyBearing = myRobot.getHeading() + e.getBearing();
        double enemyX = myRobot.getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
        double enemyY = myRobot.getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
        RobotPosition point = new RobotPosition(enemyX, enemyY);
        point.setName(e.getName());
        point.setEnergy(e.getEnergy());
        point.setVelocity(e.getVelocity());
        point.setBearingRadians(e.getBearingRadians());
        point.setDistance(e.getDistance());
        point.setHeadingRadians(e.getHeadingRadians());
        return point;
    }

    public static void goTo(RobotPosition destination, AdvancedRobot robot) {
        // Calculate x and y to target
        double dx = destination.getX() - robot.getX();
        double dy = destination.getY() - robot.getY();
        // Calculate angle to target
        double theta = Math.toDegrees(Math.atan2(dx, dy));
        // Turn gun to target and go to that point
        robot.turnGunRight(normalRelativeAngleDegrees(theta - robot.getHeading()));
        robot.turnRight(normalRelativeAngleDegrees(theta - robot.getHeading()));
        robot.ahead(distanceTo(destination.getX(), destination.getY(), robot));
    }

    private static double distanceTo(double x, double y, Robot robot) {
        return Math.hypot(x - robot.getX(), y - robot.getY());
    }
    
    public static void randomMove(AdvancedRobot robot) {
//        robot.setMaxVelocity(Math.random() * 10 % 8 + 1);
        if ((int)(Math.random() * 10) % 2 == 0) {
            robot.turnRight(Math.random() * 360);
        } else {
            robot.turnLeft(Math.random() * 360);
        }
        if ((int)(Math.random() * 10) % 2 == 0) {
            robot.ahead(Math.random() * 1000);
        } else {
            robot.back(Math.random() * 1000);
        }
    }

    public double getPowerFire(Robot robot) {
        if (robot.getEnergy() > 80) {
            return Math.min(3d, energy);
        }
        if (robot.getEnergy() > 40) {
            return Math.min(1.5d, energy);
        }
        return Math.min(0.5d, energy);
    }
    
    public double getBearing(Robot robot) {
        RobotPosition tmpPoint = new RobotPosition(x, y);
        double doi = getDistance(tmpPoint, this);
        double huyen = getDistanceToEnemey(robot);
        return Math.toDegrees(Math.asin(doi/huyen));
        
    }
}
