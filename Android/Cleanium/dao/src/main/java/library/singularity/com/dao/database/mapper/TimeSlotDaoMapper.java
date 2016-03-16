package library.singularity.com.dao.database.mapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;
import java.util.HashMap;

import library.singularity.com.dao.database.DatabaseMetaData;
import library.singularity.com.data.model.TimeSlot;

public class TimeSlotDaoMapper {

    public static HashMap<String, Integer> NAME_INDEX_MAP = new HashMap<>();

    public static void mapCursor(Cursor cursor) {
        int n = cursor.getColumnCount();
        for (int i = 0; i < n; i++) {
            NAME_INDEX_MAP.put(cursor.getColumnName(i), i);
        }
    }

    public static TimeSlot convertCursorToTimeSlot(Cursor cursor) {
        TimeSlot timeSlot = new TimeSlot();

        Integer columnIndex = getColumnIndex(DatabaseMetaData.TimeSlotTableMetaData.ID);

        if (columnIndex != null) {
            timeSlot.setId(cursor.getColumnName(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.TimeSlotTableMetaData.FROM_DATE);

        if (columnIndex != null) {
            timeSlot.setFromDate(new Date(cursor.getLong(columnIndex)));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.TimeSlotTableMetaData.TO_DATE);
        if (columnIndex != null) {
            timeSlot.setToDate(new Date(cursor.getLong(columnIndex)));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.TimeSlotTableMetaData.POST_CODE);
        if (columnIndex != null) {
            timeSlot.setPostCode(cursor.getInt(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.TimeSlotTableMetaData.ORDER_ID);
        if (columnIndex != null) {
            timeSlot.setOrderId(cursor.getString(columnIndex));
        }

        return timeSlot;
    }

    public static Integer getColumnIndex(String columnName) {
        return NAME_INDEX_MAP.get(columnName);
    }

    public static ContentValues getTimeSlotContentValues(TimeSlot timeSlot) {
        ContentValues values = new ContentValues();

        values.put(DatabaseMetaData.TimeSlotTableMetaData.ID,timeSlot.getId());
        values.put(DatabaseMetaData.TimeSlotTableMetaData.FROM_DATE, timeSlot.getFromDate().getTime());
        values.put(DatabaseMetaData.TimeSlotTableMetaData.TO_DATE, timeSlot.getToDate().getTime());
        values.put(DatabaseMetaData.TimeSlotTableMetaData.POST_CODE, timeSlot.getPostCode());
        values.put(DatabaseMetaData.TimeSlotTableMetaData.ORDER_ID, timeSlot.getOrderId());

        return values;
    }
}
