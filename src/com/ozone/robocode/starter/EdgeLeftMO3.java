package com.ozone.robocode.starter;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;

import robocode.MessageEvent;
import robocode.tma.TTeamMemberRobot;

public class EdgeLeftMO3 extends TTeamMemberRobot {

    RobotPosition point1;
    RobotPosition point2;
    RobotPosition point3;

    @Override
    public void onMessageReceived(MessageEvent event) {
        if (event.getMessage() instanceof RobotColors) {
            RobotColors.setColorTeamRobot(this, (RobotColors) event.getMessage());
        }
        if (event.getMessage() instanceof RobotPosition) {
            RobotPosition p = (RobotPosition) event.getMessage();
            double dx = p.getX() - this.getX();
            double dy = p.getY() - this.getY();
            double target = Math.toDegrees(Math.atan2(dx, dy));
            turnGunRight(normalRelativeAngleDegrees(target - getGunHeading()));

            fire(3);
        }
    }

    @Override
    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        double borderRange = (float) getBattleFieldWidth() / 2;
        this.setAdjustGunForRobotTurn(true);
        if (getX() < borderRange) {
            point1 = new RobotPosition(60, 120);
            point2 = new RobotPosition(60, 970);
            point3 = new RobotPosition(600, 970);

        } else {
            point1 = new RobotPosition(getBattleFieldWidth() - 60, getBattleFieldHeight() - 120);
            point2 = new RobotPosition(getBattleFieldWidth() - 60, 60);
            point3 = new RobotPosition(600, 60);
        }
        while (true) {
            RobotPosition.goTo(point1, this);
            RobotPosition.goTo(point2, this);
            RobotPosition.goTo(point3, this);
        }

    }
}
