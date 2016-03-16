package singularity.com.cleanium.ui.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.parse.ParseUser;
import com.singularity.cleanium.R;

import library.singularity.com.presenter.SplashscreenPresenter;
import library.singularity.com.presenter.interfaces.SplashscreenView;

public class SplashscreenActivity extends AppCompatActivity implements SplashscreenView {

    private static final int SPLASH_SCREEN_TIMEOUT = 1000;

    private Handler handler;
    private SplashscreenPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        presenter = new SplashscreenPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.showStartScreenAfterFewSeconds();
    }

    private void showStartScreenAfterFewSeconds() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = presenter.isTutorialSeen() ?
                        new Intent(SplashscreenActivity.this, StartingActivity.class)
                        :
                        new Intent(SplashscreenActivity.this, TutorialActivity.class);

                SplashscreenActivity.this.startActivity(intent);
                SplashscreenActivity.this.finish();
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.finish();
        }
    }
}