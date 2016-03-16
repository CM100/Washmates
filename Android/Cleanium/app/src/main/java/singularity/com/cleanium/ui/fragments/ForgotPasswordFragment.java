package singularity.com.cleanium.ui.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.singularity.cleanium.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import library.singularity.com.presenter.ForgotPasswordPresenter;
import library.singularity.com.presenter.interfaces.ForgotPasswordView;


public class ForgotPasswordFragment extends Fragment implements ForgotPasswordView {

    public static final String TAG = "ForgotPasswordFragment";

    @Bind(R.id.forgot_password_email)
    EditText email;

    @Bind(R.id.forgot_password_reset_btn)
    Button forgotPasswordBtn;

    ForgotPasswordPresenter forgotPasswordPresenter;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        ButterKnife.bind(this, v);
        setupFonts();
        forgotPasswordPresenter = new ForgotPasswordPresenter(this);
        return v;
    }

    void setupFonts() {
        Typeface regular = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");
        Typeface thin = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        email.setTypeface(thin);
        forgotPasswordBtn.setTypeface(regular);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(forgotPasswordPresenter != null){
            forgotPasswordPresenter.finish();
        }
    }

    @OnClick(R.id.forgot_password_reset_btn)
    void onResetButtonClicked() {
        forgotPasswordPresenter.onResetClicked(email.getText().toString());
    }

    @Override
    public void setEmailValid() {
        email.setTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
        email.setHintTextColor(getActivity().getResources().getColor(R.color.valid_font_color));
        email.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email_edittext_valid_drawable_left,
                0,
                0,
                0);
    }

    @Override
    public void setEmailInvalid() {
        email.setText(null);
        email.setHintTextColor(getActivity().getResources().getColor(R.color.invalid_font_color));
        email.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email_edittext_invalid_drawable_left,
                0,
                0,
                0);
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
    public void onPasswordResetSuccess() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.success))
                .setMessage(getActivity().getResources().getString(R.string.success_reset_password_message))
                .setPositiveButton(getActivity().getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void onPasswordResetFailed(String error) {
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
}