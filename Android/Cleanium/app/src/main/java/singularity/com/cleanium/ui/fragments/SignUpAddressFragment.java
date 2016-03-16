package singularity.com.cleanium.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.singularity.cleanium.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.User;
import library.singularity.com.presenter.SignUpAddressPresenter;
import library.singularity.com.presenter.interfaces.SignUpAddressView;
import singularity.com.cleanium.ui.screens.AddressActivity;
import singularity.com.cleanium.utils.Utils;

public class SignUpAddressFragment extends Fragment implements SignUpAddressView {

    public static final String TAG = "SignUpAddressFragment";
    public static final String ADDRESS = "ADDRESS";
    public static final String LOGGED_IN_USER = "LOGGED_IN_USER";
    public static final String LOCATIONS = "LOCATIONS";

    @Bind(R.id.addressLine1)
    EditText addressLine1;

    @Bind(R.id.addressLine2)
    EditText addressLine2;

    @Bind(R.id.city)
    EditText city;

    @Bind(R.id.state)
    EditText state;

    @Bind(R.id.postCode)
    EditText postCode;

    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;

    @Bind(R.id.accessNotes)
    EditText accessNotes;

    @Bind(R.id.info)
    TextView info;

    @Bind(R.id.bottomBar)
    View bottomBar;

    @Bind(R.id.bottomBar2)
    View bottomBar2;

    @Bind(R.id.location)
    TextView location;

    @Bind(R.id.deleteBtn)
    TextView deleteBtn;

    @Bind(R.id.saveBtn)
    TextView saveBtn;

    @Bind(R.id.backBtn)
    TextView backBtn;

    @Bind(R.id.nextBtn)
    TextView nextBtn;

    @Bind(R.id.choice1)
    RadioButton choice1;

    @Bind(R.id.choice2)
    RadioButton choice2;

    @Bind(R.id.choice3)
    RadioButton choice3;

    SignUpAddressPresenter signUpAddressPresenter;
    String locationString;
    Address address;
    boolean loggedInUser;
    List<String> takenLocations;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup_address, container, false);
        ButterKnife.bind(this, v);
        address = null;
        loggedInUser = false;

        setupFont();

        Bundle bundle = getArguments();
        if (bundle != null) {
            address = bundle.getParcelable(ADDRESS);
            loggedInUser = bundle.getBoolean(LOGGED_IN_USER, false);
            takenLocations = bundle.getStringArrayList(LOCATIONS);
        }

        if (address != null || loggedInUser) {
            if (address == null) {
                deleteBtn.setVisibility(View.GONE);
            }

            bottomBar.setVisibility(View.GONE);
            bottomBar2.setVisibility(View.VISIBLE);
        }

        signUpAddressPresenter = new SignUpAddressPresenter(this, address, loggedInUser);

        if (takenLocations != null) {
            for (String takenLocation : takenLocations) {
                if (takenLocation.equals(getString(R.string.home))) {
                    ButterKnife.findById(v, R.id.choice1).setVisibility(View.GONE);
                } else if (takenLocation.equals(getString(R.string.work))) {
                    ButterKnife.findById(v, R.id.choice2).setVisibility(View.GONE);
                } else if (takenLocation.equals(getString(R.string.other))) {
                    ButterKnife.findById(v, R.id.choice3).setVisibility(View.GONE);
                }
            }
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.choice1) {
                    locationString = getString(R.string.home);
                    location.setTextColor(getResources().getColor(R.color.valid_font_color));
                    location.setCompoundDrawablesWithIntrinsicBounds(0,
                            0,
                            R.drawable.edittext_location_icon_blue,
                            0);
                }
                if (checkedId == R.id.choice2) {
                    locationString = getString(R.string.work);
                    location.setTextColor(getResources().getColor(R.color.valid_font_color));
                    location.setCompoundDrawablesWithIntrinsicBounds(0,
                            0,
                            R.drawable.edittext_location_icon_blue,
                            0);
                }
                if (checkedId == R.id.choice3) {
                    locationString = getString(R.string.other);
                    location.setTextColor(getResources().getColor(R.color.valid_font_color));
                    location.setCompoundDrawablesWithIntrinsicBounds(0,
                            0,
                            R.drawable.edittext_location_icon_blue,
                            0);
                }
            }
        });
        return v;
    }

    private void setupFont() {
        Typeface thin = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        info.setTypeface(thin);
        addressLine1.setTypeface(thin);
        addressLine2.setTypeface(thin);
        city.setTypeface(thin);
        state.setTypeface(thin);
        postCode.setTypeface(thin);
        location.setTypeface(thin);
        choice1.setTypeface(thin);
        choice2.setTypeface(thin);
        choice3.setTypeface(thin);
        accessNotes.setTypeface(thin);
        deleteBtn.setTypeface(thin);
        saveBtn.setTypeface(thin);
        nextBtn.setTypeface(thin);
        backBtn.setTypeface(thin);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (signUpAddressPresenter != null) {
            signUpAddressPresenter.onViewCreated();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(signUpAddressPresenter != null){
            signUpAddressPresenter.finish();
        }
    }

    @OnClick(R.id.nextBtn)
    void onNextBtnClick() {
        if (signUpAddressPresenter == null) return;
        signUpAddressPresenter.onNextClick(addressLine1.getText().toString(),
                addressLine2.getText().toString(),
                city.getText().toString(),
                state.getText().toString(),
                postCode.getText().toString(),
                locationString,
                accessNotes.getText().toString());
    }

    @OnClick(R.id.saveBtn)
    void onSaveClicked() {
        onNextBtnClick();
    }

    @OnClick(R.id.deleteBtn)
    void onDeleteClicked() {
        if (signUpAddressPresenter == null) return;

        signUpAddressPresenter.onAddressDeleteClicked();
    }

    @Override
    public void setAddressLine1Valid() {
        addressLine1.setHintTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
        addressLine1.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_address_line_1_icon_blue,
                0);
    }

    @Override
    public void setAddressLine1Invalid() {
        addressLine1.setText(null);
        addressLine1.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        addressLine1.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_address_line_1_icon_red,
                0);
    }

    @Override
    public void setCityValid() {
        city.setHintTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
        city.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_city_icon_blue,
                0);
    }

    @Override
    public void setCityInvalid() {
        city.setText(null);
        city.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        city.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_city_icon_red,
                0);
    }

    @Override
    public void setStateValid() {
        state.setHintTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
        state.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_state_icon_blue,
                0);
    }

    @Override
    public void setStateInvalid() {
        state.setText(null);
        state.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        state.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_state_icon_red,
                0);
    }

    @Override
    public void setPostCodeValid() {
        postCode.setHint(R.string.signup_post_code);
        postCode.setHintTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
        postCode.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_email_icon_blue,
                0);
    }

    @Override
    public void setPostCodeInvalid() {
        postCode.setText(null);
        postCode.setHint(R.string.invalidPostCode);
        postCode.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        postCode.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_email_icon_red,
                0);
    }

    @Override
    public void setLocationValid() {
        location.setHintTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
        location.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_location_icon_blue,
                0);
    }

    @Override
    public void setLocationInvalid() {
        location.setTextColor(getResources().getColor(R.color.invalid_font_color));
        location.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_location_icon_red,
                0);
    }

    @Override
    public void openSignUpPaymentFragment() {
        SignUpPaymentFragment signupPaymentFragment = new SignUpPaymentFragment();
        Utils.openFragment(getActivity(), R.id.signupContainer, signupPaymentFragment, SignUpPaymentFragment.TAG);
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void setValues(String address1, String address2, String city, String state, String postCode, String notes, String location) {
        this.addressLine1.setText(address1);
        this.addressLine2.setText(address2);
        this.city.setText(city);
        this.state.setText(state);
        this.postCode.setText(postCode);
        this.locationString = location;
        this.accessNotes.setText(notes);
        if (locationString == null) return;
        if (locationString.equals(getString(R.string.home))) {
            radioGroup.check(R.id.choice1);
        } else if (locationString.equals(getString(R.string.work))) {
            radioGroup.check(R.id.choice2);
        } else {
            radioGroup.check(R.id.choice3);
        }
    }

    @OnClick(R.id.backBtn)
    public void saveValuesAndGoBack() {
        if (signUpAddressPresenter == null) return;
        signUpAddressPresenter.saveAddress(addressLine1.getText().toString(),
                addressLine2.getText().toString(),
                city.getText().toString(),
                state.getText().toString(),
                postCode.getText().toString(),
                locationString,
                accessNotes.getText().toString());
        SignUpProfileFragment profileFragment = new SignUpProfileFragment();
        Utils.openFragment(getActivity(), R.id.signupContainer, profileFragment, SignUpProfileFragment.TAG);
    }

    @Override
    public void showProgressDialog() {
        this.progressDialog = new ProgressDialog(getActivity());
        this.progressDialog.setMessage(getResources().getString(R.string.loading_message));
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
    public void showNoInternetConnectionDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.no_internet))
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void showAddressUpdatedSuccessfully() {
        ((AddressActivity) getActivity()).onAddressSaved();
    }

    @Override
    public void showAddressAddedSuccessfully() {
        ((AddressActivity) getActivity()).onAddressSaved();
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
}
