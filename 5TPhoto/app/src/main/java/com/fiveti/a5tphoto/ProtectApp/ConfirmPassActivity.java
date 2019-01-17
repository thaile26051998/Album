package com.fiveti.a5tphoto.ProtectApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fiveti.a5tphoto.Activity.FirstActivity;
import com.fiveti.a5tphoto.Activity.MainActivity;
import com.fiveti.a5tphoto.R;

public class ConfirmPassActivity extends AppCompatActivity {
    EditText edtConf;
    Button btnConf;
    String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pass);

        //load password
        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCES", 0);
        pass = sharedPreferences.getString("password","");

        edtConf = (EditText) findViewById(R.id.edtEnterPass);
        btnConf = (Button) findViewById(R.id.btnConf);
        btnConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtPass = edtConf.getText().toString();
                if(txtPass.equals(pass)){
                    //truy cấp vào app
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(ConfirmPassActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
