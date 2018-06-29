package com.ozone.robocode.starter;

import static com.ozone.robocode.utils.RobotPosition.randomMove;
import static robocode.util.Utils.normalRelativeAngleDegrees;

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
    double enemyX;
    double enemyY;

    @Override
    public void onRun() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        RobotColors.setColorTeamRobot(this, RobotColors.getRobotColorCaptain());
        broadCastToDroid(RobotColors.getRobotColorDroid());

        ahead(40);

        while (true) {
            randomMove(this);
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
    public void onScannedRobot(ScannedRobotEvent e) {
        // Don't fire on teammates
        if (isTeammate(e.getName())) {
            return;
        }
        double enemyBearing = this.getHeading() + e.getBearing();
        // Calculate enemy's position
        enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
        enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));

        double dx = enemyX - this.getX();
        double dy = enemyY - this.getY();
        // Calculate angle to target
        double theta = Math.toDegrees(Math.atan2(dx, dy));

        // Turn gun to target
        turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
        // Fire hard!
        if (this.getEnergy() > 50) {
            fire(3);
        } else if (this.getEnergy() <= 50) {
            fire(0.5D);
        }

        try {
            // Send enemy position to teammates
            RobotPosition robotPosition = new RobotPosition(enemyX, enemyY);
            robotPosition.setEnergy(e.getEnergy());
            robotPosition.setName(e.getName());
            broadcastMessage(robotPosition);

        } catch (IOException ex) {
            out.println("Unable to send order: ");
            ex.printStackTrace(out);
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        randomMove(this);
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        randomMove(this);
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        randomMove(this);
    }
}
