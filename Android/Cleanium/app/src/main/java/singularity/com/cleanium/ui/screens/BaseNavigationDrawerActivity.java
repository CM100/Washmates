package singularity.com.cleanium.ui.screens;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import com.singularity.cleanium.R;
import singularity.com.cleanium.adapter.NavigationDrawerAdapter;
import singularity.com.cleanium.ui.events.BaseNavigationActivityEventHandler;
import singularity.com.cleanium.utils.DividerItemDecoration;

public class BaseNavigationDrawerActivity extends AppCompatActivity {

	private ActionBarDrawerToggle mDrawerToggle;
	public DrawerLayout mDrawerLayout;
	public GestureDetector mGestureDetector;

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
		if(drawer != null && drawer.isDrawerOpen(Gravity.LEFT)){
			drawer.closeDrawers();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void setupToolbarAndNavigationDrawer(int activePosition) {

		String titles[] = {
				getString(R.string.menu_home),
				getString(R.string.menu_home),
				getString(R.string.menu_schedule_pickup),
				getString(R.string.menu_pricing),
				getString(R.string.menu_order_history),
				getString(R.string.menu_support),
				getString(R.string.menu_settings),
				getString(R.string.menu_coupons),
				getString(R.string.menu_logout)
		};

		int icons[] = {
				R.drawable.menu_home_white,
				R.drawable.menu_home_white,
				R.drawable.menu_schadule_pickup_white,
				R.drawable.menu_pricing_white,
				R.drawable.menu_order_history,
				R.drawable.menu_contact_white,
				R.drawable.menu_settings_white,
				R.drawable.menu_coupons,
				R.drawable.menu_logout_white
		};

        int activeIcons[] = {
                R.drawable.menu_home_blue,
                R.drawable.menu_home_blue,
                R.drawable.menu_schadule_pickup_blue,
                R.drawable.menu_pricing_blue,
                R.drawable.menu_order_list_blue,
                R.drawable.menu_contact_blue,
                R.drawable.menu_settings_blue,
                R.drawable.menu_coupons_blue,
                R.drawable.menu_logout_blue
        };

		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		toolbar.setContentInsetsAbsolute(0, 0);

		setupListeners(activePosition);

		RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.navigation_drawer);
        if (mRecyclerView == null) return;

		mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, 0xffffffff));

		mRecyclerView.setHasFixedSize(true);
		RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> mAdapter = new NavigationDrawerAdapter(titles, icons, activeIcons, activePosition);
		mRecyclerView.setAdapter(mAdapter);

		mGestureDetector = new GestureDetector(BaseNavigationDrawerActivity.this, new GestureDetector.SimpleOnGestureListener() {

			@Override public boolean onSingleTapUp(MotionEvent e) {
				return true;
			}

		});

		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
		mDrawerLayout.setFocusableInTouchMode(true);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.home, R.string.work){
			@Override
			public void onDrawerOpened(View drawerView) {

				RecyclerView recyclerView = (RecyclerView) findViewById(R.id.navigation_drawer);
				NavigationDrawerAdapter adapter = (NavigationDrawerAdapter) recyclerView.getAdapter();
				adapter.notifyDataSetChanged();
				super.onDrawerOpened(drawerView);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();
	}

	private void setupListeners(int activePosition){
		BaseNavigationActivityEventHandler eventHandler = new BaseNavigationActivityEventHandler(this, activePosition);
		((RecyclerView) findViewById(R.id.navigation_drawer)).addOnItemTouchListener(eventHandler);
	}
}