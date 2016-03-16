package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import library.singularity.com.dao.database.DaoHandler;
import library.singularity.com.data.model.Address;

public class UpdateAddressTask extends AsyncTask<Void, Void, Void> {

    public interface IOnAddressUpdateListener {
        void onAddressUpdated();
        void onAddressFailedToUpdate();
    }

    private Address address;
    private IOnAddressUpdateListener listener;
    private boolean success;
    private Context context;

    public UpdateAddressTask(Context context, Address address, IOnAddressUpdateListener listener) {
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
        daoHandler.updateAddress(address);
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
            listener.onAddressUpdated();
            return;
        }

        if (listener == null) return;
        listener.onAddressFailedToUpdate();
    }
}