package library.singularity.com.presenter.interfaces;

public interface ForgotPasswordView {
    void setEmailValid();
    void setEmailInvalid();
    void showProgressDialog();
    void closeProgressDialog();
    void onPasswordResetSuccess();
    void onPasswordResetFailed(String error);
    void showNoInternetConnectionDialog();
}
