package com.ozone.robocode.starter;

import java.io.IOException;
import java.io.Serializable;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;

import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;
import robocode.tma.TTeamLeaderRobot;

public class ThanhCaptainMO3 extends TTeamLeaderRobot {

    @Override
    public void onRun() {
        RobotColors robotColorDefault = RobotColors.getRobotColorDefault();
        RobotColors.setColorTeamRobot(this, robotColorDefault);
        broadCastToDroid(robotColorDefault);

        ahead(40);

        while (true) {
            randomMove();
        }
    }

    @Override
    public void onStatus(StatusEvent e) {
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
    }

    private void broadCastToDroid(Serializable message) {
        try {
            broadcastMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        if (isTeammate(event.getName())) {
            return;
        }
        RobotPosition robotPosition = RobotPosition.getPoint(event, this);
        broadCastToDroid(robotPosition);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        randomMove();
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        randomMove();
    }

    private void randomMove() {
        if ((int)(Math.random() * 10) % 2 == 0) {
            turnRight(Math.random() * 360);
        } else {
            turnLeft(Math.random() * 360);
        }
        if ((int)(Math.random() * 10) % 2 == 0) {
            ahead(Math.random() * 1000);
        } else {
            back(Math.random() * 1000);
        }
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        randomMove();
    }
}
