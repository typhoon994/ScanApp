package com.example.mihael.scanapp;

import java.util.HashMap;

/**
 * Created by BigaMasta on 2/11/16.
 */
public class DBImmitator {
    private HashMap<String, String> translator = new HashMap<>();
    private static DBImmitator instance;

    private DBImmitator() {
        initializeData();
    }

    public static DBImmitator getInstance() {
        if (instance == null) {
            instance = new DBImmitator();
        }
        return instance;
    }

    public String get(String barcodeData) {
        return translator.get(barcodeData);
    }

    public void put(String barcodeData, String qrData) {
        translator.put(barcodeData, qrData);
    }

    public void initializeData() {
        translator.put("036000291452", "ibalgin|400|2x400mg|zentiva|tbl_flm");
        translator.put("4008617149637", "neoangin|100|24x200mg|divapharma|lozenge");
        translator.put("8594739205734", "paralen|500|12x200mg|zentiva|tbl_flm");
        translator.put("4030142018345", "wobenzym|x|800x250mg|mucos_pharma|tbl_ent");
        translator.put("064642020700", "B12|x|100x250mg|jamieson|tbl");
    }

    public HashMap<String, String> getTranslator() {
        return translator;
    }
}