package library.singularity.com.network.orders;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import library.singularity.com.data.model.Configuration;
import library.singularity.com.data.model.Order;
import library.singularity.com.data.model.OrderStatus;
import library.singularity.com.data.model.mappers.OrderEntityMapper;
import library.singularity.com.data.utils.Constants;
import library.singularity.com.network.resolver.ErrorCodeResolver;

public class OrderWebServiceProvider {

    private static String GET_ORDERS_TABLE_NAME_KEY = "table";
    private static String GET_ORDERS_TABLE_NAME_VALUE = "Order";
    private static String GET_ORDERS_CRITERIA_KEY = "criteria";
    private static String GET_ORDERS_EQUAL_TO_KEY = "equalTo";
    private static String GET_ORDERS_USER_KEY = "user";
    private static String GET_ORDERS_INCLUDE_KEY = "include";
    private static String CREATE_ORDER_ORDER_KEY = "order";

    private static String CLOUD_METHOD_GET_ALL = "getAll";
    private static String CLOUD_METHOD_CREATE_ORDER = "createOrder";

    public static void getOrders(final OrderStatus[] statuses, final OrderWebServiceInterfaces.IOnOrdersObtainedListener listener) {
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            if (listener == null) return;
            listener.onOrdersFailedToObtain(new NullPointerException());
        }


        HashMap<String, Object> params = new HashMap<>();
        params.put(GET_ORDERS_TABLE_NAME_KEY, GET_ORDERS_TABLE_NAME_VALUE);


        HashMap<String, Object> userObject = new HashMap<>();

        String userJsonString = "{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\""+user.getObjectId()+"\"}";
        userObject.put(GET_ORDERS_EQUAL_TO_KEY, userJsonString);

        HashMap<String, Object> criteria = new HashMap<>();
        criteria.put(GET_ORDERS_USER_KEY, userObject);

        List<String> includes = new ArrayList<>();
        includes.add(Constants.PARSE_ORDER_PICKUP_SCHEDULE);
        includes.add(Constants.PARSE_ORDER_DELIVERY_SCHEDULE);
        includes.add(Constants.PARSE_ORDER_PICK_UP_ADDRESS);
        includes.add(Constants.PARSE_ORDER_DELIVERY_ADDRESS);
        includes.add(Constants.PARSE_ORDER_DELIVERY_DRIVER);
        includes.add(Constants.PARSE_ORDER_PICKUP_DRIVER);
        params.put(GET_ORDERS_CRITERIA_KEY, criteria);
        params.put(GET_ORDERS_INCLUDE_KEY, includes);


        ParseCloud.callFunctionInBackground(CLOUD_METHOD_GET_ALL, params, new FunctionCallback<Object>() {
            public void done(Object response, ParseException e) {
                if (e != null) {
                    if (listener == null) return;
                    listener.onOrdersFailedToObtain(ErrorCodeResolver.resolveError(e));
                    return;
                }

                if (listener == null) return;

                List<Order> orders = new ArrayList<>();
                List<ParseObject> parseOrders = (ArrayList<ParseObject>) response;
                for (ParseObject parseOrder : parseOrders) {
                    orders.add(OrderEntityMapper.convertToOrder(parseOrder, statuses));
                }

                listener.onOrdersObtained(orders);
            }
        });
    }

    public static void createOrder(Order order, final OrderStatus[] statuses, final OrderWebServiceInterfaces.IOnOrderCreatedListener listener) {
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null || order == null) {
            if (listener == null) return;
            listener.onOrderFailedToCreate(new NullPointerException());
        }


        HashMap<String, Object> params = new HashMap<>();

        Gson gson = new Gson();
        params.put(CREATE_ORDER_ORDER_KEY, gson.toJson(order, Order.class));

        ParseCloud.callFunctionInBackground(CLOUD_METHOD_CREATE_ORDER, params, new FunctionCallback<Object>() {
            public void done(Object response, ParseException e) {
                if (e != null) {
                    if (listener == null) return;
                    listener.onOrderFailedToCreate(ErrorCodeResolver.resolveError(e));
                    return;
                }

                if (listener == null) return;

                Order order = OrderEntityMapper.convertToOrder((ParseObject) response, statuses);
                listener.onOrderCreated(order);
            }
        });
    }

}
