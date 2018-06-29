package com.ozone.robocode.starter;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;

import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.MessageEvent;
import robocode.tma.TTeamMemberRobot;

public class EdgeRightMO3 extends TTeamMemberRobot {
    RobotPosition[] point  = new RobotPosition[3];

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

            double energy = p.getPowerFire(this);
            fire(energy);
        }
    }

    @Override
    public void run() {
        setMaxVelocity(4);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        double borderRange = (float) getBattleFieldWidth() / 2;
        if (getY() < borderRange) {
            point[0] = new RobotPosition(100, 60);
            point[1] = new RobotPosition(970, 60);
            point[2] = new RobotPosition(970, 600);

        } else {
            point[0] = new RobotPosition(getBattleFieldWidth() - 100, getBattleFieldHeight() - 60);
            point[1] = new RobotPosition(60, getBattleFieldHeight() - 60);
            point[2] = new RobotPosition(60, 400);
        }
        while (true) {
            go();
        }
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        if (!isTeammate(event.getName())) {
            turnGunRight(normalRelativeAngleDegrees(event.getBearing() - getGunHeading()));
        }
        go();
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        go();
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        turnGunRight(normalRelativeAngleDegrees(event.getBearing() - getGunHeading()));
        go();
    }

    private void go() {
        RobotPosition.randomMove(this);
        int position = (int) (Math.random() * 100) % point.length;
        RobotPosition.goTo(point[position], this);
    }
}
