package improvement;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.ozone.robocode.utils.RobotColors;
import com.ozone.robocode.utils.RobotPosition;
import com.sun.javafx.geom.Point2D;

import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;
import robocode.tma.TTeamLeaderRobot;
import robocode.util.Utils;

public class CaptainMO3 extends TTeamLeaderRobot {
    Point2D[] point = new Point2D[4];
    private int move = 200;
    Set<String> enemies = new HashSet<>();
    Set<String> teamMates = new HashSet<>();
    private boolean moveRandom = false;
    private boolean isPlanB = false;
    private int numberMember = 5;
    private boolean melee = false;
    private RobotPosition target = null;

    @Override
    public void onStatus(StatusEvent e) {
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        if (isTeammate(event.getName())) {
            // if (event.getEnergy() < 1) {
            // teamMates.remove(event.getName());
            // return;
            // }
            // teamMates.add(event.getName());
            return;
        }
        enemies.add(event.getName());
        if (!isPlanB) {
            if (enemies.size() > 1) {
                moveRandom = true;
                broadCastToTeam("PLANB");
                isPlanB = true;
            }
        }

        RobotPosition enemyPoint = RobotPosition.getPoint(event, this);
        enemyPoint.setMelee(melee);
        broadCastToTeam(enemyPoint);

        if (event.getEnergy() < 2) {
            goToDestination(new Point2D((float) enemyPoint.getX(), (float) enemyPoint.getY()));
        }
//        captainFire(event);
         if (numberMember == 1) {
             linearTarget(event);
         }
    }

    private void broadCastToTeam(Serializable message) {
        try {
            broadcastMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRun() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        RobotColors.setColorTeamRobot(this, RobotColors.getRobotColorCaptain());
        broadCastToTeam(RobotColors.getRobotColorDroid());
        if (getY() < 512) {
            point[0] = new Point2D(20, 20);
        } else {
            point[0] = new Point2D(1004, 1004);
            move *= -1;
        }
        updatePoints();
        while (true) {
            if (moveRandom || numberMember == 1) {
                if (target != null) {
                    goToDestination(new Point2D((float) target.getX(), (float) target.getY()));
                    continue;
                }
                randomMove();
                continue;
            }
            for (int i = 0; i < 4; i++) {
                goToDestination(point[i]);
            }
        }
    }

    private void updatePoints() {
        point[1] = new Point2D(point[0].x + move, point[0].y);
        point[2] = new Point2D(point[1].x, point[1].y + move);
        point[3] = new Point2D(point[2].x - move, point[2].y);
    }

    private void goToDestination(Point2D point) {
        double dx = point.x - this.getX();
        double dy = point.y - this.getY();

        double theta = Math.toDegrees(Math.atan2(dx, dy));
        double degree = normalRelativeAngleDegrees(theta - getHeading());
        turnRight(degree);
        double distance = Math.sqrt(dx * dx + dy * dy);

        ahead(distance);
    }

    private void randomMove() {
        int x, y;
        if (point[0].y < 512) {
            x = getRandom(34, 524);
            y = getRandom(10, 1000);
        } else {
            x = getRandom(500, 990);
            y = getRandom(10, 1000);

        }
        goTo(x, y);
        execute();
    }

    private void goTo(double x, double y) {

        double dx = x - this.getX();
        double dy = y - this.getY();

        double theta = Math.toDegrees(Math.atan2(dx, dy));
        double degree = normalRelativeAngleDegrees(theta - getHeading());
        turnRight(degree);
        double distance = Math.sqrt(dx * dx + dy * dy);

        setAhead(Math.min(distance, 300));
    }

    public int getRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private void captainFire(ScannedRobotEvent event) {
        double enemyBearing = this.getHeading() + event.getBearing();
        // Calculate enemy's position
        double enemyX = getX() + event.getDistance() * Math.sin(Math.toRadians(enemyBearing));
        double enemyY = getY() + event.getDistance() * Math.cos(Math.toRadians(enemyBearing));
        if (numberMember == 1) {
            target = new RobotPosition(enemyX, enemyY);
        }

        double dx = enemyX - this.getX();
        double dy = enemyY - this.getY();
        // Calculate angle to target
        double theta = Math.toDegrees(Math.atan2(dx, dy));

        // Turn gun to target
        turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));

        if (this.getEnergy() > 50) {
            fire(3);
        } else if (this.getEnergy() <= 50) {
            fire(1.5D);
        }
    }

    private void linearTarget(ScannedRobotEvent e) {
        double bulletPower = Math.min(3.0D, getEnergy());
        double myX = getX();
        double myY = getY();
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
        double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
        double enemyHeading = e.getHeadingRadians();
        double enemyVelocity = e.getVelocity();
        double deltaTime = 0;
        double battleFieldHeight = getBattleFieldHeight(), battleFieldWidth = getBattleFieldWidth();
        double predictedX = enemyX, predictedY = enemyY;
        while ((++deltaTime) * (20.0 - 3.0 * bulletPower) < java.awt.geom.Point2D.Double.distance(myX, myY, predictedX,
                predictedY)) {
            predictedX += Math.sin(enemyHeading) * enemyVelocity;
            predictedY += Math.cos(enemyHeading) * enemyVelocity;
            if (predictedX < 18.0 || predictedY < 18.0 || predictedX > battleFieldWidth - 18.0
                    || predictedY > battleFieldHeight - 18.0) {
                predictedX = Math.min(Math.max(18.0, predictedX), battleFieldWidth - 18.0);
                predictedY = Math.min(Math.max(18.0, predictedY), battleFieldHeight - 18.0);
                break;
            }
        }
        double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX - getX(), predictedY - getY()));
        if (numberMember == 1) {
            target = new RobotPosition(predictedX, predictedY);
        }
        // if (numberEnemy <= 2) {
        // enemy.setMelee(true);
        // } else {
        // enemy.setMelee(false);
        // }
        setTurnRadarRightRadians(Utils.normalRelativeAngle(absoluteBearing - getRadarHeadingRadians()));
        setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
        if (this.getEnergy() > 50 || distanceTo(enemyX, enemyY) < 50) {
            setFire(3);
        } else if (this.getEnergy() <= 50) {
            setFire(1.5D);
        }
        // setFire(bulletPower);
    }

    private double distanceTo(double x, double y) {
        return Math.hypot(x - this.getX(), y - this.getY());
    }

    @Override
    public void onDeath(DeathEvent event) {
        String dead = "dead";
        broadCastToTeam(dead);
    }

    public void onHitRobot(HitRobotEvent event) {
        if (!moveRandom) {
            return;
        }
        if (event.isMyFault()) {
            setBack(100);
        }
    }

    public void onHitByBullet(HitByBulletEvent event) {
        if (!moveRandom) {
            return;
        }
        double bearing = event.getBearing();
        if (Math.abs(bearing) > 45 && Math.abs(bearing) < 135) {
            return;
        }
        boolean front = Math.abs(bearing) < 45 ? true : false;
        boolean right = bearing > 0 ? true : false;
        if (bearing > 135) {
            bearing = 180 - bearing;
        }
        if (front && right || !front && !right) {
            setTurnLeft(60 - bearing);
            setAhead(150);
        }
        if (front && !right || !front && right) {
            setTurnRight(bearing + 60);
            setAhead(150);
        }
    }

    @Override
    public void onTeammateDeath(RobotDeathEvent event) {
        numberMember--;
        changeSkin(enemies.size()); 
    }

    @Override
    public void onEnemyDeath(RobotDeathEvent event) {
        enemies.remove(event.getName());
        int size = enemies.size();
        if (size <= 2) {
            melee = true;
        }
        changeSkin(size); 
    }

    private void changeSkin(int size) {
        if (numberMember > size) {
            broadCastToTeam(RobotColors.getRobotColorDroidMelee());
            RobotColors.setColorTeamRobot(this, RobotColors.getRobotColorSoloTeam());
            return;
        }
        if (numberMember < size) {
            broadCastToTeam(RobotColors.getRobotColorDroidLow());
            RobotColors.setColorTeamRobot(this, RobotColors.getRobotColorSoloTeam());
            return;
        }
    }
}
