package library.singularity.com.presenter;

import java.util.List;

import library.singularity.com.data.model.Configuration;
import library.singularity.com.data.model.Order;
import library.singularity.com.data.model.OrderStatus;
import library.singularity.com.presenter.interfaces.OrderHistoryView;
import library.singularity.com.repository.Repository;
import library.singularity.com.repository.SharedPreferencesProvider;
import library.singularity.com.repository.async.GetOrderHistoryTask;

public class OrderHistoryPresenter {

    private OrderHistoryView orderHistoryView;

    public OrderHistoryPresenter(OrderHistoryView orderHistoryView){
        this.orderHistoryView = orderHistoryView;
    }

    public void finish(){
        orderHistoryView = null;
    }


    public void onViewCreated() {
        if (orderHistoryView == null) return;

        orderHistoryView.showProgressDialog();

        Repository.getInstance(orderHistoryView.getContext())
                .getOrderHistory(new GetOrderHistoryTask.IOnOrderHistoryObtainedListener() {
                    @Override
                    public void OnOrderHistoryObtained(List<Order> orders) {
                        if (orderHistoryView == null) return;

                        if (orders != null) {
                            Configuration configuration = SharedPreferencesProvider.getConfiguration(orderHistoryView.getContext());
                            for (Order order : orders) {
                                for (OrderStatus orderStatus : configuration.getOrderStatuses()) {
                                    if (order.getStatus().equals(orderStatus.getName())) {
                                        order.setStatus(orderStatus.getText());
                                        order.setColor(orderStatus.getColor());
                                        break;
                                    }
                                }
                            }
                        }

                        orderHistoryView.setValues(orders);
                        orderHistoryView.closeProgressDialog();
                    }
                });
    }
}
