package com.ozone.robocode.starter;

import java.io.IOException;

import com.ozone.robocode.utils.RobotColors;

import robocode.tma.TTeamLeaderRobot;

public class ThanhCaptainMO3 extends TTeamLeaderRobot {

    @Override
    public void onRun() {
        RobotColors robotColorDefault = RobotColors.getRobotColorDefault();
        RobotColors.setColorTeamRobot(this, robotColorDefault);
        try {
            broadcastMessage(robotColorDefault);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
