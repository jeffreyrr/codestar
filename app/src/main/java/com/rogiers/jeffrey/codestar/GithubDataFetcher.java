package com.rogiers.jeffrey.codestar;

import android.os.AsyncTask;

import com.rogiers.jeffrey.codestar.util.BusProvider;
import com.rogiers.jeffrey.codestar.util.BusUtil;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by jeffrey on 11/11/17.
 */

public class GithubDataFetcher extends AsyncTask<String, Void, Void> {

    private static final String TAG = "[GithubDataFetcher]";

    @Override
    protected Void doInBackground(String... strings) {
        List<Repository> repositories;
        String user = strings[0].toLowerCase();

        RepositoryService service = new RepositoryService();
        try {
            repositories = service.getRepositories(user);
            // sort by stars descending
            Collections.sort(repositories, new Comparator<Repository>() {
                @Override
                public int compare(Repository o1, Repository o2) {
                    return o2.getWatchers() - o1.getWatchers();
                }
            });
        } catch (IOException e) {
            repositories = new ArrayList<>();
        }

        Event.UserRepositories event = new Event.UserRepositories(user, repositories);
        BusUtil.postOnMain(BusProvider.getBus(), event);

        return null;
    }
}
