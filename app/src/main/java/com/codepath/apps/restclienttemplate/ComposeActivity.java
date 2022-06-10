package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    private static final String TAG = "COMPOSE ACTIVITY";

    ActivityComposeBinding binding;
    TwitterClient client;
    private MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComposeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ProfileToolbar.Initialize(binding.header, this);

        client = TwitterApp.getRestClient(this);

        binding.btnTweet.setOnClickListener(getButtonListener());
    }

    @NonNull
    private View.OnClickListener getButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = binding.etTweetText.getText().toString();
                if(tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, cannot post empty tweet", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tweetContent.length() > 280) {
                    Toast.makeText(ComposeActivity.this, "Sorry, text too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                miActionProgressItem.setVisible(true);
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_SHORT).show();

                client.publishTweet(tweetContent, -1L, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "success to publish");
                        Tweet tweet = Tweet.fromJson(json.jsonObject);
                        Intent i = new Intent();

                        i.putExtra("tweet", Parcels.wrap(tweet));
                        setResult(RESULT_OK, i);
                        miActionProgressItem.setVisible(false);
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        miActionProgressItem.setVisible(false);
                        Log.i(TAG, "failed to publish");
                    }
                });
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.compose, menu);
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        return true;
    }
}