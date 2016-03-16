package library.singularity.com.presenter.interfaces;

import android.content.Context;

import java.util.Date;

import library.singularity.com.data.model.Bill;
import library.singularity.com.data.model.OrderStatus;

public interface WashStatusView {
    void showProgressDialog();
    Context getContext();
    void stopProgressDialog();
    void setNoCurrentOrderStatus();
    void setWashStatuses(OrderStatus[] statuses);
    void setCurrentWashStatus(OrderStatus status);
    void setInvalidPickupDate();
    void setInvalidPickupTime();
    void setInvalidDeliveryDate();
    void setInvalidDeliveryTime();
    void hideNotes();
    void hideBill();
    void hideCancelButton();
    void setPickupDate(Date date);
    void setPickupTime(Date fromDate, Date toDate);
    void setDeliveryDate(Date date);
    void setDeliveryTime(Date fromDate, Date toDate);
    void showNotes(String note);
    void showBill(Bill bill);
    void showCancelButton();
    void hideDriver();
    void showDriver(String driverName, String imageUrl);
}
