package com.rogiers.jeffrey.codestar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.rogiers.jeffrey.codestar.util.StringUtil;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchupFragment extends Fragment {

    private static final String TAG = "[MatchupFragment]";

    private EditText mUser1;
    private EditText mUser2;
    private Button mCompareUsers;

    public static Fragment newInstance() {
        return new MatchupFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matchup, container, false);

        mUser1 = v.findViewById(R.id.text_user_1);
        mUser2 = v.findViewById(R.id.text_user_2);

        mCompareUsers = v.findViewById(R.id.button_compare_users);
        mCompareUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateUsername(mUser1) || !validateUsername(mUser2)){
                    return;
                }
                mCompareUsers.setClickable(false);

                ArrayList<String> users = new ArrayList<>();
                users.add(mUser1.getText().toString());
                users.add(mUser2.getText().toString());

                Intent compareRepoIntent = CompareReposActivity.newIntent(getActivity(), users);
                startActivity(compareRepoIntent);
            }
        });
        return v;
    }

    private boolean validateUsername(EditText editText){
        if(!StringUtil.validateGithubUsername(editText.getText().toString())){
            editText.setError(getString(R.string.invalid_username));
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCompareUsers.setClickable(true);
    }
}