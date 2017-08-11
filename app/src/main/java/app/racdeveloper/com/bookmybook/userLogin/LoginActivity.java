package app.racdeveloper.com.bookmybook.userLogin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

import app.racdeveloper.com.bookmybook.Constants;
import app.racdeveloper.com.bookmybook.ProfileActivity;
import app.racdeveloper.com.bookmybook.QueryPreferences;
import app.racdeveloper.com.bookmybook.R;
import app.racdeveloper.com.bookmybook.admin.AdminActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
//    private FirebaseAuth auth;
    private Button btnSignup, btnLogin, btnReset;
    private View mProgressView;
    private CheckBox checkBox;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fetchBooks();
//        auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser() != null) {
//            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
//            finish();
//        }
        mProgressView = findViewById(R.id.progressBar);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        checkBox= (CheckBox) findViewById(R.id.checkBox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isAdmin = true;
//                    Toast.makeText(LoginActivity.this, "Admin", Toast.LENGTH_SHORT).show();
                }
                else
                    isAdmin = false;
//                    Toast.makeText(LoginActivity.this, "User", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isAdmin)
                    loginAsAdmin(email, password);
                else {
                    showProgress(true);

                    QueryPreferences.setUserName(LoginActivity.this, email);
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class).putExtra("name", email);
                    startActivity(intent);
                    finish();

                    //authenticate user
//                    auth.signInWithEmailAndPassword(email, password)
//                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    // If sign in fails, display a message to the user. If sign in succeeds
//                                    // the auth state listener will be notified and logic to handle the
//                                    // signed in user can be handled in the listener.
//                                    showProgress(false);
//                                    if (!task.isSuccessful()) {
//                                        // there was an error
//                                        if (password.length() < 6) {
//                                            inputPassword.setError(getString(R.string.minimum_password));
//                                        } else {
//                                            Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
//                                        }
//                                    } else {
//                                        QueryPreferences.setUserName(LoginActivity.this, email);
//
//                                        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                }
//                            });
                }
            }
        });
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
    private void loginAsAdmin(String email, String password) {

        showProgress(true);
        String url= Constants.URL + "admin.php";

        Map<String, String> params = new HashMap<>();
        params.put("username", email);
        params.put("password", password);

        final RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                showProgress(false);
                try {
                    if(jsonObject.getString("success").equals("1")){
                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                        Toast.makeText(LoginActivity.this, "You are an Admin", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(LoginActivity.this, "Don't mess with Admin", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showProgress(false);
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jor);

    }

    public void fetchBooks() {
        String url= Constants.URL+"fetchList.php";
        final RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
//                Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                QueryPreferences.setBookInfo(LoginActivity.this, jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jor);
    }
}
