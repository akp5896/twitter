package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    public String body;
    public String created_at;
    public User user;
    public String media;

    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
           tweet.body = jsonObject.getString("text");

            tweet.created_at = getRelativeTimeAgo(jsonObject.getString("created_at"));
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
            if(jsonObject.getJSONObject("entities").has("media"))
            {
                tweet.media = jsonObject.
                        getJSONObject("entities").
                        getJSONArray("media").
                        getJSONObject(0).
                        getString("media_url_https");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    public Tweet() {
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                tweets.add(Tweet.fromJson(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tweets;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
