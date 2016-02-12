package com.example.mihael.scanapp;

import android.provider.BaseColumns;

/**
 * Created by Mihael on 11-Feb-16.
 */
public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "Medicals";
        public static final String COLUMN_NAME_ENTRY_ID = "medicalId";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        ;
    }
}