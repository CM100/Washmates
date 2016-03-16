package library.singularity.com.network.user;

import com.parse.Parse;
import com.parse.ParseException;

import library.singularity.com.data.model.User;
import library.singularity.com.network.resolver.NetworkError;

public class UserWebServiceInterfaces {
    public interface IOnUserRegisteredListener {
        void onUserRegistered(User user);
        void onUserRegistrationFailed(Exception error);
    }

    public interface IOnUserLogInListener {
        void onUserLoggedIn(User user);
        void onUserFailedToLogin(Exception error);
    }

    public interface IOnUserForgotPasswordListener {
        void onPasswordResetSuccessfully();
        void onPasswordResetFailed(Exception error);
    }

    public interface IOnUserUpdateListener {
        void onUserUpdatedSuccessfully();
        void onUserFailedToUpdate(Exception error);
    }

    public interface IOnUserLoggedOutListener {
        void onLoggedOut();
        void onLogoutFailed(Exception error);
    }

    public interface IOnEmailQueryListener {
        void onEmailQueryFailed(Exception error);
        void onEmailQuerySuccess(int numberOfUsers);
    }

    public interface IOnCreditCardSaveListener {
        void onCreditCardSaved();
        void onCreditCardFailedToSave(Exception error);
    }

    public interface IOnUserFetchedListener {
        void onFetchSuccess(User user);
        void onFetchFailed(Exception e);
    }
}
