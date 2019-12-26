package developer.billx.android.redditapp.account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;

import developer.billx.android.redditapp.FeedAPI;
import developer.billx.android.redditapp.R;
import developer.billx.android.redditapp.URLS;
import developer.billx.android.redditapp.comments.CommentsActivity;
import developer.billx.android.redditapp.model.Feed;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private ProgressBar mProgressBar;
    private EditText mUsername;
    private EditText mPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: started");

        btnLogin = findViewById(R.id.btn_login);
        mUsername = findViewById(R.id.input_username);
        mPassword = findViewById(R.id.input_password);
        mProgressBar = findViewById(R.id.loginRequestLoadingProgressBar);
        mProgressBar.setVisibility(View.GONE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: attempting to log in.");
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                if (!username.equals("") && !password.equals("")) {
                    mProgressBar.setVisibility(View.VISIBLE);

                    login(username, password);
                }
            }
        });
    }

    private void login(final String username, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLS.LOGIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");

        Call<CheckLogin> call = feedAPI.signIn(
                headerMap, username, username, password, "json");

        call.enqueue(new Callback<CheckLogin>() {
            @Override
            public void onResponse(Call<CheckLogin> call, Response<CheckLogin> response) {
                try {
                    Log.d(TAG, "onResponse: Server Response: " + response.toString());

                    String modhash = response.body().getJson().getData().getModhash();
                    String cookie = response.body().getJson().getData().getCookie();
                    Log.d(TAG, "onResponse: modhash: " + modhash);
                    Log.d(TAG, "onResponse: cookie: " + cookie);

                    if (!modhash.equals("")) {
                        setSessionParams(username, modhash, cookie);
                        mProgressBar.setVisibility(View.GONE);
                        mUsername.setText("");
                        mPassword.setText("");
                        Toast.makeText(LoginActivity.this,
                                "Login Successful", Toast.LENGTH_SHORT).show();

                        //navigate back to previous activity
                        finish();
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "onResponse: NullPointerException: " +
                        e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<CheckLogin> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(LoginActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSessionParams(String username, String modhash, String cookie) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = preferences.edit();

        Log.d(TAG, "setSessionParams: Storing session variables: \n" +
                "username: " + username + "\n" +
                "modhash: " + modhash + "\n" +
                "cookie: " + cookie + "\n"
        );

        editor.putString("@string/SessionUsername", username);
        editor.commit();
        editor.putString("@string/SessionModhash", modhash);
        editor.commit();
        editor.putString("@string/SessionCookie", cookie);
        editor.commit();
        
    }
}