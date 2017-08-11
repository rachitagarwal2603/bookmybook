package app.racdeveloper.com.bookmybook;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.racdeveloper.com.bookmybook.userProfile.BookDetails;

/**
 * Created by Rachit on 5/24/2017.
 */

public class ReviewActivity extends AppCompatActivity{

    RatingBar ratingBar;
    Button submit;
    EditText review;
    boolean isRatingSet=false;
    float oldRateValue;
    int totalRateFreq;
    float rate;
    String bookName;
    View mProgressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        int bookId = intent.getExtras().getInt("bookId");
        BookDetails detail = BookDetails.details.get(bookId);
        bookName = detail.getName();
        oldRateValue = detail.getRating();
        totalRateFreq = detail.getTotalRating();
        mProgressView = findViewById(R.id.progress1);

        ratingBar = (RatingBar) findViewById(R.id.rbChoice);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                isRatingSet = true;
                rate = rating;
//                Toast.makeText(ReviewActivity.this, rating +"   "+ fromUser, Toast.LENGTH_SHORT).show();
            }
        });

        review= (EditText) findViewById(R.id.etReview);

        submit= (Button) findViewById(R.id.bSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///Toast.makeText(ReviewActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                String content = review.getText().toString();
                if(content.equals("")  || !isRatingSet)
                    Toast.makeText(ReviewActivity.this, "Please rate and review both!!", Toast.LENGTH_SHORT).show();
                else{
                    ratingBar.setIsIndicator(true);
                    generateSubmitRequest(content, rate);
                }
            }
        });
    }

    private void generateSubmitRequest(String content, float rate) {

        showProgress(true);
        submit.setVisibility(View.GONE);

        rate = (rate+oldRateValue*totalRateFreq)/(totalRateFreq+1);             //Calculate new average rate
        Log.d("pppppp", totalRateFreq+" "+oldRateValue+" "+ rate);

        final RequestQueue requestQueue = Volley.newRequestQueue(ReviewActivity.this);
        String url = Constants.URL + "addReview.php";
        Map<String, String> param = new HashMap<>();
        param.put("bookName", bookName);
        param.put("userName", QueryPreferences.getUserName(ReviewActivity.this));
        param.put("content", content);
        param.put("rating", String.valueOf(rate));

        JsonObjectRequest jor= new JsonObjectRequest(Request.Method.POST, url, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                showProgress(false);
                Log.d("pppppp", jsonObject.toString());
//                Toast.makeText(ReviewActivity.this, " In Response", Toast.LENGTH_SHORT).show();
                try {
                    if(jsonObject.getString("success").equals("1")){
                        Toast.makeText(ReviewActivity.this, "Review Submitted", Toast.LENGTH_SHORT).show();
                        fetchBooks();
                    }
                    else{
                        submit.setVisibility(View.VISIBLE);
                        Toast.makeText(ReviewActivity.this, "Submit Again", Toast.LENGTH_SHORT).show();
                        ratingBar.setIsIndicator(false);
                        review.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                submit.setVisibility(View.VISIBLE);
                Toast.makeText(ReviewActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jor);
    }

    public void fetchBooks() {
        String url= Constants.URL+"fetchList.php";

        final RequestQueue requestQueue = Volley.newRequestQueue(ReviewActivity.this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                QueryPreferences.setBookInfo(ReviewActivity.this, jsonObject.toString());
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ReviewActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jor);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
