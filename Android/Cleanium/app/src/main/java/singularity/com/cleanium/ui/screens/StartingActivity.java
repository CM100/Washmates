package singularity.com.cleanium.ui.screens;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import com.singularity.cleanium.R;

import library.singularity.com.presenter.StartingPresenter;
import library.singularity.com.presenter.interfaces.StartingView;
import singularity.com.cleanium.ui.fragments.ForgotPasswordFragment;
import singularity.com.cleanium.ui.fragments.LoginFragment;
import singularity.com.cleanium.ui.fragments.StartingFragment;
import singularity.com.cleanium.utils.Utils;

public class StartingActivity extends AppCompatActivity implements StartingView {

    StartingPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        presenter = new StartingPresenter(this);
        if(savedInstanceState == null) {
            this.init();
        }

        presenter.onScreenCreated();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.finish();
        }
    }

    private void init() {
        StartingFragment startingFragment = new StartingFragment();
        Utils.openFragment(this, R.id.prelogin_screen_frame_layout, startingFragment, StartingFragment.TAG);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onBackPressed() {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag(LoginFragment.TAG);
        Fragment forgotPasswordFragment = getSupportFragmentManager().findFragmentByTag(ForgotPasswordFragment.TAG);
        if (forgotPasswordFragment != null && forgotPasswordFragment.isVisible()) {
            loginFragment = new LoginFragment();
            Utils.openFragment(this, R.id.prelogin_screen_frame_layout, loginFragment, LoginFragment.TAG);
        } else if (loginFragment != null && loginFragment.isVisible()) {
            init();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void showProgressDialog() {
    }

    @Override
    public void closeProgressDialog() {
    }

    @Override
    public void showNoInternetConnectionDialog() {
    }

    @Override
    public void onConfigurationDownloaded() {
    }

    @Override
    public void setUserLoggedIn() {
    }
}
