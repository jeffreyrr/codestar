package com.rogiers.jeffrey.codestar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rogiers.jeffrey.codestar.model.Event;
import com.rogiers.jeffrey.codestar.util.BusProvider;
import com.squareup.otto.Subscribe;

import org.eclipse.egit.github.core.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Github Repositories.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RepositoryFragment extends Fragment {

    private static final String TAG = "[RepositoryFragment]";
    private static final String ARG_USER_NAME = "github_user_name";

    private int mColumnCount = 1;
    private String mUser;
    private List<Repository> mRepositories;
    private RepositoryRecyclerViewAdapter mAdapter;

    private OnListFragmentInteractionListener mListener;

    public static RepositoryFragment newInstance(String username) {
        RepositoryFragment fragment = new RepositoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_NAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = getArguments().getString(ARG_USER_NAME);
        mRepositories = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repository_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new RepositoryRecyclerViewAdapter(mUser, mRepositories, mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Subscribe
    public void onProcessMessage(Event.UserRepositories event){
        String user = event.getUser();
        if(user == mUser) {
            List<Repository> repositories = event.getRepositories();
            mAdapter.updateRepositoriesList(repositories);
            Log.d(TAG, user + " has " + repositories.size() + " repositories");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getBus().unregister(this);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Repository repository);
    }
}
