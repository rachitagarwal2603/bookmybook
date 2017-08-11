package app.racdeveloper.com.bookmybook.admin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.Map;

import app.racdeveloper.com.bookmybook.Constants;
import app.racdeveloper.com.bookmybook.R;

/**
 * Created by Rachit on 4/15/2017.
 */

public class DataBaseBookList extends AppCompatActivity{

    ListView listBook;
    HashMap<String,String> str;
    TextView tvStatus;

    static ArrayList<HashMap<String,String>> arrayList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        listBook= (ListView) findViewById(R.id.listView);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        arrayList= new ArrayList<>();

        fetchBookList();
        assert listBook != null;

        if(Constants.isAdmin) {
            listBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(DataBaseBookList.this);
                    builder.setTitle("Update this Book");

                    LinearLayout layout = new LinearLayout(DataBaseBookList.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText etbook = new EditText(DataBaseBookList.this);
                    etbook.setText(arrayList.get(position).get("BOOK"));
                    etbook.setInputType(InputType.TYPE_CLASS_TEXT);
                    layout.addView(etbook);

                    final EditText etcopies = new EditText(DataBaseBookList.this);
                    etcopies.setText(arrayList.get(position).get("AVAILABLE"));
                    etcopies.setInputType(InputType.TYPE_CLASS_NUMBER);
                    layout.addView(etcopies);

                    final EditText etissues = new EditText(DataBaseBookList.this);
                    etissues.setText(arrayList.get(position).get("ISSUES"));
                    etissues.setInputType(InputType.TYPE_CLASS_NUMBER);
                    layout.addView(etissues);

                    builder.setView(layout);
                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String bookName = etbook.getText().toString();
                            int copies = Integer.parseInt(etcopies.getText().toString());
                            int issues = Integer.parseInt(etissues.getText().toString());
                            addBookToDatabase(bookName, copies, issues);
                        }
                    });

                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });
        }
    }
    private void addBookToDatabase(String bookName, int copies, int issued) {
        String url = Constants.URL + "addbook.php";

        Map<String, String> params = new HashMap<>();
        params.put("name", bookName);
        params.put("available", ""+copies);
        params.put("issues", ""+issued);

        Log.d("ppppp", params.toString());

        final RequestQueue requestQueue = Volley.newRequestQueue(DataBaseBookList.this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Log.i("ppppp", jsonObject.toString());
                    if(jsonObject.getString("success").equals("1")) {
                        Toast.makeText(DataBaseBookList.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(DataBaseBookList.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jor);
    }

    private void fetchBookList() {
        String url= Constants.URL+"adminFetchList.php";
        final RequestQueue requestQueue = Volley.newRequestQueue(DataBaseBookList.this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                tvStatus.setVisibility(View.INVISIBLE);
                try {
                    Log.i("ppppp", jsonObject.toString());
                    if(jsonObject.getString("success").equals("1")) {
                        JSONArray array = jsonObject.getJSONArray("bookList");
                        int i,length = array.length();
                        for(i=0;i<length;i++){
                            str = new HashMap<String,String>();
                            str.put("BOOK", array.getJSONObject(i).getString("bookName"));
                            str.put("AVAILABLE", array.getJSONObject(i).getString("available"));
                            str.put("ISSUES", array.getJSONObject(i).getString("issues"));
                            arrayList.add(str);
                        }

                        SimpleAdapter adapter= new SimpleAdapter(DataBaseBookList.this, arrayList ,R.layout.custom_list2,
                                new String[]{"BOOK","AVAILABLE","ISSUES"},new int[]{R.id.textView3,R.id.textView4,R.id.textView5});
                        listBook.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                tvStatus.setText("Error, Retry.");
                Toast.makeText(DataBaseBookList.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jor);
    }


}
