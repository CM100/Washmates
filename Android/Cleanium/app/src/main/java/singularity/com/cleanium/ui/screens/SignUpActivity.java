package singularity.com.cleanium.ui.screens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;

import com.singularity.cleanium.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import singularity.com.cleanium.ui.fragments.SignUpAddressFragment;
import singularity.com.cleanium.ui.fragments.SignUpPaymentFragment;
import singularity.com.cleanium.ui.fragments.SignUpProfileFragment;
import singularity.com.cleanium.utils.Utils;

public class SignUpActivity extends AppCompatActivity {

    @Bind(R.id.signup_bar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        initBar();
        if (savedInstanceState == null) this.initFragment();
    }

    private void initFragment() {
        SignUpProfileFragment signUpProfileFragment = new SignUpProfileFragment();
        Utils.openFragment(this, R.id.signupContainer, signUpProfileFragment, SignUpProfileFragment.TAG);
    }

    public void initBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setContentInsetsAbsolute(0, 0);
    }

    @OnClick(R.id.back)
    void onBackClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        SignUpProfileFragment profileFragment = (SignUpProfileFragment) getSupportFragmentManager()
                .findFragmentByTag(SignUpProfileFragment.TAG);
        SignUpAddressFragment addressFragment = (SignUpAddressFragment) getSupportFragmentManager()
                .findFragmentByTag(SignUpAddressFragment.TAG);
        SignUpPaymentFragment paymentFragment = (SignUpPaymentFragment) getSupportFragmentManager()
                .findFragmentByTag(SignUpPaymentFragment.TAG);
        if (profileFragment != null && profileFragment.isVisible()) {
            super.onBackPressed();
        } else if (addressFragment != null && addressFragment.isVisible()) {
            addressFragment.saveValuesAndGoBack();
            profileFragment = new SignUpProfileFragment();
            Utils.openFragment(this, R.id.signupContainer, profileFragment, SignUpProfileFragment.TAG);
        } else if (paymentFragment != null && paymentFragment.isVisible()){
            paymentFragment.saveValuesAndGoBack();
            addressFragment = new SignUpAddressFragment();
            Utils.openFragment(this, R.id.signupContainer, addressFragment, SignUpAddressFragment.TAG);
        }

    }
}
