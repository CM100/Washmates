package library.singularity.com.presenter;

import com.parse.ParseException;
import com.parse.ParseUser;

import library.singularity.com.data.model.Configuration;
import library.singularity.com.network.config.ConfigWebServiceInterfaces;
import library.singularity.com.network.config.ConfigWebServiceProvider;
import library.singularity.com.presenter.interfaces.StartingView;
import library.singularity.com.repository.SharedPreferencesProvider;

public class StartingFragmentPresenter {

    private StartingView view;
    public StartingFragmentPresenter(StartingView startingView){
        this.view = startingView;
        downloadConfiguration(false);
    }

    public void onScreenCreated() {
        boolean isAutoSignedIn = ParseUser.getCurrentUser() != null;
        if (isAutoSignedIn) {
            view.setUserLoggedIn();
            downloadConfiguration(true);
            return;
        }
    }

    public void finish(){
        view = null;
    }

    public boolean isConfigurationSave(){
        return SharedPreferencesProvider.getConfiguration(view.getContext()) != null;
    }

    public void downloadConfiguration(final boolean isCalledFromFragment) {
        if (isCalledFromFragment) {
            view.showProgressDialog();
        }
        ConfigWebServiceProvider.getConfig(new ConfigWebServiceInterfaces.IOnConfigObtainedListener() {
            @Override
            public void onConfigObtained(Configuration configuration) {
                if (view == null) return;
                SharedPreferencesProvider.saveConfiguration(view.getContext(), configuration);
                if (isCalledFromFragment) {
                    view.closeProgressDialog();
                    view.onConfigurationDownloaded();
                }
            }

            @Override
            public void onConfigFailedToObtain(Exception error) {
                if (view == null) return;
                view.closeProgressDialog();
                if (error instanceof ParseException) {
                    if (((ParseException) error).getCode() == 100) {
                        view.showNoInternetConnectionDialog();
                        return;
                    }
                }
            }
        });
    }
}

