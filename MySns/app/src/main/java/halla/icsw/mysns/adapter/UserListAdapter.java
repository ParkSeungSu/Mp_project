package halla.icsw.mysns.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;

import halla.icsw.mysns.FirebaseHelper;
import halla.icsw.mysns.PostInfo;
import halla.icsw.mysns.R;
import halla.icsw.mysns.UserInfo;
import halla.icsw.mysns.activity.PostActivity;
import halla.icsw.mysns.activity.WritePostActivity;
import halla.icsw.mysns.listener.OnPostListener;
import halla.icsw.mysns.view.ReadContentsView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
    private ArrayList<UserInfo> mDataset;
    private Activity activity;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        MyViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    public UserListAdapter(Activity activity, ArrayList<UserInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public UserListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);
        final MyViewHolder itemViewHolder = new MyViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView photo=cardView.findViewById(R.id.photoImageView);
        TextView nameTextView = cardView.findViewById(R.id.nameTextView);
        TextView addressTextView = cardView.findViewById(R.id.addressTextView);

        UserInfo userInfo = mDataset.get(position);
        if(userInfo.getPhotoUrl()!=null) {
            Glide.with(activity).load(userInfo.getPhotoUrl()).centerCrop().override(500).into(photo);
        }
        nameTextView.setText(userInfo.getName());
        addressTextView.setText(userInfo.getAddress());


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
