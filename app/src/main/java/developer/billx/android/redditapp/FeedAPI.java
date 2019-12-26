package developer.billx.android.redditapp;

import java.util.Map;

import developer.billx.android.redditapp.account.CheckLogin;
import developer.billx.android.redditapp.comments.CheckComment;
import developer.billx.android.redditapp.model.Feed;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FeedAPI {

    String BASE_URL = "https://www.reddit.com/r/";

    //Non-static feed name
    @GET("{feed_name}/.rss")
    Call<Feed> getFeed(@Path("feed_name") String feed_name);

//    @GET("earthporn/.rss")
//    Call<Feed> getFeed();

    @POST("{user}")
    Call<CheckLogin> signIn(
            @HeaderMap Map<String, String> headers,
            @Path("user") String username,
            @Query("user") String user,
            @Query("passwd") String password,
            @Query("api_type") String type
    );

    @POST("{comment}")
    Call<CheckComment> submitComment(
            @HeaderMap Map<String, String> headers,
            @Path("comment") String comment,
            @Query("parent") String parent,
            @Query("amp;text") String text
    );
}
