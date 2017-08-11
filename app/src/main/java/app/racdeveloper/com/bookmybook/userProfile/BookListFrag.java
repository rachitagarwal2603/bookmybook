package app.racdeveloper.com.bookmybook.userProfile;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.racdeveloper.com.bookmybook.QueryPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookListFrag extends ListFragment {

    LayoutInflater inflater;
    ViewGroup container;
    Bundle savedInstanceState;
    public BookListFrag() {
        // Required empty public constructor
    }

    public interface BookListListener{
        void itemClicked(int id);
    }
    BookListListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;
        this.container = container;
        this.savedInstanceState = savedInstanceState;

        List<BookDetails> detail= new ArrayList<>();
        if(QueryPreferences.getBookInfo(getContext())!=null) {
            try {
                JSONObject jsonObject = new JSONObject(QueryPreferences.getBookInfo(getContext()));
                Log.d("ppppp", jsonObject.toString());
                if (jsonObject.getString("success").equals("1")) {
                    JSONArray array = null;
                    try {
                        array = jsonObject.getJSONArray("bookList");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int i, length = array.length();
                    for (i = 0; i < length; i++) {
                        BookDetails book = new BookDetails();
                        book.setName(array.getJSONObject(i).getString("bookName"));
                        book.setDescription(array.getJSONObject(i).getString("description") + "\n\nNumber of Copies - " + array.getJSONObject(i).getString("available") + "\nNumber of Issues - " + array.getJSONObject(i).getString("issues"));
                        book.setRating(Float.parseFloat(array.getJSONObject(i).getString("rating")));
                        book.setTotalRating(Integer.parseInt(array.getJSONObject(i).getString("totalRating")));
                        detail.add(book);
                    }
                    BookDetails.details = detail;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String[] names = new String[BookDetails.details.size()];
        int numberOfItems = BookDetails.details.size();
        for(int i=0;i<numberOfItems;i++){
            names[i]= BookDetails.details.get(i).getName();
        }
        ArrayAdapter<String> ad = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, names);
        setListAdapter(ad);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        onCreateView(inflater, container, savedInstanceState);
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity= null;
        if(context instanceof Activity)
            activity= (Activity) context;
        this.listener= (BookListListener) activity;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(listener!=null){
            listener.itemClicked((int) id);
        }
    }
}
