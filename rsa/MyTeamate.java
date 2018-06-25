package rsa;

import robocode.*;
import robocode.tma.TTeamMemberRobot;
import robocode.util.Utils;

import java.awt.geom.Point2D;
import java.io.IOException;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class MyTeamate extends TTeamMemberRobot {

    @Override
    public void run() {
        out.println("MyTeamate is ready.");
        this.ahead(200);
        this.fire(3.0D);
        this.turnRight(90);
        this.ahead(200);
        while (true){
            this.ahead(200);
            this.turnRight(90);
        }

    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        // Fire at a point
        if (e.getMessage() instanceof Point) {
            Point p = (Point) e.getMessage();
            // Calculate x and y to target
            double dx = p.getX() - this.getX();
            double dy = p.getY() - this.getY();
            // Calculate angle to target
            double theta = Math.toDegrees(Math.atan2(dx, dy));
            // Turn gun to target
            turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));

            // Fire hard!
            if(this.getEnergy() > 50){
                fire(3);
            }else if(this.getEnergy() <= 50){
                fire(0.5D);
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



//
//    @Override
//    public void onHitByBullet(HitByBulletEvent e) {
//        this.turnRight(Utils.normalRelativeAngleDegrees(90.0D - (this.getHeading() - e.getHeading())));
//        this.ahead((double)this.dist);
//        this.dist *= -1;
//        this.scan();
//    }

    @Override
    public void onHitRobot(HitRobotEvent e) {
        // If he's in front of us, set back up a bit.
        if (e.getBearing() > -90 && e.getBearing() < 90) {
            back(100);
        } // else he's in back of us, so set ahead a bit.
        else {
            ahead(100);
        }
    }

    private double bearingTo(double heading, double x, double y) {
        return Utils.normalRelativeAngle(this.angleTo(x, y) - heading);
    }

    private double angleTo(double x, double y) {
        return Math.atan2(x - this.getX(), y - this.getY());
    }

    private double distanceTo(double x, double y) {
        return Math.hypot(x - this.getX(), y - this.getY());
    }

}
