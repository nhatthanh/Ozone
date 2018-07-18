package com.ozone.robocode.starter;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;

import robocode.BulletHitEvent;
import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.tma.TTeamMemberRobot;
import robocode.util.Utils;

public class TrivelaMO3 extends TTeamMemberRobot {
    RobotPosition startPoint;
    RobotPosition secondPoint;
    RobotPosition thirdPoint;
    RobotPosition fourthPoint;
    RobotPosition myPos;
    boolean melee = false;
    RobotPosition target;
    boolean soloTeam = false;

    @Override
    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        double borderRange = (float) getBattleFieldWidth() / 2;
        this.setAdjustGunForRobotTurn(true);
        // this.setMaxVelocity(5);
        if (getY() < borderRange) {
            startPoint = new RobotPosition(300, 300);
            secondPoint = new RobotPosition(300, 800);
            thirdPoint = new RobotPosition(800, 800);
            fourthPoint = new RobotPosition(800, 300);

        } else {
            startPoint = new RobotPosition(getBattleFieldWidth() - 300, getBattleFieldHeight() - 300);
            secondPoint = new RobotPosition(300, 800);
            thirdPoint = new RobotPosition(300, 300);
            fourthPoint = new RobotPosition(800, 300);
        }
        // this.goTo(startPoint);
        while (true) {
            if(soloTeam){
                this.setMaxVelocity(8);
                goToDestination(250,250);
                goToDestination(500,250);
                continue;
            }
            if(!melee){
                goTo(startPoint);
                goTo(secondPoint);
                goTo(thirdPoint);
                goTo(fourthPoint);
            }else if(target != null) {
                setMaxVelocity(8);
                goTo(target);
            }

        }
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        if (e.getMessage() instanceof RobotPosition) {
            myPos = new RobotPosition(this.getX(), this.getY());
            RobotPosition p = (RobotPosition) e.getMessage();
            if(p.isMelee()){
                melee = p.isMelee();
                target = p;
                findEnemyPoint(p);
            }else {
                melee = p.isMelee();
                findEnemyPoint(p);
            }
            if(p.isSoloTeam()){
                soloTeam = p.isSoloTeam();
                target = p;
                findEnemyPoint(p);
            }else {
                soloTeam = p.isSoloTeam();
                findEnemyPoint(p);
            }

        } // Set our colors
        else if (e.getMessage() instanceof RobotColors) {
            RobotColors c = (RobotColors) e.getMessage();
            setBodyColor(c.bodyColor);
            setGunColor(c.gunColor);
            setRadarColor(c.radarColor);
            setScanColor(c.scanColor);
            setBulletColor(c.bulletColor);
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


    private void goTo(RobotPosition position) {

        double x = position.getX();
        double y = position.getY();
        double dx = x - this.getX();
        double dy = y - this.getY();

        double theta = Math.toDegrees(Math.atan2(dx, dy));
        double degree = normalRelativeAngleDegrees(theta - getHeading());
        turnRight(degree);
        double distance = Math.sqrt(dx * dx + dy * dy);

        ahead(Math.min(distance, 300));
    }

    private void goToDestination(double x, double y){
        double dx = x - this.getX();
        double dy = y - this.getY();

        double theta = Math.toDegrees(Math.atan2(dx, dy));
        double degree = normalRelativeAngleDegrees(theta - getHeading());
        turnRight(degree);
        double distance = Math.sqrt(dx * dx + dy * dy);

        ahead(distance);
    }


    private void fireGun(){
        if(this.getEnergy() > 50){
            this.setFire(3.0D);
        }else if(this.getEnergy() <= 50){
            this.setFire(1.0D);
        }
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        if(!isTeammate(event.getName())){
            fireGun();
        }
    }

    private void findEnemyPoint(RobotPosition p){
        double dx = p.getX() - this.getX();
        double dy = p.getY() - this.getY();
        double theta = Math.toDegrees(Math.atan2(dx, dy));
        this.turnGunRight(Utils.normalRelativeAngleDegrees(theta - this.getGunHeading()));
        if(soloTeam){
            setFire(3);
        }else {
            if (this.getEnergy() > 50 && p.getDistance(myPos, p) <= 400) {
                setFire(3);
            } else if (this.getEnergy() <= 50 || p.getDistance(myPos, p) > 400) {
                setFire(1.5D);
            }
        }
    }

}
