package library.singularity.com.dao.database.mapper;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import library.singularity.com.dao.database.DatabaseMetaData;
import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.Bill;
import library.singularity.com.data.model.Driver;
import library.singularity.com.data.model.Order;
import library.singularity.com.data.model.TimeSlot;

public class OrderDaoMapper {

    public static HashMap<String,Integer> NAME_INDEX_MAP = new HashMap<>();

    public static void mapCursor(Cursor cursor){
        int n = cursor.getColumnCount();
        for (int i = 0; i < n; i++){
            NAME_INDEX_MAP.put(cursor.getColumnName(i),i);
        }
    }


    public static Order convertCursorToOrder(Cursor cursor){
        Order order = new Order();

        Integer columnIndex = getColumnIndex(DatabaseMetaData.OrderTableMetaData.ID);
        if(columnIndex != null){
            order.setId(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.OrderTableMetaData.PICK_UP_SCHEDULE);
        if(columnIndex != null){
            TimeSlot pickupTimeSlot = new TimeSlot();
            pickupTimeSlot.setId(cursor.getString(columnIndex));
            order.setPickUpSchedule(pickupTimeSlot);
        }

        columnIndex = getColumnIndex(DatabaseMetaData.OrderTableMetaData.DELIVERY_SCHEDULE);
        if(columnIndex != null){
            TimeSlot deliveryTimeSlot = new TimeSlot();
            deliveryTimeSlot.setId(cursor.getString(columnIndex));
            order.setDeliverySchedule(deliveryTimeSlot);
        }

        columnIndex = getColumnIndex(DatabaseMetaData.OrderTableMetaData.STATUS);
        if(columnIndex != null){
            order.setStatus(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.OrderTableMetaData.RATING);
        if(columnIndex != null){
            order.setRating((float) cursor.getDouble(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.OrderTableMetaData.DISCOUNT_CODE);
        if(columnIndex != null){
            order.setDiscountCode(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.OrderTableMetaData.PICK_UP_ADDRESS);
        if(columnIndex != null){
            Address pickUpAddress = new Address();
            pickUpAddress.setId(cursor.getString(columnIndex));
            order.setPickUpAddress(pickUpAddress);
        }

        columnIndex = getColumnIndex(DatabaseMetaData.OrderTableMetaData.DELIVERY_ADDRESS);
        if(columnIndex != null){
            Address deliveryAddress = new Address();
            deliveryAddress.setId(cursor.getString(columnIndex));
            order.setDeliveryAddress(deliveryAddress);
        }

        columnIndex = getColumnIndex(DatabaseMetaData.OrderTableMetaData.WASH_AND_DRY);
        if(columnIndex != null) {
            order.setWashAndDry(cursor.getInt(columnIndex) == 1);
        }

        columnIndex = getColumnIndex(DatabaseMetaData.OrderTableMetaData.PRICE);
        if(columnIndex != null && cursor.getString(columnIndex) != null) {
            Gson gson = new Gson();
            order.setBill(gson.fromJson(cursor.getString(columnIndex), Bill.class));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.OrderTableMetaData.NOTES);
        if(columnIndex != null){
            order.setNotes(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.OrderTableMetaData.DRIVERS);
        if(columnIndex != null && !cursor.isNull(columnIndex)){
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Driver>>() {}.getType();
            order.setDriverList((List<Driver>) gson.fromJson(cursor.getString(columnIndex), listType));
        }

        return order;
    }

    public static Integer getColumnIndex(String columnName){
        return NAME_INDEX_MAP.get(columnName);
    }

    public static ContentValues getOrderFromItems(Order order){

        ContentValues values = new ContentValues();

        values.put(DatabaseMetaData.OrderTableMetaData.ID,order.getId());
        values.put(DatabaseMetaData.OrderTableMetaData.PICK_UP_SCHEDULE,order.getPickUpSchedule().getId());
        values.put(DatabaseMetaData.OrderTableMetaData.DELIVERY_SCHEDULE,order.getDeliverySchedule().getId());
        values.put(DatabaseMetaData.OrderTableMetaData.STATUS,order.getStatus());
        values.put(DatabaseMetaData.OrderTableMetaData.RATING,order.getRating());
        values.put(DatabaseMetaData.OrderTableMetaData.DISCOUNT_CODE,order.getDiscountCode());
        values.put(DatabaseMetaData.OrderTableMetaData.PICK_UP_ADDRESS,order.getPickUpAddress().getId());
        values.put(DatabaseMetaData.OrderTableMetaData.DELIVERY_ADDRESS,order.getDeliveryAddress().getId());
        values.put(DatabaseMetaData.OrderTableMetaData.WASH_AND_DRY,order.isWashAndDry() ? 1 : 0);

        if (order.getBill() != null) {
            Gson gson = new Gson();
            values.put(DatabaseMetaData.OrderTableMetaData.PRICE, gson.toJson(order.getBill(), Bill.class));
        }

        values.put(DatabaseMetaData.OrderTableMetaData.NOTES, order.getNotes());
        if (order.getDriverList() != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Driver>>() {}.getType();
            values.put(DatabaseMetaData.OrderTableMetaData.DRIVERS, gson.toJson(order.getDriverList(), listType));
        }

        return values;
    }


}
