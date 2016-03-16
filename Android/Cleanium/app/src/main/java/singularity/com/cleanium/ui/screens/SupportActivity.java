package singularity.com.cleanium.ui.screens;

import android.os.Bundle;

import com.singularity.cleanium.R;

public class SupportActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        setupToolbarAndNavigationDrawer(5);
    }
}
