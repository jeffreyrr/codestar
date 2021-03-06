package com.rogiers.jeffrey.codestar;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rogiers.jeffrey.codestar.RepositoryFragment.OnListFragmentInteractionListener;

import org.eclipse.egit.github.core.Repository;

import java.util.List;

public class RepositoryRecyclerViewAdapter extends RecyclerView.Adapter<RepositoryRecyclerViewAdapter.ViewHolder> {

    private String mUser;
    private List<Repository> mRepositories;
    private final OnListFragmentInteractionListener mListener;

    public RepositoryRecyclerViewAdapter(String user, List<Repository> repositories, OnListFragmentInteractionListener listener) {
        mUser = user;
        mRepositories = repositories;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_repository, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mRepositories.get(position);
        holder.mRepositoryView.setText(mRepositories.get(position).getName());
        holder.mRepositoryStars.setText(Integer.toString(mRepositories.get(position).getWatchers()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRepositories.size();
    }

    public void updateRepositoriesList(List<Repository> repositories){
        mRepositories = repositories;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mRepositoryView;
        public final TextView mRepositoryStars;
        public Repository mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mRepositoryView = (TextView) view.findViewById(R.id.repositoryName);
            mRepositoryStars = (TextView) view.findViewById(R.id.repositoryStarsCount);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mRepositoryView.getText() + "'";
        }
    }
}
