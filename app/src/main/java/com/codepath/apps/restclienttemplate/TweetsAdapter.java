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

        ImageView ivAvatar;
        TextView tvBody;
        TextView tvHandle;
        ImageView ivMedia;
        TextView tvDate;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvHandle = itemView.findViewById(R.id.tvHandle);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvName = itemView.findViewById(R.id.tvName);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvName.setText(tweet.user.name);
            tvHandle.setText(tweet.user.handle);
            tvDate.setText(tweet.created_at);
            Glide.with(context)
                    .load(tweet.user.publicImage)
                    .transform(new RoundedCorners(50))
                    .into(ivAvatar);
            if(tweet.media != null) {
                ivMedia.setVisibility(View.VISIBLE);
                Glide
                        .with(context)
                        .load(tweet.media)
                        .transform(new RoundedCorners(30))
                        .into(ivMedia);
            }
            else {
                ivMedia.setVisibility(View.GONE);
            }
        }
    }
}
