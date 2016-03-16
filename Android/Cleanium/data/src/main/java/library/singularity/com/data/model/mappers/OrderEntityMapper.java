package library.singularity.com.data.model.mappers;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.parse.ParseObject;
import com.parse.ParseUser;

import library.singularity.com.data.model.Bill;
import library.singularity.com.data.model.Driver;
import library.singularity.com.data.model.Order;
import library.singularity.com.data.model.OrderStatus;
import library.singularity.com.data.utils.Constants;

public class OrderEntityMapper {

    public static Order convertToOrder(ParseObject parseOrder, OrderStatus[] statuses) {
        if(parseOrder == null) return null;

        Order order = new Order();
        order.setId(parseOrder.getObjectId());
        order.setPickUpSchedule(TimeSlotEntityMapper.convertToTimeSlot(parseOrder.getParseObject(Constants.PARSE_ORDER_PICKUP_SCHEDULE)));
        order.setDeliverySchedule(TimeSlotEntityMapper.convertToTimeSlot(parseOrder.getParseObject(Constants.PARSE_ORDER_DELIVERY_SCHEDULE)));
        order.setStatus(parseOrder.getString(Constants.PARSE_ORDER_STATUS));
        order.setRating((float) parseOrder.getDouble(Constants.PARSE_ORDER_RATING));
        order.setDiscountCode(parseOrder.getString(Constants.PARSE_ORDER_DISCOUNT_CODE));
        order.setPickUpAddress(AddressEntityMapper.convertToAddress(parseOrder.getParseObject(Constants.PARSE_ORDER_PICK_UP_ADDRESS)));
        order.setDeliveryAddress(AddressEntityMapper.convertToAddress(parseOrder.getParseObject(Constants.PARSE_ORDER_DELIVERY_ADDRESS)));
        order.setWashAndDry(parseOrder.getBoolean(Constants.PARSE_ORDER_WASH_AND_DRY));
        if (parseOrder.getJSONObject(Constants.PARSE_ORDER_PAYMENT_RECEIPT) != null) {
            Gson gson = new Gson();
            Bill bill = gson.fromJson(String.valueOf(parseOrder.getJSONObject(Constants.PARSE_ORDER_PAYMENT_RECEIPT)), Bill.class);
            order.setBill(bill);
        }

        order.setNotes(parseOrder.getString(Constants.PARSE_ORDER_NOTES));
        if (statuses == null) return order;

        for (OrderStatus status : statuses) {
            if (!TextUtils.isEmpty(status.getDriverFrom())) {
                ParseObject driver = parseOrder.getParseObject(status.getDriverFrom());
                if (driver != null && driver.getParseObject(Constants.PARSE_DRIVER_USER_KEY) != null) {
                    ParseObject parseDriver = driver.getParseObject(Constants.PARSE_DRIVER_USER_KEY);
                    Driver orderDriver = new Driver();
                    orderDriver.setName(parseDriver.getString(Constants.PARSE_USER_FIRST_NAME) + " " + parseDriver.getString(Constants.PARSE_USER_LAST_NAME));
                    if (parseDriver.getParseFile(Constants.PARSE_USER_IMAGE) != null) {
                        orderDriver.setImageUrl(parseDriver.getParseFile(Constants.PARSE_USER_IMAGE).getUrl());
                    }

                    orderDriver.setStatusAssigned(status.getName());
                    order.addDriver(orderDriver);
                }
            }
        }

        return order;
    }

    public  static ParseObject convertToParseObject(Order order){

        ParseObject orderParseObject = new ParseObject(Constants.PARSE_ORDER_TABLE_NAME);

        if(order != null){
            orderParseObject.setObjectId(order.getId());
        }

        orderParseObject.put(Constants.PARSE_ORDER_PICKUP_SCHEDULE,order.getPickUpSchedule());
        orderParseObject.put(Constants.PARSE_ORDER_DELIVERY_SCHEDULE,order.getDeliverySchedule());
        orderParseObject.put(Constants.PARSE_ORDER_STATUS,order.getStatus());
        orderParseObject.put(Constants.PARSE_ORDER_RATING,order.getRating());
        orderParseObject.put(Constants.PARSE_ORDER_DISCOUNT_CODE,order.getDiscountCode());
        orderParseObject.put(Constants.PARSE_ORDER_PICK_UP_ADDRESS,order.getPickUpAddress());
        orderParseObject.put(Constants.PARSE_ORDER_DELIVERY_ADDRESS,order.getDeliveryAddress());
        orderParseObject.put(Constants.PARSE_ORDER_WASH_AND_DRY,order.isWashAndDry());
        orderParseObject.put(Constants.PARSE_ORDER_NOTES, order.getNotes());
        return orderParseObject;
    }
}
