package library.singularity.com.data.model.mappers;

import com.google.gson.Gson;
import com.parse.ParseConfig;


import library.singularity.com.data.model.Configuration;
import library.singularity.com.data.model.OrderStatus;
import library.singularity.com.data.utils.Constants;

public class ConfigurationEntityMapper {

    public static Configuration convertToConfiguration(ParseConfig parseObject){
        if(parseObject == null) return null;

        Configuration configuration = new Configuration();
        configuration.setDriverAvailabilityInterval(parseObject.getLong(Constants.PARSE_CONFIGURATION_DRIVER_AVAILABILITY_INTERVAL));
        configuration.setOrderStatuses(
                getOrderStatusesFromJSON(
                        parseObject.getJSONArray(Constants.PARSE_CONFIGURATION_ORDER_STATUSES).toString())
        );
       configuration.setPostCodes(parseObject.getList(Constants.PARSE_CONFIGURATION_POST_CODE));
       configuration.setStripeApiKey(parseObject.getString(Constants.PARSE_CONFIGURATION_STRIPE_API_KEY));
        return configuration;
    }

    public static OrderStatus[] getOrderStatusesFromJSON(String jsonArray) {
        Gson gson = new Gson();
        return gson.fromJson(jsonArray, OrderStatus[].class);
    }

    public static String getConfigurationAsJSON(Configuration configuration) {
        Gson gson = new Gson();
        return gson.toJson(configuration, Configuration.class);
    }

    public static Configuration getConfigurationFromJSON(String json){
        Gson gson = new Gson();
        return gson.fromJson(json,Configuration.class);
    }
}
