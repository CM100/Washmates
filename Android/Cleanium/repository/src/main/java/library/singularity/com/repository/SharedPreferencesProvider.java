package library.singularity.com.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.Configuration;
import library.singularity.com.data.model.User;
import library.singularity.com.data.model.mappers.AddressEntityMapper;
import library.singularity.com.data.model.mappers.ConfigurationEntityMapper;
import library.singularity.com.data.model.mappers.UserEntityMapper;

public class SharedPreferencesProvider {

    private static final String SHARED_PREFERENCES_FILENAME = "cleanium_shared-prefs";
    private static final String TUTORIAL_SEEN_KEY = "TUTORIAL_SEEN_KEY";
    private static final String USER_KEY = "USER_KEY";
    private static final String ADDRESS_KEY = "ADDRESS_KEY";
    private static final String CARD_KEY = "CARD_KEY";
    private static final String CONFIGURATION_KEY = "CONFIGURATION_KEY";

    public static void saveTutorialSeen(Context context) {
        if (context == null || context.getApplicationContext() == null) return;

        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_FILENAME, Context.MODE_PRIVATE);
        if (preferences == null) return;

        SharedPreferences.Editor editor = preferences.edit();
        if (editor == null) return;

        editor.putBoolean(TUTORIAL_SEEN_KEY, true);
        editor.apply();
    }

    public static boolean isTutorialSeen(Context context) {
        if (context == null || context.getApplicationContext() == null) return false;

        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_FILENAME, Context.MODE_PRIVATE);
        return preferences != null && preferences.getBoolean(TUTORIAL_SEEN_KEY, false);
    }

    public static void saveUser(Context context, User user) {
        if (context == null) return;

        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES_FILENAME, Context.MODE_PRIVATE);
        if (preferences == null) return;

        SharedPreferences.Editor editor = preferences.edit();
        if (editor == null) return;


        editor.putString(USER_KEY, UserEntityMapper.getUserAsJson(user));
        editor.apply();
    }

    public static User getUser(Context context) {
        if (context == null) return null;

        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES_FILENAME, Context.MODE_PRIVATE);
        if (preferences == null) return null;

        String userAsJson = preferences.getString(USER_KEY, null);
        if (userAsJson == null) return null;

        return UserEntityMapper.getUserFromJson(userAsJson);
    }

    public static void saveAddress(Context context, Address address) {
        if (context == null) return;

        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES_FILENAME, Context.MODE_PRIVATE);
        if (preferences == null) return;

        SharedPreferences.Editor editor = preferences.edit();
        if (editor == null) return;


        editor.putString(ADDRESS_KEY, AddressEntityMapper.getAddressAsJson(address));
        editor.apply();
    }
    public static void saveConfiguration(Context context,  Configuration configuration){
        if(context == null) return;

        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_FILENAME,Context.MODE_PRIVATE);
        if(preferences == null) return;

        SharedPreferences.Editor editor = preferences.edit();
        if(editor == null) return;

        editor.putString(CONFIGURATION_KEY, ConfigurationEntityMapper.getConfigurationAsJSON(configuration));
        editor.apply();
    }

    public static Address getAddress(Context context) {
        if (context == null) return null;

        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES_FILENAME, Context.MODE_PRIVATE);
        if (preferences == null) return null;

        String addressJson = preferences.getString(ADDRESS_KEY, null);
        if (addressJson == null) return null;

        return AddressEntityMapper.getAddressFromJson(addressJson);
    }

    public static Configuration getConfiguration(Context context){
        if(context == null) return null;

        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES_FILENAME, Context.MODE_PRIVATE);
        if (preferences == null) return null;

        String configurationJson = preferences.getString(CONFIGURATION_KEY, null);
        if (configurationJson == null) return null;

        return ConfigurationEntityMapper.getConfigurationFromJSON(configurationJson);

    }

    public static void saveCard(Context context, Object card, Class cardClass) {
        if (context == null) return;

        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES_FILENAME, Context.MODE_PRIVATE);
        if (preferences == null) return;

        SharedPreferences.Editor editor = preferences.edit();
        if (editor == null) return;

        Gson gson = new Gson();
        editor.putString(CARD_KEY, gson.toJson(card, cardClass));
        editor.apply();
    }

    public static void resetCard(Context context) {
        if (context == null) return;

        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES_FILENAME, Context.MODE_PRIVATE);
        if (preferences == null) return;

        SharedPreferences.Editor editor = preferences.edit();
        if (editor == null) return;

        editor.putString(CARD_KEY, null);
        editor.apply();
    }

    public static Object getCard(Context context, Class cardClass) {
        if (context == null) return null;

        SharedPreferences preferences = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES_FILENAME, Context.MODE_PRIVATE);
        if (preferences == null) return null;

        String cardJson = preferences.getString(CARD_KEY, null);
        if (cardJson == null) return null;

        Gson gson = new Gson();
        return gson.fromJson(cardJson, cardClass);
    }
}
