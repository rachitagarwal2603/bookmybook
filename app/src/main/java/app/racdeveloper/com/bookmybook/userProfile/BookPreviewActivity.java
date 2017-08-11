package app.racdeveloper.com.bookmybook.userProfile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.racdeveloper.com.bookmybook.Constants;
import app.racdeveloper.com.bookmybook.R;

/**
 * Created by Rachit on 5/26/2017.
 */

public class BookPreviewActivity extends AppCompatActivity {

    private int bookId;
    ListView listReview;
    ImageView image1, image2, expandedImageView;
    TextView tvLoad;
    HashMap<String, String> str;
    static ArrayList<HashMap<String,String>> arrayList;

    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;            //     set to 200
    View reviewListLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_preview);

        arrayList= new ArrayList<>();
        listReview = (ListView) findViewById(R.id.lvReview);
        image1 = (ImageView) findViewById(R.id.iv1);
        image2 = (ImageView) findViewById(R.id.iv2);
        tvLoad = (TextView) findViewById(R.id.tvLoad);
        expandedImageView = (ImageView) findViewById(R.id.expanded_image);
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        reviewListLayout = listReview;

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandedImageView.setImageDrawable(image1.getDrawable());
                zoomImageFromThumb(image1);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandedImageView.setImageDrawable(image2.getDrawable());
                zoomImageFromThumb(image2);
            }
        });

        bookId = getIntent().getExtras().getInt("bookId");
        BookDetails details = BookDetails.details.get(bookId);
        fetchReviews(details.getName());
    }

    private void fetchReviews(String bookName) {

        final RequestQueue requestQueue = Volley.newRequestQueue(BookPreviewActivity.this);
        String url = Constants.URL + "fetchReview.php";

        Map<String, String> param = new HashMap<>();
        param.put("bookName", bookName);

//        Toast.makeText(this, bookName+ " "+ Constants.USERNAME + " "+ content + " "+ rate, Toast.LENGTH_SHORT).show();

        final JsonObjectRequest jor= new JsonObjectRequest(Request.Method.POST, url, new JSONObject(param), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                try {
                    if(jsonObject.getString("success").equals("1")){
                        tvLoad.setVisibility(View.GONE);
                        Log.d("pppppp", jsonObject.toString());

                        JSONArray array= jsonObject.getJSONArray("review");

                        int i, length = array.length();
                        for(i=0;i<length;i++){
                            str= new HashMap<>();
                            str.put("USER_NAME", array.getJSONObject(i).getString("userName"));
                            str.put("CONTENT", array.getJSONObject(i).getString("content"));
                            arrayList.add(str);
                        }

                        SimpleAdapter adapter = new SimpleAdapter(BookPreviewActivity.this, arrayList, R.layout.custom_list3,
                                new String[]{"USER_NAME", "CONTENT"}, new int[]{R.id.tv1, R.id.tv2});
                        listReview.setAdapter(adapter);
                    }
                    else{
                        tvLoad.setText("No reviews available.");
//                        Toast.makeText(BookPreviewActivity.this, "Submit Again", Toast.LENGTH_SHORT).show();
                    }

                    JSONArray picArray = jsonObject.getJSONArray("pics");

                    Glide.with(BookPreviewActivity.this)
                            .load(Constants.BOOK_PIC_URL + picArray.getJSONObject(0).getString("image"))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(image1);

                    Glide.with(BookPreviewActivity.this)
                            .load(Constants.BOOK_PIC_URL + picArray.getJSONObject(1).getString("image"))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(image2);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(BookPreviewActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jor);
    }

    private void zoomImageFromThumb(final View imageLoaderView) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        imageLoaderView.getGlobalVisibleRect(startBounds);

        imageLoaderView.getRootView().findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.

        reviewListLayout.setAlpha(0.3f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        reviewListLayout.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        reviewListLayout.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
