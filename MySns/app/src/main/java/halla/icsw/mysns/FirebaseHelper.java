package halla.icsw.mysns;

import android.app.Activity;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import halla.icsw.mysns.activity.MainActivity;
import halla.icsw.mysns.listener.OnPostListener;

public class FirebaseHelper {
    private int successCount;
    private Activity activity;
    private OnPostListener onPostListener;

    public FirebaseHelper(Activity activity) {
        this.activity=activity;
    }
    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener=onPostListener;
    }

    public void storageDelete(final PostInfo postInfo) {
        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageRef=storage.getReference();

        final String Id = postInfo.getId();
        ArrayList<String> contentsList = postInfo.getContents();
        for (int i = 0; i < contentsList.size(); i++) {
            String contents = contentsList.get(i);
            if (Patterns.WEB_URL.matcher(contents).matches() && contents.contains("https://firebasestorage.googleapis.com/v0/b/sns-project-cb9e5.appspot.com/o/post")) {
                successCount++;
                String[] list = contents.split("\\?");
                String[] list2 = list[0].split("%2F");
                String name = list2[list2.length - 1];

                StorageReference desertRef = storageRef.child("posts/" + Id + "/" + name);
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        successCount--;
                        storeDelete(Id,postInfo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        storeDelete(Id,postInfo);
    }

    private void storeDelete(String id, final PostInfo postInfo) {
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        if (successCount == 0) {
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(activity, "게시글을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                            onPostListener.onDelete(postInfo);
                            //postUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity, "게시글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
