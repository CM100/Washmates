package library.singularity.com.presenter.interfaces;

import android.content.Context;

public interface SignUpPaymentView {

    void setCreditCardNumberValid();
    void setCreditCardNumberInvalid();
    void setExpirationDateValid();
    void setExpirationDateInvalid();
    void setCVCNumberValid();
    void setCVCNumberInvalid();
    void showStripeError(String error);
    void authenticationError();
    void showProgressDialog();
    void closeProgressDialog();
    void onRegistrationSuccess();
    void onRegistrationFailed(String error);
    void showNoInternetConnectionDialog();
    void setValues(String creditCardNumber, String expirationMonth, String expirationYear, String CVC);
    Context getContext();
    void showCreditCardDataInvalid();
    void onCreditCardAdded();
}
