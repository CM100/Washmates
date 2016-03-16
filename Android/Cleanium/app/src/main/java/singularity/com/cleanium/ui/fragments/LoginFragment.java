package singularity.com.cleanium.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.singularity.cleanium.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import library.singularity.com.presenter.LoginPresenter;
import library.singularity.com.presenter.interfaces.LoginView;
import singularity.com.cleanium.ui.screens.WashingStatusActivity;
import singularity.com.cleanium.utils.Utils;

public class LoginFragment extends Fragment implements LoginView {

    public static final String TAG = "LoginFragment";

    @Bind(R.id.login_email)
    EditText loginEmail;

    @Bind(R.id.login_password)
    EditText loginPassword;

    LoginPresenter loginPresenter;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, v);
        loginEmail.setText("simic.aleksandar87@gmail.com");
        loginPassword.setText("sifraa");
        setupFonts(v);
        loginPresenter = new LoginPresenter(this);
        return v;
    }

    void setupFonts(View v) {
        Typeface regular = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        Typeface thin = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");

        ((Button) ButterKnife.findById(v, R.id.login_button)).setTypeface(regular);
        ((EditText) ButterKnife.findById(v, R.id.login_email)).setTypeface(thin);
        ((EditText) ButterKnife.findById(v, R.id.login_password)).setTypeface(thin);
        ((EditText) ButterKnife.findById(v, R.id.login_password)).setTypeface(thin);
        ((TextView) ButterKnife.findById(v, R.id.forgot_details)).setTypeface(thin);
        ((TextView) ButterKnife.findById(v, R.id.click)).setTypeface(thin);
        ((TextView) ButterKnife.findById(v, R.id.link_for_forgot_password)).setTypeface(regular);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(loginPresenter != null){
            loginPresenter.finish();
        }
    }

    @OnClick(R.id.login_button)
    void onLoginBtnClick() {
        loginPresenter.onLoginClicked(loginEmail.getText().toString(), loginPassword.getText().toString());
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void setEmailValid() {
        loginEmail.setHintTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
        loginEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email_edittext_valid_drawable_left, 0, 0, 0);
    }

    @Override
    public void setEmailInvalid() {
        loginEmail.setText(null);
        loginEmail.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        loginEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email_edittext_invalid_drawable_left, 0, 0, 0);
    }

    @Override
    public void setPasswordValid() {
        loginPassword.setHintTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
        loginPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.password_edittext_valid_drawable_left, 0, 0, 0);
    }

    @Override
    public void setPasswordInvalid() {
        loginPassword.setText(null);
        loginPassword.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        loginPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.password_edittext_invalid_drawable_left, 0, 0, 0);
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
    public void onLoginSuccess() {
        Intent i = new Intent(getActivity(), WashingStatusActivity.class);
        getActivity().startActivity(i);
        getActivity().finish();
    }

    @Override
    public void onLoginFailed(String error) {
        if (error == null) return;

        String errorFirstLetter = error.substring(0, 1).toUpperCase();
        String capitalizedFirstLetterError = errorFirstLetter + error.substring(1);
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.error_title))
                .setMessage(capitalizedFirstLetterError)
                .setPositiveButton(getActivity().getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
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

    @OnClick(R.id.forgot_password_clickable_area)
    void onForgotPasswordClicked() {
        ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
        Utils.openFragment(getActivity(), R.id.prelogin_screen_frame_layout, forgotPasswordFragment, ForgotPasswordFragment.TAG);
    }

    @Override
    public void showNotVerifiedEmail() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.error_title))
                .setMessage(getActivity().getResources().getString(R.string.email_not_verified))
                .setPositiveButton(getActivity().getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }
}