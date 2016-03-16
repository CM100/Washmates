package library.singularity.com.data.model.mappers;

import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import library.singularity.com.data.model.Configuration;
import library.singularity.com.data.model.LaundrySchedule;
import library.singularity.com.data.model.TimeSlot;
import library.singularity.com.data.model.comparators.TimeSlotComparator;
import library.singularity.com.data.utils.Constants;

public class LaundryScheduleEntityMapper {

    public static LaundrySchedule convertToLaundrySchedule(HashMap<String, Object> laundryScheduleAsHashMap, int timeSlotInterval) {
        try {
            LaundrySchedule laundrySchedule = convertParseObjectToLaundrySchedule(laundryScheduleAsHashMap);
            List<HashMap<String, Object>> reservedSlots = (ArrayList<HashMap<String, Object>>) laundryScheduleAsHashMap.get(Constants.PARSE_LAUNDRY_RESERVED_CODE);
            if(reservedSlots == null) {
                return laundrySchedule;
            }

            List<TimeSlot> reservedTimeSlots = getSortedReservedSlots(reservedSlots);
            HashMap<Integer, List<TimeSlot>> slotsByPostCode = getTimeSlotsByPostCode(reservedTimeSlots);
            List<TimeSlot> availableSlots = new ArrayList<>();
            for (Integer postCode : slotsByPostCode.keySet()) {
                HashMap<Long, Date> freeSlotsMap = getAllFreeSlotsInInterval(laundrySchedule.getMinPickUpDate(), laundrySchedule.getMaxDeliveryDate(), timeSlotInterval);
                List<TimeSlot> timeSlotsForPostCode = slotsByPostCode.get(postCode);
                removeReservedSlots(freeSlotsMap, timeSlotsForPostCode, timeSlotInterval);

                List<TimeSlot> freeSlots = getFreeSlots(freeSlotsMap, postCode, timeSlotInterval);
                availableSlots.addAll(freeSlots);
            }

            Collections.sort(availableSlots, new TimeSlotComparator());
            laundrySchedule.setFreeSlots(availableSlots);
            return laundrySchedule;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static LaundrySchedule convertParseObjectToLaundrySchedule(HashMap<String, Object> laundryScheduleAsHashMap) {
        LaundrySchedule laundrySchedule = new LaundrySchedule();
        Date maxDeliveryDate = (Date) laundryScheduleAsHashMap.get(Constants.PARSE_LAUNDRY_MAX_DELIVERY_DATE);
        Date minPickUpDate = (Date) laundryScheduleAsHashMap.get(Constants.PARSE_LAUNDRY_MIN_PICKUP_DATE);
        laundrySchedule.setMinPickUpDate(minPickUpDate);
        laundrySchedule.setMaxDeliveryDate(maxDeliveryDate);
        return laundrySchedule;
    }

    private static List<TimeSlot> getSortedReservedSlots(List<HashMap<String, Object>> reservedSlots) {
        List<TimeSlot> reservedTimeSlots = new ArrayList<>();
        int i = 0;
        for(HashMap<String, Object> reservedSlot : reservedSlots) {
            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setFromDate((Date) reservedSlot.get(Constants.PARSE_LAUNDRY_FROM_DATE));
            timeSlot.setToDate((Date) reservedSlot.get(Constants.PARSE_LAUNDRY_TO_DATE));
            timeSlot.setPostCode(Integer.parseInt(((ArrayList) reservedSlot.get(Constants.PARSE_LAUNDRY_POST_CODE)).get(0).toString()));
            timeSlot.setId(++i + "");
            reservedTimeSlots.add(timeSlot);
        }

        Collections.sort(reservedTimeSlots, new TimeSlotComparator());

        return reservedTimeSlots;
    }

    private static HashMap<Integer, List<TimeSlot>> getTimeSlotsByPostCode(List<TimeSlot> reservedTimeSlots) {
        HashMap<Integer, List<TimeSlot>> slotsByPostCode = new HashMap<>();
        for (TimeSlot timeSlot : reservedTimeSlots) {
            if (slotsByPostCode.containsKey(timeSlot.getPostCode())) continue;

            List<TimeSlot> timeSlotsInPostCode = new ArrayList<>();
            for (TimeSlot slot : reservedTimeSlots) {
                if (slot.getPostCode() == timeSlot.getPostCode()) {
                    timeSlotsInPostCode.add(slot);
                }
            }
            slotsByPostCode.put(timeSlot.getPostCode(), timeSlotsInPostCode);
        }

        return slotsByPostCode;
    }

    private static HashMap<Long, Date> getAllFreeSlotsInInterval(Date minPickUpDate, Date maxDeliveryDate, int timeSlotInterval) {
        HashMap<Long, Date> freeSlotsMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(minPickUpDate);
        while(calendar.getTime().before(maxDeliveryDate)) {
            freeSlotsMap.put(calendar.getTimeInMillis(), calendar.getTime());
            calendar.add(Calendar.MILLISECOND, timeSlotInterval);
        }

        return freeSlotsMap;
    }

    private static void removeReservedSlots(HashMap<Long, Date> freeSlotsMap, List<TimeSlot> timeSlotsForPostCode, int timeSlotInterval) {
        Calendar calendar = Calendar.getInstance();
        for (TimeSlot reservedSlot : timeSlotsForPostCode) {
            calendar.setTime(reservedSlot.getFromDate());
            while(calendar.getTime().before(reservedSlot.getToDate())) {
                if (calendar.getTimeInMillis() % timeSlotInterval != 0) {
                    calendar.add(Calendar.MILLISECOND, (int) ((-1)*(calendar.getTimeInMillis() % timeSlotInterval)));
                }

                freeSlotsMap.remove(calendar.getTimeInMillis());
                calendar.add(Calendar.MILLISECOND, timeSlotInterval);
            }
        }
    }

    private static List<TimeSlot> getFreeSlots(HashMap<Long, Date> freeSlotsMap, int postCode, int timeSlotInterval) {
        List<TimeSlot> freeSlots = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (Long freeSlotKey : freeSlotsMap.keySet()) {
            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setFromDate(freeSlotsMap.get(freeSlotKey));
            calendar.setTime(freeSlotsMap.get(freeSlotKey));
            calendar.add(Calendar.MILLISECOND, timeSlotInterval);
            timeSlot.setToDate(calendar.getTime());
            timeSlot.setPostCode(postCode);
            freeSlots.add(timeSlot);
        }

        return freeSlots;
    }
}
