package library.singularity.com.data.utils;

public class Constants {

    //Parse table names
    public static String PARSE_ADDRESS_TABLE_NAME = "Address";
    public static String PARSE_DISCOUNT_CODE_TABLE_NAME = "DiscountCode";
    public static String PARSE_DRIVER_SCHEDULE_TABLE_NAME = "DriverSchedule";
    public static String PARSE_ORDER_TABLE_NAME = "Order";
    public static final String PARSE_USER_PASSWORD = "password";

    //ParseUser columns
    public static String PARSE_USER_EMAIL_VERIFIED = "emailVerified";
    public static String PARSE_USER_EMAIL= "email";
    public static String PARSE_USER_IMAGE = "image";
    public static String PARSE_USER_IS_STRIPE_CLIENT = "isStripeClient";
    public static String PARSE_USER_FIRST_NAME = "fname";
    public static String PARSE_USER_LAST_NAME = "lname";
    public static String PARSE_USER_PHONE = "phone";

    //Parse Address columns
    public static String PARSE_ADDRESS_STREET = "street";
    public static String PARSE_ADDRESS_POSTAL_CODE = "postCode";
    public static String PARSE_ADDRESS_SUBURB = "suburb";
    public static String PARSE_ADDRESS_NUMBER = "number";
    public static String PARSE_ADDRESS_STREET_TWO = "streetTwo";
    public static String PARSE_ADDRESS_NOTES = "notes";
    public static String PARSE_ADDRESS_LOCATION = "location";
    public static String PARSE_ADDRESS_STATE = "state";
    public static String PARSE_ADDRESS_CITY = "city";
    public static String PARSE_ADDRESS_USER = "user";

    //Parse Issue columns
    public static String PARSE_ISSUE_REASON = "reason";
    public static String PARSE_ISSUE_DESCRIPTION = "description";
    public static String PARSE_ISSUE_ORDER = "order";

    //Parse Order columns
    public static String PARSE_ORDER_PICKUP_SCHEDULE = "pickUpSchedule";
    public static String PARSE_ORDER_DELIVERY_SCHEDULE = "deliverySchedule";
    public static String PARSE_ORDER_RATING = "rating";
    public static String PARSE_ORDER_STATUS = "status";
    public static String PARSE_ORDER_ETA = "ETA";
    public static String PARSE_ORDER_PICK_UP_ADDRESS = "pickUpAddress";
    public static String PARSE_ORDER_DELIVERY_ADDRESS = "deliveryAddress";
    public static String PARSE_ORDER_DELIVERY_DRIVER = "deliveryDriver.user";
    public static String PARSE_ORDER_PICKUP_DRIVER = "pickUpDriver.user";
    public static String PARSE_ORDER_DISCOUNT_CODE = "discountCode";
    public static String PARSE_ORDER_WASH_AND_DRY = "washAndDry";
    public static String PARSE_PAYMENT_AMOUNT = "amount";
    public static String PARSE_ORDER_PAYMENT_RECEIPT = "paymentReceipt";
    public static String PARSE_ORDER_NOTES = "notes";
    public static String PARSE_DRIVER_USER_KEY = "user";

    //Parse Laundry
    public static String PARSE_LAUNDRY_MAX_DELIVERY_DATE = "maxDeliveryDate";
    public static String PARSE_LAUNDRY_MIN_PICKUP_DATE = "minPickUpDate";
    public static String PARSE_LAUNDRY_FROM_DATE = "fromDate";
    public static String PARSE_LAUNDRY_TO_DATE = "toDate";
    public static String PARSE_LAUNDRY_POST_CODE = "postCode";
    public static String PARSE_LAUNDRY_RESERVED_CODE = "reserved";

    // Parse DiscountCode
    public static String PARSE_DISCOUNT_CODE_ID = "code";
    public static String PARSE_DISCOUNT_CODE_CODE = "code";
    public static String PARSE_DISCOUNT_CODE_AMOUNT = "amount";
    public static String PARSE_DISCOUNT_CODE_IS_PERCENTAGE = "isPercentage";

    // Parse DriverSchedule
    public static String PARSE_DRIVER_SCHEDULE_FROM_DATE = "fromDate";
    public static String PARSE_DRIVER_SCHEDULE_TO_DATE = "toDate";

    //Parse cloud
    public static String PARSE_CLOUD_SAVE_TOKEN_FUNCTION = "createStripeCustomer";
    public static String PARSE_CLOUD_TOKEN = "creditCardToken";

    // Parse Configuration
    public static String PARSE_CONFIGURATION_DRIVER_AVAILABILITY_INTERVAL = "driverAvailabilityInterval";
    public static String PARSE_CONFIGURATION_ORDER_STATUSES = "orderStatuses";
    public static String PARSE_CONFIGURATION_POST_CODE = "postCodes";
    public static String PARSE_CONFIGURATION_STRIPE_API_KEY = "stripeApiKey";
}
