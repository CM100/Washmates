package library.singularity.com.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LaundrySchedule {
    private Date minPickUpDate;
    private Date maxDeliveryDate;
    private List<TimeSlot> freeSlots;

    public LaundrySchedule() {
        minPickUpDate = null;
        maxDeliveryDate = null;
        freeSlots = null;
    }

    public Date getMinPickUpDate() {
        return minPickUpDate;
    }

    public void setMinPickUpDate(Date minPickUpDate) {
        this.minPickUpDate = minPickUpDate;
    }

    public Date getMaxDeliveryDate() {
        return maxDeliveryDate;
    }

    public void setMaxDeliveryDate(Date maxDeliveryDate) {
        this.maxDeliveryDate = maxDeliveryDate;
    }

    public List<TimeSlot> getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(List<TimeSlot> freeSlots) {
        this.freeSlots = freeSlots;
    }

    public List<TimeSlot> getTimeSlotsForPostCode(int postCode) {
        List<TimeSlot> freeTimeSlots = new ArrayList<>();
        if (freeSlots == null) return freeTimeSlots;

        for (TimeSlot timeSlot : freeSlots) {
            if (timeSlot.getPostCode() == postCode) {
                freeTimeSlots.add(timeSlot);
            }
        }

        return freeTimeSlots;
    }
}
