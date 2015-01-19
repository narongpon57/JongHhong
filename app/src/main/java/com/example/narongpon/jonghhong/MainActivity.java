package com.example.narongpon.jonghhong;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonRectangle;



public class MainActivity extends ActionBarActivity {

    private EditText edtUser;
    private EditText edtPass;
    private ButtonRectangle btnLogin;
    MaterialDialog.Builder mtrDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jh_login);

        btnLogin = (ButtonRectangle)findViewById(R.id.btn_login);
        edtUser = (EditText)findViewById(R.id.edt_user);
        edtPass = (EditText)findViewById(R.id.edt_pass);
        mtrDialog = new MaterialDialog.Builder(MainActivity.this);


        edtUser.setText("");
        edtPass.setText("");



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUser = edtUser.getText().toString();
                String txtPass = edtPass.getText().toString();

                if(txtUser.equals("zdbw0057") && txtPass.equals("123456")) {
                    Intent i = new Intent(getApplicationContext(),MainDrawer.class);
                    startActivity(i);
                } else if(txtUser.equals("") || txtPass.equals("")) {
                    mtrDialog.title("ข้อผิดพลาด");
                    mtrDialog.content("กรุณากรอกชื่อผู้ใช้และรหัสผ่านให้ครบถ้วน");
                    mtrDialog.negativeText("ปิด");
                    mtrDialog.show();
                } else {
                    mtrDialog.title("ข้อผิดพลาด");
                    mtrDialog.content("ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
                    mtrDialog.negativeText("ปิด");
                    mtrDialog.show();

                    edtUser.setText("");
                    edtPass.setText("");
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
