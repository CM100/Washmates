package library.singularity.com.presenter;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import library.singularity.com.data.model.Configuration;
import library.singularity.com.data.model.Driver;
import library.singularity.com.data.model.Order;
import library.singularity.com.data.model.OrderStatus;
import library.singularity.com.presenter.interfaces.WashStatusView;
import library.singularity.com.repository.Repository;
import library.singularity.com.repository.SharedPreferencesProvider;
import library.singularity.com.repository.async.GetCurrentOrderAsyncTask;

public class WashStatusPresenter implements GetCurrentOrderAsyncTask.IOnOrderObtainedListener {

    WashStatusView view;
    Configuration configuration;
    Order currentOrder;

    public WashStatusPresenter(WashStatusView view) {
        this.view = view;
    }

    public void finish() {
        view = null;
    }

    public void onScreenCreated() {
        if (view == null) return;

        view.showProgressDialog();
        configuration = SharedPreferencesProvider.getConfiguration(view.getContext());
        if (configuration == null || configuration.getOrderStatuses() == null) {
            view.stopProgressDialog();
            return;
        }

        Repository.getInstance(view.getContext())
                .getCurrentOrder(this);
    }

    @Override
    public void OnOrderObtained(Order order) {
        this.currentOrder = order;

        if (view == null) return;

        List<OrderStatus> progressStatuses = new ArrayList<>();
        for (OrderStatus status : configuration.getOrderStatuses()) {
            if (status.getMode().toLowerCase().equals("progress")) {
                progressStatuses.add(status);
            }
        }

        if (order == null) {
            view.setWashStatuses(progressStatuses.toArray(new OrderStatus[progressStatuses.size()]));
            view.setNoCurrentOrderStatus();
            view.setInvalidPickupDate();
            view.setInvalidPickupTime();
            view.setInvalidDeliveryDate();
            view.setInvalidDeliveryTime();
            view.hideNotes();
            view.hideBill();
            view.hideCancelButton();
            view.hideDriver();
            view.stopProgressDialog();
            return;
        }

        OrderStatus currentStatus = getCurrentOrderStatus(order);
        OrderStatus[] statuses = null;
        if (!currentStatus.isShowOnHistory()) {
            statuses = progressStatuses.toArray(new OrderStatus[progressStatuses.size()]);
        }

        if (currentStatus.getMode().toLowerCase().equals("fail")) {
            view.setWashStatuses(null);
            view.hideCancelButton();
        } else {
            view.setWashStatuses(statuses);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(order.getPickUpSchedule().getFromDate());
            calendar.add(Calendar.HOUR_OF_DAY, -2);

            Calendar now = Calendar.getInstance();
            if (currentStatus.isCancellable() && calendar.after(now)) {
                view.showCancelButton();
            } else {
                view.hideCancelButton();
            }
        }

        view.setCurrentWashStatus(currentStatus);
        view.setPickupDate(order.getPickUpSchedule().getFromDate());
        view.setPickupTime(order.getPickUpSchedule().getFromDate(), order.getPickUpSchedule().getToDate());
        view.setDeliveryDate(order.getDeliverySchedule().getFromDate());
        view.setDeliveryTime(order.getDeliverySchedule().getFromDate(), order.getDeliverySchedule().getToDate());

        if (TextUtils.isEmpty(order.getNotes())) {
            view.hideNotes();
        } else {
            view.showNotes(order.getNotes());
        }

        if (order.getBill() == null) {
            view.hideBill();
        } else {
            view.showBill(order.getBill());
        }

        if (TextUtils.isEmpty(currentStatus.getDriverFrom())) {
            view.hideDriver();
        } else {
            Driver driver = order.getDriver(currentStatus.getName());
            view.showDriver(driver.getName(), driver.getImageUrl());
        }

        view.stopProgressDialog();
    }

    OrderStatus getCurrentOrderStatus(Order order) {
        OrderStatus currentStatus = null;
        for (OrderStatus status : configuration.getOrderStatuses()) {
            if (status.getName().equals(order.getStatus())) {
                currentStatus = status;
                break;
            }
        }

        return currentStatus;
    }
}
