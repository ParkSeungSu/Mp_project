package halla.icsw.mysns.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;

import halla.icsw.mysns.FirebaseHelper;
import halla.icsw.mysns.PostInfo;
import halla.icsw.mysns.R;
import halla.icsw.mysns.activity.PostActivity;
import halla.icsw.mysns.activity.WritePostActivity;
import halla.icsw.mysns.listener.OnPostListener;
import halla.icsw.mysns.view.ReadContentsView;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private final int MORE_INDEX = 2;
    private FirebaseHelper firebaseHelper;
    private ArrayList<ArrayList<SimpleExoPlayer>> playerArrayListArrayList = new ArrayList<>();

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        MyViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    public MainAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
        firebaseHelper = new FirebaseHelper(activity);

    }

    public void setOnPostListener(OnPostListener onPostListener) {
        firebaseHelper.setOnPostListener(onPostListener);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MainAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final MyViewHolder itemViewHolder = new MyViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra("postInfo", mDataset.get(itemViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        cardView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, itemViewHolder.getAdapterPosition());
            }
        });

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView TitleTextView = cardView.findViewById(R.id.titleTextView);

        PostInfo postInfo = mDataset.get(position);
        TitleTextView.setText(postInfo.getTitle());

        ReadContentsView readContentsView = cardView.findViewById(R.id.readContentsView);
        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);


        if (contentsLayout.getTag() == null || !contentsLayout.getTag().equals(postInfo)) {
            contentsLayout.setTag(postInfo);
            contentsLayout.removeAllViews();

            readContentsView.setMoreIndex(MORE_INDEX);
            readContentsView.setPostInfo(postInfo);
            ArrayList<SimpleExoPlayer> playerArrayList=readContentsView.getPlayerArrayList();
            if (playerArrayList != null) {
                playerArrayListArrayList.add(playerArrayList);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void showPopup(View v, final int position) {
        final PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modify:
                        myStartMain(WritePostActivity.class, mDataset.get(position));
                        return true;
                    case R.id.delete:
                        firebaseHelper.storageDelete(mDataset.get(position));
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }

    private void myStartMain(Class c, PostInfo postInfo) {
        Intent intent = new Intent(activity, c);
        intent.putExtra("postInfo", postInfo);
        activity.startActivity(intent);
    }

    public void playerStop() {
        for (int i = 0; i < playerArrayListArrayList.size(); i++) {
            ArrayList<SimpleExoPlayer> playerArrayList=playerArrayListArrayList.get(i);
            for (int j = 0; j < playerArrayList.size(); j++) {
                SimpleExoPlayer player = playerArrayListArrayList.get(i).get(j);
                if (player.getPlayWhenReady()) {
                    player.setPlayWhenReady(false);
                }
            }
        }
    }
}
