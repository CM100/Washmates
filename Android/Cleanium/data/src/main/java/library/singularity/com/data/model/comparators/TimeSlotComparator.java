package library.singularity.com.data.model.comparators;

import java.util.Comparator;

import library.singularity.com.data.model.TimeSlot;

public class TimeSlotComparator implements Comparator<TimeSlot> {
    @Override
    public int compare(TimeSlot a, TimeSlot b) {
        if (a == null || b == null || a.getFromDate() == null ||
                b.getFromDate() == null) return 0;

        return a.getFromDate().before(b.getFromDate()) ? -1 :
                a.getFromDate().equals(b.getFromDate()) ? 0 : 1;
    }
}