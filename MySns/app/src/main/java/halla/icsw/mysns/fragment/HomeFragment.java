package halla.icsw.mysns.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import halla.icsw.mysns.PostInfo;
import halla.icsw.mysns.R;
import halla.icsw.mysns.activity.WritePostActivity;
import halla.icsw.mysns.adapter.HomeAdapter;
import halla.icsw.mysns.listener.OnPostListener;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FirebaseFirestore firebaseFirestore;
    private HomeAdapter homeAdapter;
    private ArrayList<PostInfo> postList;
    private boolean updating;
    private boolean topScrolled;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        postList = new ArrayList<>();
        homeAdapter = new HomeAdapter(getActivity(), postList);
        homeAdapter.setOnPostListener(onPostListener);

        RecyclerView recyclerView = view.findViewById(R.id.recycleView);
        view.findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(homeAdapter);
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
                    postUpdate(true);
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
                    postUpdate(false);
                }
                if(0<firstVisibleItemPosition){
                    topScrolled=false;
                }

            }
        });

        postUpdate(false);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onPause() {
        super.onPause();
        homeAdapter.playerStop();
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
            homeAdapter.notifyDataSetChanged();
        }

        @Override
        public void onModify() {

        }
    };

    private void postUpdate(final boolean clear) {
        updating=true;
        Date date= postList.size()==0 || clear ?new Date() : postList.get(postList.size()-1).getCreatedAt();
        CollectionReference collectionReference = firebaseFirestore.collection("posts");
        collectionReference.orderBy("createdAt", Query.Direction.DESCENDING)
                .whereLessThan("createdAt",date)
                .limit(10)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(clear) {
                        postList.clear();
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        postList.add(
                                new PostInfo(
                                        document.getData().get("title").toString(),
                                        (ArrayList<String>) document.getData().get("contents"),
                                        (ArrayList<String>) document.getData().get("formats"),
                                        document.getData().get("publisher").toString(),
                                        new Date(document.getDate("createdAt").getTime()), document.getId()));
                    }
                    homeAdapter.notifyDataSetChanged();
                } else {

                }
                updating=false;
            }
        });
    }

    private void myStartMain(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivityForResult(intent,0);
    }
}
