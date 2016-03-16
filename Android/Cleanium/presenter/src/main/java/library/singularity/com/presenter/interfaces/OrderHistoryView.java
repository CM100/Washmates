package library.singularity.com.presenter.interfaces;

import android.content.Context;

import java.util.List;

import library.singularity.com.data.model.Order;

public interface OrderHistoryView {
    void showProgressDialog();
    void closeProgressDialog();
    void setValues(List<Order> orders);
    Context getContext();
}
