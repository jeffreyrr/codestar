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
import android.view.View;

import com.rogiers.jeffrey.codestar.model.Event;
import com.rogiers.jeffrey.codestar.util.BusProvider;
import com.rogiers.jeffrey.codestar.util.WebPageHelper;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;


import org.eclipse.egit.github.core.Repository;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import static com.rogiers.jeffrey.codestar.util.WebPageHelper.openGithubUserPage;
import static com.rogiers.jeffrey.codestar.util.WebPageHelper.openPage;

public class CompareReposActivity extends AppCompatActivity implements RepositoryFragment.OnListFragmentInteractionListener {

    private static final String TAG = "[CompareReposActivity]";
    public static final String GITHUB_USER_LIST = "com.rogiers.jeffrey.GITHUB_USER_LIST";

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

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

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(sectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        if(mUsers.size() >= 2) {
            CircleImageView imageView1 = findViewById(R.id.photo_user_1);
            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGithubUserPage(getApplicationContext(), mUsers.get(0));
                }
            });

            CircleImageView imageView2 = findViewById(R.id.photo_user_2);
            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGithubUserPage(getApplicationContext(), mUsers.get(1));
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
                        .load(WebPageHelper.GITHUB_BASE_URL + tmpUser + ".png")
                        .placeholder(R.drawable.facebat)
                        .into((CircleImageView) findViewById(resProfileImageView));
            }
        }
    }

    @Subscribe
    public void onProcessMessage(Event.UserRepositories event){
        mUserStars.put(event.getUser(), event.getStars());
        mTabLayout.setupWithViewPager(mViewPager);
        crownWinner(findWinner(mUsers, mUserStars));
    }

    public static Integer findWinner(ArrayList<String> users, HashMap<String, Integer> stars) {
        if(users.size() < 2 || stars.size() < 2) {
            // minimum size requirements not met
            return -2;
        }

        int firstUserIndex = 0;
        int secondUserIndex = 1;

        if(!stars.containsKey(users.get(firstUserIndex)) ||
                !stars.containsKey(users.get(secondUserIndex))){
            return -2;
        }

        Integer firstUserStars = stars.get(users.get(firstUserIndex));
        Integer secondUserStars = stars.get(users.get(secondUserIndex));

        if (firstUserStars > secondUserStars) {
            return firstUserIndex;
        } else if (secondUserStars > firstUserStars) {
            return secondUserIndex;
        }

        // Draw
        return -1;
    }

    private void crownWinner(Integer winnerIndex){
        CircleImageView profileImageView;
        String winnerText = getString(R.string.match_result_draw);

        if(winnerIndex >= 0){
            profileImageView = findViewById(getProfilePhotoImageView(winnerIndex));
            profileImageView.setBorderColor(getResources().getColor(R.color.colorAccent));
            winnerText = MessageFormat.format(
                    getString(R.string.match_result_winner_template), mUsers.get(winnerIndex));

            KonfettiView konfettiView = findViewById(R.id.viewKonfetti);
            konfettiView.build()
                    .addColors(getResources().getColor(R.color.colorAccent),
                            getResources().getColor(R.color.colorPrimaryDark))
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.RECT, Shape.CIRCLE)
                    .addSizes(new Size(12, 5f))
                    .setPosition(profileImageView.getLeft() + (profileImageView.getWidth() / 2), null,
                            profileImageView.getTop() + (profileImageView.getHeight() / 2), null)
                    .stream(200, 2000L);
        }

        if(winnerIndex >= -1) {
            setTitle(winnerText);
        }
    }

    @Override
    public void onListFragmentInteraction(Repository repository) {
        Log.d(TAG, "Repository Clicked: " + repository.getHtmlUrl());
        openPage(getApplicationContext(), repository.getHtmlUrl().toString());
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
    protected void onResume() {
        super.onResume();
        BusProvider.getBus().register(this);

        if(findWinner(mUsers, mUserStars) < -1){
            loadUserData();
        } else {
            crownWinner(findWinner(mUsers, mUserStars));
        }
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
            if(mUserStars.containsKey(mUsers.get(position))){
                return MessageFormat.format(getString(R.string.tabbar_title_template),
                        mUsers.get(position), mUserStars.get(mUsers.get(position)));
            }
            return mUsers.get(position);
        }
    }
}
