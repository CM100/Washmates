package singularity.com.cleanium.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.singularity.cleanium.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import library.singularity.com.data.model.User;
import library.singularity.com.presenter.SignUpProfilePresenter;
import library.singularity.com.presenter.interfaces.SignUpProfileView;
import singularity.com.cleanium.ui.screens.ProfileActivity;
import singularity.com.cleanium.utils.Utils;

public class SignUpProfileFragment extends Fragment implements SignUpProfileView {

    public static final String TAG = "SignUpProfileFragment";

    @Bind(R.id.firstName)
    EditText firstName;

    @Bind(R.id.lastName)
    EditText lastName;

    @Bind(R.id.email)
    EditText email;

    @Bind(R.id.phone)
    EditText phone;

    @Bind(R.id.password) EditText password;

    @Bind(R.id.confirmPassword)
    EditText confirmPassword;

    @Bind(R.id.bottomBar)
    View nextBottomBar;

    @Bind(R.id.bottomBar2)
    View saveBottomBar;

    @Bind(R.id.info)
    TextView info;

    @Bind(R.id.saveBtn)
    TextView saveBtn;

    @Bind(R.id.nextBtn)
    TextView nextBtn;

    @Bind(R.id.phone_area_code)
    TextView phoneCodeArea;

    SignUpProfilePresenter signUpProfilePresenter;
    ProgressDialog progressDialog;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup_profile, container, false);
        ButterKnife.bind(this, v);
        Bundle bundle = getArguments();
        user = null;
        if (bundle != null) {
            user = bundle.getParcelable(ProfileActivity.USER_BUNDLE_KEY);
        }

        if (user != null) {
            nextBottomBar.setVisibility(View.GONE);
            saveBottomBar.setVisibility(View.VISIBLE);
            email.setEnabled(false);
        }

        signUpProfilePresenter = new SignUpProfilePresenter(this, user);

        this.setFont();
        return v;
    }



    private void setFont() {
        Typeface thin = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");

        info.setTypeface(thin);
        firstName.setTypeface(thin);
        lastName.setTypeface(thin);
        email.setTypeface(thin);
        phone.setTypeface(thin);
        password.setTypeface(thin);
        confirmPassword.setTypeface(thin);
        saveBtn.setTypeface(thin);
        nextBtn.setTypeface(thin);
        phoneCodeArea.setTypeface(thin);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signUpProfilePresenter.viewCreated();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(signUpProfilePresenter != null){
            signUpProfilePresenter.finish();
        }
    }

    @OnClick(R.id.saveBtn)
    void onSaveClicked() {
        signUpProfilePresenter.onSaveUserData(firstName.getText().toString(),
                lastName.getText().toString(),
                email.getText().toString(),
                phone.getText().toString(),
                password.getText().toString(),
                confirmPassword.getText().toString());
    }

    @OnClick(R.id.nextBtn)
    void onNextBtnClick() {
        signUpProfilePresenter.onSaveUserData(firstName.getText().toString(),
                lastName.getText().toString(),
                email.getText().toString(),
                phone.getText().toString(),
                password.getText().toString(),
                confirmPassword.getText().toString());
    }

    @Override
    public void setFirstNameValid() {
        firstName.setHintTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
        firstName.setHint(R.string.signup_first_name);
        firstName.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_user_icon_blue,
                0);
    }

    @Override
    public void setFirstNameInvalid() {
        firstName.setText(null);
        firstName.setHint(R.string.invalidFirstName);
        firstName.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        firstName.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_user_icon_red,
                0);
    }

    @Override
    public void setLastNameValid() {
        lastName.setHint(R.string.signup_last_name);
        lastName.setHintTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
        lastName.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_user_icon_blue,
                0);
    }

    @Override
    public void setLastNameInvalid() {
        lastName.setText(null);
        lastName.setHint(R.string.invalidLastName);
        lastName.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        lastName.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_user_icon_red,
                0);
    }

    @Override
    public void setEmailValid() {
        email.setHintTextColor(getResources().getColor(R.color.valid_font_color));
        email.setHint(R.string.signup_email);
        email.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_email_icon_blue,
                0);
    }

    @Override
    public void setEmailInvalid() {
        email.setText(null);
        email.setHint(R.string.invalidEmailAddress);
        email.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        email.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_email_icon_red,
                0);
    }

    @Override
    public void setPhoneNumberValid() {
        phone.setHint(R.string.signup_phone_number);
        phone.setHintTextColor(getResources().getColor(R.color.valid_font_color));
        phone.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_phone_icon_blue,
                0);
    }

    @Override
    public void setPhoneNumberInvalid() {
        phone.setText(null);
        phone.setHint(R.string.invalidPhoneNumber);
        phone.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        phone.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_phone_icon_red,
                0);
    }

    @Override
    public void setPasswordValid() {
        password.setHint(R.string.signup_password);
        password.setHintTextColor(getResources().getColor(R.color.valid_font_color));
        password.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_password_icon_blue,
                0);
    }

    @Override
    public void setPasswordInvalid() {
        password.setText(null);
        password.setHint(R.string.invalidPassword);
        password.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        password.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_password_icon_red,
                0);
    }

    @Override
    public void setConfirmPasswordValid() {
        confirmPassword.setHint(R.string.signup_confirm_password);
        confirmPassword.setHintTextColor(getResources().getColor(R.color.valid_font_color));
        confirmPassword.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_confirm_pass_icon_blue,
                0);
    }

    @Override
    public void setConfirmPasswordInvalid() {
        confirmPassword.setText(null);
        confirmPassword.setHint(R.string.isNotSameConfirmPassword);
        confirmPassword.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        confirmPassword.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_confirm_pass_icon_red,
                0);
    }

    @Override
    public void openSignUpAddressFragment() {
        SignUpAddressFragment signUpAddressFragment = new SignUpAddressFragment();
        Utils.openFragment(getActivity(), R.id.signupContainer, signUpAddressFragment, SignUpAddressFragment.TAG);
    }

    @Override
    public void setSingUpProfileDetails(String firstName, String lastName,
                                        String email, String phoneNumber, String password) {
        this.firstName.setText(firstName);
        this.lastName.setText(lastName);
        this.email.setText(email);
        this.phone.setText(phoneNumber);
        this.password.setText(password);
        this.confirmPassword.setText(password);
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
    public void stopProgressDialog() {
        if(this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    @Override
    public void showError(String error) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.error_title))
                .setMessage(error)
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void emailAlreadyTaken() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.email_taken))
                .setPositiveButton(getString(R.string.ok),
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

    @Override
    public void userUpdated() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.success))
                .setMessage(getActivity().getResources().getString(R.string.profile_updated))
                .setPositiveButton(getActivity().getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }
}
