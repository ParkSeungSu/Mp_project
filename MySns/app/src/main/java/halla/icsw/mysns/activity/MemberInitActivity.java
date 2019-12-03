package halla.icsw.mysns.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import halla.icsw.mysns.MemberInfo;
import halla.icsw.mysns.R;

public class MemberInitActivity extends BasicActivity {
    private static final String TAG = "MemberInitActivity";
    private ImageView profileImageView;
    private String profilePath;
    private FirebaseUser user;
    private RelativeLayout loaderLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);
        loaderLayout=findViewById(R.id.loaderlayout);
        profileImageView = findViewById(R.id.profileImageView);
        profileImageView.setOnClickListener(onClickListener);
        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.gallery).setOnClickListener(onClickListener);
        findViewById(R.id.picture).setOnClickListener(onClickListener);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (0): {
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra("profilePath");
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageView);
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checkButton:
                    storageUpdate();
                    break;
                case R.id.profileImageView:
                    CardView cardView=findViewById(R.id.buttonsCard);
                    Log.e("a","a");
                    if(cardView.getVisibility()==View.VISIBLE){
                        cardView.setVisibility(View.INVISIBLE);
                    }else{
                        cardView.setVisibility(View.VISIBLE);
                    }

                    break;
                case R.id.picture:
                    myStartMain(CameraActivity.class);
                    break;
                case R.id.gallery:
                    myStartMain(GalleryActivity.class);
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:{
                if(grantResults.length>0&&
                grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    myStartMain(GalleryActivity.class);
                }else{
                    Toast.makeText(MemberInitActivity.this, "권한을 허용해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void storageUpdate() {
        final String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
        final String phone = ((EditText) findViewById(R.id.phoneNumberEditText)).getText().toString();
        final String birth = ((EditText) findViewById(R.id.birthEditText)).getText().toString();
        final String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();
        if (name.length() > 0 && phone.length() > 9 && birth.length() > 5 && address.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
           user = FirebaseAuth.getInstance().getCurrentUser();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final StorageReference mountainsRef = storageRef.child("users/" + user.getUid() + "profileImage.jpg");

         if(profilePath==null){
             MemberInfo memberInfo = new MemberInfo(name, phone, birth, address);
             storageUploader(memberInfo);
         }else{
             try {
                 InputStream stream = new FileInputStream(new File(profilePath));
                 UploadTask uploadTask = mountainsRef.putStream(stream);
                 uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                     @Override
                     public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                         if (!task.isSuccessful()) {
                             Log.e("실패", "실패" + task.getException());
                             throw task.getException();
                         }
                         return mountainsRef.getDownloadUrl();
                     }
                 }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                     @Override
                     public void onComplete(@NonNull Task<Uri> task) {
                         if (task.isSuccessful()) {
                             Uri downloadUri = task.getResult();
                             MemberInfo memberInfo = new MemberInfo(name, phone, birth, address, downloadUri.toString());
                             storageUploader(memberInfo);
                         } else {
                             Toast.makeText(MemberInitActivity.this, "회원정보 전송 오류", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });

             } catch (FileNotFoundException e) {
                 Log.e("error: ", "에러" + e.toString());
             }
         }
        } else {
            Toast.makeText(this, "회원정보를 입력해주세요", Toast.LENGTH_SHORT).show();
        }

    }
private void storageUploader( MemberInfo memberInfo){
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("users").document(user.getUid()).set(memberInfo)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(MemberInitActivity.this, "회원정보 등록을 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    loaderLayout.setVisibility(View.GONE);
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MemberInitActivity.this, "회원정보 등록에 실패!", Toast.LENGTH_SHORT).show();
                    loaderLayout.setVisibility(View.GONE);
                    Log.v(TAG, "Error adding document", e);
                }
            });
}
    private void myStartMain(Class c) {
        Intent intent = new Intent(this, c);

        startActivityForResult(intent, 0);
    }

}
