package singularity.com.cleanium.ui.screens;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.singularity.cleanium.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CouponsActivity extends BaseNavigationDrawerActivity {

    @Bind(R.id.coupon_edittext)
    EditText coupon;

    @Bind(R.id.coupon_submit_button)
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);
        ButterKnife.bind(this);

        setupFont();
        setupToolbarAndNavigationDrawer(7);
    }

    void setupFont() {
        Typeface regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        Typeface thin = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

        coupon.setTypeface(thin);
        submit.setTypeface(regular);
    }
}
