package rsa;

import robocode.BorderSentry;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.tma.TTeamMemberRobot;
import robocode.util.Utils;

public class Wally extends TTeamMemberRobot{
    boolean peek;
    double moveAmount;

    public Wally() {
    }

    @Override
    public void run() {
        this.moveAmount = Math.max(this.getBattleFieldWidth(), this.getBattleFieldHeight());
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
        if (e.getMessage() instanceof Point) {
            Point p = (Point)e.getMessage();
            double dx = p.getX() - this.getX();
            double dy = p.getY() - this.getY();
            double theta = Math.toDegrees(Math.atan2(dx, dy));
            this.turnGunRight(Utils.normalRelativeAngleDegrees(theta - this.getGunHeading()));
            if(this.getEnergy() > 50){
                this.fire(3.0D);
            }else if(this.getEnergy() <= 50){
                this.fire(1.0D);
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
    public void onHitByBullet(HitByBulletEvent e) {
        if (e.getBearing() > -90.0D && e.getBearing() < 90.0D) {
            this.back(100.0D);
        } else {
            this.ahead(100.0D);
        }
    }
}
