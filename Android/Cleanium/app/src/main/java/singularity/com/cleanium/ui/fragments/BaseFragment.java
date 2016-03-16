package singularity.com.cleanium.ui.fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    public View onCreateView(int layoutId, LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(layoutId, container, false);

        ButterKnife.bind(this, v);
        this.setFonts(v);
        return v;
    }

    protected abstract void setFonts(View v);
}
