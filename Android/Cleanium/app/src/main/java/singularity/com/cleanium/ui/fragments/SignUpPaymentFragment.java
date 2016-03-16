package singularity.com.cleanium.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.singularity.cleanium.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import library.singularity.com.data.model.User;
import library.singularity.com.presenter.SignUpPaymentPresenter;
import library.singularity.com.presenter.interfaces.SignUpPaymentView;
import singularity.com.cleanium.controls.NoDefaultSpinner;
import singularity.com.cleanium.ui.screens.PaymentInformationActivity;
import singularity.com.cleanium.ui.screens.ProfileActivity;
import singularity.com.cleanium.ui.screens.StartingActivity;
import singularity.com.cleanium.utils.Utils;

public class SignUpPaymentFragment extends DialogFragment implements SignUpPaymentView {

    public interface OnDismissListener {
        void onDismiss();
    }

    String monthsNumber[] = {
            "1", "2","3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
    };

    List<String> yearsNumber  = new ArrayList<>();

    public static final String TAG = "SignUpPaymentFragment";

    @Bind(R.id.info)
    TextView header;

    @Bind(R.id.creditCardNumber)
    EditText creditCardNumber;

    @Bind(R.id.expirationDateView)
    TextView expirationDateView;

    @Bind(R.id.month_spinner)
    NoDefaultSpinner expirationDateMonth;

    @Bind(R.id.year_spinner)
    NoDefaultSpinner expirationDateYear;

    @Bind(R.id.cvc)
    EditText cvcNumber;

    @Bind(R.id.expDateIcon)
    ImageView image;

    @Bind(R.id.bottomBar)
    View nextBottomBar;

    @Bind(R.id.bottomBar2)
    View saveBottomBar;

    @Bind(R.id.backBtn)
    TextView backBtn;

    @Bind(R.id.finishBtn)
    TextView finishBtn;

    @Bind(R.id.saveBtn)
    TextView saveBtn;

    SignUpPaymentPresenter signUpPaymentPresenter;
    ProgressDialog progressDialog;

    @Bind(R.id.not_mandatory_text)
    TextView notMandatoryText;

    User user;

    OnDismissListener listener;

    public void setOnDismissListener(OnDismissListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        View v = inflater.inflate(R.layout.fragment_signup_payment, container, false);
        ButterKnife.bind(this, v);

        Bundle bundle = getArguments();
        user = null;
        if (bundle != null) {
            user = bundle.getParcelable(PaymentInformationActivity.USER_BUNDLE_KEY);
        }

        if (user != null) {
            nextBottomBar.setVisibility(View.GONE);
            saveBottomBar.setVisibility(View.VISIBLE);
            notMandatoryText.setVisibility(View.GONE);
        }

        signUpPaymentPresenter = new SignUpPaymentPresenter(this, user);
        this.setupSpinners(v);
        setupFonts();
        return v;
    }

    private void setupSpinners(View v) {
        int currentYear = 0;
        Calendar cal = Calendar.getInstance();
        currentYear = cal.get(Calendar.YEAR) ;
        for(int i = 0; i < 12; i++){
            yearsNumber.add(currentYear + i + "");
        }

        expirationDateMonth.setPlaceholderLayout(R.layout.spinner_center_text);
        expirationDateYear.setPlaceholderLayout(R.layout.spinner_center_text);

        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(v.getContext(),
                R.layout.spinner_black_center_text ,monthsNumber);

        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(v.getContext(),
                R.layout.spinner_black_center_text , yearsNumber);

        monthsAdapter.setDropDownViewResource(R.layout.spinner_black_center_text);
        yearsAdapter.setDropDownViewResource(R.layout.spinner_black_center_text);

        expirationDateMonth.setAdapter(monthsAdapter);
        expirationDateYear.setAdapter(yearsAdapter);
    }

    void setupFonts() {
        Typeface thin = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");

        header.setTypeface(thin);
        creditCardNumber.setTypeface(thin);
        expirationDateView.setTypeface(thin);
        cvcNumber.setTypeface(thin);
        notMandatoryText.setTypeface(thin);
        backBtn.setTypeface(thin);
        finishBtn.setTypeface(thin);
        saveBtn.setTypeface(thin);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (signUpPaymentPresenter != null) {
            signUpPaymentPresenter.viewCreated();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(signUpPaymentPresenter != null){
            signUpPaymentPresenter.finish();
        }
    }

    @OnClick(R.id.finishBtn)
    public void onFinshBtnClick() {
        signUpPaymentPresenter.onFinshBtnClick(creditCardNumber.getText().toString(),
                (String) expirationDateMonth.getSelectedItem(),
                (String) expirationDateYear.getSelectedItem(),
                cvcNumber.getText().toString());
    }

    @OnClick(R.id.saveBtn)
    public void onSaveClicked() {
        signUpPaymentPresenter.onFinshBtnClick(creditCardNumber.getText().toString(),
                (String) expirationDateMonth.getSelectedItem(),
                (String) expirationDateYear.getSelectedItem(),
                cvcNumber.getText().toString());
    }

    @Override
    public void setCreditCardNumberValid() {
        creditCardNumber.setHintTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
        creditCardNumber.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_credit_card_icon_blue,
                0);
    }

    @Override
    public void setCreditCardNumberInvalid() {
        creditCardNumber.setText(null);
        creditCardNumber.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        creditCardNumber.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_credit_card_icon_red,
                0);
    }

    @Override
    public void setExpirationDateValid() {
        image.setImageResource(R.drawable.edittext_expiration_date_icon_blue);
        expirationDateView.setTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
    }

    @Override
    public void setExpirationDateInvalid() {
        image.setImageResource(R.drawable.edittext_expiration_date_icon_red);
        expirationDateView.setTextColor(getActivity().getResources().getColor(R.color.invalid_font_color));
    }

    @Override
    public void setCVCNumberValid() {
        cvcNumber.setHintTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
        cvcNumber.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_cvc_icon_blue,
                0);
    }

    @Override
    public void setCVCNumberInvalid() {
        cvcNumber.setText(null);
        cvcNumber.setHintTextColor(getResources().getColor(R.color.invalid_font_color));
        cvcNumber.setCompoundDrawablesWithIntrinsicBounds(0,
                0,
                R.drawable.edittext_cvc_icon_red,
                0);
    }

    @Override
    public void showStripeError(String error) {
        Toast.makeText(getActivity(),
                error,
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    public void authenticationError() {
        Toast.makeText(getActivity(),
                getString(R.string.authenticationError),
                Toast.LENGTH_LONG
        ).show();
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
    public void onRegistrationSuccess() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.success))
                .setMessage(getActivity().getResources().getString(R.string.registration_successful))
                .setPositiveButton(getActivity().getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getActivity().startActivity(
                                        new Intent(getActivity(), StartingActivity.class));
                                getActivity().finish();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void onRegistrationFailed(String error) {
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

    @Override
    public void setValues(String creditCardNumber, String expirationMonth, String expirationYear, String CVC) {
        this.creditCardNumber.setText(creditCardNumber);
//        this.expirationDateMonth.setText(expirationMonth);
//        this.expirationDateYear.setText(expirationYear);
        this.cvcNumber.setText(CVC);
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @OnClick(R.id.backBtn)
    public void saveValuesAndGoBack() {
        signUpPaymentPresenter.saveCard(creditCardNumber.getText().toString(),
                (String) expirationDateMonth.getSelectedItem(),
                (String) expirationDateYear.getSelectedItem(),
                cvcNumber.getText().toString());
        SignUpAddressFragment addressFragment = new SignUpAddressFragment();
        Utils.openFragment(getActivity(), R.id.signupContainer, addressFragment, SignUpAddressFragment.TAG);
    }

    @Override
    public void showCreditCardDataInvalid() {
        Toast.makeText(getActivity(),
                    getString(R.string.credit_card_data_invalid),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreditCardAdded() {
        if (getDialog() != null && getDialog().isShowing()) {
            dismiss();
        }

        if (listener != null) {
            listener.onDismiss();
        } else {
            Toast.makeText(getActivity(),
                    getString(R.string.credit_card_saved),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
