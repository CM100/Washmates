package library.singularity.com.repository.async;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import library.singularity.com.dao.database.DaoHandler;
import library.singularity.com.data.model.Configuration;
import library.singularity.com.data.model.Order;
import library.singularity.com.data.model.OrderStatus;
import library.singularity.com.repository.SharedPreferencesProvider;

public class GetCurrentOrderAsyncTask extends AsyncTask<Void, Void, Void> {

    public interface IOnOrderObtainedListener {
        void OnOrderObtained(Order order);
    }

    private IOnOrderObtainedListener listener;
    private Context context;
    private Order order;

    public GetCurrentOrderAsyncTask(Context context, IOnOrderObtainedListener listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Configuration configuration = SharedPreferencesProvider.getConfiguration(context);

        List<String> historyStatuses = new ArrayList<>();
        for (OrderStatus status : configuration.getOrderStatuses()) {
            if (status.isShowOnHistory()) {
                historyStatuses.add(status.getName());
            }
        }

        DaoHandler daoHandler = DaoHandler.getInstance(context);
        daoHandler.open();

        order = daoHandler.getCurrentOrder(historyStatuses);
        daoHandler.close();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        context = null;

        if (listener == null) return;
        listener.OnOrderObtained(order);
    }
}


