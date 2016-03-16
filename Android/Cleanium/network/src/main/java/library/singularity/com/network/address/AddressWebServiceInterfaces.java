package library.singularity.com.network.address;

import com.parse.ParseException;

import java.util.List;

import library.singularity.com.data.model.Address;
import library.singularity.com.network.resolver.NetworkError;

public class AddressWebServiceInterfaces {

    public interface IOnAddressSaveListener {
        void onAddressSavedSuccessfully(Address address);
        void onAddressFailedToSave(Exception error);
    }

    public interface IOnAddressUpdateListener {
        void onAddressUpdatedSuccessfully(Address address);
        void onAddressFailedToUpdate(Exception error);
    }

    public interface IOnAddressDeleteListener {
        void onAddressDeletedSuccessfully(Address address);
        void onAddressFailedToDelete(Exception error);
    }

    public interface IOnAddressesObtainedListener {
        void onAddressesObtained(List<Address> addressList);
        void onAddressFailedToObtain(Exception error);
    }
}
