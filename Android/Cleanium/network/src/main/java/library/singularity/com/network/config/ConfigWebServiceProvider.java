package library.singularity.com.network.config;

import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseObject;

import library.singularity.com.data.model.Configuration;
import library.singularity.com.data.model.mappers.AddressEntityMapper;
import library.singularity.com.data.model.mappers.ConfigurationEntityMapper;
import library.singularity.com.network.config.ConfigWebServiceInterfaces.*;
import library.singularity.com.network.resolver.ErrorCodeResolver;

public class ConfigWebServiceProvider {

    public static void getConfig(final IOnConfigObtainedListener listener) {
        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig config, ParseException e) {
                if (listener == null) return;
                if (e == null) {
                    listener.onConfigObtained(ConfigurationEntityMapper.convertToConfiguration(config));
                }

                listener.onConfigFailedToObtain(ErrorCodeResolver.resolveError(e));
            }
        });
    }
}
