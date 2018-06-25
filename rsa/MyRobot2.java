package rsa;

import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import robocode.tma.TTeamLeaderRobot;
import robocode.util.Utils;

import java.awt.*;
import java.io.IOException;


public class MyRobot2 extends TTeamLeaderRobot {
    boolean peek;
    double moveAmount;
    int count = 0;
    double gunTurnAmt;
    String trackName;

    public MyRobot2(){
    }

    @Override
    public void onRun() {
        RobotColors c = new RobotColors();

        c.bodyColor = Color.blue;
        c.gunColor = Color.blue;
        c.radarColor = Color.blue;
        c.scanColor = Color.yellow;
        c.bulletColor = Color.yellow;

        // Set the color of this robot containing the RobotColors
        setBodyColor(c.bodyColor);
        setGunColor(c.gunColor);
        setRadarColor(c.radarColor);
        setScanColor(c.scanColor);
        setBulletColor(c.bulletColor);
        this.trackName = null;
        this.setAdjustGunForRobotTurn(true);
        this.gunTurnAmt = 10.0D;

        try {
            // Send RobotColors object to our entire team

            broadcastMessage(c);
        } catch (IOException ignored) {}
        // Normal behavior
        while (true){
//            setTurnRadarRight(10000);
            this.turnGunRight(this.gunTurnAmt);
            ++this.count;
            if (this.count > 2) {
                this.gunTurnAmt = -10.0D;
            }

            if (this.count > 5) {
                this.gunTurnAmt = 10.0D;
            }

            if (this.count > 11) {
                this.trackName = null;
            }
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        if (this.trackName == null || e.getName().equals(this.trackName)) {
            if (this.trackName == null) {
                this.trackName = e.getName();
                this.out.println("Tracking " + this.trackName);
            }

            this.count = 0;
            if (e.getDistance() > 150.0D) {
                this.gunTurnAmt = Utils.normalRelativeAngleDegrees(e.getBearing() + (this.getHeading() - this.getRadarHeading()));
                this.turnGunRight(this.gunTurnAmt);
                this.turnRight(e.getBearing());
                this.ahead(e.getDistance() - 140.0D);
            } else {
                this.gunTurnAmt = Utils.normalRelativeAngleDegrees(e.getBearing() + (this.getHeading() - this.getRadarHeading()));
                this.turnGunRight(this.gunTurnAmt);
//                this.fire(3.0D);
                // Calculate enemy bearing
                double enemyBearing = this.getHeading() + e.getBearing();
                // Calculate enemy's position
                double enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
                double enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));

                try {
                    // Send enemy position to teammates
                    broadcastMessage(new Point(enemyX, enemyY));
                } catch (IOException ex) {
                    out.println("Unable to send order: ");
                    ex.printStackTrace(out);
                }
                if (e.getDistance() < 100.0D) {
                    if (e.getBearing() > -90.0D && e.getBearing() <= 90.0D) {
                        this.back(40.0D);
                    } else {
                        this.ahead(40.0D);
                    }
                }

                this.scan();
            }
        }
        // Don't fire on teammates
        if (isTeammate(e.getName())) {
            return;
        }

    }

//    @Override
//    public void onHitByBullet(HitByBulletEvent event) {
//        this.turnLeft(90.0D - event.getBearing());
//    }

    public void onHitRobot(HitRobotEvent e) {
        if (this.trackName != null && !this.trackName.equals(e.getName())) {
            this.out.println("Tracking " + e.getName() + " due to collision");
        }

        this.trackName = e.getName();
        this.gunTurnAmt = Utils.normalRelativeAngleDegrees(e.getBearing() + (this.getHeading() - this.getRadarHeading()));
        this.turnGunRight(this.gunTurnAmt);
        this.fire(3.0D);
        this.back(50.0D);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        if (e.getBearing() > -90.0D && e.getBearing() < 90.0D) {
            this.back(100.0D);
        } else {
            this.ahead(100.0D);
        }
    }

    public void onWin(WinEvent e) {
        for(int i = 0; i < 50; ++i) {
            this.turnRight(30.0D);
            this.turnLeft(30.0D);
        }

    }
}