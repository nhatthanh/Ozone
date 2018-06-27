/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package tma.o2.robot;

import robocode.*;
import robocode.tma.TTeamLeaderRobot;
import tma.o2.action.ActionPool;
import tma.o2.misc.*;
import tma.o2.misc.Target;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * MyFirstLeader - a sample team robot by Mathew Nelson.
 * <p/>
 * Looks around for enemies, and orders teammates to fire
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Leader extends TTeamLeaderRobot {

    private final Color LEADER_COLOR = Color.WHITE;
    private final Color TEAM_COLOR = Color.BLACK;
    private final Color GUN_COLOR = Color.WHITE;

    private ActionPool action = new ActionPool();

    public void onRun() {
        setColor();
        while (true) {
            setTurnRadarRight(10000);
            int x = getRandom(10, 900);
            int y = getRandom(10, 900);
            goTo(x, y);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if (isTeammate(e.getName())) {
            return;
        }
        Target enemy = constructEnemyOnScanEvent(e);

        fire(enemy);
        sendMessage(enemy);
    }

    private void fire(Target enemy) {
        double dx = enemy.getX() - this.getX();
        double dy = enemy.getY() - this.getY();

        double theta = Math.toDegrees(Math.atan2(dx, dy));
        turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));

        double distance = enemy.getDistance();

        // if (distance > 800)
        // fire(5);
        // else if (distance > 600 && distance <= 800)
        // fire(4);
        // else if (distance > 400 && distance <= 600)
        // fire(3);
        // else if (distance > 200 && distance <= 400)
        // fire(2);
        // else if (distance < 200)
        // fire(1);
        fire(1);
    }

    private void sendMessage(Target enemy) {
        try {
            broadcastMessage(enemy);
        } catch (IOException ex) {
            out.println("Unable to send order: ");
            ex.printStackTrace(out);
        }
    }

    private Target constructEnemyOnScanEvent(ScannedRobotEvent e) {
        double enemyBearing = this.getHeading() + e.getBearing();
        double enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
        double enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
        double distance = e.getDistance();
        double bearing = e.getBearing();
        double heading = e.getHeading();
        int priority = e.getPriority();
        double energy = e.getEnergy();

        Target enemy = new Target(enemyX, enemyY, distance, bearing, heading, priority, energy);
        return enemy;
    }

    public int getRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private void setColor() {
        RobotColors c = new RobotColors();

        c.bodyColor = TEAM_COLOR;
        c.gunColor = TEAM_COLOR;
        c.radarColor = TEAM_COLOR;
        c.scanColor = TEAM_COLOR;
        c.bulletColor = GUN_COLOR;

        setBodyColor(LEADER_COLOR);
        setGunColor(LEADER_COLOR);
        setRadarColor(LEADER_COLOR);
        setScanColor(LEADER_COLOR);
        setBulletColor(GUN_COLOR);
        try {
            broadcastMessage(c);
        } catch (IOException ignored) {
        }
    }

    private void goTo(double x, double y) {

        double dx = x - this.getX();
        double dy = y - this.getY();

        double theta = Math.toDegrees(Math.atan2(dx, dy));
        double degree = normalRelativeAngleDegrees(theta - getHeading());
        turnRight(degree);

        this.ahead(Math.sqrt(dx * dx + dy * dy));

    }

    private double distanceTo(double x, double y) {
        double dx = x - this.getX();
        double dy = x - this.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
