package com.example.myapplication.User;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class Profile extends AppCompatActivity {
    Button back;
    TextView userE, userPh, userPass, userUs;
    SharedPreferences sharedpreferences;
    String userNumber = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        userE = findViewById(R.id.userEmail);
        userPh = findViewById(R.id.userPhoneNumber);
        userUs = findViewById(R.id.userUserName);
        userPass = findViewById(R.id.userPassword);
        userE.setText("getting data now !!!");
        userPh.setText("getting data now !!!");
        userUs.setText("getting data now !!!");
        userPass.setText("getting data now !!!");
        userNumber = getUserNumberFromCache ();
        getProfileDate(userNumber);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    String getEmailFromCache (){
        sharedpreferences = getSharedPreferences("new", 0);
        return sharedpreferences.getString("logged_in", "");
    }
    String getUserNumberFromCache (){
        sharedpreferences = getSharedPreferences("new", 0);
        return sharedpreferences.getString("userNumber2", "");
    }
    void getProfileDate (String userNumber){
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userE.setText(String.valueOf(dataSnapshot.child("Users").child(userNumber).child("Em").getValue()));
                userPh.setText(String.valueOf(dataSnapshot.child("Users").child(userNumber).child("Ph").getValue()));
                userUs.setText(String.valueOf(dataSnapshot.child("Users").child(userNumber).child("user").getValue()));
                userPass.setText(String.valueOf(dataSnapshot.child("Users").child(userNumber).child("Pass").getValue()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
