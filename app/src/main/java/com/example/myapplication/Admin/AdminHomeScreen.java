package com.example.myapplication.Admin;
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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.Model.OrderModel;
import com.example.myapplication.R;
import com.example.myapplication.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
public class AdminHomeScreen extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listView;
    ArrayAdapter<String> ordersListAdapter;
    ProgressDialog progressDialog;
    Button LogOutButtonAdmin;
    ArrayList<String> orderDetails = new ArrayList<String>();
    ArrayList<String> orderNumberList = new ArrayList<String>();
    ArrayList<String> statusList = new ArrayList<String>();
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String [] statusListRadioGroup = new String[5] ;
    String radioButton ;
    int index = 0;
    String globleOrderNumber = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_home_screen);
        listView = findViewById(R.id.listViewAdmin);
        listView.setOnItemClickListener(this);
        statusListRadioGroup[0]="Pending";
        statusListRadioGroup[1]="Acceptable";
        statusListRadioGroup[2]="Canceled";
        statusListRadioGroup[3]="The receipt of the request";
        statusListRadioGroup[4]="The truck is on its way to you";
        radioButton = statusListRadioGroup[0];
        LogOutButtonAdmin = findViewById(R.id.LogOutButtonAdmin);
        LogOutButtonAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedpreferences = getSharedPreferences("new", 0);
                editor = sharedpreferences.edit();
                editor.remove("user_type");
                editor.apply();
                Intent intent = new Intent(AdminHomeScreen.this, Login.class);
                startActivity(intent);
            }
        });
        getOrders();
    }
    void getOrders() {
        progressDialog = new ProgressDialog(this, R.style.DialogStyle);
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("AllOrders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists()){
                        Map<String,Object> mapObject =(HashMap<String, Object>)ds.getValue();
                        OrderModel order = new OrderModel();
                        order.setNumberOfAmountWeight(String.valueOf(mapObject.get("Number Of Amount-Weight")));
                        order.setNote(String.valueOf(mapObject.get("Note")));
                        order.setOrderNumber(String.valueOf(mapObject.get("totalOrderNumber")));
                        order.setStatus(String.valueOf(mapObject.get("status")));
                        order.setRadioValue(String.valueOf(mapObject.get("radioValue")));
                        order.setItemsList(new ArrayList<String>((Collection<String>)mapObject.get("itemsList")));
                        order.setLocationList(new ArrayList<String>((Collection<String>)mapObject.get("locationList")));
                        Log.d("TAG",  String.valueOf( order.getLocationList().get(0)));
                        String [] list = new String[]{
                                "Order Number is : ",
                                order.getOrderNumber(),
                                "\nThe order status is : ",
                                order.getStatus()
                        };
                        globleOrderNumber = String.valueOf(mapObject.get("totalOrderNumber"));
                        orderDetails.add("Order Number is : " + order.getOrderNumber() +  "\nThe order status is : " + order.getStatus() );
                        statusList.add(order.getStatus());
                        orderNumberList.add(order.getOrderNumber());
                    }
                }
                ordersListAdapter = new ArrayAdapter(AdminHomeScreen.this, android.R.layout.simple_list_item_1, orderDetails);
                listView.setAdapter(ordersListAdapter);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminHomeScreen.this, String.valueOf(error), Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long l) {
        openDialog(itemIndex);
    }
    void openDialog (int itemIndex){
        for(int i =0;i < statusListRadioGroup.length ; i++){
            if(statusList.get(itemIndex).equals(statusListRadioGroup[i])){
                index = i;
            }
        }
        radioButton = statusList.get(itemIndex);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Request status change");
        builder.setSingleChoiceItems( statusListRadioGroup, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                radioButton = statusListRadioGroup[i];
            }
        });
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                statusList.set(itemIndex , radioButton);
                orderDetails.set(itemIndex , "Order Number is : " + orderNumberList.get(itemIndex) +  "\nThe order status is : " + radioButton);
                ordersListAdapter.notifyDataSetChanged();
                Log.d("qwe" , "111");
                updateUserOrderStatus (orderNumberList.get(itemIndex)  , radioButton);
                updateStatus(orderNumberList.get(itemIndex) , radioButton);
            }
        });
        builder.setNegativeButton("Back", null);
        builder.show();
    }
    void updateStatus(String orderNumber , String newStatus) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("AllOrders").child(orderNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> orderNumberMap = new HashMap<>();
                orderNumberMap.put("status", newStatus);
                RootRef.child("AllOrders").child(orderNumber).updateChildren(orderNumberMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AdminHomeScreen.this, "updated status", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AdminHomeScreen.this, "Network Error:please try again after some time ...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    void updateUserOrderStatus (String orderNumber , String newStatus){
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                boolean hasSame = false;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String k = ds.getKey();
                    Log.d("qwe" , "k is " + k);
                    for(DataSnapshot ds2 : ds.child("Orders").getChildren()) {
                        Map<String,Object> mapObject =(HashMap<String, Object>)ds2.getValue();
                        String s = String.valueOf(mapObject.get("globleOrderNumber"));
                        String n = String.valueOf(mapObject.get("orderNumber"));
                        if(s.equals(orderNumber)){
                            Log.d("qwe" , "globleOrderNumber = " +  globleOrderNumber + " ;;;; orderNumber = " + orderNumber);
                            Log.d("qwe" , String.valueOf(s.equals(globleOrderNumber)));
                            Log.d("qwe" , n);
                            HashMap<String, Object> orderNumberMap = new HashMap<>();
                            orderNumberMap.put("status", newStatus);
                            RootRef.child("Users").child(k).child("Orders").child(n).updateChildren(orderNumberMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("qwe1" , "qwe1111");
                                                Toast.makeText(AdminHomeScreen.this, "updated status", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d("qwe1" , "qwe1111");
                                                Toast.makeText(AdminHomeScreen.this, "Network Error:please try again after some time ...", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            hasSame = true;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
