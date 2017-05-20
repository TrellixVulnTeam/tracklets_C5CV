package com.skrajcovic;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

public class FileReader {
    public static void processFile(FITSBatch batch, String fileLocation) {
        boolean skipFirstLine = true;
        HashMap<double[], FITSObject> data = batch.getDataStructure();

        File file = new File(fileLocation);
        try {
            BufferedReader bf = new BufferedReader(new java.io.FileReader(file));
            String text;
            Pattern pattern = Pattern.compile("\t");

            boolean insertFirst = true, insertSecond = false;

            while ((text = bf.readLine()) != null) {
                if (!skipFirstLine) {

                    String[] splitLine = pattern.split(text.replace(',', '.'), 0);

                    if (splitLine.length != 0) {
                        boolean real = Boolean.parseBoolean(splitLine[1]);
                        double x = Double.valueOf(splitLine[3]);
                        double y = Double.valueOf(splitLine[4]);
                        double intensity = Double.valueOf(splitLine[5]);

                        FITSObject obj = new FITSObject(splitLine[0], real, splitLine[2], x, y, intensity);

                        if (insertFirst) {
                            batch.firstSetInsert(obj);
                        } else if (insertSecond) {
                            batch.secondSetInsert(obj);
                        }
                        batch.mainDataInsert(new double[] {x, y}, obj);

//                        batch.regression.addData(x, y);
                    } else {
                        if (insertFirst) {
                            insertFirst = false;
                            insertSecond = true;
                        } else if (insertSecond) {
                            insertSecond = false;
                        }

                    }
                } else {
                    skipFirstLine = false;
                }
            }

            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
