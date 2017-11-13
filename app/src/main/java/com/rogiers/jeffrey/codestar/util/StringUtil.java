package com.rogiers.jeffrey.codestar.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    /*
     * Username requirements according to Github:
     * Username may only contain alphanumeric characters or single hyphens,
     * and cannot begin or end with a hyphen.
     * This is a simplified version of the requirements that only focuses on
     * the character set in order to provide useful feedback.
     */

    public static boolean validateGithubUsername(String text) {
        String SIMPLE_USERNAME_PATTERN = "^[A-Za-z0-9-]+$";

        Pattern pattern = Pattern.compile(SIMPLE_USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(text);

        return matcher.matches();
    }
}