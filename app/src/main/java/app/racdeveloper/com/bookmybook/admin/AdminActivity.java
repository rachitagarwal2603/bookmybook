package app.racdeveloper.com.bookmybook.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import app.racdeveloper.com.bookmybook.Constants;
import app.racdeveloper.com.bookmybook.FilePath;
import app.racdeveloper.com.bookmybook.R;

/**
 * Created by Rachit on 4/15/2017.
 */

public class AdminActivity extends AppCompatActivity {

    Button addBook, bookList, issuedList, deleteIssueRecords, addPDF;
    LinearLayout linearlayout;
    private Uri filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Constants.isAdmin = true;

        linearlayout = (LinearLayout) findViewById(R.id.linearLayout);
        addBook = (Button) findViewById(R.id.bAdd);
        bookList = (Button) findViewById(R.id.bList);
        issuedList = (Button) findViewById(R.id.bIssueList);
        deleteIssueRecords = (Button) findViewById(R.id.bDeleteIssueList);
        addPDF = (Button) findViewById(R.id.bAddPDF);

        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder= new AlertDialog.Builder(AdminActivity.this);
                builder.setTitle("Add a Book");

                LinearLayout layout = new LinearLayout(AdminActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText etbook = new EditText(AdminActivity.this);
                etbook.setHint("Book Name");
                etbook.setInputType(InputType.TYPE_CLASS_TEXT);
                layout.addView(etbook);

                final EditText etcopies = new EditText(AdminActivity.this);
                etcopies.setHint("Number of Copies");
                etcopies.setInputType(InputType.TYPE_CLASS_NUMBER);
                layout.addView(etcopies);

                builder.setView(layout);
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String bookName= etbook.getText().toString();
                        int copies= Integer.parseInt(etcopies.getText().toString());
                        addBookToDatabase(bookName, copies, 0);
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

        bookList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, DataBaseBookList.class));
            }
        });

        issuedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, IssuedBookList.class));
            }
        });

        deleteIssueRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder= new AlertDialog.Builder(AdminActivity.this);
                builder.setTitle("Delete Issued Books Record?");

                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //runs issued books table delete query

                        String url = Constants.URL + "deleteIssueRecords.php";
                        final RequestQueue requestQueue = Volley.newRequestQueue(AdminActivity.this);
                        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    if(jsonObject.getString("success").equals("1"))
                                        Toast.makeText(AdminActivity.this, "Successfully deleted", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(AdminActivity.this, "Error Deleting Records", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Toast.makeText(AdminActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                        requestQueue.add(jor);
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
        // add pdfs
        addPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(linearlayout, "     Upload PDFs", Snackbar.LENGTH_LONG)
                        .setAction("UPLOAD", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                                i.setType("application/pdf");
                                i.addCategory(Intent.CATEGORY_OPENABLE);
                                startActivityForResult(Intent.createChooser(i, "Select your PDF"), 1);
                            }
                        });

                // Changing message text color
                snackbar.setActionTextColor(Color.WHITE);

                // Changing action button text color
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(Color.rgb(18,86,136));//125688);//
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(18);
                snackbar.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK  && data!=null){
            filePath = data.getData();

            final AlertDialog.Builder builder= new AlertDialog.Builder(AdminActivity.this);
            builder.setTitle("Give Book Name");
            LinearLayout layout = new LinearLayout(AdminActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText etbook = new EditText(AdminActivity.this);
            etbook.setHint("Book Name");
            etbook.setInputType(InputType.TYPE_CLASS_TEXT);
            layout.addView(etbook);

            builder.setView(layout);
            builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    uploadMultipart(etbook.getText().toString());
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
    }

    private void uploadMultipart(String book) {

        String URL_UPLOAD = Constants.URL + "upload.php";
        String path = FilePath.getPath(this, filePath);
        Toast.makeText(this, ""+ path, Toast.LENGTH_LONG).show();

        if (path == null) {
            Toast.makeText(this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, URL_UPLOAD)
                        .addFileToUpload(path, "pdf") //Adding file
                        .addParameter("name", book)
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addBookToDatabase(String bookName, int copies, int issued) {
        String url = Constants.URL + "addbook.php";

        Map<String, String> params = new HashMap<>();
        params.put("name", bookName);
        params.put("available", ""+copies);
        params.put("issues", ""+issued);

        Log.d("ppppp", params.toString());

        final RequestQueue requestQueue = Volley.newRequestQueue(AdminActivity.this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Log.i("ppppp", jsonObject.toString());
                    if(jsonObject.getString("success").equals("1"))
                        Toast.makeText(AdminActivity.this, "Success", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(AdminActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jor);
    }
}
