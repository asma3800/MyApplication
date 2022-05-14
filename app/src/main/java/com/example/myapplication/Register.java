package com.example.myapplication;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
public class Register extends AppCompatActivity {
    private EditText Username, Email, Password, Repassword, PhoneNumber;
    private Button Register;
    private TextView AlreadyHaveAccount;
    private ProgressDialog loadingBar;
    FirebaseAuth fAuth;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private static final String USER_NUMBER = "UserNumber";
    int userNumberCounter = 0;
    private static final String ADMIN_NUMBER = "AdminNumber";
    int adminNumberCounter = 0;
    boolean isAnAdmin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Username = (EditText) findViewById(R.id.username);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        Repassword = (EditText) findViewById(R.id.repassword);
        PhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        Register = (Button) findViewById(R.id.register);
        AlreadyHaveAccount = (TextView) findViewById(R.id.AlreadyHaveAccount);
        fAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        AlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
        Switch onOffSwitch = (Switch)  findViewById(R.id.switch1);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Switch State=", ""+isChecked);
                isAnAdmin = isChecked;
            }
        });
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAnAdmin){
                    Log.v("asd", "yess admin");
                    getOrderNumberForAdmin();
                }else{
                    getOrderNumberForUser();
                }
            }
        });
    }
    void saveToCache(String email) {
        sharedpreferences = getSharedPreferences("new", 0);
        editor = sharedpreferences.edit();
        editor.putString("logged_in", email);
        editor.commit();
    }
    void saveUserNameToCache(String userName) {
        sharedpreferences = getSharedPreferences("new", 0);
        editor = sharedpreferences.edit();
        editor.putString("userName", userName);
        editor.commit();
    }
    void saveUserNumberToCache(String userNumber2) {
        sharedpreferences = getSharedPreferences("new", 0);
        editor = sharedpreferences.edit();
        editor.putString("userNumber2", userNumber2);
        editor.commit();
    }
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    void getOrderNumberForUser() {
        loadingBar.setTitle("Create Account");
        loadingBar.setMessage("Please wait,while we are checking the credentials.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Users").child(USER_NUMBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(USER_NUMBER) || dataSnapshot.child(USER_NUMBER).exists()) {
                    userNumberCounter = Integer.valueOf(String.valueOf(dataSnapshot.child(USER_NUMBER).getValue()));
                    RegisterUser();
                } else {
                    HashMap<String, Object> orderNumberMap = new HashMap<>();
                    orderNumberMap.put(USER_NUMBER, 0);
                    RootRef.child("Users").child(USER_NUMBER).updateChildren(orderNumberMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        RegisterUser();
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(com.example.myapplication.Register.this, "Network Error:please try again after some time ..."
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void RegisterUser() {
        String user = Username.getText().toString();
        String pass = Password.getText().toString();
        String repass = Repassword.getText().toString();
        String em = Email.getText().toString();
        String ph = PhoneNumber.getText().toString();
        Pattern letter = Pattern.compile("[a-z]");
        Pattern letterC = Pattern.compile("[A-Z]");
        Pattern digit = Pattern.compile("[0-9]");
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(em)|| TextUtils.isEmpty(pass) || TextUtils.isEmpty(repass) || TextUtils.isEmpty(ph) ) {
            Toast.makeText(com.example.myapplication.Register.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
        else if (!isEmailValid(em)){
            Toast.makeText(com.example.myapplication.Register.this, "Your Email is Invalid", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
        else if((!letter.matcher(pass).find()) || (!digit.matcher(pass).find()) || (!letterC.matcher(pass).find())){
            Toast.makeText(com.example.myapplication.Register.this, "Your Password Should has digits and letters and at least one capital letter"
                    , Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
        else if(!repass.equals(pass)){
            Toast.makeText(com.example.myapplication.Register.this, "Your Password And Repassword Not Match ", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }
        else if (ph.length() != 10|| (!digit.matcher(ph).find())){
            Toast.makeText(com.example.myapplication.Register.this, "Your Phone Number Should be 10 Numbers only", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }

        else {
            ValidateEmailForUser(user, pass, em, ph);
        }
        fAuth.createUserWithEmailAndPassword(Email.getText().toString(),Password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", Username);
                    user.put("email", Email);
                    user.put("phoneNumber", PhoneNumber);
                }else {
                    Toast.makeText(com.example.myapplication.Register.this, "Error !"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void ValidateEmailForUser(String user, String pass, String em, String ph) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasSame = false;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map<String,Object> mapObject =(HashMap<String, Object>)ds.getValue();
                    Log.d("asdasd" , String.valueOf(mapObject.containsValue("s")));
                    if(mapObject.containsValue(em)){
                        hasSame = true;
                    }
                }
                if (!hasSame) {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("user", user);
                    userdataMap.put("Em", em);
                    userdataMap.put("Pass", pass);
                    userdataMap.put("Ph", ph);
                    RootRef.child("Users").child(String.valueOf(userNumberCounter + 1)).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        saveUserNumberToCache(String.valueOf(userNumberCounter + 1));
                                        Toast.makeText(com.example.myapplication.Register.this, "Congratulation,Your account has been created. "
                                                , Toast.LENGTH_SHORT).show();
                                        userNumberCounter += 1;
                                        updateOrderNumberForUser();
                                        saveUserNameToCache(user);
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(com.example.myapplication.Register.this, "Network Error:please try again after some time ..."
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(com.example.myapplication.Register.this, "This " + em + " already exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(com.example.myapplication.Register.this, "Please try again using another Email.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(com.example.myapplication.Register.this, Login.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    void updateOrderNumberForUser() {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Users").child(USER_NUMBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> orderNumberMap = new HashMap<>();
                orderNumberMap.put(USER_NUMBER, userNumberCounter);
                RootRef.child("Users").child(USER_NUMBER).updateChildren(orderNumberMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(com.example.myapplication.Register.this, Login.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(com.example.myapplication.Register.this, "Network Error:please try again after some time ..."
                                            , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    void getOrderNumberForAdmin() {
        loadingBar.setTitle("Create Account");
        loadingBar.setMessage("Please wait,while we are checking the credentials.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Admins").child(ADMIN_NUMBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(ADMIN_NUMBER) || dataSnapshot.child(ADMIN_NUMBER).exists()) {
                    adminNumberCounter = Integer.valueOf(String.valueOf(dataSnapshot.child(ADMIN_NUMBER).getValue()));
                    RegisterAdmin();
                } else {
                    HashMap<String, Object> orderNumberMap = new HashMap<>();
                    orderNumberMap.put(ADMIN_NUMBER, 0);
                    RootRef.child("Admins").child(ADMIN_NUMBER).updateChildren(orderNumberMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        RegisterAdmin();
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(com.example.myapplication.Register.this, "Network Error:please try again after some time ..."
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void RegisterAdmin() {
        String user = Username.getText().toString();
        String pass = Password.getText().toString();
        String repass = Repassword.getText().toString();
        String em = Email.getText().toString();
        String ph = PhoneNumber.getText().toString();
        if (TextUtils.isEmpty(user)) {
            Toast.makeText(com.example.myapplication.Register.this, "Please write your username", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(em)) {
            Toast.makeText(com.example.myapplication.Register.this, "Please write your email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(com.example.myapplication.Register.this, "Please write your password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(repass)) {
            Toast.makeText(com.example.myapplication.Register.this, "Please write your repassword", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(ph)) {
            Toast.makeText(com.example.myapplication.Register.this, "Please write your phoneNumber", Toast.LENGTH_SHORT).show();
        } else {
            ValidateEmailForAdmin(user, pass, em, ph);
        }
    }
    private void ValidateEmailForAdmin(String user, String pass, String em, String ph) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Admins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasSame = false;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map<String,Object> mapObject =(HashMap<String, Object>)ds.getValue();
                    Log.d("asdasd" , String.valueOf(mapObject.containsValue("s")));
                    if(mapObject.containsValue(em)){
                        hasSame = true;
                    }
                }
                if (!hasSame) {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("user", user);
                    userdataMap.put("Em", em);
                    userdataMap.put("Pass", pass);
                    userdataMap.put("Ph", ph);
                    RootRef.child("Admins").child(String.valueOf(adminNumberCounter + 1)).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("asd", String.valueOf(adminNumberCounter + 1));
                                        Toast.makeText(com.example.myapplication.Register.this, "Congratulation,Your account has been created. "
                                                , Toast.LENGTH_SHORT).show();
                                        adminNumberCounter += 1;
                                        updateOrderNumberForAdmin();
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(com.example.myapplication.Register.this, "Network Error:please try again after some time ..."
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(com.example.myapplication.Register.this, "This " + em + " already exists. ", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(com.example.myapplication.Register.this, "Please try again using another Email.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(com.example.myapplication.Register.this, Login.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    void updateOrderNumberForAdmin() {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Admins").child(ADMIN_NUMBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> orderNumberMap = new HashMap<>();
                orderNumberMap.put(ADMIN_NUMBER, adminNumberCounter);
                RootRef.child("Admins").child(ADMIN_NUMBER).updateChildren(orderNumberMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(com.example.myapplication.Register.this, Login.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(com.example.myapplication.Register.this, "Network Error:please try again after some time ..."
                                            , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
