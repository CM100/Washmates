package singularity.com.cleanium.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.singularity.cleanium.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import library.singularity.com.data.model.Bill;
import library.singularity.com.data.model.Service;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {

    Bill bill;
    Typeface thin, regular;

    public BillAdapter(Context context, Bill bill) {
        this.bill = bill;
        thin = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        regular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.service_title)
        TextView title;

        @Bind(R.id.service_price)
        TextView price;

        @Bind(R.id.breakdown_row_wrapper)
        View wrapper;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            title.setTypeface(thin);
            price.setTypeface(regular);
        }
    }

    @Override
    public BillAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_breakdown_row, parent, false);
        ViewHolder vhItem = new ViewHolder(v);
        return vhItem;
    }

    @Override
    public void onBindViewHolder(BillAdapter.ViewHolder holder, int position) {
        holder.title.setVisibility(View.VISIBLE);
        holder.price.setVisibility(View.VISIBLE);
        holder.wrapper.setPadding(0,
                holder.wrapper.getResources().getDimensionPixelSize(R.dimen.bill_row_vertical_padding),
                0,
                holder.wrapper.getResources().getDimensionPixelSize(R.dimen.bill_row_vertical_padding));
        if (bill.getServices().size() > position) {
            Service service = bill.getServices().get(position);
            holder.title.setText(service.getTitle() + " x " + service.getQuantity());
            holder.price.setText("$" + service.getPrice());
        } else if (bill.getServices().size() == position) {
            holder.title.setText(holder.title.getContext().getString(R.string.discount));
            double discount = bill.getDiscount() == null ? 0 : bill.getDiscount().getAmount();
            holder.price.setText("$"+discount);
        } else if (bill.getServices().size()+1 == position) {
            holder.wrapper.setPadding(0,
                    holder.wrapper.getResources().getDimensionPixelSize(R.dimen.bill_row_total_top_padding),
                    0,
                    holder.wrapper.getResources().getDimensionPixelSize(R.dimen.bill_row_vertical_padding));

            holder.title.setText(holder.title.getContext().getString(R.string.total));
            holder.price.setText("$" + bill.getTotal());
        } else {
            holder.wrapper.setPadding(0,
                    holder.wrapper.getResources().getDimensionPixelSize(R.dimen.bill_row_total_vertical_padding),
                    0,
                    holder.wrapper.getResources().getDimensionPixelSize(R.dimen.bill_row_total_vertical_padding));
            holder.title.setVisibility(View.GONE);
            holder.price.setVisibility(View.GONE);
            holder.title.setText("");
            holder.price.setText("");
        }
    }

    @Override
    public int getItemCount() {
        if (bill.getServices() == null) return 0;

        return bill.getServices().size()+4;
    }
}
