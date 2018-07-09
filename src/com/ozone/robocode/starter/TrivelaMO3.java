package com.ozone.robocode.starter;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;

import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.tma.TTeamMemberRobot;

public class TrivelaMO3 extends TTeamMemberRobot {
    RobotPosition startPoint;
    RobotPosition secondPoint;
    RobotPosition thirdPoint;
    RobotPosition fourthPoint;
    RobotPosition myPos;
    RobotPosition enemyPos = null;
    RobotPosition deathRobot = new RobotPosition(0, 0);

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
//            RobotPosition.goTo(secondPoint, this);
//            RobotPosition.goTo(thirdPoint, this);
//            RobotPosition.goTo(fourthPoint, this);
            goTo(secondPoint);
            goTo(thirdPoint);
            goTo(fourthPoint);
        }
    }

    // private boolean isArrive(RobotPosition destination){
    // myPos = new RobotPosition(this.getX(),this.getY());
    // long x = Math.round(myPos.getX());
    // long y = Math.round(myPos.getY());
    // return x == destination.getX() && y == destination.getY();
    // }

    @Override
    public void onMessageReceived(MessageEvent e) {
        if (e.getMessage() instanceof RobotPosition) {
            myPos = new RobotPosition(this.getX(), this.getY());
            RobotPosition enemy = (RobotPosition) e.getMessage();
            // if(enemy.getName().equals(deathRobot.getName())){
            // enemyPos = null;
            // return;
            // }
            // if(enemyPos != null){
            // if(enemyPos.getName().equals(enemy.getName())){
            // double dx = enemy.getX() - this.getX();
            // double dy = enemy.getY() - this.getY();
            // // Calculate angle to target
            // double theta = Math.toDegrees(Math.atan2(dx, dy));
            // // Turn gun to target
            // turnGunRight(normalRelativeAngleDegrees(theta -
            // getGunHeading()));
            // // Fire hard!
            // if(this.getEnergy() > 50 && enemy.getDistance(myPos,enemy) <=
            // 200){
            // fire(3);
            // }else if(this.getEnergy() <= 50 || enemy.getDistance(myPos,enemy)
            // > 200){
            // fire(1.0D);
            // }
            // }
            // }else {
            // deathRobot.setName("");
            // enemyPos = enemy;
            // double dx = enemy.getX() - this.getX();
            // double dy = enemy.getY() - this.getY();
            // // Calculate angle to target
            // double theta = Math.toDegrees(Math.atan2(dx, dy));
            // // Turn gun to target
            // turnGunRight(normalRelativeAngleDegrees(theta -
            // getGunHeading()));
            // // Fire hard!
            // if(this.getEnergy() > 50 && enemy.getDistance(myPos,enemy) <=
            // 200){
            // fire(3);
            // }else if(this.getEnergy() <= 50 || enemy.getDistance(myPos,enemy)
            // > 200){
            // fire(1.0D);
            // }
            // }

            double dx = enemy.getX() - this.getX();
            double dy = enemy.getY() - this.getY();
            // Calculate angle to target
            double theta = Math.toDegrees(Math.atan2(dx, dy));
            // Turn gun to target
            turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
            // Fire hard!
                if (this.getEnergy() > 50 && enemy.getDistance(myPos, enemy) <= 400) {
                    setFire(3);
                } else if (this.getEnergy() <= 50 || enemy.getDistance(myPos, enemy) > 400) {
                    setFire(1.0D);
                }

        } // Set our colors
        else if (e.getMessage() instanceof RobotColors) {
            RobotColors c = (RobotColors) e.getMessage();
            setBodyColor(c.bodyColor);
            setGunColor(c.gunColor);
            setRadarColor(c.radarColor);
            setScanColor(c.scanColor);
            setBulletColor(c.bulletColor);
        }
    }

    @Override
    public void onHitRobot(HitRobotEvent e) {
        if (e.getBearing() > -90.0D && e.getBearing() < 90.0D) {
            if (isTeammate(e.getName())) {
                this.turnRight(90);
            } else {
                this.back(100);
            }

        } else {
            if (isTeammate(e.getName())) {
                this.turnRight(90);
            } else {
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

        setAhead(Math.min(distance, 300));
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        if (enemyPos.getName().equals(event.getName())) {
            deathRobot.setName(event.getName());
            enemyPos = null;
        }
    }

}
