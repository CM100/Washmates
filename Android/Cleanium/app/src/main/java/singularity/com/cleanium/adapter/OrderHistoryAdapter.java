package singularity.com.cleanium.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.singularity.cleanium.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import library.singularity.com.data.model.Order;
import singularity.com.cleanium.ui.fragments.BillFragmentDialog;
import singularity.com.cleanium.utils.Utils;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> implements View.OnClickListener {

    List<Order> orders;
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
    Typeface thin, regular;

    public OrderHistoryAdapter(Context context, List<Order> orders) {
        this.orders = orders;
        thin = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
        regular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.pickUpDate)
        TextView pickUpDate;

        @Bind(R.id.orderStatus)
        TextView orderStatus;

        @Bind(R.id.dropOffDate)
        TextView dropOffDate;

        @Bind(R.id.orderPrice)
        TextView orderPrice;

        @Bind(R.id.pickup)
        TextView pickup;

        @Bind(R.id.drop_off)
        TextView dropOff;

        @Bind(R.id.cost)
        TextView cost;

        @Bind(R.id.order_history_row_wrapper)
        View wrapper;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            pickup.setTypeface(thin);
            pickUpDate.setTypeface(regular);
            dropOff.setTypeface(thin);
            dropOffDate.setTypeface(regular);
            cost.setTypeface(thin);
            orderPrice.setTypeface(regular);
            orderStatus.setTypeface(regular);
        }
    }

    @Override
    public OrderHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_row, parent, false);
        ViewHolder vhItem = new ViewHolder(v);
        return vhItem;
    }

    @Override
    public void onBindViewHolder(OrderHistoryAdapter.ViewHolder holder, int position) {
        Order order = orders.get(position);
        Date pickupDate = order.getPickUpSchedule().getFromDate();
        String pickupDateString = pickupDate == null ? null : format.format(pickupDate);
        holder.pickUpDate.setText(pickupDateString);

        holder.orderStatus.setText(order.getStatus());

        Date deliveryDate = order.getDeliverySchedule().getFromDate();
        String deliveryDateString = deliveryDate == null ? null : format.format(deliveryDate);
        holder.dropOffDate.setText(deliveryDateString);

        double total = 0;
        if (order.getBill() != null) {
            total = order.getBill().getTotal();
        }

        holder.orderPrice.setText(total + "$");
        holder.wrapper.setBackgroundColor(Color.parseColor(order.getColor()));
        holder.orderStatus.setTextColor(Color.parseColor(order.getColor()));
        holder.wrapper.setTag(order);
        holder.wrapper.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        if (orders == null) return 0;

        return orders.size();
    }

    @Override
    public void onClick(View v) {
        Order order = (Order) v.getTag();
        Context context = v.getContext();
        if (order == null || order.getBill() == null) {
            showNoBillDialog(context);
            return;
        }

        BillFragmentDialog fragment = new BillFragmentDialog();
        fragment.setBill(order.getBill());
        fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "dialog");
    }

    void showNoBillDialog(Context context) {
        Utils.showAlertDialog(context,
                context.getString(R.string.error_title),
                context.getString(R.string.invalid_order_bill),
                context.getString(R.string.ok)
        );
    }
}