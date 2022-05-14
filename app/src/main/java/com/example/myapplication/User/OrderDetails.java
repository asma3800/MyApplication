package com.example.myapplication.User;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import java.util.ArrayList;
public class OrderDetails extends AppCompatActivity {
    Button Next;
    String radioValue,NumberOfAmountWeight,Note;
    RadioGroup radioGroup;
    RadioButton radioButton;
    EditText myEditText1,myEditText2;
    ArrayList<String> itemsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderdetails);
        itemsList = (ArrayList<String>) getIntent().getSerializableExtra("itemsList");
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        radioValue = radioButton.getText().toString();
        Next = (Button) findViewById(R.id.next);
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEditText1 =  (EditText) findViewById(R.id.editTextTextPersonName3);
                NumberOfAmountWeight = myEditText1.getText().toString();
                myEditText2 =  (EditText) findViewById(R.id.editTextTextPersonName);
                Note = myEditText2.getText().toString();
                Intent intent = new Intent(OrderDetails.this, MyLocation.class);
                intent.putExtra("itemsList"  , itemsList);
                intent.putExtra("radioValue", radioValue);
                intent.putExtra("Number Of Amount-Weight", NumberOfAmountWeight);
                intent.putExtra("Note", Note);
                startActivity(intent);
            }
        });
    }
    public void checkButton(View v) {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        radioValue = radioButton.getText().toString();
    }
}
