package library.singularity.com.repository;

import android.content.Context;

import java.util.List;

import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.LaundrySchedule;
import library.singularity.com.data.model.Order;
import library.singularity.com.data.model.TimeSlot;
import library.singularity.com.data.model.User;
import library.singularity.com.repository.async.AddAddressTask;
import library.singularity.com.repository.async.AddAddressTask.*;
import library.singularity.com.repository.async.AddAddressesTask;
import library.singularity.com.repository.async.AddLaundryScheduleTask;
import library.singularity.com.repository.async.AddLaundryScheduleTask.*;
import library.singularity.com.repository.async.AddOrdersTask;
import library.singularity.com.repository.async.AddTimeSlotTask;
import library.singularity.com.repository.async.AddTimeSlotTask.*;
import library.singularity.com.repository.async.AddUserTask;
import library.singularity.com.repository.async.AddUserTask.*;
import library.singularity.com.repository.async.DeleteAddressTask;
import library.singularity.com.repository.async.DeleteAddressTask.*;
import library.singularity.com.repository.async.DeleteAddressesTask;
import library.singularity.com.repository.async.DeleteAddressesTask.*;
import library.singularity.com.repository.async.DeleteLaundryScheduleTask;
import library.singularity.com.repository.async.DeleteLaundryScheduleTask.*;
import library.singularity.com.repository.async.DeleteTimeSlotsTask;
import library.singularity.com.repository.async.DeleteTimeSlotsTask.*;
import library.singularity.com.repository.async.DeleteUsersTask;
import library.singularity.com.repository.async.DeleteUsersTask.*;
import library.singularity.com.repository.async.GetCurrentOrderAsyncTask;
import library.singularity.com.repository.async.GetLaundryScheduleTask;
import library.singularity.com.repository.async.GetLaundryScheduleTask.*;
import library.singularity.com.repository.async.GetOrderHistoryTask;
import library.singularity.com.repository.async.GetUserTask;
import library.singularity.com.repository.async.GetUserTask.*;
import library.singularity.com.repository.async.UpdateAddressTask;
import library.singularity.com.repository.async.UpdateAddressTask.*;
import library.singularity.com.repository.async.UpdateUserTask;
import library.singularity.com.repository.async.UpdateUserTask.*;

public class Repository {

    private static Repository repository;
    private Context context;

    /**
     * Gets an instance of a repository to use.
     * @param context Context of the repository.
     * @return Repository which can be used to obtain data.
     */
    public static Repository getInstance(Context context) {
        if (repository == null || context == null) {
            repository = new Repository(context);
        }

        return repository;
    }

    private Repository(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Clears memory and all its references
     */
    public void destroy() {
        this.context = null;
    }

    /**
     * Adds user to local database on separate thread.
     * @param user User to add to database
     * @param listener Listener which will be invoked on success or fail
     */
    public void addUser(User user, IOnAddUserListener listener) {
        new AddUserTask(context, user, listener).execute();
    }

    /**
     * Adds address to local database on separate thread.
     * @param address Address to add to database
     * @param listener Listener which will be invoked on success or fail
     */
    public void addAddress(Address address, IOnAddAddressListener listener) {
        new AddAddressTask(context, address, listener).execute();
    }

    public void addAddresses(List<Address> addresses, IOnAddAddressListener listener){
        new AddAddressesTask(context, addresses, listener).execute();
    }

    /**
     * Gets user with specified ID from local database on separate thread.
     * @param listener Listener which will be invoked on success or fail
     */
    public void getCurrentUser(IOnGetUserListener listener) {
        new GetUserTask(context, listener).execute();
    }

    /**
     * Deletes users and their addresses from local database on separate thread.
     * @param listener Listener which will be invoked on success
     */
    public void deleteUsers(IOnDeleteUsersListener listener) {
        new DeleteUsersTask(context, listener).execute();
    }

    /**
     * Deletes addresses from local database on separate thread.
     * @param listener Listener which will be invoked on success
     */
    public void deleteAddresses(IOnDeleteAddressesListener listener) {
        new DeleteAddressesTask(context, listener).execute();
    }

    /**
     * Deletes given address from local database on separate thread.
     * @param address Address to delete
     * @param listener Listener which will be invoked on success
     */
    public void deleteAddress(Address address, IOnDeleteAddressListener listener) {
        new DeleteAddressTask(context, address, listener).execute();
    }

    /**
     * Updates user in local database on separate thread.
     * @param user User with new values to update in database
     * @param listener Listener which will be invoked on success or fail
     */
    public void updateUser(User user, IOnUpdateUserListener listener) {
        new UpdateUserTask(context, user, listener).execute();
    }

    /**
     * Updates address in local database on separate thread.
     * @param address Address with new values to update in database
     * @param listener Listener which will be invoked on success or fail
     */
    public void updateAddress(Address address, IOnAddressUpdateListener listener) {
        new UpdateAddressTask(context, address, listener).execute();
    }

    /**
     * Adds laundry schedule and its reserved time slots to local database on separate thread.
     * @param laundrySchedule Laundry schedule to add to database
     * @param listener Listener which will be invoked on success or fail
     */
    public void addLaundrySchedule(LaundrySchedule laundrySchedule, IOnAddLaundryScheduleListener listener) {
        new AddLaundryScheduleTask(context, laundrySchedule, listener).execute();
    }

    /**
     * Adds reserved time slot to local database on separate thread.
     * @param timeSlot Time slot to add to database
     * @param listener Listener which will be invoked on success or fail
     */
    public void addTimeSlot(TimeSlot timeSlot, IOnAddTimeSlotListener listener) {
        new AddTimeSlotTask(context, timeSlot, listener).execute();
    }

    /**
     * Gets laundry schedule from local database on separate thread.
     * @param listener Listener which will be invoked on success or fail
     */
    public void getOccupidedTimeSlots(IOnGetLaundryScheduleListener listener) {
        new GetLaundryScheduleTask(context, listener).execute();
    }

    /**
     * Deletes laundry schedules from local database on separate thread.
     * @param listener Listener which will be invoked on success
     */
    public void deleteAddresses(IOnDeleteLaundryScheduleListener listener) {
        new DeleteLaundryScheduleTask(context, listener).execute();
    }

    /**
     * Deletes time slots from local database on separate thread.
     * @param listener Listener which will be invoked on success
     */
    public void deleteTimeSlots(IOnDeleteTimeSlotsListener listener) {
        new DeleteTimeSlotsTask(context, listener).execute();
    }

    /**
     * Adds orders to local database
     * @param orders List of orders to add to database
     * @param listener Listener which will be invoked on success
     */
    public void addOrders(List<Order> orders, AddOrdersTask.IOnAddOrdersListener listener){
        new AddOrdersTask(context, orders, listener).execute();
    }

    /**
     * Gets entire order history for a user
     * @param listener Listener which will be invoked on success
     */
    public void getOrderHistory(GetOrderHistoryTask.IOnOrderHistoryObtainedListener listener) {
        new GetOrderHistoryTask(context, listener).execute();
    }

    public void getCurrentOrder(GetCurrentOrderAsyncTask.IOnOrderObtainedListener listener) {
        new GetCurrentOrderAsyncTask(context, listener).execute();
    }
}
