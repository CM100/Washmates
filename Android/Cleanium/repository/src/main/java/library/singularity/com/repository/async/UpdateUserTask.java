package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import library.singularity.com.dao.database.DaoHandler;
import library.singularity.com.data.model.User;

public class UpdateUserTask extends AsyncTask<Void, Void, Void> {

    public interface IOnUpdateUserListener {
        void onUserUpdated();
        void onUserFailedToUpdate();
    }

    private User user;
    private IOnUpdateUserListener listener;
    private boolean success;
    private Context context;

    public UpdateUserTask(Context context, User user, IOnUpdateUserListener listener) {
        this.user = user;
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (this.user == null) {
            success = false;
            return null;
        }

        DaoHandler daoHandler = DaoHandler.getInstance(context);
        daoHandler.open();
        daoHandler.updateUserWithId(user);
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
            listener.onUserUpdated();
            return;
        }

        if (listener == null) return;
        listener.onUserFailedToUpdate();
    }
}