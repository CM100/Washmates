package library.singularity.com.data.model.mappers;

import com.parse.ParseObject;

import library.singularity.com.data.model.TimeSlot;
import library.singularity.com.data.utils.Constants;

/**
 * Created by XPC on 8/7/2015.
 */
public class TimeSlotEntityMapper {

    public static TimeSlot convertToTimeSlot(ParseObject parseTimeSlot){
        if(parseTimeSlot == null) return null;

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setFromDate(parseTimeSlot.getDate(Constants.PARSE_DRIVER_SCHEDULE_FROM_DATE));
        timeSlot.setToDate(parseTimeSlot.getDate(Constants.PARSE_DRIVER_SCHEDULE_TO_DATE));
        timeSlot.setId(parseTimeSlot.getObjectId());

        return timeSlot;
    }

    public  static ParseObject convertToParseObject(TimeSlot timeSlot){

        ParseObject timeSlotParseObject = new ParseObject(Constants.PARSE_DRIVER_SCHEDULE_TABLE_NAME);

        if (timeSlot.getId() != null) {
            timeSlotParseObject.setObjectId(timeSlot.getId());
        }

        timeSlotParseObject.put(Constants.PARSE_DRIVER_SCHEDULE_FROM_DATE, timeSlot.getFromDate());
        timeSlotParseObject.put(Constants.PARSE_DRIVER_SCHEDULE_TO_DATE,timeSlot.getToDate());
        return timeSlotParseObject;
    }
}
