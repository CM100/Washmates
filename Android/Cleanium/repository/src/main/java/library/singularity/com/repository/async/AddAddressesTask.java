package library.singularity.com.repository.async;


import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import library.singularity.com.dao.database.DaoHandler;
import library.singularity.com.data.model.Address;

public class AddAddressesTask extends AsyncTask<Void, Void, Void> {

    private List<Address> addresses;
    private AddAddressTask.IOnAddAddressListener listener;
    private boolean success;
    private Context context;

    public AddAddressesTask(Context context, List<Address> addresses, AddAddressTask.IOnAddAddressListener listener) {
        this.addresses = addresses;
        this.listener = listener;
        this.context = context;
    }


    @Override
    protected Void doInBackground(Void... params) {
        if (this.addresses == null) {
            success = false;
            return null;
        }

        DaoHandler daoHandler = DaoHandler.getInstance(context);
        daoHandler.open();
        daoHandler.addAddresses(addresses);
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