package com.example.myapplication;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.Admin.AdminHomeScreen;
import com.example.myapplication.User.Items;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
public class Login extends AppCompatActivity {
    private EditText Email, Password;
    private Button Login;
    private TextView CreateNewAccount, forgetpassword ,Admin,notAdmin;
    private ProgressDialog loadingBar;
    AlertDialog.Builder reset_alert;
    LayoutInflater inflater;
    private String parentDbName = "Users";
    FirebaseAuth fAuth;
    void saveToCache(String email) {
        sharedpreferences = getSharedPreferences("new", 0);
        editor = sharedpreferences.edit();
        editor.putString("logged_in", email);
        editor.commit();
    }
    void saveTypeToCache(String type) {
        sharedpreferences = getSharedPreferences("new", 0);
        editor = sharedpreferences.edit();
        editor.putString("user_type", type);
        editor.commit();
    }
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Admin=(TextView)findViewById(R.id.Admin);
        notAdmin=(TextView)findViewById(R.id.notAdmin);
        forgetpassword = (TextView)findViewById(R.id.forgetpassword);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        Login = (Button) findViewById(R.id.login);
        CreateNewAccount = (TextView) findViewById(R.id.CreateNewAccount);
        loadingBar = new ProgressDialog(this);
        reset_alert = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();
        fAuth=FirebaseAuth.getInstance();
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view=inflater.inflate(R.layout.reset,null);
                reset_alert.setTitle("Reset Forget Password ?").setMessage("Enter Your Email to get password reset link.").setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText email=view.findViewById(R.id.reset);
                        if(email.getText().toString().isEmpty()){
                            email.setError("Required Field");
                            return;
                        }
                        fAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(com.example.myapplication.Login.this, "Check your email for reset password ", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(com.example.myapplication.Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel",null).setView(view).create().show();
            }
        });
        CreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
        Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Login.setText("Login Admin");
                Admin.setVisibility(View.INVISIBLE);
                notAdmin.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });
        notAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Login.setText("Login");
                Admin.setVisibility(View.VISIBLE);
                notAdmin.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
    }
    private void LoginUser()
    {
        String em = Email.getText().toString();
        String pass = Password.getText().toString();
        if (TextUtils.isEmpty(em)) {
            Toast.makeText(this, "Please write your Email...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            AllowAccessToAccount(em, pass);
        }
    }
    private void AllowAccessToAccount( final String em, final String pass)
    {
        if(parentDbName.equals("Users")){
            Log.d("asd" , "asd1");
            final DatabaseReference RootRef;
            RootRef = FirebaseDatabase.getInstance().getReference();
            RootRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    boolean hasSame = false;
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map<String,Object> mapObject =(HashMap<String, Object>)ds.getValue();
                        String s = String.valueOf(mapObject.get("Em"));
                        Log.d("qwe" , String.valueOf(s.equals(em)));
                        if(s.equals(em)){
                            hasSame = true;
                        }
                    }
                    if (hasSame)
                    {
                        if (!em.isEmpty())
                        {
                            if (!pass.isEmpty())
                            {
                                Toast.makeText(com.example.myapplication.Login.this, "logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                saveToCache(em);
                                saveTypeToCache("user");
                                Intent intent = new Intent(com.example.myapplication.Login.this, Items.class);
                                startActivity(intent);
                            }
                            else
                            {
                                loadingBar.dismiss();
                                Toast.makeText(com.example.myapplication.Login.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(com.example.myapplication.Login.this, "Account with this " + em + " email do not exists.", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else  if(parentDbName.equals("Admins")){
            Log.d("asd" , "asd2");
            final DatabaseReference RootRef;
            RootRef = FirebaseDatabase.getInstance().getReference();
            RootRef.child("Admins").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    boolean hasSame = false;
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map<String,Object> mapObject =(HashMap<String, Object>)ds.getValue();
                        String s = String.valueOf(mapObject.get("Em"));
                        Log.d("qwe" , String.valueOf(s.equals(em)));
                        if(s.equals(em)){
                            hasSame = true;
                        }
                    }
                    if (hasSame)
                    {
                        if (!em.isEmpty())
                        {
                            if (!pass.isEmpty()) {
                                Toast.makeText(com.example.myapplication.Login.this, "Welcome Admin, you are logged in Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                saveTypeToCache("admin");
                                Intent intent = new Intent(com.example.myapplication.Login.this, AdminHomeScreen.class);
                                startActivity(intent);
                            }
                            else
                            {
                                loadingBar.dismiss();
                                Toast.makeText(com.example.myapplication.Login.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(com.example.myapplication.Login.this, "Account with this " + em + " email do not exists.", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}

