package com.ozone.robocode.starter;

import robocode.*;
import robocode.tma.TTeamMemberRobot;
import robocode.util.Utils;
import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;

import java.awt.geom.Point2D;

import static robocode.util.Utils.normalRelativeAngleDegrees;


public class WallyMO3 extends TTeamMemberRobot {

    double moveAmount;
    RobotPosition myPos;
    boolean melee = false;
    RobotPosition target;
    public WallyMO3() {
    }

    @Override
    public void onRun() {
        moveAmount = Math.max(this.getBattleFieldWidth(), this.getBattleFieldHeight()) - 100;
        turnLeft(this.getHeading() % 90.0D);
        ahead(this.moveAmount);
        turnGunRight(90.0D);
        turnRight(90.0D);

        while(true) {
            if(!melee){
                ahead(this.moveAmount);
                turnLeft(90.0D);
            }else if(target != null) {
                setMaxVelocity(8);
                goTo(target.getX(),target.getY());
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        if (e.getMessage() instanceof RobotPosition) {
            myPos = new RobotPosition(this.getX(), this.getY());
            RobotPosition p = (RobotPosition)e.getMessage();
            if(p.getNumberEnemy() <= 2){
                melee = true;
                target = p;
                findEnemyPoint(p);
            }else {
                melee = false;
                findEnemyPoint(p);
            }
        } else if (e.getMessage() instanceof RobotColors) {
            RobotColors c = (RobotColors)e.getMessage();
            this.setBodyColor(c.bodyColor);
            this.setGunColor(c.gunColor);
            this.setRadarColor(c.radarColor);
            this.setScanColor(c.scanColor);
            this.setBulletColor(c.bulletColor);
        }else if(e.getMessage().equals("dead")){
            melee = false;
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
            this.fire(3.0D);
        }else if(this.getEnergy() <= 50){
            this.fire(1.5D);
        }
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        if(!isTeammate(event.getName())){
            fireGun();
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
            turnLeft(60 - bearing);
            ahead(150);
        }
        if (front && !right || !front && right) {
            turnRight(bearing + 60);
            ahead(150);
        }
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

    private void findEnemyPoint(RobotPosition p){
        double dx = p.getX() - this.getX();
        double dy = p.getY() - this.getY();
        double theta = Math.toDegrees(Math.atan2(dx, dy));
        this.turnGunRight(Utils.normalRelativeAngleDegrees(theta - this.getGunHeading()));
        if (this.getEnergy() > 50 && p.getDistance(myPos, p) <= 400) {
            fire(3);
        } else if (this.getEnergy() <= 50 || p.getDistance(myPos, p) > 400) {
            fire(1.5D);
        }
    }
}
