package library.singularity.com.dao.database.mapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;

import library.singularity.com.dao.database.DatabaseMetaData;
import library.singularity.com.data.model.User;

public class UserDaoMapper {

    public static HashMap<String, Integer> NAME_INDEX_MAP = new HashMap<>();

    public static void mapCursor(Cursor cursor) {
        int n = cursor.getColumnCount();
        for (int i = 0; i < n; i++) {
            NAME_INDEX_MAP.put(cursor.getColumnName(i), i);
        }
    }

    public static User convertCursorToUser(Cursor cursor) {
        User user = new User();
        Integer columnIndex = getColumnIndex(DatabaseMetaData.UserTableMetaData.ID);
        if (columnIndex != null) {
            user.setId(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.UserTableMetaData.USERNAME);
        if (columnIndex != null) {
            user.setUsername(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.UserTableMetaData.PASSWORD);
        if (columnIndex != null) {
            user.setPassword(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.UserTableMetaData.EMAIL_VERIFIED);
        if (columnIndex != null) {
            user.setEmailVerified(cursor.getInt(columnIndex) == 1);
        }

        columnIndex = getColumnIndex(DatabaseMetaData.UserTableMetaData.EMAIL);
        if (columnIndex != null) {
            user.setEmail(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.UserTableMetaData.IMAGE_PATH);
        if (columnIndex != null) {
            user.setImagePath(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.UserTableMetaData.IS_STRIPE_CLIENT);
        if (columnIndex != null) {
            user.setIsStripeClient(cursor.getInt(columnIndex) == 1);
        }

        columnIndex = getColumnIndex(DatabaseMetaData.UserTableMetaData.FIRST_NAME);
        if (columnIndex != null) {
            user.setFirstName(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.UserTableMetaData.LAST_NAME);
        if (columnIndex != null) {
            user.setLastName(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.UserTableMetaData.PHONE_NUMBER);
        if (columnIndex != null) {
            user.setPhoneNumber(cursor.getLong(columnIndex));
        }

        return user;
    }

    public static Integer getColumnIndex(String columnName) {
        return NAME_INDEX_MAP.get(columnName);
    }

    public static ContentValues getUsersContentValues(User user) {
        ContentValues values = new ContentValues();

        values.put(DatabaseMetaData.UserTableMetaData.ID, user.getId());
        values.put(DatabaseMetaData.UserTableMetaData.USERNAME, user.getUsername());
        values.put(DatabaseMetaData.UserTableMetaData.PASSWORD, user.getPassword());
        values.put(DatabaseMetaData.UserTableMetaData.EMAIL_VERIFIED, user.isEmailVerified() ? 1 : 0);
        values.put(DatabaseMetaData.UserTableMetaData.EMAIL, user.getEmail());
        values.put(DatabaseMetaData.UserTableMetaData.IMAGE_PATH, user.getImagePath());
        values.put(DatabaseMetaData.UserTableMetaData.IS_STRIPE_CLIENT, user.isStripeClient() ? 1 : 0);
        values.put(DatabaseMetaData.UserTableMetaData.FIRST_NAME, user.getFirstName());
        values.put(DatabaseMetaData.UserTableMetaData.LAST_NAME, user.getLastName());
        values.put(DatabaseMetaData.UserTableMetaData.PHONE_NUMBER, user.getPhoneNumber());

        return values;
    }
}
