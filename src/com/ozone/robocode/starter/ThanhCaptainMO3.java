package com.ozone.robocode.starter;

import static com.ozone.robocode.utils.RobotPosition.randomMove;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;

import robocode.*;
import robocode.tma.TTeamLeaderRobot;
import robocode.util.Utils;

public class ThanhCaptainMO3 extends TTeamLeaderRobot {

    double enemyX;
    double enemyY;
    int numberEnemy = 5;
    @Override
    public void onRun() {
        RobotColors.setColorTeamRobot(this, RobotColors.getRobotColorCaptain());
        broadCastToDroid(RobotColors.getRobotColorDroid());

        this.ahead(40);

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
        if(numberEnemy == 1){
            linearTarget(event);
        }else {
            RobotPosition robotPosition = RobotPosition.getPoint(event, this);
            broadCastToDroid(robotPosition);
            captainFire(event);
        }
    }

    @Override
    public void onEnemyDeath(RobotDeathEvent event) {
        numberEnemy--;
    }

    private void captainFire(ScannedRobotEvent event){
        double enemyBearing = this.getHeading() + event.getBearing();
//             Calculate enemy's position
        enemyX = getX() + event.getDistance() * Math.sin(Math.toRadians(enemyBearing));
        enemyY = getY() + event.getDistance() * Math.cos(Math.toRadians(enemyBearing));

        double dx = enemyX - this.getX();
        double dy = enemyY - this.getY();
        // Calculate angle to target
        double theta = Math.toDegrees(Math.atan2(dx, dy));

        // Turn gun to target
        turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));

        if(this.getEnergy() > 50){
            fire(3);
        }else if(this.getEnergy() <= 50){
            fire(0.5D);
        }
    }

    private void linearTarget(ScannedRobotEvent e){
        double bulletPower = Math.min(3.0,getEnergy());
        double myX = getX();
        double myY = getY();
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
        double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
        double enemyHeading = e.getHeadingRadians();
        double enemyVelocity = e.getVelocity();
        double deltaTime = 0;
        double battleFieldHeight = getBattleFieldHeight(),
                battleFieldWidth = getBattleFieldWidth();
        double predictedX = enemyX, predictedY = enemyY;
        while((++deltaTime) * (20.0 - 3.0 * bulletPower) <
                Point2D.Double.distance(myX, myY, predictedX, predictedY)){
            predictedX += Math.sin(enemyHeading) * enemyVelocity;
            predictedY += Math.cos(enemyHeading) * enemyVelocity;
            if(	predictedX < 18.0
                    || predictedY < 18.0
                    || predictedX > battleFieldWidth - 18.0
                    || predictedY > battleFieldHeight - 18.0){
                predictedX = Math.min(Math.max(18.0, predictedX),
                        battleFieldWidth - 18.0);
                predictedY = Math.min(Math.max(18.0, predictedY),
                        battleFieldHeight - 18.0);
                break;
            }
        }
        double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX - getX(), predictedY - getY()));
        broadCastToDroid(new RobotPosition(predictedX,predictedY));
        setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
        setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
        fire(bulletPower);
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

        this.setMaxVelocity(10);
    }


    private void randomMove() {
//        setMaxVelocity(5);
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

//    public int getRandom(int min, int max) {
//        return ThreadLocalRandom.current().nextInt(min, max + 1);
//    }
//
//    private void goTo(RobotPosition destination){
//        // Calculate x and y to target
//        double dx = destination.getX() - this.getX();
//        double dy = destination.getY() - this.getY();
//        // Calculate angle to target
//        double theta = Math.toDegrees(Math.atan2(dx, dy));
//        // Turn gun to target and go to that point
//        turnGunRight(normalRelativeAngleDegrees(theta - getHeading()));
//        turnRight(normalRelativeAngleDegrees(theta - getHeading()));
//        this.ahead(distanceTo(destination.getX(),destination.getY()));
//    }

    private double distanceTo(double x, double y) {
        return Math.hypot(x - this.getX(), y - this.getY());
    }

}
