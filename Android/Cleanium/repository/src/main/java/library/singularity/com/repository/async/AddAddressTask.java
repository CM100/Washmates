package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import library.singularity.com.dao.database.DaoHandler;
import library.singularity.com.data.model.Address;

public class AddAddressTask extends AsyncTask<Void, Void, Void> {

    public interface IOnAddAddressListener {
        void onAddressAdded();
        void onAddressFailedToAdd();
    }

    private Address address;
    private IOnAddAddressListener listener;
    private boolean success;
    private Context context;

    public AddAddressTask(Context context, Address address, IOnAddAddressListener listener) {
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
        daoHandler.addAddress(address);
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
            listener.onAddressAdded();
            return;
        }

        if (listener == null) return;
        listener.onAddressFailedToAdd();
    }
}