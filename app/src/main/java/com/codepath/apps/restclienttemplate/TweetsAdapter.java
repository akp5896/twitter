package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.w3c.dom.Text;

import java.util.List;

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
    }
}
