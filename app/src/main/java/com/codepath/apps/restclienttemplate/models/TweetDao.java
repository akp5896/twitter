package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDao {
    @Query("SELECT Tweet.body as tweet_body, Tweet.created_at as tweet_created_at, Tweet.tweetId as tweet_id," +
            "Tweet.media as tweet_media, Tweet.likes as tweet_likes, Tweet.isLiked as tweet_isLiked, " +
            "Tweet.retweeted as tweet_retweeted, Tweet.retweets as tweet_retweets, User.*" +
            " FROM Tweet INNER JOIN User ON Tweet.userId = User.Id ORDER BY created_at DESC LIMIT 5")
    List<TweetWithUser> recentItems();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(User... users);
}
