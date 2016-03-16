package singularity.com.cleanium.ui.screens;

import android.os.Bundle;
import android.widget.RatingBar;

import com.singularity.cleanium.R;

import singularity.com.cleanium.ui.events.RateServiceActivityEventHandler;
import singularity.com.cleanium.utils.Utils;


public class RateServiceActivity extends BaseNavigationDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_service);

        setupToolbarAndNavigationDrawer(3);
        setupListeners();
        setupFont();
    }

    private void setupListeners(){
        RateServiceActivityEventHandler eventHandler = new RateServiceActivityEventHandler(this);
        ((RatingBar) findViewById(R.id.ratingBarService)).setOnRatingBarChangeListener(eventHandler);
        ((RatingBar) findViewById(R.id.ratingBarPrice)).setOnRatingBarChangeListener(eventHandler);
        ((RatingBar) findViewById(R.id.ratingBarTiming)).setOnRatingBarChangeListener(eventHandler);
    }

    private void setupFont(){

    }
}
