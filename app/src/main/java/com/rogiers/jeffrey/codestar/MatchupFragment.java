package com.rogiers.jeffrey.codestar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if(!validateGithubUsername(editText.getText().toString())){
            editText.setError(getString(R.string.invalid_username));
            return false;
        }
        return true;
    }

    /*
     * Username requirements according to Github:
     * Username may only contain alphanumeric characters or single hyphens,
     * and cannot begin or end with a hyphen.
     * This is a simplified version of the requirements that only focuses on
     * the character set in order to provide useful feedback.
     */
    public boolean validateGithubUsername(String text) {
        String SIMPLE_USERNAME_PATTERN = "^[A-Za-z0-9-]+$";

        Pattern pattern = Pattern.compile(SIMPLE_USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(text);

        return matcher.matches();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCompareUsers.setClickable(true);
    }
}