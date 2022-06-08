package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import fragments.ComposeTweet;
import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    private static final String TAG = "TIMELINE ACTIVITY";
    private final int REQUEST_CODE = 20;

    public List<Tweet> tweets = new ArrayList<>();
    public ActivityTimelineBinding binding;
    public TwitterClient client;
    public TweetsAdapter adapter;
    private MenuItem miActionProgressItem;
    private Long maxId = 0L;
    private EndlessRecyclerViewScrollListener scrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        client = TwitterApp.getRestClient(this);

        adapter = new TweetsAdapter(this, tweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvTimeline.setLayoutManager(linearLayoutManager);
        binding.rvTimeline.setAdapter(adapter);

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_dark);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateHomeTimeline();
            }
        };
        binding.rvTimeline.addOnScrollListener(scrollListener);
    }

    private void fetchTimelineAsync(int i) {
        showProgressBar();
        client.getTimeline(0L, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "success");
                adapter.clear();
                tweets.addAll(Tweet.fromJsonArray(json.jsonArray));
                adapter.notifyItemRangeInserted(0, 25);
                binding.swipeContainer.setRefreshing(false);
                maxId = tweets.get(tweets.size() - 1).selfId;
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "failure: " + response);
                hideProgressBar();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        populateHomeTimeline();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.compose) {
            Toast.makeText(this, "Compose!", Toast.LENGTH_LONG).show();
            //Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
            //startActivityForResult(i, REQUEST_CODE);
            FragmentManager fm = getSupportFragmentManager();
            ComposeTweet composeFragment = ComposeTweet.newInstance(null);
            composeFragment.show(fm, "fragment_compose_tweet");
        }
        if(item.getItemId() == R.id.logout) {
            TwitterApp.getRestClient(TimelineActivity.this).clearAccessToken();
            finish();
        }
        return true;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            binding.rvTimeline.smoothScrollToPosition(0);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        return super.onPrepareOptionsMenu(menu);
    }



    private void populateHomeTimeline() {
        showProgressBar();
        client.getTimeline(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "success");
                tweets.addAll(Tweet.fromJsonArray(json.jsonArray));
                adapter.notifyItemRangeInserted(0, 25);
                maxId = tweets.get(tweets.size() - 1).selfId;
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i(TAG, "failure: " + response);
                hideProgressBar();
            }
        });
    }

    public void showProgressBar() {
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        miActionProgressItem.setVisible(false);
    }
}