package improvement;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;
import com.sun.javafx.geom.Point2D;

import robocode.MessageEvent;
import robocode.tma.TTeamMemberRobot;
import robocode.util.Utils;

public class Droid3MO3 extends TTeamMemberRobot {
    Point2D[] point = new Point2D[4];
    int move = 100;

    private void goToDestination(Point2D point) {
        double dx = point.x - this.getX();
        double dy = point.y - this.getY();

        double theta = Math.toDegrees(Math.atan2(dx, dy));
        double degree = normalRelativeAngleDegrees(theta - getHeading());
        turnRight(degree);
        double distance = Math.sqrt(dx * dx + dy * dy);

        ahead(distance);
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        if (event.getMessage() instanceof RobotColors) {
            RobotColors.setColorTeamRobot(this, (RobotColors) event.getMessage());
            return;
        }
        if (event.getMessage() instanceof RobotPosition) {
            RobotPosition p = (RobotPosition) event.getMessage();
            RobotPosition myPos = new RobotPosition(getX(), getY());
            double dx = p.getX() - this.getX();
            double dy = p.getY() - this.getY();
            double theta = Math.toDegrees(Math.atan2(dx, dy));
            this.turnGunRight(Utils.normalRelativeAngleDegrees(theta - this.getGunHeading()));
            if (this.getEnergy() > 50 && p.getDistance(myPos, p) <= 400) {
                setFire(3);
            } else if (this.getEnergy() <= 50 || p.getDistance(myPos, p) > 400) {
                setFire(1.5D);
            }
            return;
        }
        if (event.getMessage() == "PLANB") {
            updatePoints(true);
            return;
        }
    }

    @Override
    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        if (getY() < 512) {
            point[0] = new Point2D(512, 30);
        } else {
            point[0] = new Point2D(512, 994);
            move *= -1;
        }
        updatePoints(false);
        while (true) {
            for (int i = 0; i < 4; i++) {
                goToDestination(point[i]);
            }
        }
    }

    private void updatePoints(boolean isRandom) {
        if (isRandom) {
            if (point[0].y < 512) {
                point[0] = new Point2D(300, 300);
                point[1] = new Point2D(300, 800);
                point[2] = new Point2D(800, 800);
                point[3] = new Point2D(800, 300);
            } else {
                point[0] = new Point2D(724, 724);
                point[1] = new Point2D(300, 800);
                point[2] = new Point2D(300, 300);
                point[3] = new Point2D(800, 300);
            }
            return;
        }
        point[1] = new Point2D(point[0].x + move, point[0].y);
        point[2] = new Point2D(point[1].x, point[1].y + move);
        point[3] = new Point2D(point[2].x - move, point[2].y);
    }
}
