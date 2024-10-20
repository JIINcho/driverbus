package com.example.driverr_bus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class route_time extends AppCompatActivity {

    private Spinner firstSpinner, secondSpinner;
    private FirebaseFirestore db;
    private static final String TAG = "RouteTimeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_time);

        // Firestore 데이터베이스 초기화
        db = FirebaseFirestore.getInstance();

        // 스피너 시작
        firstSpinner = findViewById(R.id.firstSpinner);
        secondSpinner = findViewById(R.id.secondSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.first_spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstSpinner.setAdapter(adapter);

        firstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = firstSpinner.getSelectedItem().toString();
                ArrayAdapter<CharSequence> secondAdapter;
                switch (selectedItem) {
                    case "교내순환":
                        secondAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.second_spinner_items_option1, android.R.layout.simple_spinner_item);
                        break;
                    case "하양역->교내순환":
                        secondAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.second_spinner_items_option2, android.R.layout.simple_spinner_item);
                        break;
                    case "안심역->교내순환":
                        secondAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.second_spinner_items_option3, android.R.layout.simple_spinner_item);
                        break;
                    case "사월역->교내순환":
                        secondAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.second_spinner_items_option4, android.R.layout.simple_spinner_item);
                        break;
                    case "A2->안심역->사월역":
                        secondAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                                R.array.second_spinner_items_option5, android.R.layout.simple_spinner_item);
                        break;
                    default:
                        secondAdapter = new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_spinner_item);
                }
                secondAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                secondSpinner.setAdapter(secondAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        // 스피너 끝

        // 예약 버튼 클릭 시 동작
        Button reservationButton = findViewById(R.id.reservation);
        reservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String route = firstSpinner.getSelectedItem().toString();
                String time = secondSpinner.getSelectedItem().toString();
                addReservationToFirestore(route, time);
                Toast.makeText(route_time.this, "노선 예약이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                // 예약 완료 후 clock 페이지로 이동
                Intent intent = new Intent(route_time.this, clock.class);
                intent.putExtra("selected_route", route); // 선택한 노선 데이터를 전달
                startActivity(intent);
            }
        });

        // Back 버튼 추가
        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(route_time.this, login.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });
    }

    // Firestore에 데이터를 추가하는 메서드
    private void addReservationToFirestore(String route, String time) {
        // bus_reservation 컬렉션에 데이터 추가
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("route", route);
        reservation.put("time", time);

        // Firestore에 데이터 추가
        db.collection("bus_reservation")
                .add(reservation)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // 추가가 성공했을 때의 동작
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 추가가 실패했을 때의 동작
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
