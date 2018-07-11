package com.ozone.robocode.starter;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;

import robocode.*;
import robocode.tma.TTeamMemberRobot;
import robocode.util.Utils;

public class EdgeRightMO3 extends TTeamMemberRobot {
    RobotPosition[] point  = new RobotPosition[3];
    boolean melee = false;
    RobotPosition target;
    RobotPosition myPos;

    @Override
    public void onMessageReceived(MessageEvent event) {
        if (event.getMessage() instanceof RobotColors) {
            RobotColors.setColorTeamRobot(this, (RobotColors) event.getMessage());
        }
        if (event.getMessage() instanceof RobotPosition) {
            RobotPosition p = (RobotPosition) event.getMessage();
            myPos = new RobotPosition(this.getX(), this.getY());
            if(p.getNumberEnemy() <= 2) {
                melee = true;
                target = p;
                findEnemyPoint(p);
            }else {
                melee = false;
                findEnemyPoint(p);
            }
        }else if(event.getMessage().equals("dead")){
            melee = false;
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
            if(!melee){
                go();
            }else if(target != null) {
                setMaxVelocity(8);
                goTo(target.getX(),target.getY());
            }

        }
    }

    @Override
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
            this.setFire(1.5D);
        }
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

    @Override
    public void onBulletHit(BulletHitEvent event) {
        if(!isTeammate(event.getName())){
            fireGun();
        }
    }

    private void goTo(double x, double y) {

        double dx = x - this.getX();
        double dy = y - this.getY();

        double theta = Math.toDegrees(Math.atan2(dx, dy));
        double degree = normalRelativeAngleDegrees(theta - getHeading());
        turnRight(degree);
        double distance = Math.sqrt(dx * dx + dy * dy);

        ahead(Math.min(distance, 300));
    }

    private void findEnemyPoint(RobotPosition p){
        double dx = p.getX() - this.getX();
        double dy = p.getY() - this.getY();
        double theta = Math.toDegrees(Math.atan2(dx, dy));
        this.turnGunRight(Utils.normalRelativeAngleDegrees(theta - this.getGunHeading()));
        if (this.getEnergy() > 50 && p.getDistance(myPos, p) <= 400) {
            setFire(3);
        } else if (this.getEnergy() <= 50 || p.getDistance(myPos, p) > 400) {
            setFire(1.5D);
        }
    }
}
