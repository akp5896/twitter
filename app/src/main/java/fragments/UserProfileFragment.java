package fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.UserAdapter;
import com.codepath.apps.restclienttemplate.databinding.FragmentUserProfileBinding;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;


public class UserProfileFragment extends DialogFragment {

    private static final String TAG = "USER FRAGMENT";
    FragmentUserProfileBinding binding;
    User user;
    private final List<User> followers = new ArrayList<>();
    private final List<User> following = new ArrayList<>();
    private UserAdapter followersAdapter;
    private UserAdapter followingAdapter;
    Context context;

    public UserProfileFragment() {
    }
    public static UserProfileFragment newInstance(User user) {
        UserProfileFragment fragment = new UserProfileFragment();
        fragment.user = user;
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        followersAdapter = new UserAdapter(followers, context);
        binding.rvFollowers.setAdapter(followersAdapter);
        binding.rvFollowers.setLayoutManager(new LinearLayoutManager(context));

        followingAdapter = new UserAdapter(following, context);
        binding.rvFollowing.setAdapter(followingAdapter);
        binding.rvFollowing.setLayoutManager(new LinearLayoutManager(context));

        binding.btnClose.setOnClickListener(view1 -> dismiss());

        populateFollows("followers/list.json", followersAdapter, followers);
        populateFollows("friends/list.json", followingAdapter, following);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(getLayoutInflater());
        context = getActivity().getApplicationContext();
        return binding.getRoot();
    }

    private void populateFollows(String endpoint, UserAdapter adapter, List<User> users) {
        TwitterClient client = TwitterApp.getRestClient(context);
        client.getFollows(user.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    List<User> userList = User.fromJsonArray(json.jsonObject.getJSONArray("users"));
                    users.addAll(userList);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "user failure");
            }
        }, endpoint);
    }

    @Override
    public void onStart() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        getDialog().getWindow().setLayout((8 * width) / 9, (4 * height) / 5);
        super.onStart();
    }
}