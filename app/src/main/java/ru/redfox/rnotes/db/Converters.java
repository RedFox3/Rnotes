package ru.redfox.rnotes.db;

import android.net.Uri;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static String fromUri(Uri value) {
        return value == null ? null : value.toString();
    }

    @TypeConverter
    public static Uri toUri(String value) {
        return value == null ? null : Uri.parse(value);
    }
}
