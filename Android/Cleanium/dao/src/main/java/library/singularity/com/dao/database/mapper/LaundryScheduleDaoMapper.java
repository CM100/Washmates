package library.singularity.com.dao.database.mapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;
import java.util.HashMap;

import library.singularity.com.dao.database.DatabaseMetaData;
import library.singularity.com.data.model.LaundrySchedule;

public class LaundryScheduleDaoMapper {

    public static HashMap<String, Integer> NAME_INDEX_MAP = new HashMap<>();

    public static void mapCursor(Cursor cursor) {
        int n = cursor.getColumnCount();
        for (int i = 0; i < n; i++) {
            NAME_INDEX_MAP.put(cursor.getColumnName(i), i);
        }
    }

    public static LaundrySchedule convertCursorToLaundrySchedule(Cursor cursor) {
        LaundrySchedule laundrySchedule = new LaundrySchedule();

        Integer columnIndex = getColumnIndex(DatabaseMetaData.LaundryScheduleTableMetaData.MIN_PICKUP_DATE);
        if (columnIndex != null) {
            laundrySchedule.setMinPickUpDate(new Date(cursor.getLong(columnIndex)));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.LaundryScheduleTableMetaData.MAX_DELIVERY_DATE);
        if (columnIndex != null) {
            laundrySchedule.setMaxDeliveryDate(new Date(cursor.getLong(columnIndex)));
        }

        return laundrySchedule;
    }

    public static Integer getColumnIndex(String columnName) {
        return NAME_INDEX_MAP.get(columnName);
    }

    public static ContentValues getLaundryContentValues(LaundrySchedule laundrySchedule) {
        ContentValues values = new ContentValues();

        values.put(DatabaseMetaData.LaundryScheduleTableMetaData.MIN_PICKUP_DATE, laundrySchedule.getMinPickUpDate().getTime());
        values.put(DatabaseMetaData.LaundryScheduleTableMetaData.MAX_DELIVERY_DATE, laundrySchedule.getMaxDeliveryDate().getTime());

        return values;
    }
}
