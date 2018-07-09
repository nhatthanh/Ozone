package com.ozone.robocode.starter;

import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import robocode.tma.TTeamMemberRobot;
import robocode.util.Utils;
import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;

import java.awt.geom.Point2D;


public class WallyMO3 extends TTeamMemberRobot {


    private final int NUMBER_OF_ENEMY_LEFT = 2;
    boolean peek;
    double moveAmount;
    RobotPosition myPos;
    public WallyMO3() {
    }

    @Override
    public void run() {
        this.moveAmount = Math.max(this.getBattleFieldWidth(), this.getBattleFieldHeight()) - 100;
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
            this.turnLeft(90.0D);
        }
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        if (e.getMessage() instanceof RobotPosition) {
            myPos = new RobotPosition(this.getX(), this.getY());
            RobotPosition p = (RobotPosition)e.getMessage();
                double dx = p.getX() - this.getX();
                double dy = p.getY() - this.getY();
                double theta = Math.toDegrees(Math.atan2(dx, dy));
                this.turnGunRight(Utils.normalRelativeAngleDegrees(theta - this.getGunHeading()));
                    if (this.getEnergy() > 50 && p.getDistance(myPos, p) <= 400) {
                        setFire(3);
                    } else if (this.getEnergy() <= 50 || p.getDistance(myPos, p) > 400) {
                        setFire(1.0D);
                    }


        } else if (e.getMessage() instanceof RobotColors) {
            RobotColors c = (RobotColors)e.getMessage();
            this.setBodyColor(c.bodyColor);
            this.setGunColor(c.gunColor);
            this.setRadarColor(c.radarColor);
            this.setScanColor(c.scanColor);
            this.setBulletColor(c.bulletColor);
        }
    }

    public void onHitRobot(HitRobotEvent e) {
        if (e.getBearing() > -90.0D && e.getBearing() < 90.0D) {
            if(isTeammate(e.getName())){
                this.turnRight(90);
            }else{
                this.turnGunRight(getHeading() - getGunHeading() + e.getBearing());
                fireGun();
                this.back(100);
            }

        } else {
            if(isTeammate(e.getName())){
                this.turnRight(90);
            }else{

                this.turnGunRight(getHeading() - getGunHeading() + e.getBearing());
                fireGun();
                this.ahead(100.0D);
            }
        }
    }

    private void fireGun(){
        if(this.getEnergy() > 50){
            this.setFire(3.0D);
        }else if(this.getEnergy() <= 50){
            this.setFire(1.0D);
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        if (e.getBearing() > -90.0D && e.getBearing() < 90.0D) {
            this.back(100.0D);
        } else {
            this.ahead(100.0D);
        }
    }

//    private void linearTarget(RobotPosition e){
//            double bulletPower = Math.min(3.0,getEnergy());
//            double myX = getX();
//            double myY = getY();
//            double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
//            double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
//            double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
//            double enemyHeading = e.getHeadingRadians();
//            double enemyVelocity = e.getVelocity();
//            double deltaTime = 0;
//            double battleFieldHeight = getBattleFieldHeight(),
//                    battleFieldWidth = getBattleFieldWidth();
//            double predictedX = enemyX, predictedY = enemyY;
//            while((++deltaTime) * (20.0 - 3.0 * bulletPower) <
//                    Point2D.Double.distance(myX, myY, predictedX, predictedY)){
//                predictedX += Math.sin(enemyHeading) * enemyVelocity;
//                predictedY += Math.cos(enemyHeading) * enemyVelocity;
//                if(	predictedX < 18.0
//                        || predictedY < 18.0
//                        || predictedX > battleFieldWidth - 18.0
//                        || predictedY > battleFieldHeight - 18.0){
//                    predictedX = Math.min(Math.max(18.0, predictedX),
//                            battleFieldWidth - 18.0);
//                    predictedY = Math.min(Math.max(18.0, predictedY),
//                            battleFieldHeight - 18.0);
//                    break;
//                }
//            }
//            double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX - getX(), predictedY - getY()));
//            setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
//            setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
//            setFire(bulletPower);
//    }
}
