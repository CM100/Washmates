package library.singularity.com.dao.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import library.singularity.com.dao.database.mapper.AddressDaoMapper;
import library.singularity.com.dao.database.mapper.DiscountCodeDaoMapper;
import library.singularity.com.dao.database.mapper.LaundryScheduleDaoMapper;
import library.singularity.com.dao.database.mapper.OrderDaoMapper;
import library.singularity.com.dao.database.mapper.TimeSlotDaoMapper;
import library.singularity.com.dao.database.mapper.UserDaoMapper;
import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.DiscountCode;
import library.singularity.com.data.model.LaundrySchedule;
import library.singularity.com.data.model.Order;
import library.singularity.com.data.model.TimeSlot;
import library.singularity.com.data.model.User;

public class DaoHandler {
    private static DaoHandler daoHandler;
    private SQLiteDatabase database;
    private DatabaseHandler dbHandler;

    public static final String[] USER_TABLE_COLUMNS = {
            DatabaseMetaData.UserTableMetaData.ID,
            DatabaseMetaData.UserTableMetaData.USERNAME,
            DatabaseMetaData.UserTableMetaData.PASSWORD,
            DatabaseMetaData.UserTableMetaData.EMAIL_VERIFIED,
            DatabaseMetaData.UserTableMetaData.EMAIL,
            DatabaseMetaData.UserTableMetaData.IMAGE_PATH,
            DatabaseMetaData.UserTableMetaData.IS_STRIPE_CLIENT,
            DatabaseMetaData.UserTableMetaData.FIRST_NAME,
            DatabaseMetaData.UserTableMetaData.LAST_NAME,
            DatabaseMetaData.UserTableMetaData.PHONE_NUMBER
    };

    public static final String[] ADDRESS_TABLE_COLUMNS = {
            DatabaseMetaData.AddressTableMetaData.ID,
            DatabaseMetaData.AddressTableMetaData.STREET,
            DatabaseMetaData.AddressTableMetaData.POSTAL_CODE,
            DatabaseMetaData.AddressTableMetaData.SUBURB,
            DatabaseMetaData.AddressTableMetaData.NUMBER,
            DatabaseMetaData.AddressTableMetaData.STREET_TWO,
            DatabaseMetaData.AddressTableMetaData.NOTES,
            DatabaseMetaData.AddressTableMetaData.LOCATION,
            DatabaseMetaData.AddressTableMetaData.STATE,
            DatabaseMetaData.AddressTableMetaData.CITY
    };

    public static final String[] DISCOUNT_CODE_TABLE_COLUMNS ={
            DatabaseMetaData.DiscountCodeTableMetaData.CODE
    };

    public static final String[] ORDER_TABLE_COLUMNS = {
            DatabaseMetaData.OrderTableMetaData.ID,
            DatabaseMetaData.OrderTableMetaData.PICK_UP_SCHEDULE,
            DatabaseMetaData.OrderTableMetaData.DELIVERY_SCHEDULE,
            DatabaseMetaData.OrderTableMetaData.STATUS,
            DatabaseMetaData.OrderTableMetaData.RATING,
            DatabaseMetaData.OrderTableMetaData.DISCOUNT_CODE,
            DatabaseMetaData.OrderTableMetaData.PICK_UP_ADDRESS,
            DatabaseMetaData.OrderTableMetaData.DELIVERY_ADDRESS,
            DatabaseMetaData.OrderTableMetaData.WASH_AND_DRY,
            DatabaseMetaData.OrderTableMetaData.PRICE,
            DatabaseMetaData.OrderTableMetaData.NOTES,
            DatabaseMetaData.OrderTableMetaData.DRIVERS

    };
    public static final String[] TIME_SLOT_TABLE_COLUMNS = {
            DatabaseMetaData.TimeSlotTableMetaData.ID,
            DatabaseMetaData.TimeSlotTableMetaData.FROM_DATE,
            DatabaseMetaData.TimeSlotTableMetaData.TO_DATE,
            DatabaseMetaData.TimeSlotTableMetaData.POST_CODE
    };

    public static final String[] LAUNDRY_SCHEDULE_TABLE_COLUMNS = {
            DatabaseMetaData.LaundryScheduleTableMetaData.MIN_PICKUP_DATE,
            DatabaseMetaData.LaundryScheduleTableMetaData.MAX_DELIVERY_DATE
    };

    public static DaoHandler getInstance(Context context) {
        if (daoHandler == null) {
            daoHandler = new DaoHandler(context.getApplicationContext());
        }

        return daoHandler;
    }

    private DaoHandler(Context context) {
        this.dbHandler = new DatabaseHandler(context);
    }

    public void open() throws SQLException {
        if (this.database == null || !this.database.isOpen()) {
            this.database = this.dbHandler.getWritableDatabase();
        }
    }

    public void close() {
        if (this.database != null && this.database.isOpen()) {
            this.dbHandler.close();
        }
    }

    public void addUser(User user) {
        ContentValues values = UserDaoMapper.getUsersContentValues(user);
        this.database.delete(DatabaseMetaData.UserTableMetaData.TABLE_NAME, DatabaseMetaData.UserTableMetaData.ID + "=?", new String[] {user.getId()});
        this.database.insert(DatabaseMetaData.UserTableMetaData.TABLE_NAME, null, values);
        if (user.getAddresses() == null) return;

        try {
            this.database.beginTransaction();
            for (Address address : user.getAddresses()) {
                addAddress(address);
            }

            this.database.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.database.endTransaction();
        }
    }

    public void addAddress(Address address) {
        ContentValues addressContentValue = AddressDaoMapper.getAddressFormItems(address);
        this.database.delete(DatabaseMetaData.AddressTableMetaData.TABLE_NAME, DatabaseMetaData.AddressTableMetaData.ID +"=?", new String[] {address.getId()});
        this.database.insert(DatabaseMetaData.AddressTableMetaData.TABLE_NAME, null, addressContentValue);
    }

    public void addAddresses(List<Address> addresses) {
        try {
            this.database.beginTransaction();
            for (Address address : addresses) {
                addAddress(address);
            }

            this.database.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.database.endTransaction();
        }
    }

    public User getCurrentUser() {
        Cursor cursor = database.query(DatabaseMetaData.UserTableMetaData.TABLE_NAME,
                USER_TABLE_COLUMNS, null,
                null, null, null, DatabaseMetaData.UserTableMetaData.DEFAULT_SORT_ORDER);

        User user = null;
        if (cursor != null) {
            cursor.moveToFirst();
            UserDaoMapper.mapCursor(cursor);
            user = UserDaoMapper.convertCursorToUser(cursor);
        }

        if (cursor != null) { cursor.close(); }
        if (user == null) return null;

        cursor = database.query(DatabaseMetaData.AddressTableMetaData.TABLE_NAME,
                ADDRESS_TABLE_COLUMNS, null, null, null, null, DatabaseMetaData.AddressTableMetaData.DEFAULT_SORT_ORDER);

        List<Address> addressList = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            AddressDaoMapper.mapCursor(cursor);
            while (!cursor.isAfterLast()) {
                Address address = AddressDaoMapper.convertCursorToAddress(cursor);
                addressList.add(address);
                cursor.moveToNext();
            }
        }

        if (cursor != null) { cursor.close(); }
        user.setAddresses(addressList);

        return user;
    }

    public void deleteUsers() {
        database.delete(DatabaseMetaData.UserTableMetaData.TABLE_NAME, null, null);
        deleteAddresses();
    }

    public void deleteAddresses() {
        database.delete(DatabaseMetaData.AddressTableMetaData.TABLE_NAME, null, null);
    }

    public void deleteAddress(Address address) {
        database.delete(DatabaseMetaData.AddressTableMetaData.TABLE_NAME, DatabaseMetaData.AddressTableMetaData.ID + "=?", new String[]{address.getId()});
    }

    // addDiscountCOde,getDC,deleteDC
    // addOrder,getOrder,deleteOrder,updateOrder,

    public void updateUserWithId(User user) {
        ContentValues values = UserDaoMapper.getUsersContentValues(user);
        database.update(DatabaseMetaData.UserTableMetaData.TABLE_NAME, values, DatabaseMetaData.UserTableMetaData.ID + "=?", new String[]{user.getId()});

        if (user.getAddresses() == null) return;

        try {
            this.database.beginTransaction();
            for (Address address : user.getAddresses()) {
                updateAddress(address);
            }

            this.database.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.database.endTransaction();
        }
    }

    public void updateAddress(Address address) {
        ContentValues addressContentValue = AddressDaoMapper.getAddressFormItems(address);
        this.database.update(DatabaseMetaData.AddressTableMetaData.TABLE_NAME,
                addressContentValue,
                DatabaseMetaData.AddressTableMetaData.ID + "=?",
                new String[]{address.getId()});
    }

    public void updateOrder(Order order){
        ContentValues orderContentValues = OrderDaoMapper.getOrderFromItems(order);
        database.update(DatabaseMetaData.OrderTableMetaData.TABLE_NAME,
                orderContentValues,
                DatabaseMetaData.OrderTableMetaData.ID + "=?",
                new String[]{order.getId()});
    }

    public void addDiscountCode(DiscountCode discountCode){
        ContentValues values = DiscountCodeDaoMapper.getDiscountCodeFromItems(discountCode);
        database.insert(DatabaseMetaData.DiscountCodeTableMetaData.TABLE_NAME, null, values);
    }


    public void addOrder(Order order){
        ContentValues orderContentValue = OrderDaoMapper.getOrderFromItems(order);
        this.database.delete(DatabaseMetaData.OrderTableMetaData.TABLE_NAME, DatabaseMetaData.OrderTableMetaData.ID + "=?", new String[] {order.getId()});
        this.database.insert(DatabaseMetaData.OrderTableMetaData.TABLE_NAME, null, orderContentValue);
    }

    public void addOrders(List<Order> orders){
        if(orders != null && orders.size() >= 1){
            try{
                this.database.beginTransaction();
                for(Order order : orders){
                    addTimeSlot(order.getPickUpSchedule());
                    addTimeSlot(order.getDeliverySchedule());
                    addAddress(order.getDeliveryAddress());
                    addAddress(order.getPickUpAddress());
                    addOrder(order);
                }

                this.database.setTransactionSuccessful();
            }
            catch (SQLException e){
                e.printStackTrace();
            }finally {
                this.database.endTransaction();
            }
        }
    }

    public void addLaundrySchedule(LaundrySchedule laundrySchedule) {
        ContentValues values = LaundryScheduleDaoMapper.getLaundryContentValues(laundrySchedule);
        this.database.insert(DatabaseMetaData.LaundryScheduleTableMetaData.TABLE_NAME, null, values);
        if (laundrySchedule.getFreeSlots() == null) return;

        try {
            this.database.beginTransaction();
            for (TimeSlot timeSlot : laundrySchedule.getFreeSlots()) {
                addTimeSlot(timeSlot);
            }

            this.database.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.database.endTransaction();
        }
    }

    public void addTimeSlot(TimeSlot timeSlot) {
        ContentValues values = TimeSlotDaoMapper.getTimeSlotContentValues(timeSlot);
        this.database.insert(DatabaseMetaData.TimeSlotTableMetaData.TABLE_NAME, null, values);
    }

    public List<DiscountCode> getAllDiscountCodes(){
        Cursor cursor = database.query(DatabaseMetaData.DiscountCodeTableMetaData.TABLE_NAME,
                DISCOUNT_CODE_TABLE_COLUMNS,
                null,
                null,
                null,
                null,
                DatabaseMetaData.DiscountCodeTableMetaData.DEFAULT_SORT_ORDER);
        List<DiscountCode> discountCodes = new ArrayList<DiscountCode>();
        DiscountCode discountCode = null;

        if(cursor != null){
            while (!cursor.isAfterLast()) {
                DiscountCodeDaoMapper.mapCursor(cursor);
                discountCode = DiscountCodeDaoMapper.convertCursorToDiscountCode(cursor);
                discountCodes.add(discountCode);
                cursor.moveToNext();
            }
        }

        if (cursor != null) { cursor.close(); }

        return discountCodes;
    }

    public List<Order> getAllOrders(){
        Cursor cursor = database.query(DatabaseMetaData.OrderTableMetaData.TABLE_NAME,
                ORDER_TABLE_COLUMNS,
                null,
                null,
                null,
                null,
                DatabaseMetaData.OrderTableMetaData.DEFAULT_SORT_ORDER);

        List<Order> orders = new ArrayList<>();

        if(cursor != null){

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                OrderDaoMapper.mapCursor(cursor);
                Order order = OrderDaoMapper.convertCursorToOrder(cursor);
                orders.add(order);
                cursor.moveToNext();
            }
        }
        if (cursor != null) { cursor.close(); }

        return orders;
    }

    public Order getCurrentOrder(List<String> historyStatuses) {
        String condition = null;
        List<String> conditionValues = null;
        if (historyStatuses != null) {
            for (String status : historyStatuses) {
                if (condition == null) {
                    condition = DatabaseMetaData.OrderTableMetaData.STATUS + "!=?";
                    conditionValues = new ArrayList<>();
                } else {
                    condition += " AND " + DatabaseMetaData.OrderTableMetaData.STATUS + "!=?";
                }

                conditionValues.add(status);
            }
        }

        Cursor cursor;
        if (conditionValues == null) {
            cursor = database.query(DatabaseMetaData.OrderTableMetaData.TABLE_NAME,
                    ORDER_TABLE_COLUMNS,
                    null,
                    null,
                    null,
                    null,
                    DatabaseMetaData.OrderTableMetaData.DEFAULT_SORT_ORDER);
        } else {
            String[] conditionArray= new String[conditionValues.size()];
            conditionArray = conditionValues.toArray(conditionArray);

            cursor = database.query(DatabaseMetaData.OrderTableMetaData.TABLE_NAME,
                    ORDER_TABLE_COLUMNS,
                    condition,
                    conditionArray,
                    null,
                    null,
                    DatabaseMetaData.OrderTableMetaData.DEFAULT_SORT_ORDER);
        }

        Order order = null;
        if(cursor != null){
            cursor.moveToFirst();
            if (cursor.isAfterLast()) return null;

            OrderDaoMapper.mapCursor(cursor);
            order = OrderDaoMapper.convertCursorToOrder(cursor);

            List<TimeSlot> timeSlots = getTimeSlotsForOrder(order.getId());
            if (timeSlots.size() > 0) {
                order.setPickUpSchedule(timeSlots.get(0));
            }

            if (timeSlots.size() > 1) {
                order.setDeliverySchedule(timeSlots.get(1));
            }
        }

        if (cursor != null) { cursor.close(); }

        return order;
    }

    public List<Order> getHistoryOrders(List<String> historyStatuses){
        String condition = null;
        List<String> conditionValues = null;
        if (historyStatuses != null) {
            for (String status : historyStatuses) {
                if (condition == null) {
                    condition = DatabaseMetaData.OrderTableMetaData.STATUS + "=?";
                    conditionValues = new ArrayList<>();
                } else {
                    condition += " OR " + DatabaseMetaData.OrderTableMetaData.STATUS + "=?";
                }

                conditionValues.add(status);
            }
        }

        Cursor cursor;
        if (conditionValues == null) {
            cursor = database.query(DatabaseMetaData.OrderTableMetaData.TABLE_NAME,
                    ORDER_TABLE_COLUMNS,
                    null,
                    null,
                    null,
                    null,
                    DatabaseMetaData.OrderTableMetaData.DEFAULT_SORT_ORDER);
        } else {
            String[] conditionArray= new String[conditionValues.size()];
            conditionArray = conditionValues.toArray(conditionArray);

            cursor = database.query(DatabaseMetaData.OrderTableMetaData.TABLE_NAME,
                    ORDER_TABLE_COLUMNS,
                    condition,
                    conditionArray,
                    null,
                    null,
                    DatabaseMetaData.OrderTableMetaData.DEFAULT_SORT_ORDER);
        }

        List<Order> orders = new ArrayList<>();

        if(cursor != null){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                OrderDaoMapper.mapCursor(cursor);
                Order order = OrderDaoMapper.convertCursorToOrder(cursor);

                List<TimeSlot> timeSlots = getTimeSlotsForOrder(order.getId());
                if (timeSlots.size() > 0) {
                    order.setPickUpSchedule(timeSlots.get(0));
                }

                if (timeSlots.size() > 1) {
                    order.setDeliverySchedule(timeSlots.get(1));
                }

                orders.add(order);
                cursor.moveToNext();
            }
        }
        if (cursor != null) { cursor.close(); }

        return orders;
    }

    public List<TimeSlot> getTimeSlotsForOrder(String orderId) {
        Cursor cursor = database.query(DatabaseMetaData.TimeSlotTableMetaData.TABLE_NAME,
                TIME_SLOT_TABLE_COLUMNS,
                DatabaseMetaData.TimeSlotTableMetaData.ORDER_ID + "=?",
                new String[] {orderId},
                null,
                null,
                DatabaseMetaData.TimeSlotTableMetaData.DEFAULT_SORT_ORDER);

        List<TimeSlot> timeSlots = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            TimeSlotDaoMapper.mapCursor(cursor);
            while (!cursor.isAfterLast()) {
                TimeSlot timeSlot = TimeSlotDaoMapper.convertCursorToTimeSlot(cursor);
                timeSlots.add(timeSlot);
                cursor.moveToNext();
            }
        }

        return timeSlots;
    }

    public LaundrySchedule getLaundrySchedule() {
        Cursor cursor = database.query(DatabaseMetaData.LaundryScheduleTableMetaData.TABLE_NAME,
                LAUNDRY_SCHEDULE_TABLE_COLUMNS, null,
                null, null, null, DatabaseMetaData.LaundryScheduleTableMetaData.DEFAULT_SORT_ORDER);

        LaundrySchedule laundrySchedule = null;
        if (cursor != null) {
            cursor.moveToFirst();
            LaundryScheduleDaoMapper.mapCursor(cursor);
            laundrySchedule = LaundryScheduleDaoMapper.convertCursorToLaundrySchedule(cursor);
        }

        if (cursor != null) { cursor.close(); }
        if (laundrySchedule == null) return null;

        cursor = database.query(DatabaseMetaData.TimeSlotTableMetaData.TABLE_NAME,
                TIME_SLOT_TABLE_COLUMNS, null, null, null, null, DatabaseMetaData.TimeSlotTableMetaData.DEFAULT_SORT_ORDER);

        List<TimeSlot> timeSlots = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            TimeSlotDaoMapper.mapCursor(cursor);
            while (!cursor.isAfterLast()) {
                TimeSlot timeSlot = TimeSlotDaoMapper.convertCursorToTimeSlot(cursor);
                timeSlots.add(timeSlot);
                cursor.moveToNext();
            }
        }

        if (cursor != null) { cursor.close(); }
        laundrySchedule.setFreeSlots(timeSlots);

        return laundrySchedule;
    }

    public void deleteLaundrySchedules() {
        database.delete(DatabaseMetaData.LaundryScheduleTableMetaData.TABLE_NAME, null, null);
        deleteTimeSlots();
    }

    public void deleteTimeSlots() {
        database.delete(DatabaseMetaData.TimeSlotTableMetaData.TABLE_NAME, null, null);
    }

    public  void deleteDiscountCodes(){
        database.delete(DatabaseMetaData.DiscountCodeTableMetaData.TABLE_NAME,null,null);
    }

    public void deleteOrders(){
        database.delete(DatabaseMetaData.OrderTableMetaData.TABLE_NAME,null,null);
    }
}
