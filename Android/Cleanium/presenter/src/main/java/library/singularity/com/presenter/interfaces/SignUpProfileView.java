package library.singularity.com.presenter.interfaces;

import android.content.Context;

public interface SignUpProfileView {

    void setFirstNameValid();
    void setFirstNameInvalid();
    void setLastNameValid();
    void setLastNameInvalid();
    void setEmailValid();
    void setEmailInvalid();
    void setPhoneNumberValid();
    void setPhoneNumberInvalid();
    void setPasswordValid();
    void setPasswordInvalid();
    void setConfirmPasswordValid();
    void setConfirmPasswordInvalid();
    void openSignUpAddressFragment();
    void setSingUpProfileDetails(String firstName, String lastName,
                                 String email, String phoneNumber, String password);
    Context getContext();
    void showProgressDialog();
    void stopProgressDialog();
    void showError(String error);
    void emailAlreadyTaken();
    void showNoInternetConnectionDialog();
    void userUpdated();
}
