package com.rogiers.jeffrey.codestar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.rogiers.jeffrey.codestar.dummy.DummyContent;
import com.rogiers.jeffrey.codestar.util.BusProvider;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompareReposActivity extends AppCompatActivity implements RepositoryFragment.OnListFragmentInteractionListener {

    private static final String TAG = "[CompareReposActivity]";
    public static final String GITHUB_USER_LIST = "com.rogiers.jeffrey.GITHUB_USER_LIST";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private ArrayList<String> mUsers;
    private HashMap<String, Integer> mUserStars = new HashMap();

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

        if(mUsers.size() >= 2) {
            CircleImageView imageView1 = findViewById(R.id.photo_user_1);
            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGithubPage(mUsers.get(0));
                }
            });

            CircleImageView imageView2 = findViewById(R.id.photo_user_2);
            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGithubPage(mUsers.get(1));
                }
            });
        }

        loadUserData();
    }

    private void loadUserData() {
        for(int i=0; i < mUsers.size(); i++){
            String tmpUser = mUsers.get(i);
            // for each user, fetch their repository information
            new GithubDataFetcher().execute(tmpUser);

            // for each user, try to load their profile photo, if a placeholder exists
            int resProfileImageView = getProfilePhotoImageView(i);
            if(resProfileImageView > 0) {
                Picasso.with(getApplicationContext())
                        .load("https://github.com/" + tmpUser + ".png")
                        .placeholder(R.drawable.facebat)
                        .into((CircleImageView) findViewById(resProfileImageView));
            }
        }
    }

    @Subscribe
    public void onProcessMessage(Event.UserRepositories event){
        mUserStars.put(event.getUser(), event.getStars());

        if(mUserStars.size() == 2){
            findTheWinner();
        }
    }

    private void findTheWinner() {
        Integer winnerIndex = null;
        int firstUserIndex = 0;
        int secondUserIndex = 1;

        CircleImageView profileImageView;
        String winnerText = getString(R.string.match_result_draw);

        int firstUserStars = mUserStars.get(mUsers.get(firstUserIndex));
        int secondUserStars = mUserStars.get(mUsers.get(secondUserIndex));

        if(firstUserStars > secondUserStars){
            winnerIndex = firstUserIndex;
        } else if (secondUserStars > firstUserStars) {
            winnerIndex = secondUserIndex;
        }

        if(winnerIndex != null){
            profileImageView = findViewById(getProfilePhotoImageView(winnerIndex));
            profileImageView.setBorderColor(getResources().getColor(R.color.colorAccent));
            winnerText = MessageFormat.format(
                    getString(R.string.match_result_winner_template), mUsers.get(winnerIndex));
        }

        setTitle(winnerText);
    }

    public void openGithubPage(String user){
        final String github_url = "http://github.com/";

        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(this.getResources().getColor(R.color.colorPrimary))
                .setShowTitle(true)
                .build();

        customTabsIntent.launchUrl(this, Uri.parse(github_url + user));
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.d(TAG, "Got a click!");
    }

    private int getProfilePhotoImageView(int i) {
        Resources res = getResources();
        return res.getIdentifier("photo_user_"+(i+1), "id", getPackageName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getBus().unregister(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        BusProvider.getBus().register(this);
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return RepositoryFragment.newInstance(mUsers.get(position));
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
