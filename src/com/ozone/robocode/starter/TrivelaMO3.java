package com.ozone.robocode.starter;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.tma.TTeamMemberRobot;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class TrivelaMO3 extends TTeamMemberRobot{
    RobotPosition startPoint;
    RobotPosition secondPoint;
    RobotPosition thirdPoint;
    RobotPosition fourthPoint;
    RobotPosition myPos;
    RobotPosition enemyPos = null;
    RobotPosition deathRobot =  new RobotPosition(0,0);

    @Override
    public void run() {
        double borderRange = (float) getBattleFieldWidth()/2;
        this.setAdjustGunForRobotTurn(true);
//        this.setMaxVelocity(5);
        if(getY() < borderRange){
            startPoint = new RobotPosition(300,300);
            secondPoint = new RobotPosition(300,800);
            thirdPoint = new RobotPosition(800,800);
            fourthPoint = new RobotPosition(800,300);

        }else {
            startPoint = new RobotPosition(getBattleFieldWidth() - 300,  getBattleFieldHeight() - 300);
            secondPoint = new RobotPosition(300,800);
            thirdPoint = new RobotPosition(300,300);
            fourthPoint = new RobotPosition(800,300);
        }
//        this.goTo(startPoint);
        while (true){
            goTo(secondPoint);
            goTo(thirdPoint);
            goTo(fourthPoint);
        }
    }

    private void goTo(RobotPosition destination){
        // Calculate x and y to target
        double dx = destination.getX() - this.getX();
        double dy = destination.getY() - this.getY();
        // Calculate angle to target
        double theta = Math.toDegrees(Math.atan2(dx, dy));
        // Turn gun to target and go to that point
        turnGunRight(normalRelativeAngleDegrees(theta - getHeading()));
        turnRight(normalRelativeAngleDegrees(theta - getHeading()));
        this.ahead(distanceTo(destination.getX(),destination.getY()));
    }

//    private boolean isArrive(RobotPosition destination){
//        myPos = new RobotPosition(this.getX(),this.getY());
//        long x = Math.round(myPos.getX());
//        long y = Math.round(myPos.getY());
//        return x == destination.getX() && y == destination.getY();
//    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        if (e.getMessage() instanceof RobotPosition) {
            myPos = new RobotPosition(this.getX(),this.getY());
            RobotPosition enemy = (RobotPosition) e.getMessage();
            if(enemy.getName().equals(deathRobot.getName())){
                enemyPos = null;
                return;
            }
            if(enemyPos != null){
                if(enemyPos.getName().equals(enemy.getName())){
                    double dx = enemy.getX() - this.getX();
                    double dy = enemy.getY() - this.getY();
                    // Calculate angle to target
                    double theta = Math.toDegrees(Math.atan2(dx, dy));
                    // Turn gun to target
                    turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
                    // Fire hard!
                    if(this.getEnergy() > 50 && enemy.getDistance(myPos,enemy) <= 200){
                        fire(3);
                    }else if(this.getEnergy() <= 50 || enemy.getDistance(myPos,enemy) > 200){
                        fire(1.0D);
                    }
                }
            }else {
                deathRobot.setName("");
                enemyPos = enemy;
                double dx = enemy.getX() - this.getX();
                double dy = enemy.getY() - this.getY();
                // Calculate angle to target
                double theta = Math.toDegrees(Math.atan2(dx, dy));
                // Turn gun to target
                turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
                // Fire hard!
                if(this.getEnergy() > 50 && enemy.getDistance(myPos,enemy) <= 200){
                    fire(3);
                }else if(this.getEnergy() <= 50 || enemy.getDistance(myPos,enemy) > 200){
                    fire(1.0D);
                }
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
            if(isTeammate(e.getName())){
                this.turnRight(90);
            }else{
                this.back(100);
            }

        } else {
            if(isTeammate(e.getName())){
                this.turnRight(90);
            }else{
                this.ahead(100.0D);
            }
        }
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        if(enemyPos.getName().equals(event.getName())){
            deathRobot.setName(event.getName());
            enemyPos = null;
        }
    }

    private double distanceTo(double x, double y) {
        return Math.hypot(x - this.getX(), y - this.getY());
    }
}
