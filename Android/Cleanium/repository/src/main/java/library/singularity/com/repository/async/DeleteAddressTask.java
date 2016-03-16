package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import library.singularity.com.dao.database.DaoHandler;
import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.User;

public class DeleteAddressTask extends AsyncTask<Void, Void, Void> {

    public interface IOnDeleteAddressListener {
        void onAddressDeleted();
        void onAddressesFailedToDelete();
    }

    private IOnDeleteAddressListener listener;
    private boolean success;
    private Context context;
    private Address address;

    public DeleteAddressTask(Context context, Address address, IOnDeleteAddressListener listener) {
        this.address = address;
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (this.address == null) {
            success = false;
            return null;
        }

        DaoHandler daoHandler = DaoHandler.getInstance(context);
        daoHandler.open();
        daoHandler.deleteAddress(address);
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
            listener.onAddressDeleted();
            return;
        }

        if (listener == null) return;
        listener.onAddressesFailedToDelete();
    }
}