package singularity.com.cleanium.ui.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.singularity.cleanium.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import library.singularity.com.data.model.Address;
import library.singularity.com.presenter.AddressesListPresenter;
import library.singularity.com.presenter.interfaces.AddressesListView;

public class AddressListActivity extends AppCompatActivity implements AddressesListView {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.other_address)
    TextView otherAddress;

    @Bind(R.id.work_address)
    TextView workAddress;

    @Bind(R.id.home_address)
    TextView homeAddress;

    @Bind(R.id.home_full_address)
    TextView fullHomeAddress;

    @Bind(R.id.work_full_address)
    TextView fullWorkAddress;

    @Bind(R.id.other_full_address)
    TextView fullOtherAddress;

    @Bind(R.id.home_address_wrapper)
    View homeAddressWrapper;

    @Bind(R.id.work_address_wrapper)
    View workAddressWrapper;

    @Bind(R.id.other_address_wrapper)
    View otherAddressWrapper;

    @Bind(R.id.add_new_address)
    Button addNewAddress;

    ProgressDialog progressDialog;

    AddressesListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        ButterKnife.bind(this);
        setupToolbar();
        setupFonts();
        presenter = new AddressesListPresenter(this, getString(R.string.home), getString(R.string.work), getString(R.string.other));
        presenter.onScreenCreated();
    }

    void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setContentInsetsAbsolute(0, 0);
    }

    void setupFonts() {
        Typeface regular = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        Typeface thin = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

        homeAddress.setTypeface(regular);
        workAddress.setTypeface(regular);
        otherAddress.setTypeface(regular);
        fullOtherAddress.setTypeface(thin);
        fullHomeAddress.setTypeface(thin);
        fullWorkAddress.setTypeface(thin);
        addNewAddress.setTypeface(regular);
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
    public void showHomeAddressHeader(Address address) {
        homeAddressWrapper.setTag(address);
        setAddressText(fullHomeAddress, address);
        homeAddressWrapper.setVisibility(View.VISIBLE);
    }

    void setAddressText(TextView addressTextView, Address address) {
        String addressText = String.format("%s, %s, %s %d",
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPostalCode());

        addressTextView.setText(addressText);
    }

    @Override
    public void hideHomeAddressHeader() {
        homeAddressWrapper.setVisibility(View.GONE);
    }

    @Override
    public void showWorkAddressHeader(Address address) {
        workAddressWrapper.setTag(address);
        setAddressText(fullWorkAddress, address);
        workAddressWrapper.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideWorkAddressHeader() {
        workAddressWrapper.setVisibility(View.GONE);
    }

    @Override
    public void showOtherAddressHeader(Address address) {
        otherAddressWrapper.setTag(address);
        setAddressText(fullOtherAddress, address);
        otherAddressWrapper.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideOtherAddressHeader() {
        otherAddressWrapper.setVisibility(View.GONE);
    }

    @Override
    public void stopProgressDialog() {
        if(this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    @Override
    public void hideAddNewAddressButton() {
        addNewAddress.setVisibility(View.GONE);
    }

    @Override
    public void unhideAddNewAddressButton() {
        addNewAddress.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.back)
    void onBackClicked() {
        this.finish();
    }

    @OnClick(R.id.home_address_wrapper)
    void onHomeAddressClicked() {
        Address address = (Address) homeAddressWrapper.getTag();
        editAddress(address);
    }

    void editAddress(Address address) {
        Intent intent = new Intent(this, AddressActivity.class);
        intent.putExtra(AddressActivity.ADDRESS, address);

        ArrayList<String> addressLocationsTaken = new ArrayList<>();
        if (homeAddressWrapper.getVisibility() == View.VISIBLE) {
            Address homeAddress = (Address) homeAddressWrapper.getTag();
            if (homeAddress != address) {
                addressLocationsTaken.add(homeAddress.getLocation());
            }
        }

        if (workAddressWrapper.getVisibility() == View.VISIBLE) {
            Address workAddress = (Address) workAddressWrapper.getTag();
            if (workAddress != address) {
                addressLocationsTaken.add(workAddress.getLocation());
            }
        }

        if (otherAddressWrapper.getVisibility() == View.VISIBLE) {
            Address otherAddress = (Address) otherAddressWrapper.getTag();
            if (otherAddress != address) {
                addressLocationsTaken.add(otherAddress.getLocation());
            }
        }

        intent.putStringArrayListExtra(AddressActivity.LOCATIONS, addressLocationsTaken);
        startActivityForResult(intent, 0);
    }

    @OnClick(R.id.work_address_wrapper)
    void onWorkAddressClicked() {
        Address address = (Address) workAddressWrapper.getTag();
        editAddress(address);
    }

    @OnClick(R.id.other_address_wrapper)
    void onOtherAddressClicked() {
        Address address = (Address) otherAddressWrapper.getTag();
        editAddress(address);
    }

    @OnClick(R.id.add_new_address)
    void onAddNewAddressClicked() {
        editAddress(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (presenter == null) return;

            presenter.onScreenCreated();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.finish();
        }
    }
}
