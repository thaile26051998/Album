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

public class CreatePassActivity extends AppCompatActivity {
    EditText edtPass, edtPassAgain;
    Button btnConfirmPass;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pass);
        InitView();
        btnConfirmPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtPass = edtPass.getText().toString();
                String txtPassAgain = edtPassAgain.getText().toString();
                //nếu password rỗng
                if(txtPass.equals("") || txtPassAgain.equals("")){
                    Toast.makeText(CreatePassActivity.this, "Password must not empty!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(txtPass.equals(txtPassAgain)){
                        //password khớp nhau
                        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCES",0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("password", txtPass);
                        editor.apply();

                        //truy cập vào app
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        //password không khớp
                        Toast.makeText(CreatePassActivity.this, "Password do not match!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void InitView() {
        edtPass = (EditText) findViewById(R.id.edtPass);
        edtPassAgain = (EditText) findViewById(R.id.edtPassAgain);
        btnConfirmPass = (Button) findViewById(R.id.btnConfirm);
    }

}
