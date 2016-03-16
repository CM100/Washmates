package library.singularity.com.presenter.interfaces;

import android.content.Context;

import library.singularity.com.data.model.Address;

public interface AddressesListView {
    void showProgressDialog();
    Context getContext();
    void showHomeAddressHeader(Address address);
    void hideHomeAddressHeader();
    void showWorkAddressHeader(Address address);
    void hideWorkAddressHeader();
    void showOtherAddressHeader(Address address);
    void hideOtherAddressHeader();
    void stopProgressDialog();
    void hideAddNewAddressButton();
    void unhideAddNewAddressButton();
}
