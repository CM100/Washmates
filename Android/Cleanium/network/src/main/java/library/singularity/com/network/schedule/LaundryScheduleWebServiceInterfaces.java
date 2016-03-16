package library.singularity.com.network.schedule;

import com.parse.ParseException;

import library.singularity.com.data.model.LaundrySchedule;
import library.singularity.com.network.resolver.NetworkError;

public class LaundryScheduleWebServiceInterfaces {

    public interface IOnLaundrySchedulesObtainListener {
        void onLaundrySchedulesObtained(LaundrySchedule laundrySchedule);
        void onLaundrySchedulesFaildToObtain(Exception error);
    }
}