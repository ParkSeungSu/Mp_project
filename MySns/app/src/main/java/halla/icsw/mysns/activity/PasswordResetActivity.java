package halla.icsw.mysns.activity;

import androidx.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import halla.icsw.mysns.R;

public class PasswordResetActivity extends BasicActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        setToolbarTitle("비밀번호 재설정");
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.sendButton).setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sendButton:
                    send();
                    break;
            }
        }
    };

    private void send() {
        String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
        final RelativeLayout loaderLayout = findViewById(R.id.loaderlayout);
        loaderLayout.setVisibility(View.VISIBLE);
        if (email.length() > 0) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loaderLayout.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordResetActivity.this, "이메일을 보냈습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "이메일을 입력해 주세요", Toast.LENGTH_SHORT).show();

        }

    }

}
