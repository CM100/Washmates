package library.singularity.com.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.text.ParseException;

import library.singularity.com.data.model.User;
import library.singularity.com.data.model.mappers.UserEntityMapper;
import library.singularity.com.data.utils.Constants;
import library.singularity.com.network.user.UserWebServiceInterfaces;
import library.singularity.com.network.user.UserWebServiceProvider;
import library.singularity.com.presenter.interfaces.SignUpPaymentView;
import library.singularity.com.presenter.interfaces.SignUpProfileView;
import library.singularity.com.repository.Repository;
import library.singularity.com.repository.SharedPreferencesProvider;
import library.singularity.com.repository.async.UpdateUserTask;

public class SignUpProfilePresenter {

    private SignUpProfileView signUpProfileView;
    private User user;

    public SignUpProfilePresenter(SignUpProfileView signUpProfileView, User user){
        this.signUpProfileView = signUpProfileView;
        this.user = user;
    }

    public void finish(){
        signUpProfileView = null;
    }

    public void onSaveUserData(String firstName, String lastName, String email, String phone, String password, String confirmPassword){
        if (signUpProfileView == null) return;

        updateUser(firstName, lastName, email, password, confirmPassword, phone);
        boolean dataValid = validateDataAndUpdateView();

        if(dataValid){
            if (TextUtils.isEmpty(user.getId())) {
                SharedPreferencesProvider.saveUser(signUpProfileView.getContext(), user);
                checkIfEmailIsFree();
                return;
            }

            saveUserOnline();
        }
    }

    void updateUser(String firstName, String lastName, String email, String password, String confirmPassword, String phone) {
        if (user == null) {
            user = new User();
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(email);

        if (TextUtils.isEmpty(user.getId()) || (!TextUtils.isEmpty(password) || !TextUtils.isEmpty(confirmPassword))) {
            user.setPassword(password);
            user.setConfirmPassword(confirmPassword);
        }

        try {
            user.setPhoneNumber(Long.parseLong(phone));
        } catch (NumberFormatException e){
            user.setPhoneNumber(0);
        }
    }

    boolean validateDataAndUpdateView() {
        boolean dataValid = true;

        if(user.isFirstNameValid()){
            signUpProfileView.setFirstNameValid();
        } else {
            signUpProfileView.setFirstNameInvalid();
            dataValid = false;
        }

        if(user.isLastNameValid()){
            signUpProfileView.setLastNameValid();
        } else {
            signUpProfileView.setLastNameInvalid();
            dataValid = false;
        }

        if(user.isEmailValid()){
            signUpProfileView.setEmailValid();
        } else {
            signUpProfileView.setEmailInvalid();
            dataValid = false;
        }

        if(user.isPhoneNumberValid()){
            signUpProfileView.setPhoneNumberValid();
        } else {
            signUpProfileView.setPhoneNumberInvalid();
            dataValid = false;
        }

        boolean checkPassword = TextUtils.isEmpty(user.getEmail()) || (!TextUtils.isEmpty(user.getPassword()) || !TextUtils.isEmpty(user.getConfirmPassword()));
        if (!checkPassword) return dataValid;

        if (user.isPasswordValid()) {
            signUpProfileView.setPasswordValid();
        } else {
            signUpProfileView.setPasswordInvalid();
            dataValid = false;
        }

        if(!TextUtils.isEmpty(user.getConfirmPassword())){
            if(user.getPassword().equals(user.getConfirmPassword())){
                signUpProfileView.setConfirmPasswordValid();
            } else {
                signUpProfileView.setConfirmPasswordInvalid();
                dataValid = false;
            }
        } else {
            signUpProfileView.setConfirmPasswordInvalid();
            dataValid = false;
        }

        return dataValid;
    }

    void checkIfEmailIsFree() {
        if (signUpProfileView == null) return;
        signUpProfileView.showProgressDialog();
        UserWebServiceProvider.getInstance().checkIfEmailIsFree(user.getEmail(),
                new UserWebServiceInterfaces.IOnEmailQueryListener() {
                    @Override
                    public void onEmailQueryFailed(Exception error) {
                        if (signUpProfileView == null || error == null) return;
                        signUpProfileView.stopProgressDialog();
                        if (error instanceof com.parse.ParseException &&
                                ((com.parse.ParseException) error).getCode() == 100) {
                            signUpProfileView.showNoInternetConnectionDialog();
                            return;
                        }

                        signUpProfileView.showError(error.getMessage());
                    }

                    @Override
                    public void onEmailQuerySuccess(int numberOfUsers) {
                        if (signUpProfileView == null) return;
                        signUpProfileView.stopProgressDialog();
                        if (numberOfUsers == 0) {
                            signUpProfileView.openSignUpAddressFragment();
                        } else {
                            signUpProfileView.setEmailInvalid();
                            signUpProfileView.emailAlreadyTaken();
                        }
                    }
                });
    }

    void saveUserOnline() {
        if (signUpProfileView == null) return;

        signUpProfileView.showProgressDialog();
        UserWebServiceProvider.getInstance().updateUser(user,
                new UserWebServiceInterfaces.IOnUserUpdateListener() {
                    @Override
                    public void onUserUpdatedSuccessfully() {
                        if (signUpProfileView == null) return;

                        updateUserInDb();
                    }

                    @Override
                    public void onUserFailedToUpdate(Exception error) {
                        if (signUpProfileView == null) return;

                        signUpProfileView.stopProgressDialog();
                        if (error instanceof com.parse.ParseException) {
                            if (((com.parse.ParseException) error).getCode() == 100) {
                                signUpProfileView.showNoInternetConnectionDialog();
                                return;
                            }
                        }

                        signUpProfileView.showError(error.getMessage());
                    }
                });
    }

    void updateUserInDb() {
        if (signUpProfileView == null) return;

        Repository.getInstance(signUpProfileView.getContext()).updateUser(user,
                new UpdateUserTask.IOnUpdateUserListener() {
                    @Override
                    public void onUserUpdated() {
                        if (signUpProfileView == null) return;

                        signUpProfileView.stopProgressDialog();
                        signUpProfileView.userUpdated();
                    }

                    @Override
                    public void onUserFailedToUpdate() {
                        if (signUpProfileView == null) return;

                        signUpProfileView.stopProgressDialog();
                    }
                });
    }
    public void viewCreated() {
        if (signUpProfileView == null) return;

        if (user == null) {
            user = SharedPreferencesProvider.getUser(signUpProfileView.getContext());
        }

        if (user == null) return;

        signUpProfileView.setSingUpProfileDetails(user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber()+"",
                user.getPassword());
    }
}
