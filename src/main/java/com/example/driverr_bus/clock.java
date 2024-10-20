package com.example.driverr_bus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class clock extends AppCompatActivity {
    private TextView regBoolTextView;
    private DatabaseReference reservationsRef;
    private ValueEventListener valueEventListener;
    private boolean isListening = false;
    private TextView digitalClock;
    private TextView currentDate;
    private ImageView shape_falseImageView;
    private ImageView shape_trueImageView;
    private boolean isShape1Visible = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        // 데이터베이스 레퍼런스
        reservationsRef = FirebaseDatabase.getInstance().getReference().child("reservations");


        Intent intent = getIntent();
        String selectedRoute = intent.getStringExtra("selected_route");


        // 받아온 데이터를 텍스트뷰에 표시하는 부분
        TextView subwayTextView = findViewById(R.id.subway);
        subwayTextView.setText(selectedRoute);


        // 데이터베이스 레퍼런스
        reservationsRef = FirebaseDatabase.getInstance().getReference().child("bus_reservation");

        Button toggleButton = findViewById(R.id.start_operation);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleListening();
            }
        });

        //시계 정의
        digitalClock = findViewById(R.id.digitalClock);

        //날짜 정의
        currentDate = findViewById(R.id.currentDate);

        // 매 초마다 시간, 날짜 업데이트
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateClockAndDate();
                handler.postDelayed(this, 1000); // 1초마다 업데이트
            }
        });

        // Back 버튼 추가
        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(clock.this, login.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });

        Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(clock.this, login.class);
                startActivity(intent);
            }
        });
    }

    private void toggleListening() {
        Button start_operation = findViewById(R.id.start_operation);
        if (!isListening) {
            startDataListener();
            start_operation.setText("운행종료");
        } else {
            stopDataListener();
            start_operation.setText("운행시작");
        }
    }

    private void updateClockAndDate() {
        // 현재 시간 업데이트
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String formattedTime = timeFormat.format(currentTime.getTime());
        digitalClock.setText(formattedTime);

        // 현재 날짜 업데이트
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentTime.getTime());
        currentDate.setText(formattedDate);
    }

    private void startDataListener() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String reservedRoute = dataSnapshot.child("route").getValue(String.class);
                    TextView subwayTextView = findViewById(R.id.subway);
                    subwayTextView.setText(reservedRoute);

                    regBoolTextView.setText("예약한 사람이 있습니다!");
                    shape_falseImageView.setVisibility(View.GONE);
                    shape_trueImageView.setVisibility(View.VISIBLE);
                    isShape1Visible = true;
                } else {
                    regBoolTextView.setText("");
                    shape_falseImageView.setVisibility(View.GONE);
                    shape_trueImageView.setVisibility(View.VISIBLE);
                    isShape1Visible = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(clock.this, "데이터를 읽어올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        };

        reservationsRef.addValueEventListener(valueEventListener);
        isListening = true;
    }

    private void stopDataListener() {
        if (valueEventListener != null) {
            reservationsRef.removeEventListener(valueEventListener);
            valueEventListener = null;
            isListening = false;
        }
    }
}
