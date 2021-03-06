package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {
    @ColumnInfo
    public String name;
    @ColumnInfo
    public String handle;
    @ColumnInfo
    public String publicImage;
    @ColumnInfo
    @PrimaryKey
    public Long id;

    public User() {
    }

    public static User fromJson(JSONObject jsonObject) {
        User user = new User();
        try {
            user.id = jsonObject.getLong("id");
            user.name = jsonObject.getString("name");
            user.handle = jsonObject.getString("screen_name");
            user.publicImage = jsonObject.getString("profile_image_url_https");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static List<User> fromJsonArray(JSONArray jsonArray) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                users.add(User.fromJson(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    public static List<User> fromTweetArray(List<Tweet> tweetsList) {
        List<User> users = new ArrayList<>();
        for(Tweet t : tweetsList) {
            users.add(t.user);
        }
        return users;
    }
}
