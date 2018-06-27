package com.ozone.robocode.starter;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;
import robocode.MessageEvent;
import robocode.tma.TTeamMemberRobot;


import static robocode.util.Utils.normalRelativeAngleDegrees;

public class TrivelaMO3 extends TTeamMemberRobot{
    RobotPosition startPoint;
    RobotPosition myPos;
    @Override
    public void run() {
        double borderRange = (float) getBattleFieldWidth()/2;
        this.setAdjustGunForRobotTurn(true);
//        this.setMaxVelocity(5);
        if(getY() < borderRange){
            startPoint = new RobotPosition(300,300);
        }else {
            startPoint = new RobotPosition(getBattleFieldWidth() - 300,  getBattleFieldHeight() - 300);
        }
        this.goTo(startPoint);

        while (true){
            goTo(new RobotPosition(300,800));
            goTo(new RobotPosition(800,300));
            goTo(startPoint);
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


    @Override
    public void onMessageReceived(MessageEvent e) {
        if (e.getMessage() instanceof RobotPosition) {
            myPos = new RobotPosition(this.getX(),this.getY());
            RobotPosition enemy = (RobotPosition) e.getMessage();
            // Calculate x and y to target
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

    private double distanceTo(double x, double y) {
        return Math.hypot(x - this.getX(), y - this.getY());
    }
}
