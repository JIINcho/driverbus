package com.example.driverr_bus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;  // Firebase 인증 객체
    private DatabaseReference mDatabaseRef;  // Firebase 실시간 데이터베이스 객체
    private EditText mEtEmail, mEtPwd;  // 회원가입 입력 필드
    private Button mBtnRegister;  // 회원가입 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase 인증 및 데이터베이스 객체 초기화
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("driverr_bus");

        // 레이아웃 요소 초기화
        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mBtnRegister = findViewById(R.id.btn_register);

        // 회원가입 버튼 클릭 시 동작
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원가입 처리 시작
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                // 이메일과 비밀번호 유효성 검사
                if (strEmail.length() == 0) {
                    Toast.makeText(register.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    mEtEmail.requestFocus();
                    return;
                }
                if (strPwd.length() == 0) {
                    Toast.makeText(register.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    mEtPwd.requestFocus();
                    return;
                }

                // Firebase를 통한 사용자 등록
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공 시 사용자 정보를 데이터베이스에 저장하고 로그인 화면으로 이동
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            UsesrAccount account = new UsesrAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailId(firebaseUser.getEmail());
                            account.setPassword(strPwd);
                            // 데이터베이스에 사용자 정보 저장
                            mDatabaseRef.child("UsesrAccount").child(firebaseUser.getUid()).setValue(account);
                            Toast.makeText(register.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                            // 로그인 화면으로 이동
                            Intent intent = new Intent(register.this, login.class);
                            startActivity(intent);
                        } else {
                            // 회원가입 실패 시 에러 메시지 표시
                            Toast.makeText(register.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
