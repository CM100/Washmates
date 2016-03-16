package singularity.com.cleanium.ui.events;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.parse.ParseUser;
import com.singularity.cleanium.R;

import singularity.com.cleanium.ui.screens.BaseNavigationDrawerActivity;
import singularity.com.cleanium.ui.screens.CouponsActivity;
import singularity.com.cleanium.ui.screens.OrderHistoryActivity;
import singularity.com.cleanium.ui.screens.PricingActivity;
import singularity.com.cleanium.ui.screens.RateServiceActivity;
import singularity.com.cleanium.ui.screens.SchedulePickupActivity;
import singularity.com.cleanium.ui.screens.SettingsActivity;
import singularity.com.cleanium.ui.screens.StartingActivity;
import singularity.com.cleanium.ui.screens.SupportActivity;
import singularity.com.cleanium.ui.screens.WashingStatusActivity;

public class
        BaseNavigationActivityEventHandler implements RecyclerView.OnItemTouchListener {

    private BaseNavigationDrawerActivity control;
    private int activePosition;

    public BaseNavigationActivityEventHandler(BaseNavigationDrawerActivity control, int activePosition) {
        this.control = control;
        this.activePosition = activePosition;
    }

    @Override
    public void onTouchEvent(RecyclerView recycler, MotionEvent event) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recycler, MotionEvent event) {

        View child = recycler.findChildViewUnder(event.getX(),event.getY());
        if(child!=null && this.control.mGestureDetector.onTouchEvent(event)){
            this.control.mDrawerLayout.closeDrawers();

            final int position = recycler.getChildAdapterPosition(child);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(position != activePosition) {
                        Intent i;
                        switch (position) {
                            case 0:
                                break;
                            case 1:
                                i = new Intent(control, WashingStatusActivity.class);
                                control.startActivity(i);
                                control.finish();
                                break;
                            case 2:
                                i = new Intent(control, SchedulePickupActivity.class);
                                control.startActivity(i);
                                control.finish();
                                break;
                            case 3:
                                i = new Intent(control, PricingActivity.class);
                                control.startActivity(i);
                                control.finish();
                                break;
                            case 4:
                                i = new Intent(control, OrderHistoryActivity.class);
                                control.startActivity(i);
                                control.finish();
                                break;
                            case 5:
                                i = new Intent(control, SupportActivity.class);
                                control.startActivity(i);
                                control.finish();
                                break;
                            case 6:
                                i = new Intent(control, SettingsActivity.class);
                                control.startActivity(i);
                                control.finish();
                                break;
                            case 7:
                                i = new Intent(control, CouponsActivity.class);
                                control.startActivity(i);
                                control.finish();
                                break;
                            case 8:
                                new AlertDialog.Builder(control)
                                        .setMessage(R.string.logout_are_you_sure)
                                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ParseUser.logOutInBackground();
                                                Intent i = new Intent(control, StartingActivity.class);
                                                control.startActivity(i);
                                                control.finish();
                                            }
                                        })
                                        .setCancelable(true)
                                        .create()
                                        .show();
                                break;
                            default:
                                throw new IllegalArgumentException("NAVIGATION ITEM DOESNT EXIST");
                        }
                    }
                }
            }, 250);
            return true;
        }

        return false;
    }
}
