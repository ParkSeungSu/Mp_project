package halla.icsw.mysns.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import halla.icsw.mysns.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {
    private ArrayList<String> mDataset;
    private Activity activity;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        MyViewHolder(CardView t) {
            super(t);
            cardView = t;
        }
    }

    public GalleryAdapter(Activity activity, ArrayList<String> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @NonNull
    @Override
    public GalleryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        final MyViewHolder galleryViewHolder = new MyViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("profilePath", mDataset.get(galleryViewHolder.getAdapterPosition()));
                activity.setResult(Activity.RESULT_OK, resultIntent);
                activity.finish();
            }
        });

        ImageView imageView = cardView.findViewById(R.id.imageView);
        Glide.with(activity).load(mDataset.get(viewType)).centerCrop().override(500).into(imageView);

        return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        //CardView cardView = holder.cardView;

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
