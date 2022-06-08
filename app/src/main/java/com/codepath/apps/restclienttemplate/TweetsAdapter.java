package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.sql.Time;
import java.util.List;

import fragments.ComposeTweet;
import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
  ItemTweetBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);
        }

        public void bind(Tweet tweet) {
            binding.tvBody.setText(tweet.body);
            binding.tvName.setText(tweet.user.name);
            binding.tvHandle.setText(String.format("@%s", tweet.user.handle));
            binding.tvDate.setText(tweet.created_at);
            binding.tvLikesCount.setText(tweet.likes.toString());
            if(tweet.isLiked) {
                binding.ivHeart.setColorFilter(context.getResources().getColor(R.color.inline_action_like_pressed));
            }
            if(tweet.retweeted) {
                binding.ivShare.setColorFilter(context.getResources().getColor(R.color.inline_action_retweet));
            }

            getaVoid(tweet);
            retweetTrigger(tweet);

            binding.ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = ((TimelineActivity)context).getSupportFragmentManager();
                    ComposeTweet composeFragment = ComposeTweet.newInstance(tweet);
                    composeFragment.show(fm, "fragment_compose_tweet");
                }
            });

            binding.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, UserProfileActivity.class);
                    i.putExtra(User.class.getSimpleName(), Parcels.wrap(tweet.user));
                    context.startActivity(i);
                }
            });

            binding.tvRetweetCount.setText(tweet.retweets.toString());
            Glide.with(context)
                    .load(tweet.user.publicImage)
                    .transform(new RoundedCorners(50))
                    .into(binding.ivAvatar);
            if(tweet.media != null) {
                binding.ivMedia.setVisibility(View.VISIBLE);
                Glide
                        .with(context)
                        .load(tweet.media)
                        .transform(new RoundedCorners(30))
                        .into(binding.ivMedia);
            }
            else {
                binding.ivMedia.setVisibility(View.GONE);
            }
        }

        private void retweetTrigger(Tweet tweet) {
            binding.ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TwitterClient client = TwitterApp.getRestClient(context);
                    if(!tweet.retweeted) {
                        client.retweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.retweeted = true;
                                tweet.retweets++;
                                binding.tvRetweetCount.setText(tweet.retweets.toString());
                                binding.ivShare.setColorFilter(context.getResources().getColor(R.color.inline_action_retweet));
                                Log.i("LIKE", "success");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.i("LIKE", response);
                            }
                        });
                    } else {
                        client.unretweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.retweeted = false;
                                tweet.retweets--;
                                binding.tvRetweetCount.setText(tweet.retweets.toString());
                                binding.ivShare.setColorFilter(context.getResources().getColor(R.color.inline_action_pressed));
                                Log.i("LIKE", "success");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.i("LIKE", response);
                            }
                        });
                    }
                }
            });
        }

        private void getaVoid(Tweet tweet) {
            binding.ivHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TwitterClient client = TwitterApp.getRestClient(context);
                    if(!tweet.isLiked) {
                        client.likeTweet(tweet.selfId, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.isLiked = true;
                                tweet.likes++;
                                binding.tvLikesCount.setText(tweet.likes.toString());
                                binding.ivHeart.setColorFilter(context.getResources().getColor(R.color.inline_action_like_pressed));
                                Log.i("LIKE", "success");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.i("LIKE", response);
                            }
                        });
                    } else {
                        client.destroy(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                tweet.isLiked = false;
                                tweet.likes--;
                                binding.tvLikesCount.setText(tweet.likes.toString());
                                binding.ivHeart.setColorFilter(context.getResources().getColor(R.color.inline_action_disabled));
                                Log.i("LIKE", "success");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.i("LIKE", response);
                            }
                    });
                    }
                }
            });
        }
    }
}
