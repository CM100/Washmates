package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import library.singularity.com.dao.database.DaoHandler;
import library.singularity.com.data.model.User;

public class GetUserTask extends AsyncTask<Void, Void, Void> {

    public interface IOnGetUserListener {
        void onUserObtained(User user);
        void onUserFailedToObtain();
    }

    private User user;
    private IOnGetUserListener listener;
    private Context context;

    public GetUserTask(Context context, IOnGetUserListener listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        DaoHandler daoHandler = DaoHandler.getInstance(context);
        daoHandler.open();
        user = daoHandler.getCurrentUser();
        daoHandler.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        context = null;
        if (user == null) {
            if (listener == null) return;
            listener.onUserFailedToObtain();
            return;
        }

        if (listener == null) return;
        listener.onUserObtained(user);
    }
}
