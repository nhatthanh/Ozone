package com.ozone.robocode.utils;

import java.awt.*;
import java.io.Serializable;

import robocode.Robot;

public class RobotColors implements Serializable {
    private static final long serialVersionUID = 1L;
    public Color bodyColor;
    public Color gunColor;
    public Color radarColor;
    public Color scanColor;
    public Color bulletColor;

    public static void setColorTeamRobot(Robot robot, RobotColors robotColors) {
        robot.setBodyColor(robotColors.bodyColor);
        robot.setBulletColor(robotColors.bulletColor);
        robot.setGunColor(robotColors.gunColor);
        robot.setScanColor(robotColors.scanColor);
        robot.setRadarColor(robotColors.radarColor);
    }

    public static RobotColors getRobotColorDefault() {
        RobotColors robotColors = new RobotColors();
        robotColors.bodyColor = Color.yellow;
        robotColors.bulletColor = Color.green;
        robotColors.gunColor = Color.darkGray;
        robotColors.scanColor = Color.lightGray;
        robotColors.radarColor = Color.white;
        return robotColors;
    }
}
