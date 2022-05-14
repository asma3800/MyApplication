package com.example.myapplication.User;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
public class OrderInformation extends AppCompatActivity implements AdapterView.OnItemClickListener{
    ArrayList<String> itemsList, locationList;
    String radioValue, NumberOfAmountWeight, Note;
    private Button Confirmation;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String email = "";
    String userNumber = "-";
    private static final String ORDER_NUMBER = "OrderNumber";
    Integer orderNumber = 0;
    private static final String TOTAL_ORDER_NUMBER = "TotalOrderNumber";
    Integer totalOrderNumber = 0;
    ProgressDialog progressDialog;
    TextView notes,address, amount, type, textView15;
    ListView listView;
    ArrayAdapter<String> ordersListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderinformation);
        itemsList = (ArrayList<String>) getIntent().getSerializableExtra("itemsList");
        locationList = (ArrayList<String>) getIntent().getSerializableExtra("locationList");
        Bundle bundle = getIntent().getExtras();
        radioValue = bundle.getString("radioValue");
        NumberOfAmountWeight = bundle.getString("Number Of Amount-Weight");
        Note = bundle.getString("Note");
        listView = findViewById(R.id.listViewOrder);
        listView.setOnItemClickListener(this);
        Log.d("asd", String.valueOf(itemsList.size()));
        ordersListAdapter = new ArrayAdapter(OrderInformation.this, android.R.layout.simple_list_item_1,itemsList);
        listView.setAdapter(ordersListAdapter);
        notes = (TextView) findViewById(R.id.notesm);
        address= (TextView)findViewById(R.id.addressm);
        amount= (TextView)findViewById(R.id.amountm);
        type= (TextView)findViewById(R.id.typem);
        textView15 = (TextView)findViewById(R.id.textView15);
        if(radioValue.equals("Amount")){
            textView15.setText("The Amount");
        }else{
            textView15.setText("The Weight");
        }
        notes.setText(Note);
        address.setText(locationList.get(0));
        amount.setText(NumberOfAmountWeight);
        type.setText(radioValue);
        Confirmation = (Button) findViewById(R.id.Confirmation);
        Confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Confirmation.setEnabled(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderInformation.this);
                builder.setTitle("Confirm your order!");
                builder.setMessage("Are you sure you want to send the order");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressDialog = new ProgressDialog(OrderInformation.this, R.style.DialogStyle);
                        progressDialog.setTitle("Please wait, it is sending your order");
                        progressDialog.setCancelable(true);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();
                        email = getEmailFromCache();
                        userNumber = getUserNumberFromCache();
                        getOrderNumberForAll();
                    }
                });
                builder.setNegativeButton("back", null);
                builder.show();
            }
        });
    }
    String getEmailFromCache() {
        sharedpreferences = getSharedPreferences("new", 0);
        return sharedpreferences.getString("logged_in", "");
    }
    void getOrderNumberForUser(String userNumber , int globleOrderNumber) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Users").child(userNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(ORDER_NUMBER) || dataSnapshot.child(ORDER_NUMBER).exists()) {
                    orderNumber = Integer.valueOf(String.valueOf(dataSnapshot.child(ORDER_NUMBER).child(ORDER_NUMBER).getValue()));
                    setOrderDetailsForUser(userNumber,globleOrderNumber);
                } else {
                    HashMap<String, Object> orderNumberMap = new HashMap<>();
                    orderNumberMap.put(ORDER_NUMBER, 0);
                    RootRef.child("Users").child(userNumber).child(ORDER_NUMBER).updateChildren(orderNumberMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        setOrderDetailsForUser(userNumber, globleOrderNumber);
                                    } else {
                                        Toast.makeText(OrderInformation.this, "Network Error:please try again after some time ..."
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
    void setOrderDetailsForUser(String userNumber , int globleOrderNumber) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> userdataMap = new HashMap<>();
                userdataMap.put("orderNumber", orderNumber + 1);
                userdataMap.put("itemsList", itemsList);
                userdataMap.put("locationList", locationList);
                userdataMap.put("radioValue", radioValue);
                userdataMap.put("Number Of Amount-Weight", NumberOfAmountWeight);
                userdataMap.put("Note", Note);
                userdataMap.put("status", "Pending");
                userdataMap.put("globleOrderNumber" , globleOrderNumber + 1);
                RootRef.child("Users").child(userNumber).child("Orders").child(String.valueOf(orderNumber + 1)).updateChildren(userdataMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    orderNumber += 1;
                                    updateOrderNumberForUser(userNumber);
                                } else {
                                    Confirmation.setEnabled(true);
                                    Toast.makeText(OrderInformation.this, "Network Error:please try again after some time ..."
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
    void updateOrderNumberForUser(String userNumber) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Users").child(userNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> orderNumberMap = new HashMap<>();
                orderNumberMap.put(ORDER_NUMBER, orderNumber);
                RootRef.child("Users").child(userNumber).child(ORDER_NUMBER).updateChildren(orderNumberMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                } else {
                                    Toast.makeText(OrderInformation.this, "Network Error:please try again after some time ..."
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
    void getOrderNumberForAll() {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(TOTAL_ORDER_NUMBER) || dataSnapshot.child(TOTAL_ORDER_NUMBER).exists()) {
                    totalOrderNumber = Integer.valueOf(String.valueOf(dataSnapshot.child(TOTAL_ORDER_NUMBER).child(TOTAL_ORDER_NUMBER)
                            .getValue()));
                    getOrderNumberForUser(userNumber, totalOrderNumber);
                    setOrderDetailsForAll();
                } else {
                    HashMap<String, Object> orderNumberMap = new HashMap<>();
                    orderNumberMap.put(TOTAL_ORDER_NUMBER, 0);
                    RootRef.child(TOTAL_ORDER_NUMBER).updateChildren(orderNumberMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        getOrderNumberForUser(userNumber, totalOrderNumber);
                                        setOrderDetailsForAll();
                                    } else {
                                        Toast.makeText(OrderInformation.this, "Network Error:please try again after some time ..."
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
    void setOrderDetailsForAll() {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> userdataMap = new HashMap<>();
                userdataMap.put("totalOrderNumber", totalOrderNumber + 1);
                userdataMap.put("itemsList", itemsList);
                userdataMap.put("locationList", locationList);
                userdataMap.put("radioValue", radioValue);
                userdataMap.put("Number Of Amount-Weight", NumberOfAmountWeight);
                userdataMap.put("Note", Note);
                userdataMap.put("status", "Pending");
                RootRef.child("AllOrders").child(String.valueOf(totalOrderNumber + 1)).updateChildren(userdataMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    totalOrderNumber += 1;
                                    updateOrderNumberForAll();
                                } else {
                                    Confirmation.setEnabled(true);
                                    Toast.makeText(OrderInformation.this, "Network Error:please try again after some time ..."
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
    void updateOrderNumberForAll() {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> orderNumberMap = new HashMap<>();
                orderNumberMap.put(TOTAL_ORDER_NUMBER, totalOrderNumber);
                RootRef.child(TOTAL_ORDER_NUMBER).updateChildren(orderNumberMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Confirmation.setEnabled(true);
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(OrderInformation.this, Items.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(OrderInformation.this, "Network Error:please try again after some time ..."
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
    void saveToCache() {
        sharedpreferences = getSharedPreferences("new", 0);
        editor = sharedpreferences.edit();
        if (!sharedpreferences.contains(ORDER_NUMBER)) {
            Toast.makeText(OrderInformation.this, "not exist orderNumber", Toast.LENGTH_SHORT).show();
            editor.putInt(ORDER_NUMBER, 0);
            editor.commit();
        } else {
            Toast.makeText(OrderInformation.this, "yes exist orderNumber", Toast.LENGTH_SHORT).show();
            orderNumber += sharedpreferences.getInt(ORDER_NUMBER, 0);
        }
        Toast.makeText(OrderInformation.this, String.valueOf(orderNumber), Toast.LENGTH_SHORT).show();
    }
    String getUserNumberFromCache (){
        sharedpreferences = getSharedPreferences("new", 0);
        return sharedpreferences.getString("userNumber2", "");
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }
}
