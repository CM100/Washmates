package library.singularity.com.presenter;

import library.singularity.com.presenter.interfaces.SplashscreenView;
import library.singularity.com.repository.SharedPreferencesProvider;

public class SplashscreenPresenter {

    private SplashscreenView view;

    public SplashscreenPresenter(SplashscreenView view) {
        this.view = view;
        SharedPreferencesProvider.saveConfiguration(view.getContext(),null);
    }

    public void finish() {
        view = null;
    }

    public boolean isTutorialSeen() {
        if (view == null) return false;

        return SharedPreferencesProvider.isTutorialSeen(view.getContext());
    }
}
