package app.racdeveloper.com.bookmybook.admin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.racdeveloper.com.bookmybook.Constants;
import app.racdeveloper.com.bookmybook.R;

/**
 * Created by Rachit on 4/15/2017.
 */
public class IssuedBookList extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    ListView issuedBooks;
    static ArrayList<HashMap<String,String>> arrayList;
    HashMap<String,String> str;
    TextView tvStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_list);

        issuedBooks = (ListView) findViewById(R.id.lvIssuedList);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        arrayList= new ArrayList<>();
        fetchIssuedBookList();
    }

    private void fetchIssuedBookList() {
        String url= Constants.URL+"issuelist.php";
        final RequestQueue requestQueue = Volley.newRequestQueue(IssuedBookList.this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Log.i("ppppp", jsonObject.toString());
                    if(jsonObject.getString("success").equals("1")) {
                        tvStatus.setVisibility(View.INVISIBLE);
                        JSONArray array = jsonObject.getJSONArray("bookList");
                        int i,length = array.length();
                        for(i=0;i<length;i++){
                            str = new HashMap<>();
                            str.put("NAME", array.getJSONObject(i).getString("name"));
                            str.put("PHONE", array.getJSONObject(i).getString("phoneNo"));
                            str.put("HOSTEL", array.getJSONObject(i).getString("hostel"));
                            str.put("BOOK", array.getJSONObject(i).getString("bookIssued"));
                            arrayList.add(str);
                        }

                        SimpleAdapter adapter= new SimpleAdapter(IssuedBookList.this, arrayList ,R.layout.custom_list,
                                new String[]{"NAME","PHONE","HOSTEL","BOOK"},new int[]{R.id.textView3,R.id.textView4,R.id.textView5,R.id.textView6});
                        issuedBooks.setAdapter(adapter);
                    }
                    else
                        tvStatus.setText("Not Available.");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                tvStatus.setText("Not Available.");
                Toast.makeText(IssuedBookList.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jor);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
