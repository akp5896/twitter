package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.HeaderBinding;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

import okhttp3.Headers;
import okhttp3.internal.http2.Header;

public class ProfileToolbar {
    private static String TAG = "Profile setup";

    public static void Initialize(HeaderBinding binding, AppCompatActivity activity) {
        Context context = activity.getApplicationContext();
        TwitterClient client = TwitterApp.getRestClient(context);
        activity.setSupportActionBar(binding.toolbar);
        activity.getSupportActionBar().setTitle("");
        setBanner(client, context, binding);
    }

    public static void setBanner(TwitterClient client, Context context, HeaderBinding binding) {
        client.getUsername(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    String name = json.jsonObject.getString("screen_name");
                    Log.i(TAG, name);
                    loadBanner(name, client, context, binding);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "cannot get current username");
            }
        });
    }

    private static void loadBanner(String name, TwitterClient client, Context context, HeaderBinding binding) {
        client.getBanner(name, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    String bannerUrl = json.jsonObject.getString("profile_banner_url");
                    Glide.with(context).load(bannerUrl).into(binding.ivBanner);
                    Log.d(TAG, bannerUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "cannot get banner");
            }
        });
    }
}


