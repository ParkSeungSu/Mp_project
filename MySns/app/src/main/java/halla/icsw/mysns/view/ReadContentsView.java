package halla.icsw.mysns.view;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import halla.icsw.mysns.PostInfo;
import halla.icsw.mysns.R;

public class ReadContentsView extends LinearLayout {
    private Context context;
    private int moreIndex = -1;
    private LayoutInflater layoutInflater;
    private ArrayList<SimpleExoPlayer> playerArrayList=new ArrayList<>();
    public ReadContentsView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ReadContentsView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        initView();
    }

    private void initView() {

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);

        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_post, this, true);
    }

    public void setMoreIndex(int moreIndex) {
        this.moreIndex = moreIndex;

    }

    public void setPostInfo(PostInfo postInfo) {
        TextView createdAtTextView = findViewById(R.id.createAtTextView);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(postInfo.getCreatedAt()));

        LinearLayout contentsLayout = findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList = postInfo.getContents();
        ArrayList<String> formatList = postInfo.getFormats();
        for (int i = 0; i < contentsList.size(); i++) {
            if (i == moreIndex) {
                TextView textView = new TextView(context);
                textView.setLayoutParams(layoutParams);
                contentsLayout.addView(textView);
                textView.setText("더보기..");
                break;
            }
            String contents = contentsList.get(i);
            String format = formatList.get(i);

            if (format.equals("image")) {
                ImageView imageView = (ImageView) layoutInflater.inflate(R.layout.view_contents_images, this, false);
                contentsLayout.addView(imageView);
                Glide.with(this).load(contents).override(1000).thumbnail(0.1f).into(imageView);
            } else if (format.equals("video")) {
                final PlayerView playerView = (PlayerView) layoutInflater.inflate(R.layout.view_contents_player, this, false);

                // Produces DataSource instances through which media data is loaded.
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                        Util.getUserAgent(context, getResources().getString(R.string.app_name)));
                // This is the MediaSource representing the media to be played.
                MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(contents));
                SimpleExoPlayer player= ExoPlayerFactory.newSimpleInstance(context);
                // Prepare the player with the source.
                player.prepare(videoSource);

                player.addVideoListener(new VideoListener() {
                    @Override
                    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                        playerView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
                    }
                });

                playerArrayList.add(player);

                playerView.setPlayer(player);
                contentsLayout.addView(playerView);
            } else {
                TextView textView = (TextView) layoutInflater.inflate(R.layout.view_contents_text, this, false);
                textView.setText(contents);
                contentsLayout.addView(textView);
            }
        }

    }
    public ArrayList<SimpleExoPlayer> getPlayerArrayList(){
        return playerArrayList;
    }

}
