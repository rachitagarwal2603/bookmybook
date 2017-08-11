package app.racdeveloper.com.bookmybook;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rachit on 4/30/2017.
 */
public class IssueBookActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button submit;
    private AutoCompleteTextView etBookName;
    private EditText etBorrower, etContact, etRoll;
    private Spinner hostel, genre;
    String hostelName[]={"Hostel","F_Kalpana","F_Yashodhara","M_Vrindawan","M_Saket","M_Panchwati","M_JaiBharat"};
    String genres[]={"Search by Genres","Fiction","Romance","Thriller","Suspense","Essay","Motivation","AutoBiography"};
    String genresShort[]={"","fic","rom","thr","sus","ess","mot","aut"};
    List<String> listBook;
    View mProgressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_book);

        mProgressView = findViewById(R.id.progress);
        etBorrower = (EditText) findViewById(R.id.etBorrower);
        etContact = (EditText) findViewById(R.id.etPhone);
        etBookName = (AutoCompleteTextView) findViewById(R.id.etBookName);
        etBookName.setThreshold(1);
        //addBooksToAutoCompleteTextView();
        etRoll = (EditText) findViewById(R.id.etRollNo);
        submit = (Button) findViewById(R.id.bSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String borrower, bookName, contact, hostelName, rollNo;
                borrower = etBorrower.getText().toString();
                bookName = etBookName.getText().toString();
                contact = etContact.getText().toString();
                rollNo = etRoll.getText().toString();
                hostelName = Constants.borrowerHostel;

                if (borrower.equals("") || bookName.equals("") || contact.equals("") || hostelName.equals("") || rollNo.equals("")) {
                    Toast.makeText(IssueBookActivity.this, "Some fields missing", Toast.LENGTH_SHORT).show();
                } else
                    checkIssueRequest(borrower, bookName, contact, hostelName, rollNo);
            }
        });

        hostel = (Spinner) findViewById(R.id.spinner);
        hostel.setOnItemSelectedListener(IssueBookActivity.this);
        ArrayAdapter<String> ad = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, hostelName);
        hostel.setAdapter(ad);

        genre = (Spinner) findViewById(R.id.spinner2);
        genre.setOnItemSelectedListener(IssueBookActivity.this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, genres);
        genre.setAdapter(adapter);
    }

    private void checkIssueRequest(String borrower, String bookName, String contact, String hostelName, String rollNo) {

        showProgress(true);
        submit.setVisibility(View.GONE);
        String url = Constants.URL + "issuebook.php";
        RequestQueue requestQueue = Volley.newRequestQueue(IssueBookActivity.this);

        Map<String, String> params= new HashMap<>();
        params.put("name", borrower);
        params.put("bookname", bookName);
        params.put("contact", contact);
        params.put("hostel", hostelName);
        params.put("rollno", rollNo);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                showProgress(false);
                try {
                    if (jsonObject.getString("success").equals("1")){
                        Toast.makeText(IssueBookActivity.this, ""+ jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                        submit.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showProgress(false);
                submit.setVisibility(View.VISIBLE);
                Toast.makeText(IssueBookActivity.this, "Error : "+ volleyError, Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jor);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == hostel) {
            switch (position) {
                case 0:
                    Constants.borrowerHostel = "";
                    break;
                default:
                    Constants.borrowerHostel = hostelName[position];
//                    Toast.makeText(IssueBookActivity.this, Constants.borrowerHostel, Toast.LENGTH_SHORT).show();
            }
        } else if (parent == genre) {
            Constants.BookGenre = genresShort[position];
            addBooksToAutoCompleteTextView();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void addBooksToAutoCompleteTextView(){
        listBook = new ArrayList<>();
        if(QueryPreferences.getBookInfo(getApplicationContext())!=null) {
            try {
                JSONObject jsonObject = new JSONObject(QueryPreferences.getBookInfo(getApplicationContext()));
                if (jsonObject.getString("success").equals("1")) {
                    JSONArray array = null;
                    try {
                        array = jsonObject.getJSONArray("bookList");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int i, length = array.length();
                    for (i = 0; i < length; i++) {
                        String book;
                        int available= Integer.parseInt(array.getJSONObject(i).getString("available"));
                        int issues= Integer.parseInt(array.getJSONObject(i).getString("issues"));
                        String genre = array.getJSONObject(i).getString("genre");
                        String searchGenre = Constants.BookGenre;
                        if(available>issues && genre.contains(searchGenre)) {
                            book = array.getJSONObject(i).getString("bookName");
                            listBook.add(book);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(IssueBookActivity.this,
                        android.R.layout.simple_list_item_checked, listBook);
        //simple_dropdown_item_1line   simple_list_item_checked
        etBookName.setAdapter(adapter);
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
