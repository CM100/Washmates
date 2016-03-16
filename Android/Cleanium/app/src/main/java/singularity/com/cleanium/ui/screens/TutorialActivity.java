package singularity.com.cleanium.ui.screens;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.singularity.cleanium.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import singularity.com.cleanium.adapter.TutorialViewPagerAdapter;

public class TutorialActivity extends AppCompatActivity {

    @Bind(R.id.three_dots)
    ImageView threeDots;

    @Bind(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ButterKnife.bind(this);
        initializeViewPager();
    }

    private void initializeViewPager() {
        TutorialViewPagerAdapter tutorialAdapter = new TutorialViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tutorialAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    threeDots.setImageResource(R.drawable.tutorial_three_dots_first_page);
                } else if (position == 1) {
                    threeDots.setImageResource(R.drawable.tutorial_three_dots_second_page);
                } else if (position == 2) {
                    threeDots.setImageResource(R.drawable.tutorial_three_dots_third_page);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}