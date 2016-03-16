package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import library.singularity.com.dao.database.DaoHandler;

public class DeleteUsersTask extends AsyncTask<Void, Void, Void> {

    public interface IOnDeleteUsersListener {
        void onUsersDeleted();
    }

    private IOnDeleteUsersListener listener;
    private Context context;

    public DeleteUsersTask(Context context, IOnDeleteUsersListener listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DaoHandler daoHandler = DaoHandler.getInstance(context);
        daoHandler.open();
        daoHandler.deleteUsers();
        daoHandler.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        context = null;
        listener.onUsersDeleted();
    }
}
