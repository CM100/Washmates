package library.singularity.com.presenter;

import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.User;
import library.singularity.com.presenter.interfaces.AddressesListView;
import library.singularity.com.repository.Repository;
import library.singularity.com.repository.async.GetUserTask;

public class AddressesListPresenter implements GetUserTask.IOnGetUserListener {

    AddressesListView view;
    User user;
    String home, work, other;

    public AddressesListPresenter(AddressesListView view, String home, String work, String other) {
        this.view = view;
        this.home = home;
        this.work = work;
        this.other = other;
    }

    public void finish() {
        view = null;
    }

    public void onScreenCreated() {
        if (view == null) return;

        view.showProgressDialog();
        Repository.getInstance(view.getContext()).getCurrentUser(this);
    }

    @Override
    public void onUserObtained(User user) {
        if (view == null || user.getAddresses() == null) return;

        view.hideHomeAddressHeader();
        view.hideWorkAddressHeader();
        view.hideOtherAddressHeader();

        this.user = user;
        for (Address address : user.getAddresses()) {
            if (address.getLocation().equals(home)) {
                view.showHomeAddressHeader(address);
            } else if (address.getLocation().equals(work)) {
                view.showWorkAddressHeader(address);
            } else if (address.getLocation().equals(other)){
                view.showOtherAddressHeader(address);
            }
        }

        if (user.getAddresses().size() == 3) {
            view.hideAddNewAddressButton();
        } else {
            view.unhideAddNewAddressButton();
        }

        view.stopProgressDialog();
    }

    @Override
    public void onUserFailedToObtain() {
        if (view == null) return;

        view.hideHomeAddressHeader();
        view.hideWorkAddressHeader();
        view.hideOtherAddressHeader();
        view.stopProgressDialog();
    }
}
