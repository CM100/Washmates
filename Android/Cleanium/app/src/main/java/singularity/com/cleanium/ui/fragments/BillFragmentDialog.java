package singularity.com.cleanium.ui.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.singularity.cleanium.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import library.singularity.com.data.model.Bill;
import singularity.com.cleanium.adapter.BillAdapter;
import singularity.com.cleanium.utils.DividerItemDecoration;

public class BillFragmentDialog extends DialogFragment {

    public static final String TAG = "BillFragmentDialog";

    private Bill bill;

    @Bind(R.id.bill_breakdown)
    RecyclerView billBreakdown;

    @Bind(R.id.status_notes)
    TextView statusNotes;

    public BillFragmentDialog() { }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        View rootView = inflater.inflate(R.layout.fragment_bill, container, false);
        ButterKnife.bind(this, rootView);

        Typeface thin = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        statusNotes.setTypeface(thin);
        prepareRecyclerView();
        renderBill();
        return rootView;
    }

    void prepareRecyclerView() {
        billBreakdown.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        billBreakdown.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 0xff000000);
        billBreakdown.addItemDecoration(itemDecoration);
    }

    void renderBill() {
        BillAdapter adapter = new BillAdapter(getActivity(), bill);
        billBreakdown.setAdapter(adapter);
    }
}