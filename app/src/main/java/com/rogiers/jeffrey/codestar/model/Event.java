package com.rogiers.jeffrey.codestar.model;

import org.eclipse.egit.github.core.Repository;
import org.json.JSONArray;

import java.util.List;

/**
 * Created by jeffrey on 11/11/17.
 */

public class Event {
    public static class UserRepositories {

        private String mUser;
        private Integer mStars;
        private List<Repository> mRepositoryList;

        public UserRepositories(String user, List<Repository> repositories) {
            mUser = user.toLowerCase();
            mRepositoryList = repositories;
            mStars = 0;

            if(mRepositoryList.size() > 0) {
                for (Repository repo : mRepositoryList) {
                    mStars += repo.getWatchers();
                }
            }
        }

        public List<Repository> getRepositories(){
            return mRepositoryList;
        }

        public String getUser(){
            return mUser;
        }

        public Integer getStars() {
            return mStars;
        }
    }

}
