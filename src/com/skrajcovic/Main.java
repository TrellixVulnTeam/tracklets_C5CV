package com.skrajcovic;


public class Main {

    public static void main(String[] args) {
    	FITSBatch batch = new FITSBatch();
	    FileReader.processFile(batch, "Data_20120305_2.txt");
	    batch.doTheThing();
    }
}
