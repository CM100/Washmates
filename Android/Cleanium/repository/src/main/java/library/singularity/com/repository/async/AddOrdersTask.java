package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import library.singularity.com.dao.database.DaoHandler;
import library.singularity.com.data.model.Order;


public class AddOrdersTask extends AsyncTask<Void, Void, Void> {

    public interface IOnAddOrdersListener {
        void onOrdersAdded();
        void onOrdersFailedToAdd();
    }

    private List<Order> orders;
    private IOnAddOrdersListener listener;
    private boolean success;
    private Context context;

    public AddOrdersTask(Context context, List<Order> orders, IOnAddOrdersListener listener) {
        this.orders = orders;
        this.listener = listener;
        this.context = context;
    }


    @Override
    protected Void doInBackground(Void... params) {
        if (this.orders == null) {
            success = false;
            return null;
        }

        DaoHandler daoHandler = DaoHandler.getInstance(context);
        daoHandler.open();
        daoHandler.addOrders(orders);
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
            listener.onOrdersAdded();
            return;
        }

        if (listener == null) return;
        listener.onOrdersFailedToAdd();
    }
}
