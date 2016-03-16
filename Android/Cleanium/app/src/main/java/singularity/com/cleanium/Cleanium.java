package singularity.com.cleanium;

import android.app.Application;

import com.parse.Parse;

public class Cleanium extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "cGiRSDM8unMmInG0iDO4DG3oesrWm00QGNcU6eUN", "u8U82JgEj8CNehEA9xBL2exXTfMjsY4l348RAdz8");
    }
}
