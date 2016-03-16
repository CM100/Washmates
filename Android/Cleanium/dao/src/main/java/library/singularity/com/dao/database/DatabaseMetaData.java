package library.singularity.com.dao.database;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.SparseArray;

import java.util.HashMap;

public class DatabaseMetaData {
    public static final String AUTHORITY = "com.android.provider.DatabaseHandler";
    public static final String DATABASE_NAME = "cleanium.db";
    public static final int DATABASE_VERSION = 4;

    private DatabaseMetaData() {}

    public static final class UserTableMetaData implements BaseColumns
    {
        private UserTableMetaData() {}
        public static final String TABLE_NAME = "user_table";

        //uri and mime type definitions
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.android." + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.android." + TABLE_NAME;

        public static final String ID = "id";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String EMAIL_VERIFIED = "emailVerified";
        public static final String EMAIL = "email";
        public static final String IMAGE_PATH = "imagePath";
        public static final String IS_STRIPE_CLIENT = "isStripeClient";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String PHONE_NUMBER = "phoneNumber";

        public static final String DEFAULT_SORT_ORDER = ID + " ASC";
    }

    public static final class AddressTableMetaData implements BaseColumns
    {
        private AddressTableMetaData() {}
        public static final String TABLE_NAME = "address_table";

        //uri and mime type definitions
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.android." + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.android." + TABLE_NAME;

        public static final String ID = "id";
        public static final String STREET = "street";
        public static final String POSTAL_CODE = "postalCode";
        public static final String SUBURB = "suburb";
        public static final String NUMBER = "number";
        public static final String STREET_TWO = "streetTwo";
        public static final String NOTES = "notes";
        public static final String LOCATION = "location";
        public static final String STATE = "state";
        public static final String CITY = "city";

        public static final String DEFAULT_SORT_ORDER = ID + " ASC";
    }

    public static final class DiscountCodeTableMetaData implements BaseColumns
    {
        private DiscountCodeTableMetaData(){}
        public static final String TABLE_NAME = "discount_code_table";

        //uri and mime type definitions
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.android." + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.android." + TABLE_NAME;

        public static final String ID = "id";
        public static final String CODE = "code";
        public static final String AMOUNT = "amount";
        public static final String IS_PERCENTAGE = "isPercentage";
        public static final String DEFAULT_SORT_ORDER = ID + " ASC";

    }

    public static final class OrderTableMetaData implements  BaseColumns
    {
        private OrderTableMetaData(){}
        public static final String TABLE_NAME = "order_table";

        //uri and mime type definitions
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.android." + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.android." + TABLE_NAME;

        public static final String ID = "id";
        public static final String PICK_UP_SCHEDULE = "pickUpSchedule";
        public static final String DELIVERY_SCHEDULE = "deliverySchedule";
        public static final String STATUS = "status";
        public static final String RATING = "rating";
        public static final String DISCOUNT_CODE = "discountCode";
        public static final String PICK_UP_ADDRESS = "pickUpAddress";
        public static final String DELIVERY_ADDRESS = "deliveryAddress";
        public static final String WASH_AND_DRY = "washAndDry";
        public static final String PRICE = "price";
        public static final String NOTES = "notes";
        public static final String DRIVERS = "drivers";

        public static final String DEFAULT_SORT_ORDER = ID + " ASC";

    }

    public static final class LaundryScheduleTableMetaData implements BaseColumns
    {
        private LaundryScheduleTableMetaData() {}
        public static final String TABLE_NAME = "laundry_schedule_table";

        //uri and mime type definitions
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.android." + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.android." + TABLE_NAME;

        public static final String MIN_PICKUP_DATE = "minPickUpDate";
        public static final String MAX_DELIVERY_DATE = "maxDeliveryDate";

        public static final String DEFAULT_SORT_ORDER = _ID + " ASC";
    }

    public static final class TimeSlotTableMetaData implements BaseColumns
    {
        private TimeSlotTableMetaData() {}
        public static final String TABLE_NAME = "time_slot_table";

        //uri and mime type definitions
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.android." + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.android." + TABLE_NAME;

        public static final String FROM_DATE = "fromDate";
        public static final String TO_DATE = "toDate";
        public static final String POST_CODE = "postCode";
        public static final String ORDER_ID = "orderId";
        public static final String ID = "id";

        public static final String DEFAULT_SORT_ORDER = FROM_DATE + " ASC";
    }
}
