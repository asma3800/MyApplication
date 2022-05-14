package com.example.myapplication.User;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.myapplication.R;
import com.example.myapplication.Login;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
public class Items extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    Button paper;
    Button plastic;
    Button glass;
    Button aluminum;
    Button next;
    ArrayList<String> list = new ArrayList<String>();
    DrawerLayout drawerLayout;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    TextView UserNameDrawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items);
        paper = findViewById(R.id.paper);
        plastic = findViewById(R.id.plastic);
        glass = findViewById(R.id.glass);
        aluminum = findViewById(R.id.aluminum);
        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size() == 0 || list.isEmpty()){
                    Toast.makeText(Items.this, "you should select one at least", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(Items.this, OrderDetails.class);
                    intent.putExtra("itemsList"  , list);
                    startActivity(intent);
                }
            }
        });
        paper.setOnClickListener(this);
        plastic.setOnClickListener(this);
        glass.setOnClickListener(this);
        aluminum.setOnClickListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.UserNameDrawer);
        String userName = getUserNameFromCache ();
        navUsername.setText(userName);
        setUp(navigationView);
    }
    public void clickMenu (View view){
        drawerLayout.openDrawer(GravityCompat.START);
    }
    String getUserNameFromCache (){
        sharedpreferences = getSharedPreferences("new", 0);
        return sharedpreferences.getString("userName", "");
    }
    void setUp (NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ordersDrawer: {
                        Intent intent = new Intent(Items.this, OrdersHistory.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.profileDrawer: {
                        Intent intent = new Intent(Items.this, Profile.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.logOutdrawer: {
                        sharedpreferences = getSharedPreferences("new", 0);
                        editor = sharedpreferences.edit();
                        editor.remove("logged_in");
                        editor.remove("user_type");
                        editor.apply();
                        Intent intent = new Intent(Items.this, Login.class);
                        startActivity(intent);
                        break;
                    }
                    default:{
                        Toast.makeText(Items.this,"there is an error", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
                return true;
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Toast.makeText(Items.this,item.getTitle().toString(), Toast.LENGTH_LONG).show();
        Log.d("asd","asd");
        return false;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.paper:
                paper.setText("Paper is Selected");
                paper.setBackgroundColor(Color.parseColor("#327735"));
                list.add("paper");
                break;
            case R.id.plastic:
                plastic.setText("Plastic is Selected");
                plastic.setBackgroundColor(Color.parseColor("#327735"));
                list.add("plastic");
                break;
            case R.id.glass:
                glass.setText("Glass is Selected");
                glass.setBackgroundColor(Color.parseColor("#327735"));
                list.add("glass");
                break;
            case R.id.aluminum:
                aluminum.setText("Aluminum is Selected");
                aluminum.setBackgroundColor(Color.parseColor("#327735"));
                list.add("aluminum");
                break;
        }
    }
}
