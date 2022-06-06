package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    private static final String TAG = "COMPOSE ACTIVITY";
    EditText etCompose;
    Button btnTweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_actvity);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etTweetText);
        btnTweet = findViewById(R.id.btnTweet);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, cannot post empty tweet", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tweetContent.length() > 280) {
                    Toast.makeText(ComposeActivity.this, "Sorry, text too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_SHORT).show();
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "success to publish");
                        Tweet tweet = Tweet.fromJson(json.jsonObject);
                        Log.i(TAG, "PUblished tweet says: " + tweet.body);
                        Intent i = new Intent();
                        i.putExtra("tweet", Parcels.wrap(tweet));
                        setResult(RESULT_OK, i);
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.i(TAG, "failed to publish");
                    }
                });
            }
        });
    }
}