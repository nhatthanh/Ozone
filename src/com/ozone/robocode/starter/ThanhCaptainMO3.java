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
    String enemyLeader;
    Set<String> enemyNameList  = new HashSet<>();
    boolean finishScan = false;
    boolean melee = false;
    double startPointY;
    @Override
    public void onRun() {
        RobotColors.setColorTeamRobot(this, RobotColors.getRobotColorCaptain());
        broadCastToDroid(RobotColors.getRobotColorDroid());
        startPointY = getY();
        while (true) {
            if(!melee){
                randomMove();
            }else if(enemy.getEnergy() < this.getEnergy()){
                goTo(enemy.getX(),enemy.getY());
            }else {
                randomMove();
            }
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
        enemyNameList.add(event.getName());
        numberEnemy = enemyNameList.size();
        if(event.getEnergy() > 150){
            enemyLeader = event.getName();
        }
        if(getTime() >= 150){
            finishScan = true;
        }

        if(finishScan && numberEnemy == 1){
            if(numberMember == 1){
                melee = true;
                linearTarget(event);
            }else {
                captainFire(event);
                enemy.setSoloTeam(true);
                broadCastToDroid(enemy);
                if(robotPosition.getEnergy() == 0){
                    goTo(robotPosition.getX(),robotPosition.getY());
                }
            }
            return;
        }

        if(finishScan && numberEnemy <= 2){
            RobotColors robotColors = RobotColors.getRobotColorDroidMelee();
            RobotColors.setColorTeamRobot(this,RobotColors.getRobotColorDroidMelee());
            broadCastToDroid(robotColors);
            linearTarget(event);
            broadCastToDroid(enemy);
            if(numberMember == 1){
                melee = true;
            }

        }else {
            melee = false;
            if(robotPosition.getX() > 1000 || robotPosition.getX() < 50 || robotPosition.getY() < 50 || robotPosition.getY() > 1000){
//                setAdjustGunForRobotTurn(true);
//                setAdjustRadarForGunTurn(true);
//                setAdjustRadarForRobotTurn(true);
                linearTarget(event);
                broadCastToDroid(enemy);
            }else if(robotPosition.getEnergy() == 0){
                goTo(robotPosition.getX(),robotPosition.getY());
            }else {
                if(numberMember == 1){
                    circularFire(event);
                }else {
                    broadCastToDroid(robotPosition);
                }
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

        enemy = new RobotPosition(enemyX,enemyY);

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
        double bulletPower = Math.min(3.0D,getEnergy());
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
        if(numberEnemy <= 2){
            enemy.setMelee(true);
        }else {
            enemy.setMelee(false);
        }
        setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
        setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
        if(this.getEnergy() > 50 || distanceTo(enemyX,enemyY) < 50){
            setFire(3);
        }else if(this.getEnergy() <= 50){
            setFire(1.5D);
        }
//        setFire(bulletPower);
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
        if(numberEnemy <= 2){
            enemy.setMelee(true);
        }else {
            enemy.setMelee(false);
        }
        setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
        setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
        if(this.getEnergy() > 50 || distanceTo(enemyX,enemyY) < 50){
            setFire(3);
        }else if(this.getEnergy() <= 50){
            setFire(1.5D);
        }
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
        int x,y;
        double borderRange  = getBattleFieldWidth() / 2;
        if(startPointY < borderRange){
            x = getRandom(10, 500);
            y = getRandom(10, 1000);
        }else{
            x = getRandom(500, 990);
            y = getRandom(10, 1000);

        }
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
        String dead = "dead";
        broadCastToDroid(dead);
    }
}
