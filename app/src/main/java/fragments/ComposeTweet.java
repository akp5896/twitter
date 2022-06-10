package fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.ComposeActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TimelineActivity;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.databinding.FragmentComposeTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeTweet extends DialogFragment {
    private static final String TAG = "COMPOSE TWEET";
    FragmentComposeTweetBinding binding;
    Tweet tweet;

    public ComposeTweet() {
    }
    public static ComposeTweet newInstance(Tweet tweet) {
        ComposeTweet fragment = new ComposeTweet();
        fragment.tweet = tweet;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TimelineActivity activity = (TimelineActivity)getActivity();
        activity.client = TwitterApp.getRestClient(getActivity().getApplicationContext());

        binding.btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = binding.etTweetText.getText().toString();
                if(tweetContent.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Sorry, cannot post empty tweet", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tweetContent.length() > 280) {
                    Toast.makeText(getActivity().getApplicationContext(), "Sorry, text too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getActivity().getApplicationContext(), tweetContent, Toast.LENGTH_SHORT).show();
                Long inResponseToId;
                if(tweet == null) {
                    inResponseToId  = -1L;
                }
                else {
                    inResponseToId = tweet.tweetId;
                    tweetContent = "@" + tweet.user.handle + " " + tweetContent;
                }
                activity.client.publishTweet(tweetContent, inResponseToId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "success to publish");
                        Tweet tweet = Tweet.fromJson(json.jsonObject);

                        activity.tweets.add(0, tweet);
                        activity.adapter.notifyItemInserted(0);
                        activity.binding.rvTimeline.smoothScrollToPosition(0);

                        dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.i(TAG, "failed to publish");
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentComposeTweetBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}