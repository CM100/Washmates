package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import library.singularity.com.dao.database.DaoHandler;

public class DeleteAddressesTask extends AsyncTask<Void, Void, Void> {

    public interface IOnDeleteAddressesListener {
        void onAddressesDeleted();
    }

    private IOnDeleteAddressesListener listener;
    private Context context;

    public DeleteAddressesTask(Context context, IOnDeleteAddressesListener listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DaoHandler daoHandler = DaoHandler.getInstance(context);
        daoHandler.open();
        daoHandler.deleteAddresses();
        daoHandler.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        context = null;
        listener.onAddressesDeleted();
    }
}