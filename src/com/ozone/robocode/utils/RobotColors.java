package com.ozone.robocode.utils;

import java.awt.*;
import java.io.Serializable;
import java.util.Random;

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

    public static RobotColors getRobotColorDroid() {
        RobotColors robotColors = new RobotColors();
        robotColors.bodyColor = Color.yellow.darker();
        robotColors.bulletColor = Color.orange;
        robotColors.gunColor = Color.darkGray;
        robotColors.scanColor = Color.lightGray;
        robotColors.radarColor = Color.white;
        return robotColors;
    }

    public static RobotColors getRobotColorDroidMelee() {
        RobotColors robotColors = new RobotColors();
        robotColors.bodyColor = Color.red.darker();
        robotColors.bulletColor = Color.magenta;
        robotColors.gunColor = Color.darkGray;
        robotColors.scanColor = Color.red;
        robotColors.radarColor = Color.yellow;
        return robotColors;
    }
    
    public static RobotColors getRobotColorCaptain() {
        RobotColors robotColors = new RobotColors();
        robotColors.bodyColor = Color.orange.darker();
        robotColors.bulletColor = Color.orange;
        robotColors.gunColor = Color.red;
        robotColors.scanColor = Color.yellow;
        robotColors.radarColor = Color.white;
        return robotColors;
    }

    public static RobotColors getRobotColorSoloTeam() {
        Random random = new Random();
        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();
        Color randColor = new Color(r,g,b);
        randColor.brighter();

        RobotColors robotColors = new RobotColors();
        robotColors.bodyColor = randColor;
        robotColors.bulletColor = Color.orange;
        robotColors.gunColor = randColor;
        robotColors.scanColor = Color.yellow;
        robotColors.radarColor = randColor;
        return robotColors;
    }
}
