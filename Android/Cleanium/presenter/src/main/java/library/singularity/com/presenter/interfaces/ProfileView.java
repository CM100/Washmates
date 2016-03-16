package library.singularity.com.presenter.interfaces;

import android.content.Context;

import library.singularity.com.data.model.User;

public interface ProfileView {
    void showProgressDialog();
    Context getContext();
    void showUserDetails(User user);
    void stopProgressDialog();
}
