package com.ozone.robocode.starter;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;

import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.tma.TTeamMemberRobot;

public class EdgeLeftMO3 extends TTeamMemberRobot {

    RobotPosition point1;
    RobotPosition point2;
    RobotPosition point3;

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
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        double borderRange = (float) getBattleFieldWidth() / 2;
        this.setAdjustGunForRobotTurn(true);
        if (getX() < borderRange) {
            point1 = new RobotPosition(60, 120);
            point2 = new RobotPosition(60, 970);
            point3 = new RobotPosition(600, 970);

        } else {
            point1 = new RobotPosition(getBattleFieldWidth() - 60, getBattleFieldHeight() - 120);
            point2 = new RobotPosition(getBattleFieldWidth() - 60, 60);
            point3 = new RobotPosition(600, 60);
        }
        while (true) {
            RobotPosition.goTo(point1, this);
            RobotPosition.goTo(point2, this);
            RobotPosition.goTo(point3, this);
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
}
