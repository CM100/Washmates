package singularity.com.cleanium.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import singularity.com.cleanium.ui.fragments.TutorialFragment;
import singularity.com.cleanium.utils.Constants;

public class TutorialViewPagerAdapter extends FragmentPagerAdapter {
    private final int NUM_OF_TABS = 3;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public TutorialViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return this.NUM_OF_TABS;
    }

    @Override
    public Fragment getItem(int position) {
        TutorialFragment tutorialFragment = new TutorialFragment();

        Bundle args = new Bundle();
        args.putInt(Constants.TUTORIAL_FRAGMENT_POSITION, position);
        tutorialFragment.setArguments(args);

        return tutorialFragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        this.registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        this.registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }
}