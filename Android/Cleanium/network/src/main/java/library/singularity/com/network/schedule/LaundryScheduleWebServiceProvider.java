package library.singularity.com.network.schedule;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import library.singularity.com.data.model.Address;
import library.singularity.com.data.model.LaundrySchedule;
import library.singularity.com.data.model.User;
import library.singularity.com.data.model.mappers.LaundryScheduleEntityMapper;
import library.singularity.com.network.resolver.ErrorCodeResolver;
import library.singularity.com.network.schedule.LaundryScheduleWebServiceInterfaces.*;

public class LaundryScheduleWebServiceProvider {

    private static String GET_SCHEDULES_POST_CODES_KEY = "postCodes";
    private static String CLOUD_METHOD_GET_SCHEDULES = "getSchedulesForPostCodes";

    public static void getLaundrySchedules(User user, final long interval, final IOnLaundrySchedulesObtainListener listener) {
        if (user == null) {
            if (listener == null) return;
            listener.onLaundrySchedulesFaildToObtain(new NullPointerException());
        }

        if (user.getAddresses() == null) {
            if (listener == null) return;
            listener.onLaundrySchedulesFaildToObtain(new NullPointerException());
        }

        HashMap<String, List<Integer>> params = new HashMap<>();
        List<Integer> postCodes = new ArrayList<>();
        for (Address address : user.getAddresses()) {
            if (postCodes.contains(address.getPostalCode())) continue;

            postCodes.add(address.getPostalCode());
        }

        params.put(GET_SCHEDULES_POST_CODES_KEY, postCodes);
        ParseCloud.callFunctionInBackground(CLOUD_METHOD_GET_SCHEDULES, params, new FunctionCallback<Object>() {
            public void done(Object response, ParseException e) {
                if (e != null) {
                    if (listener == null) return;
                    listener.onLaundrySchedulesFaildToObtain(ErrorCodeResolver.resolveError(e));
                    return;
                }

                if (listener == null) return;

                LaundrySchedule laundrySchedule = LaundryScheduleEntityMapper.convertToLaundrySchedule((HashMap<String, Object>) response, (int) interval);
                listener.onLaundrySchedulesObtained(laundrySchedule);
            }
        });
    }
}
