package library.singularity.com.network.provider;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class ParseApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Initialize Crash Reporting.
  //  ParseCrashReporting.enable(this);

    Parse.initialize(this, "cGiRSDM8unMmInG0iDO4DG3oesrWm00QGNcU6eUN", "u8U82JgEj8CNehEA9xBL2exXTfMjsY4l348RAdz8");

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    // Add your initialization code here
    Parse.initialize(this);


    ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    // Optionally enable public read access.
    defaultACL.setPublicReadAccess(true);
    defaultACL.setPublicWriteAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
