package library.singularity.com.presenter;

import library.singularity.com.presenter.interfaces.StartingView;
import library.singularity.com.repository.SharedPreferencesProvider;

public class StartingPresenter {

    private StartingView view;

    public StartingPresenter(StartingView view){
        this.view = view;
    }

    public void finish() {
        this.view = null;
    }

    public void onScreenCreated() {
        if (view == null) return;

        SharedPreferencesProvider.saveTutorialSeen(view.getContext());
    }
}
