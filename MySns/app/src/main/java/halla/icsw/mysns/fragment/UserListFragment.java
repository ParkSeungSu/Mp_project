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
import halla.icsw.mysns.UserInfo;
import halla.icsw.mysns.activity.WritePostActivity;

import halla.icsw.mysns.adapter.UserListAdapter;
import halla.icsw.mysns.listener.OnPostListener;

public class UserListFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private FirebaseFirestore firebaseFirestore;
    private UserListAdapter userListAdapter;
    private ArrayList<UserInfo> userList;
    private boolean updating;
    private boolean topScrolled;

    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_user_list, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        userListAdapter = new UserListAdapter(getActivity(), userList);

        final RecyclerView recyclerView = view.findViewById(R.id.recycleView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userListAdapter);

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
            userList.remove(postInfo);
            userListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onModify() {

        }
    };

    private void postUpdate(final boolean clear) {
        updating=true;
       // Date date= userList.size()==0 || clear ?new Date() : postList.get(postList.size()-1).getCreatedAt();
        CollectionReference collectionReference = firebaseFirestore.collection("users");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(clear) {
                        userList.clear();
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        userList.add(new UserInfo(
                                        document.getData().get("name").toString(),
                                        document.getData().get("phone").toString(),
                                        document.getData().get("birth").toString(),
                                        document.getData().get("address").toString(),
                                        document.getData().get("photoUrl")==null? null:document.getData().get("photoUrl").toString()));
                    }
                    userListAdapter.notifyDataSetChanged();
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
