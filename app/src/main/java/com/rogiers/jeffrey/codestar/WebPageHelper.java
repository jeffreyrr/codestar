package com.rogiers.jeffrey.codestar;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

public class WebPageHelper {

    public static final String GITHUB_BASE_URL = "https://github.com/";

    public static void openGithubUserPage(Context context, String username) {
        openPage(context, GITHUB_BASE_URL + username);
    }

    public static void openPage(Context context, String url) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(context.getResources().getColor(R.color.colorPrimary))
                .setShowTitle(true)
                .build();

        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}
