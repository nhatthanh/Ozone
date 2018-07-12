package com.ozone.robocode.starter;

import static com.ozone.robocode.utils.RobotPosition.randomMove;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;

import robocode.*;
import robocode.tma.TTeamLeaderRobot;
import robocode.util.Utils;

public class ThanhCaptainMO3 extends TTeamLeaderRobot {

    double enemyX;
    double enemyY;
    int numberEnemy = 5;
    int numberMember = 5;
    RobotPosition enemy;
    Set<String> enemyNameList  = new HashSet<>();
    boolean finishScan = false;
    @Override
    public void onRun() {
        RobotColors.setColorTeamRobot(this, RobotColors.getRobotColorCaptain());
        broadCastToDroid(RobotColors.getRobotColorDroid());
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
        enemyNameList.add(event.getName());
        numberEnemy = enemyNameList.size();
        if(getTime() >= 250){
            finishScan = true;
        }
        if(finishScan && numberEnemy <= 2){
            RobotColors robotColors = RobotColors.getRobotColorDroidMelee();
            RobotColors.setColorTeamRobot(this,RobotColors.getRobotColorDroidMelee());
            broadCastToDroid(robotColors);
            linearTarget(event);
            broadCastToDroid(enemy);
        }else {
            RobotPosition robotPosition = RobotPosition.getPoint(event, this);
            if(robotPosition.getX() > 1000 || robotPosition.getX() < 50 || robotPosition.getY() < 50 || robotPosition.getY() > 1000){
//                setAdjustGunForRobotTurn(true);
//                setAdjustRadarForGunTurn(true);
//                setAdjustRadarForRobotTurn(true);
                linearTarget(event);
                broadCastToDroid(enemy);
            }else if(robotPosition.getEnergy() == 0){
                goTo(robotPosition.getX(),robotPosition.getY());
            }else {
                broadCastToDroid(robotPosition);
                captainFire(event);
            }
        }
    }

    @Override
    public void onTeammateDeath(RobotDeathEvent event) {
        numberMember--;
        if(numberMember == 1){
            setAdjustGunForRobotTurn(true);
            setAdjustRadarForGunTurn(true);
            setAdjustRadarForRobotTurn(true);
        }
    }

    @Override
    public void onEnemyDeath(RobotDeathEvent event) {
        enemyNameList.remove(event.getName());
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
            fire(1.5D);
        }
    }

    private void linearTarget(ScannedRobotEvent e){
        double bulletPower = Math.min(1.5,getEnergy());
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
        enemy = new RobotPosition(predictedX,predictedY);
        enemy.setNumberEnemy(numberEnemy);
        setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
        setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
        setFire(bulletPower);
    }

    double oldEnemyHeading;
    private void circularFire(ScannedRobotEvent e) {
        double bulletPower = Math.min(3.0, getEnergy());
        double myX = getX();
        double myY = getY();
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
        double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
        double enemyHeading = e.getHeadingRadians();
        double enemyHeadingChange = enemyHeading - oldEnemyHeading;
        double enemyVelocity = e.getVelocity();
        oldEnemyHeading = enemyHeading;

        double deltaTime = 0;
        double battleFieldHeight = getBattleFieldHeight(), battleFieldWidth = getBattleFieldWidth();
        double predictedX = enemyX, predictedY = enemyY;
        while ((++deltaTime) * (20.0 - 3.0 * bulletPower) < Point2D.Double.distance(myX, myY, predictedX, predictedY)) {
            predictedX += Math.sin(enemyHeading) * enemyVelocity;
            predictedY += Math.cos(enemyHeading) * enemyVelocity;
            enemyHeading += enemyHeadingChange;
            if (predictedX < 18.0 || predictedY < 18.0 || predictedX > battleFieldWidth - 18.0
                    || predictedY > battleFieldHeight - 18.0) {

                predictedX = Math.min(Math.max(18.0, predictedX), battleFieldWidth - 18.0);
                predictedY = Math.min(Math.max(18.0, predictedY), battleFieldHeight - 18.0);
                break;
            }
        }
        double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX - getX(), predictedY - getY()));
        enemy = new RobotPosition(predictedX,predictedY);
        enemy.setNumberEnemy(numberEnemy);
        setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
        setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
        setFire(3);
    }

//    public void onHitRobot(HitRobotEvent e) {
//        if (e.getBearing() > -90.0D && e.getBearing() < 90.0D) {
//            this.back(100);
//        } else {
//            this.ahead(100);
//        }
//    }
//
//
//
//    @Override
//    public void onHitByBullet(HitByBulletEvent e) {
//        if (e.getBearing() > -90.0D && e.getBearing() < 90.0D) {
//            this.turnRight(90);
//            this.back(100.0D);
//        } else {
//            this.turnRight(90);
//            this.ahead(100.0D);
//        }
//
//        this.setMaxVelocity(10);
//    }

    public void onHitRobot(HitRobotEvent event) {
        if (event.isMyFault()) {
            setBack(100);
        }
    }

    public void onHitByBullet(HitByBulletEvent event) {
        double bearing = event.getBearing();
        if (Math.abs(bearing) > 45 && Math.abs(bearing) < 135) {
            return;
        }
        boolean front = Math.abs(bearing) < 45 ? true : false;
        boolean right = bearing > 0 ? true : false;
        if (bearing > 135) {
            bearing = 180 - bearing;
        }
        if (front && right || !front && !right) {
            setTurnLeft(60 - bearing);
            setAhead(150);
        }
        if (front && !right || !front && right) {
            setTurnRight(bearing + 60);
            setAhead(150);
        }
    }


    private void randomMove() {
        int x = getRandom(10, 1000);
        int y = getRandom(10, 1000);
        goTo(x, y);
        execute();
    }

    private void goTo(double x, double y) {

        double dx = x - this.getX();
        double dy = y - this.getY();

        double theta = Math.toDegrees(Math.atan2(dx, dy));
        double degree = normalRelativeAngleDegrees(theta - getHeading());
        turnRight(degree);
        double distance = Math.sqrt(dx * dx + dy * dy);

        setAhead(Math.min(distance, 300));
    }

    private double distanceTo(double x, double y) {
        return Math.hypot(x - this.getX(), y - this.getY());
    }

    public int getRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    @Override
    public void onDeath(DeathEvent event) {
        broadCastToDroid("dead");
    }
}
