package com.skrajcovic;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class FITSObject implements Comparable<FITSObject> {
    private String name;
    private boolean real;
    private double x;
    private double y;
    private double mjd;
    private double intensity;

    public FITSObject() {}

    public FITSObject(String name, boolean real, double mjd, double x, double y, double intensity) {
        setName(name);
        setReal(real);
        setMjd(mjd);
        setX(x);
        setY(y);
        setIntensity(intensity);
    }

    public FITSObject(String[] data) {
        setName(data[0]);
        setReal(Boolean.parseBoolean(data[1]));
        setMjd(Double.valueOf(data[2]));
        setX(Double.valueOf(data[3]));
        setY(Double.valueOf(data[4]));
        setIntensity(Double.valueOf(data[5]));
    }

    public String toString() {
        return "[" + getName() + ", " + getX() + ", " + getY() + ", " + isReal() +"]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReal() {
        return real;
    }

    public void setReal(boolean real) {
        this.real = real;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getMjd() {
        return mjd;
    }

    public void setMjd(double mjd) {
        this.mjd = mjd;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public boolean isWithinLineThreshold(SimpleRegression regression, double threshold) {
        double x = this.getX();
        double y = this.getY();
        double m = regression.getSlope();
        double c = regression.getIntercept();
        double b = (y > 0) ? 1 : -1;

        double distance = (-m * x + b * y - c) / Math.sqrt(Math.pow(m, 2) + Math.pow(b, 2));

//        if (this.isReal()) {
//            System.out.println(this.getName() + " - " + distance);
//        }

        return Math.abs(distance) <= threshold;
    }

    public double calculateDeltaTime(FITSObject otherObject) {
        return Math.abs(this.getMjd() - otherObject.getMjd());
    }

    public double calculateSpeed(FITSObject otherObject) {
        return Math.sqrt(Math.pow(this.getX() - otherObject.getX(), 2) + Math.pow(this.getY() - otherObject.getY(), 2))
                / calculateDeltaTime(otherObject);
    }

    public double getHeading(FITSObject otherObject) {
//        return Math.toDegrees(Math.atan(getSlope(otherObject)));
        double theta = Math.toDegrees(Math.atan2(otherObject.getX() - getX(), otherObject.getY() - getY()));
        return Math.abs(theta);
    }


    private double getSlope(FITSObject otherObject) {
        double x2 = otherObject.getX();
        double y2 = otherObject.getY();

        return (y2 - this.getY()) / (x2 - this.getX());
    }

    public boolean isWithinAngleThreshold(FITSObject otherObject, double angle, double threshold) {
        double heading = getHeading(otherObject);
        if (otherObject.isReal()) {
            System.out.println(otherObject.getName() + " - " + angle +" <= " + heading + " + " + threshold +
            " && " + angle + " >= " + heading + " - " + threshold);
        }
        return angle <= heading + threshold && angle >= heading - threshold;
    }

    @Override
    public int compareTo(FITSObject o) {
        return Double.valueOf(this.getMjd()).compareTo(o.getMjd());
    }
}
