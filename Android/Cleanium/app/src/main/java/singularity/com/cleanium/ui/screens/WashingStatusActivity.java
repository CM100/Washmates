package singularity.com.cleanium.ui.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.singularity.cleanium.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import library.singularity.com.data.model.Bill;
import library.singularity.com.data.model.Configuration;
import library.singularity.com.data.model.OrderStatus;
import library.singularity.com.presenter.WashStatusPresenter;
import library.singularity.com.presenter.interfaces.WashStatusView;
import library.singularity.com.repository.SharedPreferencesProvider;
import singularity.com.cleanium.controls.WashingStagesControl;
import singularity.com.cleanium.ui.fragments.BillFragmentDialog;
import singularity.com.cleanium.utils.Utils;

public class WashingStatusActivity extends BaseNavigationDrawerActivity implements WashStatusView {

    @Bind(R.id.progress_stages_view)
    WashingStagesControl washStatusControl;

    @Bind(R.id.pickupDate)
    TextView pickupDate;

    @Bind(R.id.pickupDay)
    TextView pickupDay;

    @Bind(R.id.dropOffDate)
    TextView dropOffDate;

    @Bind(R.id.dropOffDay)
    TextView dropOffDay;

    @Bind(R.id.pickupTime)
    TextView pickupTime;

    @Bind(R.id.dropOffTime)
    TextView dropOffTime;

    @Bind(R.id.notes)
    TextView notes;

    @Bind(R.id.cancel)
    TextView cancel;

    @Bind(R.id.driver_data_wrapper)
    View driverWrapper;

    @Bind(R.id.drivers_picture)
    ImageView driverPicture;

    @Bind(R.id.drivers_name)
    TextView driverName;

    @Bind(R.id.assignedDriver)
    View assignedDriver;

    @Bind(R.id.statusNotes)
    View statusNotes;

    ProgressDialog progressDialog;
    WashStatusPresenter presenter;
    BillFragmentDialog billFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_washing_status);
        ButterKnife.bind(this);
        setupToolbarAndNavigationDrawer(1);
        setupFonts();
        presenter = new WashStatusPresenter(this);
        presenter.onScreenCreated();
    }

    void setupFonts() {
        Typeface thin, regular;
        thin = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
        regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");

        ((TextView) ButterKnife.findById(this, R.id.pickupHeader)).setTypeface(thin);
        ((TextView) ButterKnife.findById(this, R.id.dropOffHeader)).setTypeface(thin);
        ((TextView) ButterKnife.findById(this, R.id.pickupDate)).setTypeface(regular);
        ((TextView) ButterKnife.findById(this, R.id.pickupDay)).setTypeface(regular);
        ((TextView) ButterKnife.findById(this, R.id.dropOffDate)).setTypeface(regular);
        ((TextView) ButterKnife.findById(this, R.id.dropOffDay)).setTypeface(regular);
        ((TextView) ButterKnife.findById(this, R.id.pickupTime)).setTypeface(regular);
        ((TextView) ButterKnife.findById(this, R.id.dropOffTime)).setTypeface(regular);
        ((TextView) ButterKnife.findById(this, R.id.statusNotes)).setTypeface(thin);
        ((TextView) ButterKnife.findById(this, R.id.notes)).setTypeface(thin);
        ((TextView) ButterKnife.findById(this, R.id.cancel)).setTypeface(thin);
        ((TextView) ButterKnife.findById(this, R.id.assignedDriver)).setTypeface(thin);
        ((TextView) ButterKnife.findById(this, R.id.drivers_name)).setTypeface(thin);
    }

    @Override
    public void showProgressDialog() {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage(getResources().getString(R.string.loading_message));
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void stopProgressDialog() {
        if(this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.finish();
        }
    }

    @Override
    public void setNoCurrentOrderStatus() {
        OrderStatus status = new OrderStatus();
        status.setText(getString(R.string.you_have_no_orders));
        washStatusControl.setCurrentOrderStatus(status);
    }

    @Override
    public void setWashStatuses(OrderStatus[] statuses) {
        washStatusControl.setStages(statuses);
    }

    @Override
    public void setCurrentWashStatus(OrderStatus status) {
        washStatusControl.setCurrentOrderStatus(status);
    }

    @Override
    public void setInvalidPickupDate() {
        pickupDate.setText(getString(R.string.na));
        pickupDay.setText(null);
    }

    @Override
    public void setInvalidPickupTime() {
        pickupTime.setText(getString(R.string.na));
    }

    @Override
    public void setInvalidDeliveryDate() {
        dropOffDate.setText(getString(R.string.na));
        dropOffDay.setText(null);
    }

    @Override
    public void setInvalidDeliveryTime() {
        dropOffTime.setText(getString(R.string.na));
    }

    @Override
    public void hideNotes() {
        notes.setVisibility(View.GONE);
        statusNotes.setVisibility(View.GONE);
    }

    @Override
    public void hideBill() {
        if (billFragment == null || billFragment.isHidden()) return;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(billFragment);
        transaction.commit();
    }

    @Override
    public void hideCancelButton() {
        cancel.setVisibility(View.GONE);
    }

    @Override
    public void setPickupDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE");
        pickupDate.setText(dateFormat.format(date));
        pickupDay.setText(dayOfWeekFormat.format(date));
    }

    @Override
    public void setPickupTime(Date fromDate, Date toDate) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        SimpleDateFormat amPmFormat = new SimpleDateFormat("a");
        pickupTime.setText(timeFormat.format(fromDate) + " - "
                + timeFormat.format(toDate) +
                " " + amPmFormat.format(fromDate));
    }

    @Override
    public void setDeliveryDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE");
        dropOffDate.setText(dateFormat.format(date));
        dropOffDay.setText(dayOfWeekFormat.format(date));
    }

    @Override
    public void setDeliveryTime(Date fromDate, Date toDate) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        SimpleDateFormat amPmFormat = new SimpleDateFormat("a");
        dropOffTime.setText(timeFormat.format(fromDate) + " - "
                + timeFormat.format(toDate) +
                " " + amPmFormat.format(fromDate));
    }

    @Override
    public void showNotes(String note) {
        statusNotes.setVisibility(View.VISIBLE);
        notes.setText(note);
        notes.setVisibility(View.VISIBLE);
    }

    @Override
    public void showBill(Bill bill) {
        if (billFragment == null) {
            billFragment = new BillFragmentDialog();
        }

        billFragment.setBill(bill);
        Utils.openFragment(this, R.id.bill ,billFragment, BillFragmentDialog.TAG);
    }

    @Override
    public void showCancelButton() {
        cancel.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDriver() {
        driverWrapper.setVisibility(View.GONE);
        assignedDriver.setVisibility(View.GONE);
    }

    @Override
    public void showDriver(String driverName, String imageUrl) {
        driverWrapper.setVisibility(View.VISIBLE);
        assignedDriver.setVisibility(View.VISIBLE);
        this.driverName.setText(driverName);
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                driverPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
                driverPicture.setImageBitmap(Utils.getCircularBitmap(bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                driverPicture.setImageResource(R.drawable.driver_picture);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(driverPicture.getContext().getApplicationContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.driver_picture)
                    .error(R.drawable.driver_picture)
                    .into(target);
        } else {
            driverPicture.setImageResource(R.drawable.driver_picture);
        }
    }
}
