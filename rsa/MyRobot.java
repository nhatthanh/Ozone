package rsa;

import robocode.*;
import robocode.tma.TTeamLeaderRobot;
import robocode.util.Utils;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

import static robocode.util.Utils.normalRelativeAngleDegrees;


public class MyRobot extends TTeamLeaderRobot {
    boolean peek;
    double moveAmount;
    double enemyX;
    double enemyY;


    public MyRobot(){
    }

    @Override
    public void onRun() {
        RobotColors c = new RobotColors();
        c.bodyColor = Color.blue;
        c.gunColor = Color.blue;
        c.radarColor = Color.blue;
        c.scanColor = Color.yellow;
        c.bulletColor = Color.white;

        try {
            // Send RobotColors object to our entire team
            broadcastMessage(c);
        } catch (IOException ignored) {}
        this.initialize();
        this.moveAmount = Math.max(this.getBattleFieldWidth(), this.getBattleFieldHeight());
        this.peek = false;
        this.turnLeft(this.getHeading() % 90.0D);
        this.ahead(this.moveAmount);
        this.peek = true;
        this.turnGunRight(90.0D);
        this.turnRight(90.0D);
        while(true) {
            this.peek = true;
            this.ahead(this.moveAmount);
            this.peek = false;
            this.turnLeft(180.0D);
        }
    }

    @Override
    public void onStatus(StatusEvent e) {
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // Don't fire on teammates
        if (isTeammate(e.getName())) {
            return;
        }
        double enemyBearing = this.getHeading() + e.getBearing();
//             Calculate enemy's position
            enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
            enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));

            double dx = enemyX - this.getX();
            double dy = enemyY - this.getY();
            // Calculate angle to target
            double theta = Math.toDegrees(Math.atan2(dx, dy));

            // Turn gun to target
            turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
//             Fire hard!
            if(this.getEnergy() > 50){
                fire(3);
            }else if(this.getEnergy() <= 50){
                fire(0.5D);
            }

        try {
            // Send enemy position to teammates
            broadcastMessage(new Point(enemyX, enemyY));

        } catch (IOException ex) {
            out.println("Unable to send order: ");
            ex.printStackTrace(out);
        }
    }


    public void onHitRobot(HitRobotEvent e) {
        if (e.getBearing() > -90.0D && e.getBearing() < 90.0D) {
            this.back(100);
        } else {
            this.ahead(100);
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        if (e.getBearing() > -90.0D && e.getBearing() < 90.0D) {
            this.turnRight(90);
            this.back(100.0D);
        } else {
            this.turnRight(90);
            this.ahead(100.0D);
        }

        this.setMaxVelocity(100);
    }

    private void initialize() {
        this.setBodyColor(new Color(92, 51, 23));
        this.setGunColor(new Color(69, 139, 116));
        this.setRadarColor(new Color(210, 105, 30));
        this.setBulletColor(new Color(255, 211, 155));
        this.setScanColor(new Color(202, 255, 112));
    }


}