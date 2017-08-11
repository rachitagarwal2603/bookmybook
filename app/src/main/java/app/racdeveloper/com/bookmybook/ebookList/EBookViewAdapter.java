package app.racdeveloper.com.bookmybook.ebookList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.racdeveloper.com.bookmybook.R;

/**
 * Created by Rachit on 4/29/2017.
 */

public class EBookViewAdapter extends RecyclerView.Adapter<EBookViewAdapter.ViewHolder>  {

    private Context context;
    private List<EBookData> dataList;

    public EBookViewAdapter(Context context, List<EBookData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_card,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bookName.setText(dataList.get(position).getBookName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView bookImage;
        public TextView bookName;

        public ViewHolder(View itemView)
        {
            super(itemView);
            bookImage = (ImageView)itemView.findViewById(R.id.bookImage);
            bookName = (TextView)itemView.findViewById(R.id.bookName);
        }
    }
}
