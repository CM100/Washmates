package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import library.singularity.com.dao.database.DaoHandler;
import library.singularity.com.data.model.TimeSlot;

public class AddTimeSlotTask extends AsyncTask<Void, Void, Void> {

    public interface IOnAddTimeSlotListener {
        void onTimeSlotAdded();
        void onTimeSlotFailedToAdd();
    }

    private TimeSlot timeSlot;
    private IOnAddTimeSlotListener listener;
    private boolean success;
    private Context context;

    public AddTimeSlotTask(Context context, TimeSlot timeSlot, IOnAddTimeSlotListener listener) {
        this.timeSlot = timeSlot;
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (this.timeSlot == null) {
            success = false;
            return null;
        }

        DaoHandler daoHandler = DaoHandler.getInstance(context);
        daoHandler.open();
        daoHandler.addTimeSlot(timeSlot);
        daoHandler.close();
        success = true;
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        context = null;
        if (success) {
            if (listener == null) return;
            listener.onTimeSlotAdded();
            return;
        }

        if (listener == null) return;
        listener.onTimeSlotFailedToAdd();
    }
}