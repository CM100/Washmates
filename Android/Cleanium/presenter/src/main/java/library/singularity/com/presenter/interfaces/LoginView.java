package library.singularity.com.presenter.interfaces;

import android.content.Context;

public interface LoginView {
    Context getContext();
    void setEmailValid();
    void setEmailInvalid();
    void setPasswordValid();
    void setPasswordInvalid();
    void showProgressDialog();
    void closeProgressDialog();
    void onLoginSuccess();
    void onLoginFailed(String error);
    void showNoInternetConnectionDialog();
    void showNotVerifiedEmail();
}
