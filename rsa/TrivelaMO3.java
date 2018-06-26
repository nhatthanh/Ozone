package rsa;

import robocode.MessageEvent;
import robocode.TurnCompleteCondition;
import robocode.tma.TTeamMemberRobot;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class TrivelaMO3 extends TTeamMemberRobot{
    Point startPoint;
    @Override
    public void run() {
        double borderRange = (float) getBattleFieldWidth()/2;
        this.setAdjustGunForRobotTurn(true);
//        this.setMaxVelocity(5);
        if(getY() < borderRange){
            startPoint = new Point(300,300);
        }else {
            startPoint = new Point(getBattleFieldWidth() - 300,  getBattleFieldHeight() - 300);
        }
        this.goTo(startPoint);

        while (true){
            goTo(new Point(300,800));
            goTo(new Point(800,300));
            goTo(startPoint);
        }

    }

    private void goTo(Point destination){
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

    private double distanceTo(double x, double y) {
        return Math.hypot(x - this.getX(), y - this.getY());
    }
}
