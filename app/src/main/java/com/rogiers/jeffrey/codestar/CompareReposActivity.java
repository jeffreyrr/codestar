package com.rogiers.jeffrey.codestar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompareReposActivity extends AppCompatActivity {

    private static final String TAG = "CompareReposActivity";
    public static final String GITHUB_USER_LIST = "com.rogiers.jeffrey.GITHUB_USER_LIST";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private ArrayList<String> mUsers;

    public static Intent newIntent(Context context, ArrayList<String> users) {
        Intent intent = new Intent(context, CompareReposActivity.class);
        intent.putStringArrayListExtra(GITHUB_USER_LIST, users);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_repos);
        Intent intent = getIntent();

        mUsers = intent.getStringArrayListExtra(GITHUB_USER_LIST);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        Resources res = getResources();
        for(int i=0; i < mUsers.size(); i++){
            Log.d(TAG, "Found " + i);
            int profilePhotoId = res.getIdentifier("photo_user_"+(i+1), "id", getPackageName());
            if(profilePhotoId == 0) {
                Log.d(TAG, "Couldn't find photo placeholder " + i);
                break;
            }

            Picasso.with(getApplicationContext())
                    .load("https://github.com/" + mUsers.get(i) + ".png")
                    .placeholder(R.drawable.facebat)
                    .into((CircleImageView) findViewById(profilePhotoId));
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        public PlaceholderFragment() {}

        public static PlaceholderFragment newInstance(String sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_compare_repos, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getString(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(mUsers.get(position));
        }

        @Override
        public int getCount() {
            return mUsers.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mUsers.get(position);
        }
    }
}
