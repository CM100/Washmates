package singularity.com.cleanium.ui.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.singularity.cleanium.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import singularity.com.cleanium.ui.screens.StartingActivity;
import singularity.com.cleanium.utils.Constants;

public class TutorialFragment extends Fragment {

    @Nullable
    @Bind(R.id.header_text)
    TextView headerText;

    @Nullable
    @Bind(R.id.tutorial_text)
    TextView tutorialText;

    @Nullable
    @Bind(R.id.header_text1)
    TextView headerText1;

    @Nullable
    @Bind(R.id.tutorial_text1)
    TextView tutorialText1;

    @Nullable
    @Bind(R.id.header_text2)
    TextView headerText2;

    @Nullable
    @Bind(R.id.tutorial_text2)
    TextView tutorialText2;

    @Nullable
    @Bind(R.id.tutorial_third_page_button)
    Button tutorialEndButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        int position = getArguments().getInt(Constants.TUTORIAL_FRAGMENT_POSITION);
        int layoutID = getLayoutForFragmentOnPosition(position);

        View view = inflater.inflate(layoutID, container, false);
        ButterKnife.bind(this, view);
        setupFont();
        return view;
    }

    private int getLayoutForFragmentOnPosition(int position) {
        switch (position) {
            case 0:
                return R.layout.fragment_tutorial_first_page;
            case 1:
                return R.layout.fragment_tutorial_second_page;
            case 2:
                return R.layout.fragment_tutorial_third_page;
        }

        return 0;
    }

    void setupFont() {
        Typeface thin = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Light.ttf");
        Typeface regular = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Regular.ttf");

        if (headerText != null) {
            headerText.setTypeface(regular);
        }

        if (tutorialText != null) {
            tutorialText.setTypeface(thin);
        }

        if (headerText1 != null) {
            headerText1.setTypeface(regular);
        }

        if (tutorialText1 != null) {
            tutorialText1.setTypeface(thin);
        }

        if (headerText2 != null) {
            headerText2.setTypeface(regular);
        }

        if (tutorialText2 != null) {
            tutorialText2.setTypeface(thin);
        }

        if (tutorialEndButton != null) {
            tutorialEndButton.setTypeface(regular);
        }
    }

    @Nullable @OnClick(R.id.tutorial_third_page_button)
    void onGetStartedClicked() {
        Intent i = new Intent(getActivity(), StartingActivity.class);
        getActivity().startActivity(i);
        getActivity().finish();
    }
}
