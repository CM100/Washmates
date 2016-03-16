package library.singularity.com.presenter.interfaces;

import android.content.Context;

import java.util.Date;
import java.util.List;

import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.TimeSlot;
import library.singularity.com.data.model.User;

public interface SchedulePickupView {

    Context getContext();
    void showProgressDialog();
    void stopProgressDialog();
    void showError(String error);
    void showNoInternetConnectionDialog();
    void setupAddressesChoices(List<Address> addressList);
    void setupPickupDateDropDown(List<Date> dates);
    void setupDeliveryDateDropDown(List<Date> dates);
    void setupPickupTimeDropDown(List<String> time);
    void setupDeliveryTimeDropDown(List<String> time);
    void selectDefaultAddress();
    void disablePickupTime(boolean enabled);
    void disableDeliveryTime(boolean enabled);
    boolean isWashAndTryActive();
    Date getSelectedDeliveryDate();
    Date getSelectedPickupDate();
    int getSelectedPickupTimeIndex();
    int getSelectedDeliveryTimeIndex();
    void showInvalidPickupAndDeliveryDialog();
    void selectDeliveryDate(int position);
    void showAddCreditCardDialog(User user);
    void showOrderAlreadyExistsDialog();
    void disableDeliveryDate(boolean enabled);
    void showInvalidAddressDialog();
    void showInvalidPickupDate();
    void showInvalidPickupTime();
    void showInvalidDeliveryDate();
    void showInvalidDeliveryTime();

}
