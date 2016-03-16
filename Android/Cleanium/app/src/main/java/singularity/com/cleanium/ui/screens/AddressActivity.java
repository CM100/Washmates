package singularity.com.cleanium.ui.screens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.singularity.cleanium.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import library.singularity.com.data.model.Address;
import singularity.com.cleanium.ui.fragments.SignUpAddressFragment;
import singularity.com.cleanium.utils.Utils;

public class AddressActivity extends AppCompatActivity {

    public static final String ADDRESS = "ADDRESS";
    public static final String LOCATIONS = "LOCATIONS";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    Address address;

    ArrayList<String> locationsTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        if (getIntent() != null && getIntent().getExtras() != null) {
            address = getIntent().getExtras().getParcelable(ADDRESS);
            locationsTaken = getIntent().getExtras().getStringArrayList(LOCATIONS);
        }

        ButterKnife.bind(this);
        setupToolbar();
        prepareFragmentView();
    }

    void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setContentInsetsAbsolute(0, 0);
    }

    void prepareFragmentView() {
        SignUpAddressFragment fragment = new SignUpAddressFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SignUpAddressFragment.ADDRESS, address);
        bundle.putBoolean(SignUpAddressFragment.LOGGED_IN_USER, true);
        bundle.putStringArrayList(SignUpAddressFragment.LOCATIONS, locationsTaken);
        fragment.setArguments(bundle);
        Utils.openFragment(this, R.id.addressContainer, fragment, SignUpAddressFragment.TAG);
    }

    @OnClick(R.id.back)
    void onBackClicked() {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    public void onAddressSaved() {
        setResult(RESULT_OK, null);
        finish();
    }
}
