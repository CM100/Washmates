package library.singularity.com.presenter;

import com.parse.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.Configuration;
import library.singularity.com.data.model.LaundrySchedule;
import library.singularity.com.data.model.Order;
import library.singularity.com.data.model.TimeSlot;
import library.singularity.com.data.model.User;
import library.singularity.com.network.orders.OrderWebServiceInterfaces;
import library.singularity.com.network.orders.OrderWebServiceProvider;
import library.singularity.com.network.schedule.LaundryScheduleWebServiceInterfaces;
import library.singularity.com.network.schedule.LaundryScheduleWebServiceProvider;
import library.singularity.com.presenter.interfaces.SchedulePickupView;
import library.singularity.com.presenter.interfaces.SignUpPaymentView;
import library.singularity.com.repository.Repository;
import library.singularity.com.repository.SharedPreferencesProvider;
import library.singularity.com.repository.async.GetCurrentOrderAsyncTask;
import library.singularity.com.repository.async.GetUserTask;

public class SchedulePickupPresenter implements LaundryScheduleWebServiceInterfaces.IOnLaundrySchedulesObtainListener,
        GetUserTask.IOnGetUserListener, GetCurrentOrderAsyncTask.IOnOrderObtainedListener {

    private SchedulePickupView schedulePickupView;
    private User user;
    private Address selectedAddress;
    private LaundrySchedule laundrySchedule;
    private Configuration configuration;
    private Order existingCurrentOrder;

    public SchedulePickupPresenter(SchedulePickupView schedulePickupView) {
        this.schedulePickupView = schedulePickupView;
    }

    public void finish() { schedulePickupView = null; }

    public void onViewCreated() {
        if (schedulePickupView == null) return;

        schedulePickupView.showProgressDialog();
        Repository.getInstance(schedulePickupView.getContext()).getCurrentOrder(this);
    }

    @Override
    public void OnOrderObtained(Order order) {
        if (schedulePickupView == null) return;

        this.existingCurrentOrder = order;
        Repository.getInstance(schedulePickupView.getContext()).getCurrentUser(this);
    }

    @Override
    public void onUserObtained(User user) {
        if (schedulePickupView == null) return;

        if(user == null) {
            schedulePickupView.stopProgressDialog();
            return;
        }

        this.user = user;
        configuration = SharedPreferencesProvider.getConfiguration(schedulePickupView.getContext());

        LaundryScheduleWebServiceProvider.getLaundrySchedules(user, configuration.getDriverAvailabilityInterval(), this);
    }

    @Override
    public void onUserFailedToObtain() {
        if (schedulePickupView == null) return;

        schedulePickupView.stopProgressDialog();
    }

    @Override
    public void onLaundrySchedulesObtained(LaundrySchedule laundrySchedule) {
        if (schedulePickupView == null) return;

        this.laundrySchedule = laundrySchedule;
        schedulePickupView.setupAddressesChoices(this.user.getAddresses());
        if (this.user.getAddresses().size() == 1) {
            schedulePickupView.selectDefaultAddress();
        }

        schedulePickupView.stopProgressDialog();
    }

    public void onAddressSelected(int position) {
        if (schedulePickupView == null || user == null || laundrySchedule == null) return;

        Address chosenAddress = this.user.getAddresses().get(position);
        selectedAddress = chosenAddress;
        List<TimeSlot> timeSlotsForPostCode = laundrySchedule.getTimeSlotsForPostCode(chosenAddress.getPostalCode());

        if (timeSlotsForPostCode == null) {
            schedulePickupView.setupPickupDateDropDown(new ArrayList<Date>());
            schedulePickupView.setupDeliveryDateDropDown(new ArrayList<Date>());
            schedulePickupView.setupPickupTimeDropDown(new ArrayList<String>());
            schedulePickupView.setupDeliveryTimeDropDown(new ArrayList<String>());
            return;
        }

        List<Date> datesToChoose = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Calendar oneWeekFromNow = Calendar.getInstance();
        oneWeekFromNow.add(Calendar.DAY_OF_MONTH, 7);
        for (TimeSlot timeSlot : timeSlotsForPostCode) {
            calendar.setTime(timeSlot.getFromDate());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (!datesToChoose.contains(calendar.getTime()) && calendar.before(oneWeekFromNow)) {
                datesToChoose.add(calendar.getTime());
            }
        }

        schedulePickupView.setupPickupDateDropDown(datesToChoose);
        schedulePickupView.setupDeliveryDateDropDown(datesToChoose);

        List<String> dummyList = new ArrayList<>();
        dummyList.add("");

        schedulePickupView.setupPickupTimeDropDown(dummyList);
        schedulePickupView.setupDeliveryTimeDropDown(dummyList);

        schedulePickupView.disableDeliveryDate(false);
        schedulePickupView.disablePickupTime(false);
        schedulePickupView.disableDeliveryTime(false);
    }

    @Override
    public void onLaundrySchedulesFaildToObtain(Exception error) {
        if (schedulePickupView == null) return;

        schedulePickupView.stopProgressDialog();
        if (error instanceof ParseException) {
            if (((ParseException) error).getCode() == 100) {
                schedulePickupView.showNoInternetConnectionDialog();
                return;
            }
        }

        schedulePickupView.showError(error.getMessage());
    }

    public void onPickUpDateSelected(Date selectedDate) {
        if (schedulePickupView == null) return;

        List<String> timeSlotStrings = getTimeSlotStrings(selectedDate);
        schedulePickupView.setupPickupTimeDropDown(timeSlotStrings);

        List<Date> deliveryDates = filterTimeSlotsForDeliveryBasedOnPickupDate(selectedDate);
        Date previouslySelectedDelivery = schedulePickupView.getSelectedDeliveryDate();
        schedulePickupView.setupDeliveryDateDropDown(deliveryDates);
        selectPreviouslySelectedDeliveryDay(deliveryDates, previouslySelectedDelivery);

        schedulePickupView.disablePickupTime(true);
        schedulePickupView.disableDeliveryDate(true);
    }

    private List<String> getTimeSlotStrings(Date selectedDate) {
        List<TimeSlot> timeSlotsForPostCode = laundrySchedule.getTimeSlotsForPostCode(selectedAddress.getPostalCode());
        List<TimeSlot> timeSlots = filterTimeSlotsForSingleDay(timeSlotsForPostCode, selectedDate);
        List<String> timeSlotStrings = new ArrayList<>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        SimpleDateFormat amPmFormat = new SimpleDateFormat("a");
        for (TimeSlot timeSlot : timeSlots) {
            timeSlotStrings.add(timeFormat.format(timeSlot.getFromDate()) + " - " +
                    timeFormat.format(timeSlot.getToDate()) + " " + amPmFormat.format(timeSlot.getToDate()));
        }

        return timeSlotStrings;
    }

    private List<Date> filterTimeSlotsForDeliveryBasedOnPickupDate(Date pickupDate) {
        if (schedulePickupView == null) return new ArrayList<>();

        List<TimeSlot> timeSlotsForPostCode = laundrySchedule.getTimeSlotsForPostCode(selectedAddress.getPostalCode());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pickupDate);
        if (schedulePickupView.isWashAndTryActive()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Calendar deliveryMaxCalendar = Calendar.getInstance();
        deliveryMaxCalendar.setTime(pickupDate);
        deliveryMaxCalendar.add(Calendar.DAY_OF_MONTH, 7);

        List<Date> deliverySlots = new ArrayList<>();
        Calendar timeSlotCalendar = Calendar.getInstance();
        for (TimeSlot timeSlot : timeSlotsForPostCode) {
            if (calendar.getTime().after(timeSlot.getFromDate()) ||
                    deliveryMaxCalendar.getTime().before(timeSlot.getFromDate())) { continue; }

            timeSlotCalendar.setTime(timeSlot.getFromDate());
            timeSlotCalendar.set(Calendar.HOUR_OF_DAY, 0);
            timeSlotCalendar.set(Calendar.MINUTE, 0);
            timeSlotCalendar.set(Calendar.SECOND, 0);
            timeSlotCalendar.set(Calendar.MILLISECOND, 0);
            if (!deliverySlots.contains(timeSlotCalendar.getTime())) {
                deliverySlots.add(timeSlotCalendar.getTime());
            }
        }

        return deliverySlots;
    }

    private List<TimeSlot> filterTimeSlotsForSingleDay(List<TimeSlot> allSlots, Date selectedDate) {
        if (allSlots == null || selectedDate == null) return new ArrayList<>();

        Date dayAfterChosenDay = getTomorrowDate(selectedDate);
        List<TimeSlot> filteredSlots = new ArrayList<>();
        for (TimeSlot slot : allSlots) {
            if (slot.getFromDate().after(selectedDate) && slot.getFromDate().before(dayAfterChosenDay)) {
                filteredSlots.add(slot);
            }
        }

        return filteredSlots;
    }

    private Date getTomorrowDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    private void selectPreviouslySelectedDeliveryDay(List<Date> deliveryDates, Date previousDay) {
        if (schedulePickupView == null) return;

        int index = -1;

        if (deliveryDates != null && previousDay != null) {
            for (int i = 0; i < deliveryDates.size(); i++) {
                if (deliveryDates.get(i).getTime() == previousDay.getTime()) {
                    index = i;
                }
            }
        }

        schedulePickupView.selectDeliveryDate(index);
        if (index == -1) {
            List<String> dummy = new ArrayList<>();
            dummy.add("");
            schedulePickupView.setupDeliveryTimeDropDown(dummy);
            schedulePickupView.disableDeliveryTime(false);
        } else {
            schedulePickupView.disableDeliveryTime(true);
        }
    }

    public void onDeliveryDateSelected(Date selectedDate) {
        if (schedulePickupView == null) return;

        List<String> timeSlotStrings = getTimeSlotStrings(selectedDate);
        schedulePickupView.setupDeliveryTimeDropDown(timeSlotStrings);
        schedulePickupView.disableDeliveryTime(true);
    }

    public void onWashAndDryChanged(Date pickupDate, int pickupTimePosition, Date deliveryDate, int deliveryTimePosition) {
        if (schedulePickupView == null || pickupDate == null || deliveryDate == null) return;

        if (pickupTimePosition < 0) {
            pickupTimePosition = 0;
        }

        if (deliveryTimePosition < 0) {
            deliveryTimePosition = 0;
        }

        List<TimeSlot> timeSlotsForPostCode = laundrySchedule.getTimeSlotsForPostCode(selectedAddress.getPostalCode());
        List<TimeSlot> pickupTimeSlots = filterTimeSlotsForSingleDay(timeSlotsForPostCode, pickupDate);
        TimeSlot pickupTimeSlot = pickupTimeSlots.get(pickupTimePosition);

        List<TimeSlot> deliveryTimeSlots = filterTimeSlotsForSingleDay(timeSlotsForPostCode, deliveryDate);
        TimeSlot deliveryTimeSlot = deliveryTimeSlots.get(deliveryTimePosition);

        long timeDifference = deliveryTimeSlot.getFromDate().getTime() - pickupTimeSlot.getFromDate().getTime();
        int minHours = schedulePickupView.isWashAndTryActive() ? 24 : 6;
        int minDiff = minHours*60*60*1000;

        if (timeDifference <  minDiff) {
            onPickUpDateSelected(pickupDate);
            List<String> dummy = new ArrayList<>();
            dummy.add("");
            schedulePickupView.setupDeliveryTimeDropDown(dummy);
            schedulePickupView.disableDeliveryTime(false);
        }
    }

    public void creditCardAdded() {
        user.setIsStripeClient(true);
        onPlaceOrderClicked();
    }
    public void onPlaceOrderClicked() {
        if (schedulePickupView == null) return;

        if (existingCurrentOrder != null) {
            schedulePickupView.showOrderAlreadyExistsDialog();
            return;
        }

        if (selectedAddress == null) {
            schedulePickupView.showInvalidAddressDialog();
            return;
        }

        Date pickupDate = schedulePickupView.getSelectedPickupDate();
        int pickupTimeSelectedIndex = schedulePickupView.getSelectedPickupTimeIndex();
        Date deliveryDate = schedulePickupView.getSelectedDeliveryDate();
        int deliveryTimeSelectedIndex = schedulePickupView.getSelectedDeliveryTimeIndex();

        if (pickupDate == null) {
            schedulePickupView.showInvalidPickupDate();
            return;
        }

        if (pickupTimeSelectedIndex < 0) {
            schedulePickupView.showInvalidPickupTime();
            return;
        }

        if (deliveryDate == null) {
            schedulePickupView.showInvalidDeliveryDate();
            return;
        }

        if (deliveryTimeSelectedIndex < 0) {
            schedulePickupView.showInvalidDeliveryTime();
            return;
        }

        schedulePickupView.showProgressDialog();

        List<TimeSlot> timeSlotsForPostCode = laundrySchedule.getTimeSlotsForPostCode(selectedAddress.getPostalCode());
        List<TimeSlot> pickupTimeSlots = filterTimeSlotsForSingleDay(timeSlotsForPostCode, pickupDate);
        TimeSlot pickupTimeSlot = pickupTimeSlots.get(pickupTimeSelectedIndex);

        List<TimeSlot> deliveryTimeSlots = filterTimeSlotsForSingleDay(timeSlotsForPostCode, deliveryDate);
        TimeSlot deliveryTimeSlot = deliveryTimeSlots.get(deliveryTimeSelectedIndex);

        long timeDifference = deliveryTimeSlot.getFromDate().getTime() - pickupTimeSlot.getFromDate().getTime();
        int minHours = schedulePickupView.isWashAndTryActive() ? 24 : 6;
        int minDiff = minHours*60*60*1000;

        if (timeDifference <  minDiff) {
            onPickUpDateSelected(pickupDate);
            schedulePickupView.stopProgressDialog();
            schedulePickupView.showInvalidPickupAndDeliveryDialog();
            return;
        }

        if (!user.isStripeClient()) {
            schedulePickupView.stopProgressDialog();
            schedulePickupView.showAddCreditCardDialog(user);
            return;
        }

        Order order = new Order();
        order.setPickUpAddress(selectedAddress);
        order.setDeliveryAddress(selectedAddress);
        order.setPickUpSchedule(pickupTimeSlot);
        order.setDeliverySchedule(deliveryTimeSlot);
        order.setWashAndDry(schedulePickupView.isWashAndTryActive());
        OrderWebServiceProvider.createOrder(order, configuration.getOrderStatuses(), new OrderWebServiceInterfaces.IOnOrderCreatedListener() {
            @Override
            public void onOrderCreated(Order order) {
                if (schedulePickupView == null) return;
                schedulePickupView.stopProgressDialog();
            }

            @Override
            public void onOrderFailedToCreate(Exception error) {
                if (schedulePickupView == null) return;

                schedulePickupView.stopProgressDialog();
                if (error instanceof ParseException) {
                    if (((ParseException) error).getCode() == 100) {
                        schedulePickupView.showNoInternetConnectionDialog();
                        return;
                    }
                }

                schedulePickupView.showError(error.getMessage());
            }
        });
    }
}
