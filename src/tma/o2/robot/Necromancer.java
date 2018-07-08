/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package tma.o2.robot;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;
import robocode.tma.TTeamLeaderRobot;
import robocode.util.Utils;
import tma.o2.action.ActionPool;
import tma.o2.misc.RobotColors;
import tma.o2.misc.Target;

/**
 * MyFirstLeader - a sample team robot by Mathew Nelson.
 * <p/>
 * Looks around for enemies, and orders teammates to fire
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Necromancer extends TTeamLeaderRobot {

	private final Color LEADER_COLOR = Color.WHITE;
	private final Color TEAM_COLOR = Color.BLACK;
	private final Color GUN_COLOR = Color.WHITE;

	private ActionPool action = new ActionPool();

	//-----------------
	//-----------------
	// React function
	//-----------------
	//-----------------

	public void onRun() {
		init();
		while (true) {
			setTurnRadarRight(360);
			int x = getRandom(10, 1000);
			int y = getRandom(10, 1000);
			goTo(x, y);
			execute();
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		if (isTeammate(e.getName())) {
			setTurnRadarRight(360);
			return;
		}
		setTurnRadarRight(2.0 * Utils.normalRelativeAngleDegrees(getHeading() + e.getBearing() - getRadarHeading()));
		Target enemy = constructEnemyOnScanEvent(e);
		sendMessage(enemy);
		fire(enemy);
	}

	private void fire(Target enemy) {
		double dx = enemy.getX() - this.getX();
		double dy = enemy.getY() - this.getY();

		double theta = Math.toDegrees(Math.atan2(dx, dy));
		setTurnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));

		double distance = enemy.getDistance();

		setFire(1);
	}

	private void sendMessage(Target enemy) {
		try {
			broadcastMessage(enemy);
		} catch (IOException ex) {
			out.println("Unable to send order: ");
			ex.printStackTrace(out);
		}
	}
	
	public void onHitRobot(HitRobotEvent event) {
		if (event.isMyFault()) {
			setBack(100);
		}
	}
	
	public void onHitByBullet(HitByBulletEvent event) {
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
	
	//-----------------
	//-----------------
	// Helper function
	//-----------------
	//-----------------
	
	private void init() {
		setColor();
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
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
		String name = e.getName();

		Target enemy = new Target(enemyX, enemyY, distance, bearing, heading, priority, energy, name);
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
		double distance = Math.sqrt(dx * dx + dy * dy);
		
		setAhead(Math.min(distance, 300));
	}

	private double distanceTo(double x, double y) {
		double dx = x - this.getX();
		double dy = x - this.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}
}
