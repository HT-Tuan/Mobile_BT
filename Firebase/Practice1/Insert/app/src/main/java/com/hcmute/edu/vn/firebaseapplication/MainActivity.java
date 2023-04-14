package com.hcmute.edu.vn.firebaseapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText txtname, txtage, txtphone, txtheight;
    Button btnsave;
    DatabaseReference reff;

    Member member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtname = (EditText) findViewById(R.id.txtname);
        txtage = (EditText) findViewById(R.id.txtage);
        txtphone = (EditText) findViewById(R.id.txtphone);
        txtheight = (EditText) findViewById(R.id.txtheight);
        btnsave = (Button) findViewById(R.id.button);
        member = new Member();
        reff = FirebaseDatabase.getInstance().getReference().child("Member");
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int agea = Integer.parseInt(txtage.getText().toString().trim());
                Float hit = Float.parseFloat(txtheight.getText().toString().trim());
                Long  phn = Long.parseLong(txtphone.getText().toString().trim());

                member.setName(txtname.getText().toString().trim());
                member.setAge(agea);
                member.setHt(hit);
                member.setPh(phn);

                reff.push().setValue(member);
                Toast.makeText(MainActivity.this, "data inserted sucessfully", Toast.LENGTH_LONG).show();
            }
        });
    }
}