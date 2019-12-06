package halla.icsw.mysns.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;


import halla.icsw.mysns.FirebaseHelper;
import halla.icsw.mysns.PostInfo;
import halla.icsw.mysns.R;
import halla.icsw.mysns.listener.OnPostListener;
import halla.icsw.mysns.view.ReadContentsView;

public class PostActivity extends BasicActivity {
    private  PostInfo postInfo;
    private FirebaseHelper firebaseHelper;
    private  ReadContentsView readContentsView;
    private LinearLayout contentsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        contentsLayout=findViewById(R.id.contentsLayout);
        readContentsView = findViewById(R.id.readContentsView);

        firebaseHelper=new FirebaseHelper(this);
        firebaseHelper.setOnPostListener(onPostListener);
        uiUpdate();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (0):
                if (resultCode == Activity.RESULT_OK) {
                    postInfo =(PostInfo) data.getSerializableExtra("postInfo");
                    contentsLayout.removeAllViews();
                    readContentsView.setPostInfo(postInfo);
                    setToolbarTitle(postInfo.getTitle());
                }
                break;

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                firebaseHelper.storageDelete(postInfo);
                finish();
                return true;
            case R.id.modify:
                myStartMain(WritePostActivity.class,postInfo);
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }

    }
    OnPostListener onPostListener=new OnPostListener() {
        @Override
        public void onDelete(PostInfo postInfo) {
            Log.e("로그","삭제성공");
        }

        @Override
        public void onModify() {
            Log.e("로그","수정성공");
        }
    };
    private void uiUpdate(){
        setToolbarTitle(postInfo.getTitle());
        readContentsView.setPostInfo(postInfo);
    }
    private void myStartMain(Class c, PostInfo postInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("postInfo", postInfo);
        startActivityForResult(intent,0);
    }
}
