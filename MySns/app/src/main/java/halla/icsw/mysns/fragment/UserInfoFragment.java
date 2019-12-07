package halla.icsw.mysns.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import halla.icsw.mysns.PostInfo;
import halla.icsw.mysns.R;
import halla.icsw.mysns.UserInfo;
import halla.icsw.mysns.activity.MemberInitActivity;
import halla.icsw.mysns.activity.SingUpActivity;
import halla.icsw.mysns.activity.WritePostActivity;
import halla.icsw.mysns.adapter.UserListAdapter;
import halla.icsw.mysns.listener.OnPostListener;

public class UserInfoFragment extends Fragment {
    private static final String TAG = "UserInfoFragment";
    private FirebaseFirestore firebaseFirestore;

    public UserInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_user_info, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        final ImageView profileImageView=view.findViewById(R.id.profileImageView);
        final TextView nameTextView=  view.findViewById(R.id.nameText);
        final TextView phoneNumTextView=  view.findViewById(R.id.phoneNumberText);
        final TextView birthTextView=  view.findViewById(R.id.birthText);
        final TextView addressTextView=  view.findViewById(R.id.addressText);
        final Button logoutButton=view.findViewById(R.id.LogoutButton);
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            if(document.getData().get("photoUrl")!=null) {
                                Glide.with(getActivity()).load(document.getData().get("photoUrl")).centerCrop().override(500).into(profileImageView);
                            }
                            nameTextView.setText("Name : "+document.getData().get("name").toString());
                            phoneNumTextView.setText("Phone : "+document.getData().get("phone").toString());
                            birthTextView.setText("Birth : "+document.getData().get("birth").toString());
                            addressTextView.setText("Address : "+document.getData().get("address").toString());
                        } else {
                            Log.d(TAG, "No such document");

                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                myStartMain(SingUpActivity.class);
            }
        });
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
    private void myStartMain(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivityForResult(intent,0);
    }

}
