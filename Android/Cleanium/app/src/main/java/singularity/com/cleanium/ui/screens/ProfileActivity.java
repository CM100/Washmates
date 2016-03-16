package singularity.com.cleanium.ui.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.singularity.cleanium.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import library.singularity.com.data.model.User;
import library.singularity.com.presenter.ProfilePresenter;
import library.singularity.com.presenter.interfaces.ProfileView;
import singularity.com.cleanium.ui.fragments.SignUpProfileFragment;
import singularity.com.cleanium.utils.Utils;

public class ProfileActivity extends AppCompatActivity implements ProfileView {

    public static final String USER_BUNDLE_KEY = "USER_BUNDLE_KEY";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    ProgressDialog progressDialog;
    ProfilePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setupToolbar();
        presenter = new ProfilePresenter(this);
        presenter.onScreenCreated();
    }

    void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setContentInsetsAbsolute(0, 0);
    }

    @OnClick(R.id.back)
    void onBackClicked() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.finish();
        }
    }

    @Override
    public void showProgressDialog() {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage(getResources().getString(R.string.loading_message));
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showUserDetails(User user) {
        startProfileFragment(user);
    }

    void startProfileFragment(User user) {
        SignUpProfileFragment signUpProfileFragment = new SignUpProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(USER_BUNDLE_KEY, user);
        signUpProfileFragment.setArguments(bundle);
        Utils.openFragment(this, R.id.profileContainer, signUpProfileFragment, SignUpProfileFragment.TAG);
    }

    @Override
    public void stopProgressDialog() {
        if(this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }
}
