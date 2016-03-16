package library.singularity.com.network.config;


import library.singularity.com.data.model.Configuration;

public class ConfigWebServiceInterfaces {

    public interface IOnConfigObtainedListener {
        void onConfigObtained(Configuration configuration);
        void onConfigFailedToObtain(Exception error);
    }

}
