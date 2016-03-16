package library.singularity.com.presenter;

import android.text.TextUtils;
import android.widget.Toast;

import com.parse.ParseException;
import com.stripe.android.*;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import java.util.Calendar;

import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.Configuration;
import library.singularity.com.data.model.User;
import library.singularity.com.network.address.AddressWebServiceInterfaces;
import library.singularity.com.network.address.AddressWebServiceProvider;
import library.singularity.com.network.user.UserWebServiceInterfaces;
import library.singularity.com.network.user.UserWebServiceProvider;
import library.singularity.com.presenter.interfaces.SignUpPaymentView;
import library.singularity.com.repository.Repository;
import library.singularity.com.repository.SharedPreferencesProvider;
import library.singularity.com.repository.async.UpdateUserTask;

public class SignUpPaymentPresenter {

    private SignUpPaymentView signUpPaymentView;
    private User user;
    private Configuration configuration;
    boolean registeration;

    public SignUpPaymentPresenter(SignUpPaymentView signUpPaymentView, User user){
        this.signUpPaymentView = signUpPaymentView;
        if (user == null) {
            registeration = true;
            this.user = SharedPreferencesProvider.getUser(signUpPaymentView.getContext());
        } else {
            registeration = false;
            this.user = user;
        }

        configuration = SharedPreferencesProvider.getConfiguration(signUpPaymentView.getContext());
    }

    public void finish(){
        signUpPaymentView = null;
    }

    public void onFinshBtnClick(String creditCardNumber, String expirationDateMonth, String expirationDateYear, String cvcNumber){
        if (signUpPaymentView == null) return;

        boolean isDataEntered = isDataEntered(creditCardNumber, expirationDateMonth, expirationDateYear, cvcNumber);
        boolean dataValid = validateDataAndUpdateView(creditCardNumber, expirationDateMonth, expirationDateYear, cvcNumber);

        if (isDataEntered && dataValid) {
            this.signUpPaymentView.showProgressDialog();
            if (TextUtils.isEmpty(user.getId())) {
                saveCard(creditCardNumber, expirationDateMonth, expirationDateYear, cvcNumber);
            }

            createToken(creditCardNumber, expirationDateMonth, expirationDateYear, cvcNumber);
        } else if (!isDataEntered && TextUtils.isEmpty(user.getId())) {
            this.signUpPaymentView.showProgressDialog();
            signUpPaymentView.setCreditCardNumberValid();
            signUpPaymentView.setExpirationDateValid();
            signUpPaymentView.setCVCNumberValid();
            registerUser(null);
        } else {
            signUpPaymentView.showCreditCardDataInvalid();
        }
    }

    boolean isDataEntered(String creditCardNumber, String expirationDateMonth, String expirationDateYear, String cvcNumber) {
        boolean dataEntered = false;

        if (!TextUtils.isEmpty(creditCardNumber)) {
            dataEntered = true;
        }

        if (!TextUtils.isEmpty(expirationDateMonth) || !TextUtils.isEmpty(expirationDateYear)) {
            dataEntered = true;
        }

        if(!TextUtils.isEmpty(cvcNumber)){
            dataEntered = true;
        }

        return dataEntered;
    }

    boolean validateDataAndUpdateView(String creditCardNumber, String expirationDateMonth, String expirationDateYear, String cvcNumber) {
        boolean dataValid = true;
        if(!TextUtils.isEmpty(creditCardNumber) && TextUtils.isDigitsOnly(creditCardNumber)){
            signUpPaymentView.setCreditCardNumberValid();
        } else {
            dataValid = false;
            signUpPaymentView.setCreditCardNumberInvalid();
        }

        Integer month, year;

        try{
            month = Integer.parseInt(expirationDateMonth);
            year = Integer.parseInt(expirationDateYear);
        } catch (NumberFormatException e) {
            month = null;
            year = null;
        }

        Calendar cal = Calendar.getInstance();
        int currentMonth = 0, currentYear = 0;
        currentMonth = cal.get(Calendar.MONTH) + 1;
        currentYear = cal.get(Calendar.YEAR);

        if(year != null && month != null && ((year > currentYear) ||
                (month >= currentMonth && year == currentYear))){
            signUpPaymentView.setExpirationDateValid();
        } else {
            dataValid = false;
            signUpPaymentView.setExpirationDateInvalid();
        }

        if(!TextUtils.isEmpty(cvcNumber) && TextUtils.isDigitsOnly(cvcNumber) && cvcNumber.length() >= 3 && cvcNumber.length() <=4){
            signUpPaymentView.setCVCNumberValid();
        } else {
            dataValid = false;
            signUpPaymentView.setCVCNumberInvalid();
        }

        return dataValid;
    }

    public void saveCard(String creditCardNumber, String expirationDateMonth, String expirationDateYear, String cvcNumber)  {
        Integer month = null, year = null;

        try{
            month = Integer.parseInt(expirationDateMonth);
            year = Integer.parseInt(expirationDateYear);
        } catch (NumberFormatException e) {
            month = null;
            year = null;
        }

        Card card = new Card(creditCardNumber,
                month,
                year,
                cvcNumber);
        SharedPreferencesProvider.saveCard(signUpPaymentView.getContext(),
                card, Card.class);
    }

    public void createToken(String creditCardNumber, String expirationDateMonth, String expirationDateYear, String cvcNumber)  {
        if (signUpPaymentView == null) return;

        int month = Integer.parseInt(expirationDateMonth);
        int year = Integer.parseInt(expirationDateYear);

        Card card = new Card(creditCardNumber,
                month,
                year,
                cvcNumber);

        if ( !card.validateCard() ) {
            signUpPaymentView.closeProgressDialog();
            signUpPaymentView.setCreditCardNumberInvalid();
            signUpPaymentView.setExpirationDateInvalid();
            signUpPaymentView.setCVCNumberInvalid();
        } else {
            Stripe stripe;
            try {
                stripe = new Stripe(configuration.getStripeApiKey());
                stripe.createToken(
                        card,
                        new TokenCallback() {
                            public void onSuccess(Token token) {
                                if (TextUtils.isEmpty(user.getId())) {
                                    registerUser(token);
                                } else {
                                    saveToken(token);
                                }
                            }
                            public void onError(Exception error) {
                                signUpPaymentView.closeProgressDialog();
                                signUpPaymentView.showStripeError(error.getMessage());
                            }
                        }
                );
            } catch (AuthenticationException e) {
                signUpPaymentView.closeProgressDialog();
                signUpPaymentView.authenticationError();
            }

        }
    }

    public void viewCreated() {
        if (signUpPaymentView == null) return;

        if (registeration) {
            Card card = (Card) SharedPreferencesProvider.getCard(signUpPaymentView.getContext(), Card.class);
            if (card == null) return;
            signUpPaymentView.setValues(card.getNumber(),
                    card.getExpMonth() == null ? "" : card.getExpMonth() + "",
                    card.getExpYear() == null ? "" : card.getExpYear() + "",
                    card.getCVC());
        }
    }

    private void registerUser(final Token token) {
        if (signUpPaymentView == null ) return;
        UserWebServiceProvider.getInstance().registerUser(user,
                new UserWebServiceInterfaces.IOnUserRegisteredListener() {
                    @Override
                    public void onUserRegistered(User registeredUser) {
                        user = registeredUser;
                        if (token == null) {
                            saveAddress();
                        } else {
                            saveToken(token);
                        }
                    }

                    @Override
                    public void onUserRegistrationFailed(Exception error) {
                        if (error == null || signUpPaymentView == null) return;
                        signUpPaymentView.closeProgressDialog();
                        if (error instanceof ParseException) {
                            if (((ParseException) error).getCode() == 100) {
                                signUpPaymentView.showNoInternetConnectionDialog();
                                return;
                            }
                        }

                        signUpPaymentView.onRegistrationFailed(error.getMessage());
                    }
                }
        );
    }

    private void saveToken(Token token) {
        if (signUpPaymentView == null) return;
        UserWebServiceProvider.getInstance().saveCreditCard(token.getId(),
                new UserWebServiceInterfaces.IOnCreditCardSaveListener() {
                    @Override
                    public void onCreditCardSaved() {
                        if (registeration) {
                            saveAddress();
                            return;
                        }

                        updateUser();
                    }

                    @Override
                    public void onCreditCardFailedToSave(Exception error) {
                        if (error == null || signUpPaymentView == null) return;
                        signUpPaymentView.closeProgressDialog();
                        if (error instanceof ParseException) {
                            if (((ParseException) error).getCode() == 100) {
                                signUpPaymentView.showNoInternetConnectionDialog();
                                return;
                            }
                        }

                        signUpPaymentView.onRegistrationFailed(error.getMessage());
                    }
                });
    }

    private void updateUser() {
        if (signUpPaymentView == null) return;

        UserWebServiceProvider.getInstance().fetchUser(new UserWebServiceInterfaces.IOnUserFetchedListener() {
            @Override
            public void onFetchSuccess(User user) {
                updateUserToDatabase(user);
            }

            @Override
            public void onFetchFailed(Exception error) {
                if (error == null || signUpPaymentView == null) return;
                signUpPaymentView.closeProgressDialog();
                if (error instanceof ParseException) {
                    if (((ParseException) error).getCode() == 100) {
                        signUpPaymentView.showNoInternetConnectionDialog();
                        return;
                    }
                }

                signUpPaymentView.onRegistrationFailed(error.getMessage());
            }
        });
    }

    private void updateUserToDatabase(User user) {
        if (signUpPaymentView == null || user == null) return;

        Repository.getInstance(signUpPaymentView.getContext())
                .updateUser(user, new UpdateUserTask.IOnUpdateUserListener() {
                    @Override
                    public void onUserUpdated() {
                        if (signUpPaymentView == null) return;

                        signUpPaymentView.closeProgressDialog();
                        signUpPaymentView.onCreditCardAdded();
                    }

                    @Override
                    public void onUserFailedToUpdate() {
                        if (signUpPaymentView == null) return;

                        signUpPaymentView.closeProgressDialog();
                        signUpPaymentView.onCreditCardAdded();
                    }
                });
    }

    private void saveAddress() {
        if (signUpPaymentView == null ) return;
        Address address = SharedPreferencesProvider.getAddress(signUpPaymentView.getContext());
        AddressWebServiceProvider.getInstance().saveNewAddress(user,
                address,
                new AddressWebServiceInterfaces.IOnAddressSaveListener() {
                    @Override
                    public void onAddressSavedSuccessfully(Address address) {
                        logoutUser();
                    }

                    @Override
                    public void onAddressFailedToSave(Exception error) {
                        if (error == null || signUpPaymentView == null) return;
                        signUpPaymentView.closeProgressDialog();
                        if (error instanceof ParseException) {
                            if (((ParseException) error).getCode() == 100) {
                                signUpPaymentView.showNoInternetConnectionDialog();
                                return;
                            }
                        }

                        signUpPaymentView.onRegistrationFailed(error.getMessage());
                    }
                }
        );
    }

    private void logoutUser() {
        if (signUpPaymentView == null) return;
        UserWebServiceProvider.getInstance().logoutUser(new UserWebServiceInterfaces.IOnUserLoggedOutListener() {
            @Override
            public void onLoggedOut() {
                if (signUpPaymentView == null) return;
                signUpPaymentView.closeProgressDialog();
                signUpPaymentView.onRegistrationSuccess();
            }

            @Override
            public void onLogoutFailed(Exception error) {
                if (error == null || signUpPaymentView == null) return;
                signUpPaymentView.closeProgressDialog();
                if (error instanceof ParseException) {
                    if (((ParseException) error).getCode() == 100) {
                        signUpPaymentView.showNoInternetConnectionDialog();
                        return;
                    }
                }

                signUpPaymentView.onRegistrationFailed(error.getMessage());
            }
        });
    }
}
