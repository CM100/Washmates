package library.singularity.com.presenter.interfaces;

import android.content.Context;

public interface SignUpAddressView {

    void setAddressLine1Valid();
    void setAddressLine1Invalid();
    void setCityValid();
    void setCityInvalid();
    void setStateValid();
    void setStateInvalid();
    void setPostCodeValid();
    void setPostCodeInvalid();
    void setLocationValid();
    void setLocationInvalid();
    void openSignUpPaymentFragment();
    Context getContext();
    void setValues(String address1, String address2, String city, String state, String postCode, String notes, String location);
    void showProgressDialog();
    void stopProgressDialog();
    void showNoInternetConnectionDialog();
    void showAddressUpdatedSuccessfully();
    void showAddressAddedSuccessfully();
    void showError(String error);
}
