package singularity.com.cleanium.adapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.singularity.cleanium.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import library.singularity.com.data.model.Address;

public class LocationSpinnerAdapter extends ArrayAdapter<Address> {

    private List<Address> addresses;
    private int textViewResourceId;

    static class ViewHolder {
        @Bind(R.id.location) TextView location;
        @Bind(R.id.address) TextView address;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public LocationSpinnerAdapter(Context context, int textViewResourceId, List<Address> addresses) {
        super(context, textViewResourceId, addresses);

        this.addresses = addresses;
        this.textViewResourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater inflater = ((AppCompatActivity) parent.getContext()).getLayoutInflater();
            view = inflater.inflate(this.textViewResourceId, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.location.setText(addresses.get(position).getLocation() + " " + getContext().getString(R.string.address));
        holder.address.setText(addresses.get(position).getStreet());

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
