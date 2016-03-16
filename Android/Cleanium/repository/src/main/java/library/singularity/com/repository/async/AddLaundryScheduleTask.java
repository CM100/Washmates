package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import library.singularity.com.dao.database.DaoHandler;
import library.singularity.com.data.model.LaundrySchedule;

public class AddLaundryScheduleTask extends AsyncTask<Void, Void, Void> {

    public interface IOnAddLaundryScheduleListener {
        void onLaundryScheduleAdded();
        void onLaundryScheduleFailedToAdd();
    }

    private LaundrySchedule laundrySchedule;
    private IOnAddLaundryScheduleListener listener;
    private boolean success;
    private Context context;

    public AddLaundryScheduleTask(Context context, LaundrySchedule laundrySchedule, IOnAddLaundryScheduleListener listener) {
        this.laundrySchedule = laundrySchedule;
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (this.laundrySchedule == null) {
            success = false;
            return null;
        }

        DaoHandler daoHandler = DaoHandler.getInstance(context);
        daoHandler.open();
        daoHandler.deleteLaundrySchedules();
        daoHandler.deleteTimeSlots();
        daoHandler.addLaundrySchedule(laundrySchedule);
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
            listener.onLaundryScheduleAdded();
            return;
        }

        if (listener == null) return;
        listener.onLaundryScheduleFailedToAdd();
    }
}