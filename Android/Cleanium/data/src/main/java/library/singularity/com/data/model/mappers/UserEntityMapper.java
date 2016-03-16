package library.singularity.com.data.model.mappers;

import com.google.gson.Gson;
import com.parse.ParseUser;

import library.singularity.com.data.model.User;
import library.singularity.com.data.utils.Constants;

public class UserEntityMapper {

    public static User getUser(ParseUser parseUser) {
        if (parseUser == null) return null;

        User user = new User();
        user.setId(parseUser.getObjectId());
        user.setUsername(parseUser.getUsername());
        user.setEmailVerified(parseUser.getBoolean(Constants.PARSE_USER_EMAIL_VERIFIED));
        user.setEmail(parseUser.getEmail());
        user.setImagePath(parseUser.getParseFile(Constants.PARSE_USER_IMAGE) != null ?
                parseUser.getParseFile(Constants.PARSE_USER_IMAGE).getUrl() : null);
        user.setIsStripeClient(parseUser.getBoolean(Constants.PARSE_USER_IS_STRIPE_CLIENT));
        user.setFirstName(parseUser.getString(Constants.PARSE_USER_FIRST_NAME));
        user.setLastName(parseUser.getString(Constants.PARSE_USER_LAST_NAME));
        user.setPhoneNumber(parseUser.getLong(Constants.PARSE_USER_PHONE));

        return user;
    }

    public static ParseUser getParseUser(User user) {
        if (user == null) return null;

        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(user.getUsername());
        parseUser.setEmail(user.getEmail());
        parseUser.setPassword(user.getPassword());
        parseUser.put(Constants.PARSE_USER_IS_STRIPE_CLIENT, user.isStripeClient());
        parseUser.put(Constants.PARSE_USER_FIRST_NAME, user.getFirstName());
        parseUser.put(Constants.PARSE_USER_LAST_NAME, user.getLastName());
        parseUser.put(Constants.PARSE_USER_PHONE, user.getPhoneNumber());

        return parseUser;
    }

    public static ParseUser updateParseUser(User user, ParseUser parseUser) {
        if (parseUser == null && user == null) return null;

        if (parseUser == null) {
            parseUser = new ParseUser();
        }

        parseUser.setUsername(user.getUsername());

        // we can't see password from Parse so its null after login but it can be changed
        if (user.getPassword() != null) {
            parseUser.setPassword(user.getPassword());
        }

        parseUser.setEmail(user.getEmail());
        parseUser.put(Constants.PARSE_USER_IS_STRIPE_CLIENT, user.isStripeClient());
        parseUser.put(Constants.PARSE_USER_FIRST_NAME, user.getFirstName());
        parseUser.put(Constants.PARSE_USER_LAST_NAME, user.getLastName());
        parseUser.put(Constants.PARSE_USER_PHONE, user.getPhoneNumber());

        return parseUser;
    }

    public static String getUserAsJson(User user) {
        Gson gson = new Gson();
        return gson.toJson(user, User.class);
    }

    public static User getUserFromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, User.class);
    }
}
