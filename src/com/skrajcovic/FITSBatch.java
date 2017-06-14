package com.skrajcovic;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.*;

public class FITSBatch {
    private List<FITSObject> data;
    private Set<FITSObject> fSet;
    private Set<FITSObject> sSet;
    private Map<FITSObject[], SimpleRegression> regressions;

    public static final boolean DEBUG = false;

    public FITSBatch() {
        regressions = new HashMap<>();
        data = new ArrayList<>();
        fSet = new HashSet<>();
        sSet = new HashSet<>();
    }

    public void doTheThing() {

        findRegressions();
        for (int i = 1; i < 101; i++) {
            double[] tmp = fitPointsToRegressions(i);
            if (tmp != null) {
                System.out.format("Threshold: %d\nNumber of all points under threshold: %d\nReal points: %d\nSuccess rate: %f",
                        i, (int)tmp[0], (int)tmp[1], tmp[2]); System.out.println("%");
                System.out.println("---------------------------------");
            }
        }
    }

    void findRegressions() {
        for (FITSObject obj1 : fSet) {
            for (FITSObject obj2 : sSet) {
                SimpleRegression sr = new SimpleRegression();
                sr.addData(obj1.getX(), obj1.getY());
                sr.addData(obj2.getX(), obj2.getY());
                regressions.put(new FITSObject[]{obj1, obj2}, sr);
            }
        }
    }

    double[] fitPointsToRegressions(double threshold) {
//        double threshold = 70;
        for (Map.Entry<FITSObject[], SimpleRegression> regression : regressions.entrySet()) {
            double averageCombinedSpeed = 0;
            Set<FITSObject> result = new HashSet<>();
            int real = 0;
            for (FITSObject fitsObject : data) {

                if (isObjectUnderThreshold(fitsObject, regression.getValue(), threshold)) {
                    if (fitsObject.isReal()) {
                        real++;
                    }
                    result.add(fitsObject);
                }
            }
            if (regression.getKey()[0].isReal() && regression.getKey()[1].isReal()) {
                return new double[]{result.size(), real, (real / (double) result.size())*100};
            }
        }
        return null;
    }

    private boolean isObjectUnderThreshold(FITSObject fitsObject, SimpleRegression regression, double threshold) {
        double x = fitsObject.getX();
        double y = fitsObject.getY();
        double m = regression.getSlope();
        double c = regression.getIntercept();
        double b = (y > 0) ? 1 : -1;

        double distance = (-m * x + b * y - c) / Math.sqrt(Math.pow(m, 2) + Math.pow(b, 2));

        return Math.abs(distance) <= threshold;
    }

    public void mainDataInsert(FITSObject object) {
        data.add(object);
    }

    public void firstSetInsert(FITSObject fitsObject) {
        fSet.add(fitsObject);
    }

    public void secondSetInsert(FITSObject fitsObject) {
        sSet.add(fitsObject);
    }

    public List<FITSObject> getDataStructure() {
        return data;
    }

    public String toString() {
//        for (double[] d : data.keySet()) {
//            System.out.println(String.valueOf(d[0]).replace(".", ",") + " " + String.valueOf(d[1]).replace(".", ","));
//        }
//        System.out.println();
        return this.data.toString();
    }
}
