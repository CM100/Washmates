package singularity.com.cleanium.ui.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.singularity.cleanium.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import library.singularity.com.data.model.Order;
import library.singularity.com.presenter.OrderHistoryPresenter;
import library.singularity.com.presenter.interfaces.OrderHistoryView;
import singularity.com.cleanium.adapter.OrderHistoryAdapter;

public class OrderHistoryActivity extends BaseNavigationDrawerActivity implements OrderHistoryView {

    public static final String TAG = "WashingStatusNotesDialogFragment";

    FragmentManager fm = getSupportFragmentManager();

    @Bind(R.id.order_list)
    RecyclerView recyclerView;

    private ProgressDialog progressDialog;
    private OrderHistoryPresenter orderHistoryPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        ButterKnife.bind(this);
        setupToolbarAndNavigationDrawer(4);
        prepareRecyclerView();
        orderHistoryPresenter = new OrderHistoryPresenter(this);
        orderHistoryPresenter.onViewCreated();
    }

    void prepareRecyclerView() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void setValues(final List<Order> orders) {
        RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> mAdapter = new OrderHistoryAdapter(this, orders);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showProgressDialog() {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage(getResources().getString(R.string.loading_message));
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    @Override
    public void closeProgressDialog() {
        if(this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
    }

    @Override
    public Context getContext() {
        return this;
    }
}
