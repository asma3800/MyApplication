package com.example.myapplication.User;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.Model.OrderModel;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
public class OrdersHistory extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    String  userNumber = "";
    ArrayList<String> itemsList, locationList;
    String radioValue, NumberOfAmountWeight, Note, orderNumber, status;
    ListView listView;
    ArrayAdapter<String> ordersListAdapter;
    ProgressDialog progressDialog;
    ArrayList<OrderModel> ordersList;
    ArrayList<String> orderDetails = new ArrayList<String>();
    Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orders_history);
        listView = findViewById(R.id.listView);
        progressDialog = new ProgressDialog(this, R.style.DialogStyle); //new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        userNumber = getUserNumberFromCache ();
        getOrdersOfUser();
        back = findViewById(R.id.back2);
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
    void getOrdersOfUser() {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Users").child(userNumber).child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists()){
                        ordersList =new ArrayList<OrderModel>();
                        Map<String,Object> mapObject =(HashMap<String, Object>)ds.getValue();
                        OrderModel order = new OrderModel();
                        order.setNumberOfAmountWeight(String.valueOf(mapObject.get("Number Of Amount-Weight")));
                        order.setNote(String.valueOf(mapObject.get("Note")));
                        order.setOrderNumber(String.valueOf(mapObject.get("orderNumber")));
                        order.setStatus(String.valueOf(mapObject.get("status")));
                        order.setRadioValue(String.valueOf(mapObject.get("radioValue")));
                        order.setItemsList(new ArrayList<String>((Collection<String>)mapObject.get("itemsList")));
                        order.setLocationList(new ArrayList<String>((Collection<String>)mapObject.get("locationList")));
                        Log.d("TAG",  String.valueOf( order.getLocationList().get(0)));
                        ordersList.add(order);
                        orderDetails.add("Order Number is : " + order.getOrderNumber() +  "\nThe order status is : " + order.getStatus() );
                    }
                }
                Toast.makeText(OrdersHistory.this, String.valueOf(orderDetails.size()), Toast.LENGTH_SHORT).show();
                ordersListAdapter = new ArrayAdapter<String>(OrdersHistory.this, android.R.layout.simple_list_item_1, orderDetails);
                listView.setAdapter(ordersListAdapter);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrdersHistory.this, String.valueOf(error), Toast.LENGTH_LONG).show();
            }
        });
    }
}
