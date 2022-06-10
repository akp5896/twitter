package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityUserProfileBinding;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "user profile activity";
    ActivityUserProfileBinding binding;

    List<User> followers = new ArrayList<>();
    UserAdapter adapter;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new UserAdapter(followers, this);
        binding.rvFollowers.setAdapter(adapter);
        binding.rvFollowers.setLayoutManager(new LinearLayoutManager(this));

        user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        populateFollowers();
    }

    private void populateFollowers() {
        TwitterClient client = TwitterApp.getRestClient(this);
        client.getFollowersList(user.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    List<User> userList = User.fromJsonArray(json.jsonObject.getJSONArray("users"));
                    followers.addAll(userList);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "user failure");
            }
        });
    }
}