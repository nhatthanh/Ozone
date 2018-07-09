package com.ozone.robocode.starter;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;

import robocode.*;
import robocode.tma.TTeamMemberRobot;

public class EdgeRightMO3 extends TTeamMemberRobot {
    RobotPosition[] point  = new RobotPosition[3];

    @Override
    public void onMessageReceived(MessageEvent event) {
        if (event.getMessage() instanceof RobotColors) {
            RobotColors.setColorTeamRobot(this, (RobotColors) event.getMessage());
        }
        if (event.getMessage() instanceof RobotPosition) {
            RobotPosition p = (RobotPosition) event.getMessage();
            RobotPosition myPos = new RobotPosition(this.getX(), this.getY());
            double dx = p.getX() - this.getX();
            double dy = p.getY() - this.getY();
            double target = Math.toDegrees(Math.atan2(dx, dy));
            turnGunRight(normalRelativeAngleDegrees(target - getGunHeading()));
                if (this.getEnergy() > 50 && p.getDistance(myPos, p) <= 400) {
                    setFire(3);
                } else if (this.getEnergy() <= 50 || p.getDistance(myPos, p) > 400) {
                    setFire(1.0D);
            }
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
            go();
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
            this.setFire(1.0D);
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
}
