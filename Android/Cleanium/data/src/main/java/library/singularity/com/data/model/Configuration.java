package library.singularity.com.data.model;

import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private long driverAvailabilityInterval;
    private String stripeApiKey;
    private List<Integer> postCodes;
    private OrderStatus[] orderStatuses;

    public Configuration() {}

    public long getDriverAvailabilityInterval() {
        return driverAvailabilityInterval;
    }

    public void setDriverAvailabilityInterval(long driverAvailabilityInterval) {
        this.driverAvailabilityInterval = driverAvailabilityInterval;
    }

    public String getStripeApiKey() {
        return stripeApiKey;
    }

    public void setStripeApiKey(String stripeApiKey) {
        this.stripeApiKey = stripeApiKey;
    }

    public List<Integer> getPostCodes() {
        return postCodes;
    }

    public OrderStatus[] getOrderStatuses() {
        return orderStatuses;
    }

    public void setOrderStatuses(OrderStatus[] orderStatuses) {
        this.orderStatuses = orderStatuses;
    }

    public void setPostCodes(List<Object> objects){
        List<Integer> postCodes = new ArrayList<>();
        if(objects == null) return;
        for (Object object : objects){
            postCodes.add((int)object);
        }

        this.postCodes = postCodes;
    }
}
