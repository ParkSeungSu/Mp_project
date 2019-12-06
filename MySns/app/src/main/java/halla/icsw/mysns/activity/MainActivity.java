package halla.icsw.mysns.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Date;

import halla.icsw.mysns.PostInfo;
import halla.icsw.mysns.R;
import halla.icsw.mysns.adapter.MainAdapter;
import halla.icsw.mysns.listener.OnPostListener;
import halla.icsw.mysns.view.ContentsItemView;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private MainAdapter mainAdapter;
    private ArrayList<PostInfo> postList;
    private boolean updating;
    private boolean topScrolled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbarTitle(getResources().getString(R.string.app_name));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        if (firebaseUser == null) {
            myStartMain(SingUpActivity.class);
        } else {
            firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                myStartMain(MemberInitActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
        postList = new ArrayList<>();
        mainAdapter = new MainAdapter(MainActivity.this, postList);
        mainAdapter.setOnPostListener(onPostListener);

        RecyclerView recyclerView = findViewById(R.id.recycleView);
        findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(mainAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager=recyclerView.getLayoutManager();
                int firstVisibleItemPosition=((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

                if(newState==1&&firstVisibleItemPosition==0){
                   topScrolled=true;
                }
                if(newState==0&&topScrolled){
                    postList.clear();
                    postUpdate();
                    topScrolled=false;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager=recyclerView.getLayoutManager();
                int totalItemCount=layoutManager.getItemCount();
                int firstVisibleItemPosition=((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition=((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();

                if(totalItemCount-3<=lastVisibleItemPosition&&!updating){
                    postUpdate();
                }
                if(0<firstVisibleItemPosition){
                    topScrolled=false;
                }

            }
        });

        postUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainAdapter.playerStop();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (0):
                if(data != null){
                    postUpdate();
                }
                break;

        }
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.logoutButton:
//                    FirebaseAuth.getInstance().signOut();//로그아웃
//                    myStartMain(SingUpActivity.class);
//                    break;
                case R.id.floatingActionButton:
                    myStartMain(WritePostActivity.class);
                    break;
            }
        }
    };
    OnPostListener onPostListener=new OnPostListener() {
        @Override
        public void onDelete(PostInfo postInfo) {
            postList.remove(postInfo);
            mainAdapter.notifyDataSetChanged();
        }

        @Override
        public void onModify() {

        }
    };

    private void postUpdate() {
        if (firebaseUser != null) {
            updating=true;
            Date date= postList.size()==0?new Date() : postList.get(postList.size()-1).getCreatedAt();
            CollectionReference collectionReference = firebaseFirestore.collection("posts");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING)
                    .whereLessThan("createdAt",date)
                    .limit(10)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            postList.add(
                                    new PostInfo(
                                            document.getData().get("title").toString(),
                                            (ArrayList<String>) document.getData().get("contents"),
                                            (ArrayList<String>) document.getData().get("formats"),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()), document.getId()));
                        }
                        mainAdapter.notifyDataSetChanged();
                    } else {

                    }
                    updating=false;
                }
            });
        }
    }

    private void myStartMain(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent,0);
    }

}
