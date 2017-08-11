package app.racdeveloper.com.bookmybook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import app.racdeveloper.com.bookmybook.userLogin.LoginActivity;

/**
 * Created by Rachit on 5/6/2017.
 */

public class SplashActivity extends AppCompatActivity{

    Handler handler;
//    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        fetchBooks();
//        auth = FirebaseAuth.getInstance();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (auth.getCurrentUser() != null) {
//                    startActivity(new Intent(SplashActivity.this, ProfileActivity.class));
//                }
//                else
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
        }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void fetchBooks() {
        String url= Constants.URL+"fetchList.php";
        final RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
//                Toast.makeText(SplashActivity.this, "Success", Toast.LENGTH_SHORT).show();
                QueryPreferences.setBookInfo(SplashActivity.this, jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SplashActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jor);
    }
}
