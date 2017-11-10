package com.rogiers.jeffrey.codestar;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MatchupFragment extends Fragment {

    private static final String TAG = "MatchupFragment";

    public static Fragment newInstance() {
        return new MatchupFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_matchup, container, false);
    }
}
