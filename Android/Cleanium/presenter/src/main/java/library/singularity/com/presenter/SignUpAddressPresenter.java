package library.singularity.com.presenter;

import android.text.TextUtils;

import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.Configuration;
import library.singularity.com.data.model.User;
import library.singularity.com.network.address.AddressWebServiceInterfaces;
import library.singularity.com.network.address.AddressWebServiceProvider;
import library.singularity.com.presenter.interfaces.SignUpAddressView;
import library.singularity.com.repository.Repository;
import library.singularity.com.repository.SharedPreferencesProvider;
import library.singularity.com.repository.async.AddAddressTask;
import library.singularity.com.repository.async.DeleteAddressTask;
import library.singularity.com.repository.async.GetUserTask;
import library.singularity.com.repository.async.UpdateAddressTask;

public class SignUpAddressPresenter  {

    private SignUpAddressView signUpAddressView;
    private Configuration configuration;
    private Address address;
    private boolean loggedInUser;
    private User user;

    public SignUpAddressPresenter(SignUpAddressView signUpAddressView, Address address, boolean loggedInUser){
        this.signUpAddressView = signUpAddressView;
        this.address = address;
        this.loggedInUser = loggedInUser;
        configuration = SharedPreferencesProvider.getConfiguration(signUpAddressView.getContext());
    }

    public void finish(){
        signUpAddressView = null;
    }

    public void onAddressDeleteClicked() {
        if (signUpAddressView == null) return;

        signUpAddressView.showProgressDialog();
        AddressWebServiceProvider.getInstance().deleteAddress(address, new AddressWebServiceInterfaces.IOnAddressDeleteListener() {
            @Override
            public void onAddressDeletedSuccessfully(Address address) {
                deleteAddressLocally();
            }

            @Override
            public void onAddressFailedToDelete(Exception error) {
                if (signUpAddressView == null || error == null) return;
                signUpAddressView.stopProgressDialog();
                if (error instanceof com.parse.ParseException &&
                        ((com.parse.ParseException) error).getCode() == 100) {
                    signUpAddressView.showNoInternetConnectionDialog();
                    return;
                }

                signUpAddressView.showError(error.getMessage());
            }
        });
    }

    void deleteAddressLocally() {
        if (signUpAddressView == null) return;

        Repository.getInstance(signUpAddressView.getContext()).deleteAddress(address, new DeleteAddressTask.IOnDeleteAddressListener() {
            @Override
            public void onAddressDeleted() {
                if (signUpAddressView == null) return;

                signUpAddressView.stopProgressDialog();
                signUpAddressView.showAddressUpdatedSuccessfully();
            }

            @Override
            public void onAddressesFailedToDelete() {
                if (signUpAddressView == null) return;

                signUpAddressView.stopProgressDialog();
            }
        });
    }

    public void onNextClick(String addressLine, String addressLine2, String city, String state, String postCode, String location, String accessNote){
        if (signUpAddressView == null) return;

        getAddress(addressLine, addressLine2, city, state, postCode, location, accessNote);

        if (TextUtils.isEmpty(address.getId()) && !loggedInUser) {
            SharedPreferencesProvider.saveAddress(signUpAddressView.getContext(), address);
        }

        boolean dataValid = true;
        if(address.isStreetValid()){
            signUpAddressView.setAddressLine1Valid();
        } else {
            signUpAddressView.setAddressLine1Invalid();
            dataValid = false;
        }
        if(address.isCityValid()){
            signUpAddressView.setCityValid();
        } else {
            signUpAddressView.setCityInvalid();
            dataValid = false;
        }

        if(address.isStateValid()){
            signUpAddressView.setStateValid();
        } else {
            signUpAddressView.setStateInvalid();
            dataValid = false;
        }

        if(address.isPostalCodeValid(configuration.getPostCodes())){
            signUpAddressView.setPostCodeValid();
        } else {
            signUpAddressView.setPostCodeInvalid();
            dataValid = false;
        }

        if(address.isAddressLocationValid() && !address.getLocation().equals("Location")){
            signUpAddressView.setLocationValid();
        } else {
            signUpAddressView.setLocationInvalid();
            dataValid = false;
        }


        if(dataValid && TextUtils.isEmpty(address.getId()) && !loggedInUser) {
            signUpAddressView.openSignUpPaymentFragment();
        } else if (dataValid && !TextUtils.isEmpty(address.getId())) {
            updateAddressOnline();
        } else if (dataValid && loggedInUser) {
            createNewAddress();
        }
    }

    void updateAddressOnline() {
        if (signUpAddressView == null) return;

        signUpAddressView.showProgressDialog();
        AddressWebServiceProvider.getInstance().updateAddress(address, new AddressWebServiceInterfaces.IOnAddressUpdateListener() {
            @Override
            public void onAddressUpdatedSuccessfully(Address address) {
                updateAddressInDb();
                SignUpAddressPresenter.this.address = address;
            }

            @Override
            public void onAddressFailedToUpdate(Exception error) {
                if (signUpAddressView == null || error == null) return;
                signUpAddressView.stopProgressDialog();
                if (error instanceof com.parse.ParseException &&
                        ((com.parse.ParseException) error).getCode() == 100) {
                    signUpAddressView.showNoInternetConnectionDialog();
                    return;
                }

                signUpAddressView.showError(error.getMessage());
            }
        });
    }

    void createNewAddress() {
        if (signUpAddressView == null) return;

        signUpAddressView.showProgressDialog();
        AddressWebServiceProvider.getInstance().saveNewAddress(user, address, new AddressWebServiceInterfaces.IOnAddressSaveListener() {
            @Override
            public void onAddressSavedSuccessfully(Address address) {
                saveAddressToDb(address);
                SignUpAddressPresenter.this.address = address;
            }

            @Override
            public void onAddressFailedToSave(Exception error) {
                if (signUpAddressView == null || error == null) return;
                signUpAddressView.stopProgressDialog();
                if (error instanceof com.parse.ParseException &&
                        ((com.parse.ParseException) error).getCode() == 100) {
                    signUpAddressView.showNoInternetConnectionDialog();
                    return;
                }

                signUpAddressView.showError(error.getMessage());
            }
        });
    }

    void saveAddressToDb(Address address) {
        if (signUpAddressView == null) return;

        Repository.getInstance(signUpAddressView.getContext()).addAddress(address, new AddAddressTask.IOnAddAddressListener() {
            @Override
            public void onAddressAdded() {
                if (signUpAddressView == null) return;
                signUpAddressView.stopProgressDialog();
                signUpAddressView.showAddressAddedSuccessfully();
            }

            @Override
            public void onAddressFailedToAdd() {
                if (signUpAddressView == null) return;
                signUpAddressView.stopProgressDialog();
            }
        });
    }

    void updateAddressInDb() {
        if (signUpAddressView == null) return;

        Repository.getInstance(signUpAddressView.getContext()).updateAddress(address,
                new UpdateAddressTask.IOnAddressUpdateListener() {
                    @Override
                    public void onAddressUpdated() {
                        if (signUpAddressView == null) return;

                        signUpAddressView.stopProgressDialog();
                        signUpAddressView.showAddressUpdatedSuccessfully();
                    }

                    @Override
                    public void onAddressFailedToUpdate() {
                        if (signUpAddressView == null) return;

                        signUpAddressView.stopProgressDialog();
                    }
                });
    }

    public void saveAddress(String addressLine, String addressLine2, String city, String state, String postCode, String location, String accessNote){
        if (!TextUtils.isEmpty(address.getId()) || loggedInUser) return;

        getAddress(addressLine, addressLine2, city, state, postCode, location, accessNote);
        SharedPreferencesProvider.saveAddress(signUpAddressView.getContext(), address);
    }

    private void getAddress(String addressLine, String addressLine2, String city, String state, String postCode, String location, String accessNote){
        if (address == null) {
            address = new Address();
        }

        address.setStreet(addressLine);
        address.setStreetTwo(addressLine2);
        address.setCity(city);
        address.setState(state);
        try{
            address.setPostalCode(Integer.parseInt(postCode));
        } catch (NumberFormatException e){
            address.setPostalCode(0);
        }
        address.setLocation(location);
        address.setNotes(accessNote);
    }

    public void onViewCreated() {
        if (signUpAddressView == null) return;
        if (address == null && !loggedInUser) {
            address = SharedPreferencesProvider.getAddress(signUpAddressView.getContext());
            setValues();
            return;
        }

        signUpAddressView.showProgressDialog();
        Repository.getInstance(signUpAddressView.getContext()).getCurrentUser(new GetUserTask.IOnGetUserListener() {
            @Override
            public void onUserObtained(User user) {
                if (signUpAddressView == null) return;

                SignUpAddressPresenter.this.user = user;
                setValues();
                signUpAddressView.stopProgressDialog();
            }

            @Override
            public void onUserFailedToObtain() {
                if (signUpAddressView == null) return;

                signUpAddressView.stopProgressDialog();
            }
        });
    }

    void setValues() {
        if (address == null) return;

        signUpAddressView.setValues(address.getStreet(),
                address.getStreetTwo(),
                address.getCity(),
                address.getState(),
                address.getPostalCode() == 0 ? "" : address.getPostalCode()+"",
                address.getNotes(),
                address.getLocation());
    }
}
