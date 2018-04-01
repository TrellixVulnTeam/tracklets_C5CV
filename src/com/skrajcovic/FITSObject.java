package com.skrajcovic;

import com.skrajcovic.datastructures.Declination;
import com.skrajcovic.datastructures.Rectascension;
import com.skrajcovic.datastructures.Type;
import eap.fits.FitsCard;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.time.LocalDateTime;

public class FITSObject implements Comparable<FITSObject> {
    private String fileName;
    private Type type;

    private Rectascension rectascension;
    private Declination declination;

    private double magnitude;

    private double x;
    private double y;

    private double mjd;

    private boolean real;

    private double xComponent;
    private double yComponent;

    public FITSObject() {}

    public FITSObject(String fileName, String type, Rectascension rectascension,
                      Declination declination, double magnitude, double x, double y) {
        setFileName(fileName);
        setType(type);
        setRectascension(rectascension);
        setDeclination(declination);
        setMagnitude(magnitude);
        setX(x);
        setY(y);

        if (FITSBatch.RADEC) {
            xComponent = rectascension.getDegValue() * 1000;
            yComponent = declination.getDegValue() * 1000;
        } else {
            xComponent = x;
            yComponent = y;
        }

//        System.out.println("xcomponent " + xComponent + " x: " + getX());
//        System.out.println("ycomponent " + yComponent + " y: " + getY());
//        System.out.println("-------------------------");
    }

    public boolean isWithinLineThreshold(SimpleRegression regression, double threshold) {
        double m = regression.getSlope();
        double b = regression.getIntercept();

        double coeffy = (getyComponent() > 0) ? 1 : -1;

        double distance = Math.abs(-m * getxComponent() + coeffy * getyComponent() - b) / (Math.sqrt(Math.pow(-m, 2) + Math.pow(coeffy, 2)));

        return distance <= threshold;
    }

    public double calculateDeltaTime(FITSObject otherObject) {
        return Math.abs(this.getMjd() - otherObject.getMjd());
    }

    public double calculateSpeed(FITSObject otherObject) {
        return Math.sqrt(Math.pow(this.getxComponent() - otherObject.getxComponent(), 2) + Math.pow(this.getyComponent() - otherObject.getyComponent(), 2))
                / calculateDeltaTime(otherObject);
    }

    public double getHeading(FITSObject otherObject) {
        double theta = Math.toDegrees(Math.atan2(otherObject.getyComponent() - getyComponent(), otherObject.getxComponent() - getxComponent()));
        System.out.println(Math.abs(theta));
        return Math.abs(theta);
    }

    public boolean isWithinAngleThreshold(FITSObject otherObject, double angle, double threshold) {
        double heading = getHeading(otherObject);
        return angle <= heading + threshold && angle >= heading - threshold;
    }

    @Override
    public int compareTo(FITSObject o) {
        return Double.valueOf(this.getMjd()).compareTo(o.getMjd());
    }




    public String toString() {
        return "[" + getFileName() + ", x: " + getX() + ", y: " + getY() + ", ra: " + getRectascension().getDegValue()*1000
                + ", dec:: " + getDeclination().getDegValue() * 1000 + ", " + getType() +"]\n";
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(String type) {
        switch (type) {
            case "R":
                this.type = Type.R;
                break;
            case "S":
                this.type = Type.S;
                break;
            case "H":
                this.type = Type.H;
                break;
            case "?":
                this.type = Type.UNKNOWN;
                break;
            default:
                break;
        }
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

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public Rectascension getRectascension() {
        return rectascension;
    }

    public void setRectascension(Rectascension rectascension) {
        this.rectascension = rectascension;
    }

    public Declination getDeclination() {
        return declination;
    }

    public void setDeclination(Declination declination) {
        this.declination = declination;
    }

    public double getxComponent() {
        return this.xComponent;
    }

    public double getyComponent() {
        return this.yComponent;
    }

    public void setTime(FitsCard dateObs, FitsCard expTime) {
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        try {
            LocalDateTime ldt = LocalDateTime.parse(dateObs.stringValue());
            Double exposureTimeSeconds = expTime.doubleValue();

            int month = ldt.getMonthValue();
            int year = ldt.getYear();
            int day = ldt.getDayOfMonth();
            int hour = ldt.getHour();
            int min = ldt.getMinute();
            double sec = ldt.getSecond();

            sec += exposureTimeSeconds / 2;

            setMjd(produceMjd(month, year, day, hour, min, sec));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("Duplicates")
    private double produceMjd(int month, int year, int day, int hour, int min, double sec) {
        long MjdMidnight;
        double FracOfDay;
        int b;

        if (month <= 2) {
            month += 12;
            --year;
        }

        if ((10000L * year + 100L * month + day) <= 15821004L ) {
            b = -2 + ((year + 4716) / 4) - 1179;
        } else {
            b = (year / 400) - (year / 100) + (year / 4);
        }

        MjdMidnight = 365L * year - 679004L + b + (int)(30.6001 * (month + 1)) + day;
        FracOfDay = (hour + min / 60.0 + sec / 3600.0) / 24.0;

        return MjdMidnight + FracOfDay;
    }
}