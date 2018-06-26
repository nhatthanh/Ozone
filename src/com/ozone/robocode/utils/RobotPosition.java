package com.ozone.robocode.utils;

import java.io.Serializable;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class RobotPosition implements Serializable {
    private static final long serialVersionUID = 1L;
    private double x = 0.0D;
    private double y = 0.0D;
    private double energy;
    private double verlocity;
    private String name;

    public RobotPosition(double var1, double var3) {
        this.x = var1;
        this.y = var3;
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

    public double getVerlocity() {
        return verlocity;
    }

    public void setVerlocity(double verlocity) {
        this.verlocity = verlocity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistanceToEnemey(Robot robot) {
        return getDistance(new RobotPosition(robot.getX(), robot.getY()), this);
    }

    public double getDistance(RobotPosition point1, RobotPosition point2) {
        return Math.sqrt(Math.pow((point1.x - point2.x), 2) + Math.pow((point1.y - point2.y), 2));
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
        point.setVerlocity(e.getVelocity());
        return point;
    }
}
