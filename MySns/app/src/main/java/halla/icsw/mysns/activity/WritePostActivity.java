package halla.icsw.mysns.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import halla.icsw.mysns.MemberInfo;
import halla.icsw.mysns.R;
import halla.icsw.mysns.WriteInfo;

public class WritePostActivity extends BasicActivity {

    private static final String TAG = "WritePostActivity";
    private ArrayList<String> pathList = new ArrayList<>();
    private FirebaseUser user;
    private LinearLayout parent;
    private int pathCount = 0;
    private int successCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        parent = findViewById(R.id.contentsLayout);
        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.video).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.check:
                    storageUpload();
                    break;
                case R.id.image:
                    myStartMain(GalleryActivity.class, "image");
                    break;
                case R.id.video:
                    myStartMain(GalleryActivity.class, "video");
                    break;
            }
        }
    };

    private void storageUpload() {
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();
        if (title.length() > 0 ) {
            final ArrayList<String> contentsList = new ArrayList<>();

            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);
                if (view instanceof EditText) {
                    String text = ((EditText) view).getText().toString();
                    if (text.length() > 0) {
                        contentsList.add(text);
                    }
                } else {
                    contentsList.add(pathList.get(pathCount));
                    final StorageReference mountainsRef = storageRef.child("users/" + user.getUid() + "/" + pathCount + ".jpg");
                    try {
                        InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                        StorageMetadata metadata=new StorageMetadata.Builder().setCustomMetadata("index",""+(contentsList.size()-1)).build();

                        UploadTask uploadTask = mountainsRef.putStream(stream,metadata);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final int index=Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        contentsList.set(index,uri.toString());
                                        successCount++;
                                        if(pathList.size()==successCount){
                                            WriteInfo writeInfo = new WriteInfo(title, contentsList, user.getUid(),new Date());
                                            storeUploader(writeInfo);
                                            Toast.makeText(WritePostActivity.this, "업로드 완료!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });

                    } catch (FileNotFoundException e) {
                        Log.e("error: ", "에러" + e.toString());
                    }
                    pathCount++;
                }
            }
        } else {
            Toast.makeText(this, "제목과 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    private void storeUploader(WriteInfo writeInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").add(writeInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(WritePostActivity.this, "게시물 등록을 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WritePostActivity.this, "회원정보 등록에 실패!", Toast.LENGTH_SHORT).show();
                        Log.v(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (0): {
                if (resultCode == Activity.RESULT_OK) {
                    String profilePath = data.getStringExtra("profilePath");
                    pathList.add(profilePath);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    ImageView imageView = new ImageView(WritePostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    Glide.with(this).load(profilePath).override(1000).into(imageView);
                    parent.addView(imageView);

                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    parent.addView(editText);

                }
                break;
            }
        }
    }

    private void myStartMain(Class c, String media) {
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent, 0);
    }
}
