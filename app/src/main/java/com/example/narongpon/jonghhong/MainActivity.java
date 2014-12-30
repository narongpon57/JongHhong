package com.example.narongpon.jonghhong;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;


public class MainActivity extends ActionBarActivity {

    private EditText edtUser;
    private EditText edtPass;
    private ButtonRectangle btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jh_login);

        btnLogin = (ButtonRectangle)findViewById(R.id.btn_login);
        edtUser = (EditText)findViewById(R.id.edt_user);
        edtPass = (EditText)findViewById(R.id.edt_pass);

        edtUser.setText("");
        edtPass.setText("");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUser = edtUser.getText().toString();
                String txtPass = edtPass.getText().toString();
                Dialog dialog;
                Log.e("test","out");
                if(txtUser.equals("zdbw0057") && txtPass.equals("123456")) {
                    Intent i = new Intent(getApplicationContext(), JHCheckRoom.class);
                    startActivity(i);
                } else if (txtUser.equals("") || txtPass.equals("")){
                    Log.e("test","1");
                    dialog = new Dialog(getApplication(),"Error!","Please input Username and Password");
                    Log.e("test","2");
                    dialog.show();
                    Log.e("test","3");
                } else {
                    Log.e("test","4");
                    dialog = new Dialog(getApplication(),"Error!","Username or Password is incorrect");
                    dialog.show();
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
