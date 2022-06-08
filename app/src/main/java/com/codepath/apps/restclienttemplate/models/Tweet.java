package com.codepath.apps.restclienttemplate.models;

import android.content.Intent;
import android.provider.ContactsContract;
import android.text.format.DateUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Tweet {
    @ColumnInfo
    public String body;
    @ColumnInfo
    public String created_at;
    @Ignore
    public User user;
    @ColumnInfo
    public String media;
    @ColumnInfo
    public  Integer likes;
    @ColumnInfo
    public Boolean isLiked;
    @ColumnInfo
    public Integer retweets;
    @ColumnInfo
    public Boolean retweeted;
    @ColumnInfo
    @PrimaryKey
    public Long tweetId;
    @Ignore
    public boolean isRetweeted = false;
    @ColumnInfo
    public Long userId;


    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
           tweet.body = jsonObject.getString("text");
            tweet.tweetId = jsonObject.getLong("id");
            tweet.created_at = getRelativeTimeAgo(jsonObject.getString("created_at"));
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
            tweet.likes = jsonObject.getInt("favorite_count");
            tweet.isLiked = jsonObject.getBoolean("favorited");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.retweets = jsonObject.getInt("retweet_count");
            tweet.userId = tweet.user.id;
            if(jsonObject.getJSONObject("entities").has("media"))
            {
                tweet.media = jsonObject.
                        getJSONObject("entities").
                        getJSONArray("media").
                        getJSONObject(0).
                        getString("media_url_https");
            }
            if(jsonObject.has("retweeted_status")) {
                tweet.isRetweeted = true;
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
                Tweet tweet = Tweet.fromJson(jsonArray.getJSONObject(i));
                if(!tweet.isRetweeted)
                    tweets.add(tweet);
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
