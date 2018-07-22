/**
 * Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package tma.o2.misc;

/**
 * Point - a serializable point class
 */
public class Target implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private double x = 0.0;
    private double y = 0.0;
    private double distance = 0.0;
    private double bearing = 0.0;
    private double bearingRadians = 0.0;
    private double heading = 0.0;
    private double headingRadians = 0.0;
    private int priority = 0;
    private double energy = 0.0;
    private String name = "";
    private double velocity = 0;

    public Target(double x, double y, double distance, double bearing, double heading, int priority, double energy, String name) {
        this.x = x;
        this.y = y;
        this.distance = distance;
        this.bearing = bearing;
        this.heading = heading;
        this.priority = priority;
        this.energy = energy;
        this.name = name;
    }
    
    public Target(Target another) {
        this.x = another.x;
        this.y = another.y;
        this.distance = another.distance;
        this.bearing = another.bearing;
        this.heading = another.heading;
        this.priority = another.priority;
        this.energy = another.energy;
    }

	public Target(double x, double y, double distance, double bearing, double bearingRadians, double heading,
			double headingRadians, int priority, double energy, String name, double velocity) {
		this.x = x;
		this.y = y;
		this.distance = distance;
		this.bearing = bearing;
		this.bearingRadians = bearingRadians;
		this.heading = heading;
		this.headingRadians = headingRadians;
		this.priority = priority;
		this.energy = energy;
		this.name = name;
		this.velocity = velocity;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBearing() {
        return bearing;
    }

    public double getHeading() {
        return heading;
    }

    public int getPriority() {
        return priority;
    }

    public double getEnergy() {
        return energy;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDistance() {
        return distance;
    }
    
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }
}
