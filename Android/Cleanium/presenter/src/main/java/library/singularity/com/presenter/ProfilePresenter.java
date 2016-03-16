package library.singularity.com.presenter;

import library.singularity.com.data.model.User;
import library.singularity.com.presenter.interfaces.ProfileView;
import library.singularity.com.repository.Repository;
import library.singularity.com.repository.async.GetUserTask;

public class ProfilePresenter {
    ProfileView view;

    public ProfilePresenter(ProfileView view) {
        this.view = view;
    }

    public void finish() {
        view = null;
    }

    public void onScreenCreated() {
        if (view == null) return;

        view.showProgressDialog();
        Repository.getInstance(view.getContext()).getCurrentUser(
                new GetUserTask.IOnGetUserListener() {
                    @Override
                    public void onUserObtained(User user) {
                        if (view == null) return;

                        view.showUserDetails(user);
                        view.stopProgressDialog();
                    }

                    @Override
                    public void onUserFailedToObtain() {
                        if (view == null) return;
                        view.stopProgressDialog();
                    }
                }
        );
    }
}
