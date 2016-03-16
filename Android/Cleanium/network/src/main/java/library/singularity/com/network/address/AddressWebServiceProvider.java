package library.singularity.com.network.address;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.User;
import library.singularity.com.data.model.mappers.AddressEntityMapper;
import library.singularity.com.data.utils.Constants;
import library.singularity.com.network.address.AddressWebServiceInterfaces.*;
import library.singularity.com.network.resolver.ErrorCodeResolver;
import library.singularity.com.network.resolver.NetworkError;

public class AddressWebServiceProvider {

    private static final String TABLE_NAME = "Address";

    public static AddressWebServiceProvider instance;

    public static AddressWebServiceProvider getInstance() {
        if (instance == null) {
            instance = new AddressWebServiceProvider();
        }

        return instance;
    }

    public void saveNewAddress(User user, final Address address, final IOnAddressSaveListener listener) {
        if (user == null) {
            if (listener == null) return;
            listener.onAddressFailedToSave(new NullPointerException());
            return;
        }

        if (address == null) {
            if (listener == null) return;
            listener.onAddressFailedToSave(new NullPointerException());
            return;
        }

        final ParseObject parseAddress = AddressEntityMapper.convertToParseObject(address, user);
        if (parseAddress == null) {
            if (listener == null) return;
            listener.onAddressFailedToSave(new NullPointerException());
            return;
        }

        parseAddress.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (listener == null) return;
                    listener.onAddressFailedToSave(ErrorCodeResolver.resolveError(e));
                    return;
                }

                address.setId(parseAddress.getObjectId());
                if (listener == null) return;
                listener.onAddressSavedSuccessfully(address);
            }
        });
    }

    public void updateAddress(final Address address, final IOnAddressUpdateListener listener) {
        if (address == null) {
            if (listener == null) return;
            listener.onAddressFailedToUpdate(new NullPointerException());
            return;
        }

        final ParseObject parseAddress = AddressEntityMapper.convertToParseObject(address, null);
        if (parseAddress == null) {
            if (listener == null) return;
            listener.onAddressFailedToUpdate(new NullPointerException());
            return;
        }

        parseAddress.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (listener == null) return;
                    listener.onAddressFailedToUpdate(ErrorCodeResolver.resolveError(e));
                    return;
                }

                address.setId(parseAddress.getObjectId());
                if (listener == null) return;
                listener.onAddressUpdatedSuccessfully(address);
            }
        });
    }

    public void deleteAddress(final Address address, final IOnAddressDeleteListener listener) {
        if (address == null) {
            if (listener == null) return;
            listener.onAddressFailedToDelete(new NullPointerException());
            return;
        }

        final ParseObject parseAddress = AddressEntityMapper.convertToParseObject(address, null);
        if (parseAddress == null) {
            if (listener == null) return;
            listener.onAddressFailedToDelete(new NullPointerException());
            return;
        }

        parseAddress.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    if (listener == null) return;
                    listener.onAddressFailedToDelete(ErrorCodeResolver.resolveError(e));
                    return;
                }

                if (listener == null) return;
                listener.onAddressDeletedSuccessfully(address);
            }
        });
    }

    public static void getAllAddresses(final IOnAddressesObtainedListener listener) {
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            if (listener == null) return;
            listener.onAddressFailedToObtain(new NullPointerException());
            return;
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.PARSE_ADDRESS_TABLE_NAME);
        query.whereEqualTo(Constants.PARSE_ADDRESS_USER, user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null) {
                    if (listener == null) return;
                    listener.onAddressFailedToObtain(ErrorCodeResolver.resolveError(e));
                    return;
                }

                List<Address> addresses = new ArrayList<>();
                for(ParseObject parseAddress : list) {
                    Address address = AddressEntityMapper.convertToAddress(parseAddress);
                    if (address != null) {
                        addresses.add(address);
                    }
                }

                if (listener == null) return;
                listener.onAddressesObtained(addresses);
            }
        });
    }
}
