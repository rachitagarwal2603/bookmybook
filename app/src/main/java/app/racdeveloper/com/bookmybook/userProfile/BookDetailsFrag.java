package app.racdeveloper.com.bookmybook.userProfile;


import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import app.racdeveloper.com.bookmybook.R;
import app.racdeveloper.com.bookmybook.ReviewActivity;

public class BookDetailsFrag extends Fragment {

    private int bookId;
    public BookDetailsFrag() {}

    Button review, share;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(savedInstanceState!=null)
            bookId= savedInstanceState.getInt("bookId");

        return inflater.inflate(R.layout.fragment_book_details, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view= getView();
        if(view!= null){
            BookDetails detail= BookDetails.details.get(bookId);
            TextView title= (TextView) view.findViewById(R.id.tvBookName);
            title.setText(detail.getName());
            TextView descrip= (TextView) view.findViewById(R.id.tvBookDetails);
            descrip.setText(detail.getDescription());
            RatingBar ratingBar= (RatingBar) view.findViewById(R.id.RatingBar);
            ratingBar.setRating(detail.getRating());
            TextView totalRate= (TextView) view.findViewById(R.id.TotalRating);
            totalRate.setText("( Number of Reviews : "+detail.getTotalRating()+" )");

            ImageButton bookGist = (ImageButton) view.findViewById(R.id.ibBookGist);
            bookGist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(),BookPreviewActivity.class).putExtra("bookId", bookId));
                }
            });

            review = (Button) view.findViewById(R.id.btReview);
            review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), ReviewActivity.class).putExtra("bookId", bookId));
                }
            });

            share = (Button) view.findViewById(R.id.btShare);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getActivity(), ""+ bookId, Toast.LENGTH_SHORT).show();

                    View screen = getActivity().getWindow().getDecorView().getRootView();
                    screen.setDrawingCacheEnabled(true);
                    Bitmap screenShot = screen.getDrawingCache();
                    String filePath = Environment.getExternalStorageDirectory()
                            + File.separator + "Pictures/Screenshot.png";
                    File imagePath = new File(filePath);
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream(imagePath);
                        screenShot.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        Log.e("GREC", e.getMessage(), e);
                    } catch (IOException e) {
                        Log.e("GREC", e.getMessage(), e);
                    }
                    Uri myUri = Uri.parse("file://" + filePath);
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    // shareIntent.addCategory(Intent.CATEGORY_HOME);
                    shareIntent.putExtra(Intent.EXTRA_STREAM,myUri);
                    PackageManager packageManager= getActivity().getPackageManager();
                    if(packageManager.resolveActivity(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)==null) {
                        final AlertDialog alertDialog=new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Warning");
                        alertDialog.setMessage("Sorry,you don't have any apps in your device which will be able to handle such operation");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                    else {
                        startActivity(Intent.createChooser(shareIntent, "Share screenshot using"));
                    }
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("bookId",bookId);
    }

    public void setId(int id){
        this.bookId=id;
}
}
