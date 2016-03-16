package library.singularity.com.presenter;

import com.parse.ParseException;

import library.singularity.com.data.utils.BasicValidator;
import library.singularity.com.network.user.UserWebServiceInterfaces;
import library.singularity.com.network.user.UserWebServiceProvider;
import library.singularity.com.presenter.interfaces.ForgotPasswordView;

public class ForgotPasswordPresenter {

    private ForgotPasswordView forgotPasswordView;

    public ForgotPasswordPresenter(ForgotPasswordView forgotPasswordView) {
        this.forgotPasswordView = forgotPasswordView;
    }

    public void finish() { this.forgotPasswordView = null; }

    public void onResetClicked(String email) {
        boolean dataValid = true;

        if(BasicValidator.isValidEmail(email)) {
            forgotPasswordView.setEmailValid();
        } else {
            dataValid = false;
            forgotPasswordView.setEmailInvalid();
        }

        if(!dataValid) {
            return;
        }

        this.forgotPasswordView.showProgressDialog();

        UserWebServiceProvider.getInstance().resetPassword(email, new UserWebServiceInterfaces.IOnUserForgotPasswordListener() {
            @Override
            public void onPasswordResetSuccessfully() {
                if(forgotPasswordView == null) {
                    return;
                }

                forgotPasswordView.closeProgressDialog();
                forgotPasswordView.onPasswordResetSuccess();
            }

            @Override
            public void onPasswordResetFailed(Exception error) {
                if (forgotPasswordView == null) return;
                forgotPasswordView.closeProgressDialog();
                if (error instanceof ParseException) {
                    if (((ParseException) error).getCode() == 100) {
                        forgotPasswordView.showNoInternetConnectionDialog();
                        return;
                    }
                }

                forgotPasswordView.onPasswordResetFailed(error.getMessage());
            }
        });
    }
}
