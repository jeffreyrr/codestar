package com.rogiers.jeffrey.codestar;

import android.os.AsyncTask;

import com.rogiers.jeffrey.codestar.util.BusProvider;
import com.rogiers.jeffrey.codestar.util.BusUtil;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
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
        } catch (IOException e) {
            repositories = null;
            e.printStackTrace();
        }

        Event.UserRepositories event = new Event.UserRepositories(user, repositories);
        BusUtil.postOnMain(BusProvider.getBus(), event);

        return null;
    }
}
