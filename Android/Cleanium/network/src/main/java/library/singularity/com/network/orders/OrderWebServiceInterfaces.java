package library.singularity.com.network.orders;

import java.util.List;

import library.singularity.com.data.model.Order;

public class OrderWebServiceInterfaces {

    public interface IOnOrdersObtainedListener {
        void onOrdersObtained(List<Order> orders);
        void onOrdersFailedToObtain(Exception error);
    }

    public interface IOnOrderCreatedListener {
        void onOrderCreated(Order order);
        void onOrderFailedToCreate(Exception error);
    }

}
