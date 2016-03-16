package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import library.singularity.com.dao.database.DaoHandler;
import library.singularity.com.data.model.LaundrySchedule;
import library.singularity.com.data.model.User;

public class GetLaundryScheduleTask extends AsyncTask<Void, Void, Void> {

    public interface IOnGetLaundryScheduleListener {
        void onLaundryScheduleObtained(LaundrySchedule laundrySchedule);
        void oLaundryScheduleFailedToObtain();
    }

    private LaundrySchedule laundrySchedule;
    private IOnGetLaundryScheduleListener listener;
    private Context context;

    public GetLaundryScheduleTask(Context context, IOnGetLaundryScheduleListener listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DaoHandler daoHandler = DaoHandler.getInstance(context);
        daoHandler.open();
        laundrySchedule = daoHandler.getLaundrySchedule();
        daoHandler.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        context = null;
        if (laundrySchedule == null) {
            if (listener == null) return;
            listener.oLaundryScheduleFailedToObtain();
            return;
        }

        if (listener == null) return;
        listener.onLaundryScheduleObtained(laundrySchedule);
    }
}
