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
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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

import halla.icsw.mysns.R;
import halla.icsw.mysns.WriteInfo;

public class WritePostActivity extends BasicActivity {

    private static final String TAG = "WritePostActivity";
    private ArrayList<String> pathList = new ArrayList<>();
    private FirebaseUser user;
    private LinearLayout parent;
    private RelativeLayout buttonsBackgroundLayout;
    private RelativeLayout loaderLayout;
    private ImageView selectedImageView;
    private EditText selectedEditText;
    private int pathCount;
    private int successCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        parent = findViewById(R.id.contentsLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        loaderLayout=findViewById(R.id.loaderlayout);
        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.video).setOnClickListener(onClickListener);
        findViewById(R.id.imageModify).setOnClickListener(onClickListener);
        findViewById(R.id.videoModify).setOnClickListener(onClickListener);
        findViewById(R.id.delete).setOnClickListener(onClickListener);
        findViewById(R.id.contentsEditText).setOnFocusChangeListener(onFocusChangeListener);
        findViewById(R.id.titleEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    selectedEditText = null;
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.check:
                    storageUpload();
                    break;
                case R.id.image:
                    myStartMain(GalleryActivity.class, "image", 0);
                    break;
                case R.id.video:
                    myStartMain(GalleryActivity.class, "video", 0);
                    break;
                case R.id.buttonsBackgroundLayout:
                    if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imageModify:
                    myStartMain(GalleryActivity.class, "image", 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.videoModify:
                    myStartMain(GalleryActivity.class, "video", 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.delete:
                    parent.removeView((View) selectedImageView.getParent());
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;

            }
        }
    };
    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                selectedEditText = (EditText) v;
            }
        }
    };

    private void storageUpload() {
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();

        if (title.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            final ArrayList<String> contentsList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            StorageReference storageRef = storage.getReference();
            final DocumentReference documentReference = firebaseFirestore.collection("posts").document();

            for (int i = 0; i < parent.getChildCount(); i++) {
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                for (int ii = 0; ii < linearLayout.getChildCount(); ii++) {
                    View view = linearLayout.getChildAt(i);
                    if (view instanceof EditText) {
                        String text = ((EditText) view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);
                        }
                    } else {
                        contentsList.add(pathList.get(pathCount));
                        final StorageReference mountainsRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + ".jpg");
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
                            UploadTask uploadTask = mountainsRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            contentsList.set(index, uri.toString());
                                            successCount++;
                                            if (pathList.size() == successCount) {
                                                WriteInfo writeInfo = new WriteInfo(title, contentsList, user.getUid(), new Date());
                                                storeUploader(documentReference, writeInfo);
                                                for(int a=0;a<contentsList.size();a++){
                                                    Log.e("로그: ","콘텐츠: "+contentsList.get(a));
                                                }
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
            }
            if(pathList.size()==0){
                WriteInfo writeInfo = new WriteInfo(title, contentsList, user.getUid(), new Date());
                storeUploader(documentReference, writeInfo);
            }
        } else {
            Toast.makeText(this, "제목과 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    private void storeUploader(DocumentReference documentReference, WriteInfo writeInfo) {
        documentReference.set(writeInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loaderLayout.setVisibility(View.GONE);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loaderLayout.setVisibility(View.GONE);

            }
        });

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
            case (0):
                if (resultCode == Activity.RESULT_OK) {
                    String profilePath = data.getStringExtra("profilePath");
                    pathList.add(profilePath);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    LinearLayout linearLayout = new LinearLayout(WritePostActivity.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    if (selectedEditText == null) {
                        parent.addView(linearLayout);
                    } else {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (parent.getChildAt(i) == selectedEditText.getParent()) {
                                parent.addView(linearLayout, i + 1);
                                break;
                            }
                        }
                    }

                    ImageView imageView = new ImageView(WritePostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) v;
                        }
                    });
                    Glide.with(this).load(profilePath).override(1000).into(imageView);
                    linearLayout.addView(imageView);

                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    editText.setHint("내용");
                    editText.setOnFocusChangeListener(onFocusChangeListener);
                    linearLayout.addView(editText);

                }
                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String profilePath = data.getStringExtra("profilePath");
                    Glide.with(this).load(profilePath).override(1000).into(selectedImageView);
                }
                break;
        }
    }

    private void myStartMain(Class c, String media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent, requestCode);
    }
}