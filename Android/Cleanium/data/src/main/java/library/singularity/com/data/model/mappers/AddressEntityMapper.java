package library.singularity.com.data.model.mappers;

import com.google.gson.Gson;
import com.parse.ParseConfig;
import com.parse.ParseObject;
import com.parse.ParseUser;

import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.User;
import library.singularity.com.data.utils.Constants;

public class AddressEntityMapper {

    public static Address convertToAddress(ParseObject parseAddress) {
        if (parseAddress == null) return null;
        
        Address address = new Address();
        address.setId(parseAddress.getObjectId());
        address.setStreet(parseAddress.getString(Constants.PARSE_ADDRESS_STREET));
        address.setPostalCode(parseAddress.getInt(Constants.PARSE_ADDRESS_POSTAL_CODE));
        address.setSuburb(parseAddress.getString(Constants.PARSE_ADDRESS_SUBURB));
        address.setNumber(parseAddress.getString(Constants.PARSE_ADDRESS_NUMBER));
        address.setStreetTwo(parseAddress.getString(Constants.PARSE_ADDRESS_STREET_TWO));
        address.setNotes(parseAddress.getString(Constants.PARSE_ADDRESS_NOTES));
        address.setLocation(parseAddress.getString(Constants.PARSE_ADDRESS_LOCATION));
        address.setState(parseAddress.getString(Constants.PARSE_ADDRESS_STATE));
        address.setCity(parseAddress.getString(Constants.PARSE_ADDRESS_CITY));

        return address;
    }

    public static ParseObject convertToParseObject(Address address, User user) {
        if (address == null) return null;

        ParseObject addressParseObject = new ParseObject(Constants.PARSE_ADDRESS_TABLE_NAME);

        if (address.getId() != null) {
            addressParseObject.setObjectId(address.getId());
        }

        addressParseObject.put(Constants.PARSE_ADDRESS_STREET, address.getStreet());
        addressParseObject.put(Constants.PARSE_ADDRESS_POSTAL_CODE, address.getPostalCode());
        if (address.getSuburb() != null) {
            addressParseObject.put(Constants.PARSE_ADDRESS_SUBURB, address.getSuburb());
        }

        if (address.getNumber() != null) {
            addressParseObject.put(Constants.PARSE_ADDRESS_NUMBER, address.getNumber());
        }

        if (address.getStreetTwo() != null) {
            addressParseObject.put(Constants.PARSE_ADDRESS_STREET_TWO, address.getStreetTwo());
        }

        if (address.getNotes() != null) {
            addressParseObject.put(Constants.PARSE_ADDRESS_NOTES, address.getNotes());
        }
        
        addressParseObject.put(Constants.PARSE_ADDRESS_LOCATION, address.getLocation());
        addressParseObject.put(Constants.PARSE_ADDRESS_STATE, address.getState());
        addressParseObject.put(Constants.PARSE_ADDRESS_CITY, address.getCity());

        if (user != null) {
            ParseUser parseUser = new ParseUser();
            parseUser.setObjectId(user.getId());
            addressParseObject.put(Constants.PARSE_ADDRESS_USER, parseUser);
        }

        return addressParseObject;
    }

    public static String getAddressAsJson(Address address) {
        Gson gson = new Gson();
        return gson.toJson(address, Address.class);
    }

    public static Address getAddressFromJson(String address) {
        Gson gson = new Gson();
        return gson.fromJson(address, Address.class);
    }
}
