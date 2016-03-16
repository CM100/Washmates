package singularity.com.cleanium.ui.screens;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.singularity.cleanium.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PricingActivity extends BaseNavigationDrawerActivity {

    @Bind(R.id.service_price)
    TextView servicePrice;

    @Bind(R.id.service_title)
    TextView serviceTitle;

    @Bind(R.id.service_price2)
    TextView servicePrice2;

    @Bind(R.id.service_title2)
    TextView serviceTitle2;

    @Bind(R.id.service_price3)
    TextView servicePrice3;

    @Bind(R.id.service_title3)
    TextView serviceTitle3;

    @Bind(R.id.service_price4)
    TextView servicePrice4;

    @Bind(R.id.service_title4)
    TextView serviceTitle4;

    @Bind(R.id.service_price5)
    TextView servicePrice5;

    @Bind(R.id.service_title5)
    TextView serviceTitle5;

    @Bind(R.id.service_price6)
    TextView servicePrice6;

    @Bind(R.id.service_title6)
    TextView serviceTitle6;

    @Bind(R.id.service_price7)
    TextView servicePrice7;

    @Bind(R.id.service_title7)
    TextView serviceTitle7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricing);
        ButterKnife.bind(this);

        setupToolbarAndNavigationDrawer(3);
        setupFont();
    }

    private void setupFont() {
        Typeface thin, regular;
        thin = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");

        serviceTitle.setTypeface(thin);
        servicePrice.setTypeface(regular);
        serviceTitle2.setTypeface(thin);
        servicePrice2.setTypeface(regular);
        serviceTitle3.setTypeface(thin);
        servicePrice3.setTypeface(regular);
        serviceTitle4.setTypeface(thin);
        servicePrice4.setTypeface(regular);
        serviceTitle5.setTypeface(thin);
        servicePrice5.setTypeface(regular);
        serviceTitle6.setTypeface(thin);
        servicePrice6.setTypeface(regular);
        serviceTitle7.setTypeface(thin);
        servicePrice7.setTypeface(regular);
    }
}
