package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import library.singularity.com.dao.database.DaoHandler;

public class DeleteTimeSlotsTask extends AsyncTask<Void, Void, Void> {

    public interface IOnDeleteTimeSlotsListener {
        void onTimeSlotsDeleted();
    }

    private IOnDeleteTimeSlotsListener listener;
    private Context context;

    public DeleteTimeSlotsTask(Context context, IOnDeleteTimeSlotsListener listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DaoHandler daoHandler = DaoHandler.getInstance(context);
        daoHandler.open();
        daoHandler.deleteTimeSlots();
        daoHandler.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        context = null;
        listener.onTimeSlotsDeleted();
    }
}