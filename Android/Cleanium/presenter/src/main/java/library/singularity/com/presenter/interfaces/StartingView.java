package library.singularity.com.presenter.interfaces;

import android.content.Context;

public interface StartingView {
    Context getContext();
    void showProgressDialog();
    void closeProgressDialog();
    void showNoInternetConnectionDialog();
    void onConfigurationDownloaded();
    void setUserLoggedIn();
}
