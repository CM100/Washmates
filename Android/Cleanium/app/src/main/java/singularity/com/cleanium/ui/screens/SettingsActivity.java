package singularity.com.cleanium.ui.screens;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.singularity.cleanium.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseNavigationDrawerActivity {

    @Bind(R.id.profile)
    TextView profile;

    @Bind(R.id.address)
    TextView address;

    @Bind(R.id.payment)
    TextView payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setupToolbarAndNavigationDrawer(6);
        setupFonts();
    }

    void setupFonts() {
        Typeface thin = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        profile.setTypeface(thin);
        address.setTypeface(thin);
        payment.setTypeface(thin);
    }

    @OnClick(R.id.profile_wrapper)
    void onProfileClicked() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    @OnClick(R.id.address_wrapper)
    void onAddressClicked() {
        startActivity(new Intent(this, AddressListActivity.class));
    }

    @OnClick(R.id.payment_wrapper)
    void onPaymentClicked() {
        startActivity(new Intent(this, PaymentInformationActivity.class));
    }
}
