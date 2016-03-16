package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import library.singularity.com.dao.database.DaoHandler;

public class DeleteLaundryScheduleTask extends AsyncTask<Void, Void, Void> {

    public interface IOnDeleteLaundryScheduleListener {
        void onLaundryDeleted();
    }

    private IOnDeleteLaundryScheduleListener listener;
    private Context context;

    public DeleteLaundryScheduleTask(Context context, IOnDeleteLaundryScheduleListener listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DaoHandler daoHandler = DaoHandler.getInstance(context);
        daoHandler.open();
        daoHandler.deleteLaundrySchedules();
        daoHandler.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        context = null;
        listener.onLaundryDeleted();
    }
}