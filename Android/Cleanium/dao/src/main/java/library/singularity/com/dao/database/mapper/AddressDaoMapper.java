package library.singularity.com.dao.database.mapper;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;

import library.singularity.com.dao.database.DatabaseMetaData;
import library.singularity.com.data.model.Address;

public class AddressDaoMapper {
    public static HashMap<String, Integer> NAME_INDEX_MAP = new HashMap<>();

    public static void mapCursor(Cursor cursor) {
        int n = cursor.getColumnCount();
        for (int i = 0; i < n; i++) {
            NAME_INDEX_MAP.put(cursor.getColumnName(i), i);
        }
    }

    public static Address convertCursorToAddress(Cursor cursor) {
        Address address = new Address();
        Integer columnIndex = getColumnIndex(DatabaseMetaData.AddressTableMetaData.ID);
        if (columnIndex != null) {
            address.setId(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.AddressTableMetaData.STREET);
        if (columnIndex != null) {
            address.setStreet(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.AddressTableMetaData.POSTAL_CODE);
        if (columnIndex != null) {
            address.setPostalCode(cursor.getInt(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.AddressTableMetaData.SUBURB);
        if (columnIndex != null) {
            address.setSuburb(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.AddressTableMetaData.NUMBER);
        if (columnIndex != null) {
            address.setNumber(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.AddressTableMetaData.STREET_TWO);
        if (columnIndex != null) {
            address.setStreetTwo(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.AddressTableMetaData.NOTES);
        if (columnIndex != null) {
            address.setNotes(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.AddressTableMetaData.LOCATION);
        if (columnIndex != null) {
            address.setLocation(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.AddressTableMetaData.STATE);
        if (columnIndex != null) {
            address.setState(cursor.getString(columnIndex));
        }

        columnIndex = getColumnIndex(DatabaseMetaData.AddressTableMetaData.CITY);
        if (columnIndex != null) {
            address.setCity(cursor.getString(columnIndex));
        }

        return address;
    }

    public static Integer getColumnIndex(String columnName) {
        return NAME_INDEX_MAP.get(columnName);
    }

    public static ContentValues getAddressFormItems(Address address) {
        ContentValues values = new ContentValues();

        values.put(DatabaseMetaData.AddressTableMetaData.ID, address.getId());
        values.put(DatabaseMetaData.AddressTableMetaData.STREET, address.getStreet());
        values.put(DatabaseMetaData.AddressTableMetaData.POSTAL_CODE, address.getPostalCode());
        values.put(DatabaseMetaData.AddressTableMetaData.SUBURB, address.getSuburb());
        values.put(DatabaseMetaData.AddressTableMetaData.NUMBER, address.getNumber());
        values.put(DatabaseMetaData.AddressTableMetaData.STREET_TWO, address.getStreetTwo());
        values.put(DatabaseMetaData.AddressTableMetaData.NOTES, address.getNotes());
        values.put(DatabaseMetaData.AddressTableMetaData.LOCATION, address.getLocation());
        values.put(DatabaseMetaData.AddressTableMetaData.STATE, address.getState());
        values.put(DatabaseMetaData.AddressTableMetaData.CITY, address.getCity());

        return values;
    }
}
