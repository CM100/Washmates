package library.singularity.com.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.Configuration;
import library.singularity.com.data.model.Order;
import library.singularity.com.data.model.User;
import library.singularity.com.data.utils.BasicValidator;
import library.singularity.com.network.address.AddressWebServiceInterfaces;
import library.singularity.com.network.address.AddressWebServiceProvider;
import library.singularity.com.network.orders.OrderWebServiceInterfaces;
import library.singularity.com.network.orders.OrderWebServiceProvider;
import library.singularity.com.network.user.UserWebServiceInterfaces;
import library.singularity.com.network.user.UserWebServiceProvider;
import library.singularity.com.presenter.interfaces.LoginView;
import library.singularity.com.repository.Repository;
import library.singularity.com.repository.SharedPreferencesProvider;
import library.singularity.com.repository.async.AddAddressTask;
import library.singularity.com.repository.async.AddOrdersTask;
import library.singularity.com.repository.async.AddUserTask;

public class LoginPresenter {

    private LoginView loginView;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
    }

    public void finish(){ this.loginView = null; }

    public void onLoginClicked(String email, String password) {
        boolean dataValid = true;

        if(BasicValidator.isValidEmail(email)) {
            loginView.setEmailValid();
        } else {
            dataValid = false;
            loginView.setEmailInvalid();
        }

        if(BasicValidator.isValidString(password)) {
            loginView.setPasswordValid();
        } else {
            dataValid = false;
            loginView.setPasswordInvalid();
        }

        if(!dataValid) {
            return;
        }

        this.loginView.showProgressDialog();
        UserWebServiceProvider.getInstance().loginUser(email, password, new UserWebServiceInterfaces.IOnUserLogInListener() {

            @Override
            public void onUserLoggedIn(User user) {
                if (loginView == null) {
                    return;
                }

                if (user.isEmailVerified()) {
                    saveUser(user);
                } else {
                    UserWebServiceProvider.getInstance().logoutUser(new UserWebServiceInterfaces.IOnUserLoggedOutListener() {
                        @Override
                        public void onLoggedOut() {
                            if (loginView == null) return;
                            loginView.showNotVerifiedEmail();
                        }

                        @Override
                        public void onLogoutFailed(Exception error) {
                            if (loginView == null) return;
                            loginView.closeProgressDialog();
                            if (error instanceof ParseException) {
                                if (((ParseException) error).getCode() == 100) {
                                    loginView.showNoInternetConnectionDialog();
                                    return;
                                }
                            }

                            loginView.onLoginFailed(error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onUserFailedToLogin(Exception error) {
                if (loginView == null) return;
                loginView.closeProgressDialog();
                if (error instanceof ParseException) {
                    if (((ParseException) error).getCode() == 100) {
                        loginView.showNoInternetConnectionDialog();
                        return;
                    }
                }

                loginView.onLoginFailed(error.getMessage());
            }
        });
    }

    public void saveUser(User user){
        Repository.getInstance(loginView.getContext()).addUser(user, new AddUserTask.IOnAddUserListener() {
            @Override
            public void onUserAddedSuccessfully() {
                getUsersAddresses();
            }

            @Override
            public void onUserFailedToAdd() {
                if (loginView == null) return;
                loginView.closeProgressDialog();
            }
        });
    }

    private void getUsersAddresses() {
        AddressWebServiceProvider.getAllAddresses(new AddressWebServiceInterfaces.IOnAddressesObtainedListener() {
            @Override
            public void onAddressesObtained(List<Address> addressList) {
                saveAddresses(addressList);
            }

            @Override
            public void onAddressFailedToObtain(Exception error) {
                if (loginView == null) return;
                loginView.closeProgressDialog();
                if (error instanceof ParseException) {
                    if (((ParseException) error).getCode() == 100) {
                        loginView.showNoInternetConnectionDialog();
                        return;
                    }
                }

            }
        });
    }

    public void saveAddresses(List<Address> addresses){
       Repository.getInstance(loginView.getContext()).addAddresses(addresses, new AddAddressTask.IOnAddAddressListener() {
           @Override
           public void onAddressAdded() {
               getOrders();
           }

           @Override
           public void onAddressFailedToAdd() {
               if (loginView == null) return;
               loginView.closeProgressDialog();
           }
       });
    }

    void getOrders() {
        if (loginView == null) return;

        Configuration configuration = SharedPreferencesProvider.getConfiguration(loginView.getContext());
        OrderWebServiceProvider.getOrders(configuration.getOrderStatuses(),
                new OrderWebServiceInterfaces.IOnOrdersObtainedListener() {
            @Override
            public void onOrdersObtained(List<Order> orders) {
                saveOrders(orders);
            }

            @Override
            public void onOrdersFailedToObtain(Exception error) {
                if (loginView == null) return;
                loginView.closeProgressDialog();
                if (error instanceof ParseException) {
                    if (((ParseException) error).getCode() == 100) {
                        loginView.showNoInternetConnectionDialog();
                        return;
                    }
                }

            }
        });
    }

    void saveOrders(List<Order> orders) {
        Repository.getInstance(loginView.getContext()).addOrders(orders, new AddOrdersTask.IOnAddOrdersListener() {
            @Override
            public void onOrdersAdded() {
                if (loginView == null) return;
                loginView.closeProgressDialog();
                loginView.onLoginSuccess();
            }

            @Override
            public void onOrdersFailedToAdd() {
                if (loginView == null) return;
                loginView.closeProgressDialog();
            }
        });
    }
}
