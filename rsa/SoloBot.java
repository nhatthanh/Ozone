package rsa;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.List;

import robocode.*;
import robocode.tma.TTeamLeaderRobot;
import robocode.util.Utils;

public class SoloBot extends TTeamLeaderRobot{
    final double FIREPOWER = 3.0D;
    final double HALF_ROBOT_SIZE = 18.0D;
    final Map<String, SoloBot.RobotData> enemyMap = new LinkedHashMap<>(5, 2.0F, true);
    double scanDir = 1.0D;
    SoloBot.RobotData oldestScanned;
    SoloBot.RobotData target;
    long lastDirectionShift;
    int direction = 1;

    public SoloBot() {
    }

    @Override
    public void onRun() {
        RobotColors c = new RobotColors();
        c.bodyColor = Color.blue;
        c.gunColor = Color.blue;
        c.radarColor = Color.blue;
        c.scanColor = Color.yellow;
        c.bulletColor = Color.white;

        try {
            // Send RobotColors object to our entire team
            broadcastMessage(c);
        } catch (IOException ignored) {}
        this.initialize();
        while(true){
            this.handleRadar();
            this.handleGun();
            this.moveRobot();
            this.scan();
        }

    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        if (!e.isSentryRobot() && !isTeammate(e.getName())) {
            this.updateEnemyMap(e);
            this.updateScanDirection(e);
            this.updateEnemyTargetPositions();
        }
    }

    public void onPaint(Graphics2D g) {
        g.setStroke(new BasicStroke(2.0F));
        Color color1 = new Color(0, 255, 0, 64);
        Color color2 = new Color(255, 255, 0, 64);
        Iterator i$ = this.enemyMap.values().iterator();

        while(i$.hasNext()) {
            SoloBot.RobotData robot = (SoloBot.RobotData)i$.next();
            this.fillCircle(g, robot.scannedX, robot.scannedY, color1);
            this.fillCircle(g, robot.targetX, robot.targetY, color2);
            g.setColor(color1);
            g.drawLine((int)robot.scannedX, (int)robot.scannedY, (int)robot.targetX, (int)robot.targetY);
        }

        if (this.target != null) {
            color1 = new Color(255, 127, 0, 64);
            color2 = new Color(255, 0, 0, 128);
            this.fillCircle(g, this.target.scannedX, this.target.scannedY, color1);
            this.fillCircle(g, this.target.targetX, this.target.targetY, color2);
            g.setColor(color1);
            g.drawLine((int)this.target.scannedX, (int)this.target.scannedY, (int)this.target.targetX, (int)this.target.targetY);
        }

    }

    public void onEnemyDeath(RobotDeathEvent robotDeathEvent) {
        String deadRobotName = robotDeathEvent.getName();
        this.enemyMap.remove(deadRobotName);
        if (this.oldestScanned != null && this.oldestScanned.name.equals(deadRobotName)) {
            this.oldestScanned = null;
        }

        if (this.target != null && this.target.name.equals(deadRobotName)) {
            this.target = null;
        }

    }

    private void initialize() {
        this.setAdjustRadarForGunTurn(true);
        this.setAdjustGunForRobotTurn(true);
        this.setBodyColor(new Color(92, 51, 23));
        this.setGunColor(new Color(69, 139, 116));
        this.setRadarColor(new Color(210, 105, 30));
        this.setBulletColor(new Color(255, 211, 155));
        this.setScanColor(new Color(202, 255, 112));
    }

    private void handleRadar() {
        this.setTurnRadarRightRadians(this.scanDir * 1.0D / 0.0);
    }

    private void handleGun() {
        this.updateTarget();
        this.updateGunDirection();
        this.fireGunWhenReady();
    }



    private void updateTarget() {
        this.target = null;
        List<SoloBot.RobotData> targets = new ArrayList<>(this.enemyMap.values());
        Iterator it = targets.iterator();

        while(it.hasNext()) {
            SoloBot.RobotData robot = (SoloBot.RobotData)it.next();
            if (this.isOutsideAttackRange(robot.targetX, robot.targetY)) {
                it.remove();
            }
        }

        double minDist = 1.0D / 0.0;
        Iterator i$ = targets.iterator();

        while(i$.hasNext()) {
            SoloBot.RobotData robot = (SoloBot.RobotData)i$.next();
            double dist = this.distanceTo(robot.targetX, robot.targetY);
            if (dist < minDist) {
                minDist = dist;
                this.target = robot;
            }
        }

        if (this.target == null && targets.size() > 0) {
            this.target = targets.get(0);
        }

    }

    private void updateGunDirection() {
        if (this.target != null) {
            double targetBearing = this.bearingTo(this.getGunHeadingRadians(), this.target.targetX, this.target.targetY);
            this.setTurnGunRightRadians(targetBearing);
        }

    }

    private void fireGunWhenReady() {

        if (this.target != null) {
            if(!isTeammate(target.name)){
                try {
                    this.broadcastMessage(new Point(this.target.targetX,this.target.targetY));
                    double dist = this.distanceTo(this.target.targetX, this.target.targetY);
                    double angle = Math.atan(18.0D / dist);
                    if (Math.abs(this.getGunTurnRemaining()) < angle) {
                        this.setFire(3.0D);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void moveRobot() {
        int newDirection = this.direction;
        if (this.target != null) {
            int borderRange = this.getSentryBorderSize() - 20;
            boolean horizontal = false;
            boolean vertical = false;
            double newHeading = this.getHeadingRadians();
            if (this.getY() < (double)borderRange || this.getY() > this.getBattleFieldHeight() - (double)borderRange) {
                horizontal = true;
            }

            if (this.getX() < (double)borderRange || this.getX() > this.getBattleFieldWidth() - (double)borderRange) {
                vertical = true;
            }

            if (horizontal && vertical && Math.abs(this.target.targetX - this.getX()) <= Math.abs(this.target.targetY - this.getY())) {
                horizontal = false;
            }

            if (horizontal) {
                --newHeading;
            }

            this.setTurnLeftRadians(Utils.normalRelativeAngle(newHeading));
            if (Math.abs(this.getTurnRemaining()) < 1.0D || Math.abs(this.getVelocity()) < 0.01D) {
                double delta;
                if (horizontal) {
                    delta = this.target.targetX - this.getX();
                } else {
                    delta = this.target.targetY - this.getY();
                }

                this.setAhead(delta);
                newDirection = delta > 0.0D ? 1 : -1;
                if (this.getTime() - this.lastDirectionShift > 10L) {
                    if (Math.abs(this.getVelocity()) < 1.0D) {
                        newDirection = this.direction * -1;
                    }

                    if (newDirection != this.direction) {
                        this.direction = newDirection;
                        this.lastDirectionShift = this.getTime();
                    }
                }
            }
        }

        this.setAhead((double)(100 * this.direction));
    }

    private void updateEnemyMap(ScannedRobotEvent scannedRobotEvent) {
        String scannedRobotName = scannedRobotEvent.getName();
        SoloBot.RobotData scannedRobot = (SoloBot.RobotData)this.enemyMap.get(scannedRobotName);
        if (scannedRobot == null) {
            scannedRobot = new SoloBot.RobotData(scannedRobotEvent);
            this.enemyMap.put(scannedRobotName, scannedRobot);
        } else {
            scannedRobot.update(scannedRobotEvent);
        }

    }

    private void updateScanDirection(ScannedRobotEvent scannedRobotEvent) {
        String scannedRobotName = scannedRobotEvent.getName();
        if ((this.oldestScanned == null || scannedRobotName.equals(this.oldestScanned.name)) && this.enemyMap.size() == this.getOthers()) {
            SoloBot.RobotData oldestScannedRobot = (SoloBot.RobotData)this.enemyMap.values().iterator().next();
            double x = oldestScannedRobot.scannedX;
            double y = oldestScannedRobot.scannedY;
            double ourHeading = this.getRadarHeadingRadians();
            double bearing = this.bearingTo(ourHeading, x, y);
            this.scanDir = bearing;
        }

    }

    private void updateEnemyTargetPositions() {
        Iterator i$ = this.enemyMap.values().iterator();

        while(i$.hasNext()) {
            SoloBot.RobotData enemy = (SoloBot.RobotData)i$.next();
            double bV = Rules.getBulletSpeed(3.0D);
            double eX = enemy.scannedX;
            double eY = enemy.scannedY;
            double eV = enemy.scannedVelocity;
            double eH = enemy.scannedHeading;
            double A = (eX - this.getX()) / bV;
            double B = (eY - this.getY()) / bV;
            double C = eV / bV * Math.sin(eH);
            double D = eV / bV * Math.cos(eH);
            double a = A * A + B * B;
            double b = 2.0D * (A * C + B * D);
            double c = C * C + D * D - 1.0D;
            double discrim = b * b - 4.0D * a * c;
            if (discrim >= 0.0D) {
                double t1 = 2.0D * a / (-b - Math.sqrt(discrim));
                double t2 = 2.0D * a / (-b + Math.sqrt(discrim));
                double t = Math.min(t1, t2) >= 0.0D ? Math.min(t1, t2) : Math.max(t1, t2);
                double targetX = eX + eV * t * Math.sin(eH);
                double targetY = eY + eV * t * Math.cos(eH);
                double minX = 18.0D;
                double minY = 18.0D;
                double maxX = this.getBattleFieldWidth() - 18.0D;
                double maxY = this.getBattleFieldHeight() - 18.0D;
                enemy.targetX = this.limit(targetX, minX, maxX);
                enemy.targetY = this.limit(targetY, minY, maxY);
            }
        }

    }

    private double limit(double value, double min, double max) {
        return Math.min(max, Math.max(min, value));
    }

    public class RobotData implements Serializable {
        final String name;
        double scannedX;
        double scannedY;
        double scannedVelocity;
        double scannedHeading;
        double targetX;
        double targetY;

        RobotData(ScannedRobotEvent event) {
            this.name = event.getName();
            this.update(event);
            this.targetX = this.scannedX;
            this.targetY = this.scannedY;
        }

        void update(ScannedRobotEvent event) {
            Point2D.Double pos = this.getPosition(event);
            this.scannedX = pos.x;
            this.scannedY = pos.y;
            this.scannedVelocity = event.getVelocity();
            this.scannedHeading = event.getHeadingRadians();
        }

        Point2D.Double getPosition(ScannedRobotEvent event) {
            double distance = event.getDistance();
            double angle = SoloBot.this.getHeadingRadians() + event.getBearingRadians();
            double x = SoloBot.this.getX() + Math.sin(angle) * distance;
            double y = SoloBot.this.getY() + Math.cos(angle) * distance;
            return new Point2D.Double(x, y);
        }
    }

    private boolean isOutsideAttackRange(double x, double y) {
        double minBorderX = (double)this.getSentryBorderSize();
        double minBorderY = (double)this.getSentryBorderSize();
        double maxBorderX = this.getBattleFieldWidth() - (double)this.getSentryBorderSize();
        double maxBorderY = this.getBattleFieldHeight() - (double)this.getSentryBorderSize();
        return x > minBorderX && y > minBorderY && x < maxBorderX && y < maxBorderY;
    }

    private double distanceTo(double x, double y) {
        return Math.hypot(x - this.getX(), y - this.getY());
    }

    private double angleTo(double x, double y) {
        return Math.atan2(x - this.getX(), y - this.getY());
    }

    private double bearingTo(double heading, double x, double y) {
        return Utils.normalRelativeAngle(this.angleTo(x, y) - heading);
    }

    private void fillCircle(Graphics2D gfx, double x, double y, Color color) {
        gfx.setColor(color);
        gfx.fillOval((int)x - 20, (int)y - 20, 40, 40);
    }
}
