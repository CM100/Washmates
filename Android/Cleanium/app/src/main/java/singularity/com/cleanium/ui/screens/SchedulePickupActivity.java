package singularity.com.cleanium.ui.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.singularity.cleanium.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.User;
import library.singularity.com.presenter.SchedulePickupPresenter;
import library.singularity.com.presenter.interfaces.SchedulePickupView;
import singularity.com.cleanium.adapter.DateSpinnerAdapter;
import singularity.com.cleanium.adapter.LocationSpinnerAdapter;
import singularity.com.cleanium.controls.NoDefaultSpinner;
import singularity.com.cleanium.ui.fragments.SignUpPaymentFragment;
import singularity.com.cleanium.utils.Utils;

public class SchedulePickupActivity extends BaseNavigationDrawerActivity implements SchedulePickupView {

    @Bind(R.id.location_spinner)
    NoDefaultSpinner locationSpinner;

    @Bind(R.id.pick_up_date_spinner)
    NoDefaultSpinner pickUpDateSpinner;

    @Bind(R.id.drop_off_date_spinner)
    NoDefaultSpinner deliveryDateSpinner;

    @Bind(R.id.pick_up_time_slots)
    NoDefaultSpinner pickUpTimeSpinner;

    @Bind(R.id.drop_off_time_slots)
    NoDefaultSpinner deliveryTimeSpinner;

    @Bind(R.id.switch_compat)
    SwitchCompat washAndDry;

    private SchedulePickupPresenter schedulePickupPresenter;
    private ProgressDialog progressDialog;

    boolean animateFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_pickup);
        ButterKnife.bind(this);

        setupToolbarAndNavigationDrawer(2);
        setupSpinners();
        setupFont();

        schedulePickupPresenter = new SchedulePickupPresenter(this);
        schedulePickupPresenter.onViewCreated();
    }

    private void setupSpinners() {
        locationSpinner.setPlaceholderLayout(R.layout.location_spinner_placeholder);
        pickUpDateSpinner.setPlaceholderLayout(R.layout.location_spinner_placeholder);
        pickUpTimeSpinner.setPlaceholderLayout(R.layout.location_spinner_placeholder);
        deliveryDateSpinner.setPlaceholderLayout(R.layout.location_spinner_placeholder);
        deliveryTimeSpinner.setPlaceholderLayout(R.layout.location_spinner_placeholder);

        setupAddressesChoices(new ArrayList<Address>());
        setupPickupDateDropDown(new ArrayList<Date>());
        setupDeliveryDateDropDown(new ArrayList<Date>());
        setupPickupTimeDropDown(new ArrayList<String>());
        setupDeliveryTimeDropDown(new ArrayList<String>());
    }

    private void setupFont(){

    }

    @Override
    public void setupAddressesChoices(List<Address> addresses) {
        LocationSpinnerAdapter adapter = new LocationSpinnerAdapter(this, R.layout.location_spinner, addresses);
        locationSpinner.setAdapter(adapter);
    }

    @Override
    public void setupPickupDateDropDown(List<Date> dates) {
        setupDateTimeSpinner(pickUpDateSpinner, dates);
    }

    void setupDateTimeSpinner(NoDefaultSpinner spinner, List<Date> pickupDates) {
        DateSpinnerAdapter adapter = new DateSpinnerAdapter(this, R.layout.date_spinner, pickupDates);
        spinner.setAdapter(adapter);
    }

    @Override
    public void setupDeliveryDateDropDown(List<Date> dates) {
        setupDateTimeSpinner(deliveryDateSpinner, dates);
    }

    @Override
    public void setupPickupTimeDropDown(List<String> timeSlots) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SchedulePickupActivity.this, android.R.layout.simple_spinner_item, timeSlots);
        adapter.setDropDownViewResource(R.layout.time_slot_spinner_row);
        pickUpTimeSpinner.setAdapter(adapter);
    }

    @Override
    public void setupDeliveryTimeDropDown(List<String> timeSlots) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SchedulePickupActivity.this, android.R.layout.simple_spinner_item, timeSlots);
        adapter.setDropDownViewResource(R.layout.time_slot_spinner_row);
        deliveryTimeSpinner.setAdapter(adapter);
    }

    @Override
    public void selectDefaultAddress() {
        locationSpinner.setSelection(0);
        locationSpinner.setEnabled(false);
    }

    @Override
    public void disablePickupTime(boolean enabled) {
        pickUpTimeSpinner.setEnabled(enabled);
        pickUpTimeSpinner.setClickable(enabled);
        pickUpTimeSpinner.setLongClickable(enabled);
    }

    @Override
    public void disableDeliveryTime(boolean enabled) {
        deliveryTimeSpinner.setEnabled(enabled);
        deliveryTimeSpinner.setClickable(enabled);
        deliveryTimeSpinner.setLongClickable(enabled);
    }

    @Override
    public boolean isWashAndTryActive() {
        return washAndDry.isChecked();
    }

    @Override
    public Date getSelectedDeliveryDate() {
        return (Date) deliveryDateSpinner.getSelectedItem();
    }

    @Override
    public Date getSelectedPickupDate() {
        return (Date) pickUpDateSpinner.getSelectedItem();
    }

    @Override
    public void selectDeliveryDate(int position) {
        if (position != -1) {
            animateFlag = false;
        } else {
            animateFlag = true;
        }

        deliveryDateSpinner.setSelection(position, false);
    }

    @Override
    public int getSelectedPickupTimeIndex() {
        return pickUpTimeSpinner.getSelectedItemPosition();
    }

    @Override
    public int getSelectedDeliveryTimeIndex() {
        return deliveryTimeSpinner.getSelectedItemPosition();
    }

    @OnItemSelected(R.id.location_spinner)
    void onAddressSelected(int position) {
        schedulePickupPresenter.onAddressSelected(position);
    }

    @OnItemSelected(R.id.pick_up_date_spinner)
    void onPickupDateSelected(int position) {
        schedulePickupPresenter.onPickUpDateSelected((Date) pickUpDateSpinner.getSelectedItem());
    }

    @OnItemSelected(R.id.drop_off_date_spinner)
    void onDeliveryDateSelected(int position) {
        if (!animateFlag) {
            animateFlag = true;
            return;
        }

        schedulePickupPresenter.onDeliveryDateSelected((Date) deliveryDateSpinner.getSelectedItem());
    }

    @OnCheckedChanged(R.id.switch_compat)
    void onWashAndDryChanged() {
        schedulePickupPresenter.onWashAndDryChanged(
                (Date) pickUpDateSpinner.getSelectedItem(),
                pickUpTimeSpinner.getSelectedItemPosition(),
                (Date) deliveryDateSpinner.getSelectedItem(),
                deliveryTimeSpinner.getSelectedItemPosition()
        );
    }

    @OnClick(R.id.placeOrder)
    void onPlaceOrderClicked() {
        schedulePickupPresenter.onPlaceOrderClicked();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showProgressDialog() {
        this.progressDialog = new ProgressDialog(this);

        this.progressDialog.setMessage(getString(R.string.loading_message));
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    @Override
    public void stopProgressDialog() {
        if(this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    @Override
    public void showError(String error) {
        if (error == null) return;

        String errorFirstLetter = error.substring(0, 1).toUpperCase();
        String capitalizedFirstLetterError = errorFirstLetter + error.substring(1);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_title))
                .setMessage(capitalizedFirstLetterError)
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void showNoInternetConnectionDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.no_internet))
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void showInvalidPickupAndDeliveryDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.order_invalid))
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void showAddCreditCardDialog(User user) {
        SignUpPaymentFragment dialog = new SignUpPaymentFragment();
        dialog.setOnDismissListener(new SignUpPaymentFragment.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (schedulePickupPresenter == null) return;

                schedulePickupPresenter.creditCardAdded();
            }
        });

        Bundle bundle = new Bundle();
        bundle.putParcelable(PaymentInformationActivity.USER_BUNDLE_KEY, user);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), SignUpPaymentFragment.TAG);

    }

    @Override
    public void showOrderAlreadyExistsDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.order_already_exists))
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void showInvalidAddressDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.invalid_address))
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void showInvalidPickupDate() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.pickup_invalid))
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void showInvalidPickupTime() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.pickup_invalid))
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void showInvalidDeliveryDate() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.delivery_invalid))
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void showInvalidDeliveryTime() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.delivery_invalid))
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
        alertDialog.show();
    }

    @Override
    public void disableDeliveryDate(boolean enabled) {
        deliveryDateSpinner.setEnabled(enabled);
        deliveryDateSpinner.setClickable(enabled);
        deliveryDateSpinner.setLongClickable(enabled);
    }
}
