package singularity.com.cleanium.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.singularity.cleanium.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import library.singularity.com.presenter.StartingFragmentPresenter;
import library.singularity.com.presenter.interfaces.StartingView;
import library.singularity.com.repository.SharedPreferencesProvider;
import singularity.com.cleanium.ui.screens.SignUpActivity;
import singularity.com.cleanium.ui.screens.WashingStatusActivity;
import singularity.com.cleanium.utils.Utils;

public class StartingFragment extends BaseFragment implements StartingView {

    public static final String TAG = "StartingFragment";
    StartingFragmentPresenter startingFragmentPresenter;
    ProgressDialog progressDialog;
    boolean loginClicked, userLoggedIn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        startingFragmentPresenter = new StartingFragmentPresenter(this);
        return super.onCreateView(R.layout.fragment_starting, inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startingFragmentPresenter.onScreenCreated();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(startingFragmentPresenter != null){
            startingFragmentPresenter.finish();
        }
    }

    @Override
    public void setUserLoggedIn() {
        userLoggedIn = true;
    }

    @Override
    protected void setFonts(View v) {
        Typeface regular = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        ((Button) ButterKnife.findById(v, R.id.login_button)).setTypeface(regular);
        ((Button) ButterKnife.findById(v, R.id.signup_button)).setTypeface(regular);
    }

    @OnClick(R.id.login_button)
    void onLoginClicked() {
        if(startingFragmentPresenter.isConfigurationSave()){
            openLogin();
        } else {
            loginClicked = true;
            startingFragmentPresenter.downloadConfiguration(true);
        }
    }

    void openLogin() {
        LoginFragment loginFragment = new LoginFragment();
        Utils.openFragment(getActivity(), R.id.prelogin_screen_frame_layout, loginFragment, LoginFragment.TAG);
    }

    @OnClick(R.id.signup_button)
    void onSignUpClicked() {
        if(startingFragmentPresenter.isConfigurationSave()){
            openSignup();
        } else {
            loginClicked = false;
            startingFragmentPresenter.downloadConfiguration(true);
        }
    }

    void openSignup() {
        SharedPreferencesProvider.saveUser(getActivity(), null);
        SharedPreferencesProvider.saveAddress(getActivity(), null);
        SharedPreferencesProvider.resetCard(getActivity());
        startActivity(new Intent(getActivity(), SignUpActivity.class));
    }

    @Override
    public void onConfigurationDownloaded() {
        if (userLoggedIn) {
            getActivity().startActivity(new Intent(getActivity(), WashingStatusActivity.class));
            getActivity().finish();
            return;
        }

        if (loginClicked) {
            openLogin();
        } else {
            openSignup();
        }
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void showProgressDialog() {
        this.progressDialog = new ProgressDialog(getActivity());
        this.progressDialog.setMessage(getActivity().getResources().getString(R.string.loading_message));
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    @Override
    public void closeProgressDialog() {
        if(this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    @Override
    public void showNoInternetConnectionDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.error_title))
                .setMessage(getActivity().getResources().getString(R.string.no_internet))
                .setPositiveButton(getActivity().getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }
}
