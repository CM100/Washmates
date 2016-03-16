package library.singularity.com.dao.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    //query for creating user table
    private static final String CREATE_USER_TABLE_QUERY = "CREATE TABLE "
            + DatabaseMetaData.UserTableMetaData.TABLE_NAME + "("
            + DatabaseMetaData.UserTableMetaData.ID + " TEXT PRIMARY KEY, "
            + DatabaseMetaData.UserTableMetaData.USERNAME + " TEXT, "
            + DatabaseMetaData.UserTableMetaData.PASSWORD + " TEXT, "
            + DatabaseMetaData.UserTableMetaData.EMAIL_VERIFIED + " INTEGER, "
            + DatabaseMetaData.UserTableMetaData.EMAIL + " TEXT, "
            + DatabaseMetaData.UserTableMetaData.IMAGE_PATH + " TEXT, "
            + DatabaseMetaData.UserTableMetaData.IS_STRIPE_CLIENT + " INTEGER, "
            + DatabaseMetaData.UserTableMetaData.FIRST_NAME + " TEXT, "
            + DatabaseMetaData.UserTableMetaData.LAST_NAME + " TEXT, "
            + DatabaseMetaData.UserTableMetaData.PHONE_NUMBER + " INTEGER);";

    //query for creating address table
    private static final String CREATE_ADDRESS_TABLE_QUERY = "CREATE TABLE "
            + DatabaseMetaData.AddressTableMetaData.TABLE_NAME + "("
            + DatabaseMetaData.AddressTableMetaData.ID + " TEXT PRIMARY KEY, "
            + DatabaseMetaData.AddressTableMetaData.STREET + " TEXT, "
            + DatabaseMetaData.AddressTableMetaData.POSTAL_CODE + " INTEGER, "
            + DatabaseMetaData.AddressTableMetaData.SUBURB + " TEXT, "
            + DatabaseMetaData.AddressTableMetaData.NUMBER + " TEXT, "
            + DatabaseMetaData.AddressTableMetaData.STREET_TWO + " TEXT, "
            + DatabaseMetaData.AddressTableMetaData.NOTES + " TEXT, "
            + DatabaseMetaData.AddressTableMetaData.LOCATION + " TEXT, "
            + DatabaseMetaData.AddressTableMetaData.STATE + " TEXT, "
            + DatabaseMetaData.AddressTableMetaData.CITY + " TEXT);";

    //query for creating laundry schedule table
    private static final String CREATE_LAUNDRY_SCHEDULE_TABLE_QUERY = "CREATE TABLE "
            + DatabaseMetaData.LaundryScheduleTableMetaData.TABLE_NAME + "("
            + DatabaseMetaData.LaundryScheduleTableMetaData.MIN_PICKUP_DATE + " INTEGER, "
            + DatabaseMetaData.LaundryScheduleTableMetaData.MAX_DELIVERY_DATE + " INTEGER);";

    //query for creating time slot table
    private static final String CREATE_TIME_SLOT_TABLE_QUERY = "CREATE TABLE "
            + DatabaseMetaData.TimeSlotTableMetaData.TABLE_NAME + "("
            + DatabaseMetaData.TimeSlotTableMetaData.ID + " TEXT,"
            + DatabaseMetaData.TimeSlotTableMetaData.FROM_DATE + " INTEGER, "
            + DatabaseMetaData.TimeSlotTableMetaData.TO_DATE + " INTEGER, "
            + DatabaseMetaData.TimeSlotTableMetaData.POST_CODE + " INTEGER, "
            + DatabaseMetaData.TimeSlotTableMetaData.ORDER_ID + " TEXT);";

    // query for creating discount code table
    private static final String CREATE_DISCOUNT_CODE_TABLE_QUERY = "CREATE TABLE "
            + DatabaseMetaData.DiscountCodeTableMetaData.TABLE_NAME + "("
            + DatabaseMetaData.DiscountCodeTableMetaData.ID + " TEXT,"
            + DatabaseMetaData.DiscountCodeTableMetaData.CODE + " TEXT,"
            + DatabaseMetaData.DiscountCodeTableMetaData.AMOUNT + " REAL,"
            + DatabaseMetaData.DiscountCodeTableMetaData.IS_PERCENTAGE + " INTEGER);";

    // query for creating order table
    private static final String CREATE_ORDER_TABLE_QUERY = "CREATE TABLE "
            + DatabaseMetaData.OrderTableMetaData.TABLE_NAME + "("
            + DatabaseMetaData.OrderTableMetaData.ID + " TEXT,"
            + DatabaseMetaData.OrderTableMetaData.PICK_UP_SCHEDULE + " TEXT,"
            + DatabaseMetaData.OrderTableMetaData.DELIVERY_SCHEDULE + " TEXT,"
            + DatabaseMetaData.OrderTableMetaData.STATUS + " TEXT,"
            + DatabaseMetaData.OrderTableMetaData.RATING + " REAL,"
            + DatabaseMetaData.OrderTableMetaData.DISCOUNT_CODE + " TEXT,"
            + DatabaseMetaData.OrderTableMetaData.PICK_UP_ADDRESS + " TEXT,"
            + DatabaseMetaData.OrderTableMetaData.DELIVERY_ADDRESS + " TEXT, "
            + DatabaseMetaData.OrderTableMetaData.WASH_AND_DRY + " INTEGER, "
            + DatabaseMetaData.OrderTableMetaData.PRICE + " TEXT, "
            + DatabaseMetaData.OrderTableMetaData.NOTES + " TEXT, "
            + DatabaseMetaData.OrderTableMetaData.DRIVERS + " TEXT);";


    public DatabaseHandler(Context context) {
        super(context.getApplicationContext(), DatabaseMetaData.DATABASE_NAME, null, DatabaseMetaData.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_USER_TABLE_QUERY);
        database.execSQL(CREATE_ADDRESS_TABLE_QUERY);
        database.execSQL(CREATE_LAUNDRY_SCHEDULE_TABLE_QUERY);
        database.execSQL(CREATE_TIME_SLOT_TABLE_QUERY);
        database.execSQL(CREATE_DISCOUNT_CODE_TABLE_QUERY);
        database.execSQL(CREATE_ORDER_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseMetaData.UserTableMetaData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseMetaData.AddressTableMetaData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseMetaData.LaundryScheduleTableMetaData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseMetaData.TimeSlotTableMetaData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseMetaData.DiscountCodeTableMetaData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseMetaData.OrderTableMetaData.TABLE_NAME);
        onCreate(db);
    }
}
