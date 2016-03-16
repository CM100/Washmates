package singularity.com.cleanium.ui.events;


import android.widget.RatingBar;
import android.widget.Toast;

import com.singularity.cleanium.R;

import singularity.com.cleanium.ui.screens.RateServiceActivity;

public class RateServiceActivityEventHandler implements RatingBar.OnRatingBarChangeListener {

    private RateServiceActivity control;

    public RateServiceActivityEventHandler(RateServiceActivity control) {
        this.control = control;
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if(ratingBar.getId() == R.id.ratingBarService){
            String ratingValue = String.valueOf(rating);
            Toast.makeText(this.control, "Rate Service " + ratingValue, Toast.LENGTH_SHORT).show();
        }else if(ratingBar.getId() == R.id.ratingBarPrice){
            String ratingValue = String.valueOf(rating);
            Toast.makeText(this.control, "Rate Price " + ratingValue, Toast.LENGTH_SHORT).show();
        }else if(ratingBar.getId() == R.id.ratingBarTiming){
            String ratingValue = String.valueOf(rating);
            Toast.makeText(this.control, "Rate Timing " + ratingValue, Toast.LENGTH_SHORT).show();
        }
    }
}
