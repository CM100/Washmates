package singularity.com.cleanium.adapter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.singularity.cleanium.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DateSpinnerAdapter extends ArrayAdapter<Date> {

    private List<Date> dates;
    private int textViewResourceId;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat dayOfWeekFormat;

    public DateSpinnerAdapter(Context context, int textViewResourceId, List<Date> dates) {
        super(context, textViewResourceId, dates);

        this.textViewResourceId = textViewResourceId;
        this.dates = dates;
        dateFormat = new SimpleDateFormat("MM/dd");
        dayOfWeekFormat = new SimpleDateFormat("EEEE");
    }

    static class ViewHolder {
        @Bind(R.id.date) TextView date;
        @Bind(R.id.day_of_week) TextView dayOfWeek;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
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


        holder.date.setText(dateFormat.format(dates.get(position)));
        holder.dayOfWeek.setText(dayOfWeekFormat.format(dates.get(position)));

        return view;
    }

    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
