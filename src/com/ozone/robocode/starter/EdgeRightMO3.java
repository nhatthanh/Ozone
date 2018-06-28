package com.ozone.robocode.starter;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;

import robocode.MessageEvent;
import robocode.tma.TTeamMemberRobot;

public class EdgeRightMO3 extends TTeamMemberRobot {

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
        ahead(50);
        setMaxVelocity(4);
        turnLeft(- getHeading() + 90);
        ahead(10);
        turnRight(90);
        ahead(getBattleFieldWidth() - getX() - 100);
        while (true) {
            turnLeft(180);
            ahead(getBattleFieldWidth() - getX() - 150);
        }
    }
}
