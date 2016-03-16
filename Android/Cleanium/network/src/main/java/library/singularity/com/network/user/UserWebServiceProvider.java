package library.singularity.com.network.user;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.List;

import library.singularity.com.data.model.User;
import library.singularity.com.data.model.mappers.UserEntityMapper;
import library.singularity.com.data.utils.Constants;
import library.singularity.com.network.resolver.ErrorCodeResolver;
import library.singularity.com.network.resolver.NetworkError;
import library.singularity.com.network.user.UserWebServiceInterfaces.*;

public class UserWebServiceProvider {

    public static UserWebServiceProvider instance;

    public static UserWebServiceProvider getInstance() {
        if (instance == null) {
            instance = new UserWebServiceProvider();
        }

        return instance;
    }

    public void registerUser(User user, final IOnUserRegisteredListener listener) {
        if (user == null) {
            if (listener == null) return;
            listener.onUserRegistrationFailed(new NullPointerException(""));
            return;
        }

        ParseUser parseUser = UserEntityMapper.getParseUser(user);
        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException error) {
                if (error == null) {
                    if (listener == null) return;;

                    listener.onUserRegistered(getCurrentUser());
                    return;
                }

                if (listener == null) return;
                listener.onUserRegistrationFailed(ErrorCodeResolver.resolveError(error));
            }
        });
    }

    public User getCurrentUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        User user = UserEntityMapper.getUser(currentUser);
        return user;
    }

    public void loginUser(String username, String password, final IOnUserLogInListener listener) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser == null) {
                    if (listener == null) return;
                    listener.onUserFailedToLogin(ErrorCodeResolver.resolveError(e));
                    return;
                }

                User user = UserEntityMapper.getUser(parseUser);
                if (listener == null) return;
                listener.onUserLoggedIn(user);
            }
        });
    }

    public void checkIfEmailIsFree(String email, final IOnEmailQueryListener listener) {
        if (email == null) {
            if (listener == null) return;
            listener.onEmailQueryFailed(new NullPointerException());
            return;
        }

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(Constants.PARSE_USER_EMAIL, email);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e != null) {
                    if (listener == null) return;
                    listener.onEmailQueryFailed(ErrorCodeResolver.resolveError(e));
                    return;
                }

                if (listener == null) return;
                listener.onEmailQuerySuccess(list.size());
            }
        });
    }

    public void saveCreditCard(String token, final IOnCreditCardSaveListener listener) {
        if (token == null) {
            if (listener == null) return;
            listener.onCreditCardFailedToSave(new NullPointerException());
            return;
        }

        HashMap<String, Object> parameters = null;
        parameters = new HashMap<>();
        parameters.put(Constants.PARSE_CLOUD_TOKEN, token);

        ParseCloud.callFunctionInBackground(Constants.PARSE_CLOUD_SAVE_TOKEN_FUNCTION, parameters, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                if (e != null) {
                    if (listener == null) return;
                    listener.onCreditCardFailedToSave(ErrorCodeResolver.resolveError(e));
                    return;
                }

                if (listener == null) return;
                listener.onCreditCardSaved();
            }
        });
    }

    public void updateUser(User user, final IOnUserUpdateListener listener) {
        if (user == null) {
            if (listener == null) return;
            listener.onUserFailedToUpdate(new NullPointerException(""));
            return;
        }

        ParseUser parseUser = UserEntityMapper.updateParseUser(user, ParseUser.getCurrentUser());
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (listener == null) return;
                    listener.onUserFailedToUpdate(ErrorCodeResolver.resolveError(e));
                    return;
                }

                if (listener == null) return;
                listener.onUserUpdatedSuccessfully();
            }
        });
    }

    public void resetPassword(String email, final IOnUserForgotPasswordListener listener) {
        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    if (listener == null) return;
                    listener.onPasswordResetFailed(ErrorCodeResolver.resolveError(e));
                    return;
                }

                if (listener == null) return;
                listener.onPasswordResetSuccessfully();
            }
        });
    }

    public void logoutUser(final IOnUserLoggedOutListener listener) {
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            user.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        if (listener == null) return;
                        listener.onLogoutFailed(ErrorCodeResolver.resolveError(e));
                        return;
                    }

                    if (listener == null) return;
                    listener.onLoggedOut();
                }
            });
        }
    }

    public void fetchUser(final IOnUserFetchedListener listener) {
        ParseUser user = ParseUser.getCurrentUser();
        user.fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null) {
                    if (listener == null) return;
                    listener.onFetchFailed(ErrorCodeResolver.resolveError(e));
                    return;
                }

                if (listener == null) return;
                listener.onFetchSuccess(UserEntityMapper.getUser(parseUser));
            }
        });
    }

}
