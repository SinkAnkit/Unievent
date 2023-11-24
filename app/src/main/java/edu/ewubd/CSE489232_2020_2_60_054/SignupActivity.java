package edu.ewubd.CSE489232_2020_2_60_054;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private Button goBtn, exitBtn;


    private EditText suName, suEmail, suPhone, suUser, suPass, suConPass;

    private TextView loginRedirect, account, title;

    private CheckBox reUser, rePass;

    private TableRow rowName, rowPhone, rowEmail, rowRepass;

    private boolean isLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        goBtn = findViewById(R.id.goBtn);
        exitBtn = findViewById(R.id.exitBtn);

        suName = findViewById(R.id.suName);
        suEmail = findViewById(R.id.suEmail);
        suPhone = findViewById(R.id.suPhone);
        suUser = findViewById(R.id.suUser);
        suPass = findViewById(R.id.suPass);
        suConPass = findViewById(R.id.suConPass);

        reUser = findViewById(R.id.chUser);
        rePass = findViewById(R.id.chPass);

        rowName = findViewById(R.id.rowName);
        rowPhone = findViewById(R.id.rowPhone);
        rowEmail = findViewById(R.id.rowEmail);
        rowRepass = findViewById(R.id.rowRePass);

        account = findViewById(R.id.account);
        loginRedirect = findViewById(R.id.loginRedirect);
        title = findViewById(R.id.title);

        this.changeView();

        SharedPreferences pref = getSharedPreferences("savedUserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if(pref.getBoolean("isUserInfoSaved", false)){
            suUser.setText(pref.getString("username", ""));
            suPass.setText(pref.getString("password", ""));
        }

        if(pref.getBoolean("isLoggedIn", false)){
            Intent i = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(i);
        }

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String err = "";
                String name = suName.getText().toString();
                String email = suEmail.getText().toString();
                String phone = suPhone.getText().toString();
                String userid = suUser.getText().toString();
                String pass = suPass.getText().toString();
                String con_pass = suConPass.getText().toString();

                if(!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !userid.isEmpty() && !pass.isEmpty() && !con_pass.isEmpty() && !isLogin){
                    if(name.length() < 4 || name.length() > 12 || !name.matches("^[a-zA-Z ]+$")){
                        err += "Invalid Name (4-12 long and only alphabets)\n";
                    }
                    //email
                    String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
                    if(!email.matches(EMAIL_REGEX)){
                        err += "Invalid email format\n";
                    }
                    //phone
                    Pattern pattern = Pattern.compile("^\\+\\d{13}$");
                    Matcher matcher = pattern.matcher(phone);
                    if(!matcher.matches()){
                        err += "Invalid phone number (format +8801234556789)\n";
                    }
                    //userid
                    if(userid.length() < 4 || !userid.matches("^[a-zA-Z0-9 ]+$")){
                        err += "Invalid userid (4-12 long and only alphabets and number)\n";
                    }

                    if(pass.length() < 8 || con_pass.length() != pass.length() || !pass.equals(con_pass)){
                        err += "Password should at least 8 char or Check your password\n";
                    }

                    //checkbox
                    boolean chUser = reUser.isChecked();
                    boolean chPass = rePass.isChecked();

                    //save user information using sharedpreferenec
                    if(chUser || chPass){
                        if(chUser){
                            //editor.putBoolean("isLoggedIn", true);
                            editor.putString("username", userid);
                            suUser.setText(pref.getString("username", ""));
                        }

                        if(chPass){
                            //save user password
                            editor.putString("password", pass);
                            suPass.setText(pref.getString("password", ""));
                        }
                    }


                }
                else if (!userid.isEmpty() && !pass.isEmpty() && isLogin) {
                    boolean isUser = true;
                    boolean isPass = true;
                    //userid
                    if(userid.length() < 4 || !userid.matches("^[a-zA-Z0-9 ]+$")){
                        err += "Invalid userid (4-12 long and only alphabets and number)\n";
                        isUser=false;
                    }

                    if(pass.length() < 8){
                        err += "Please Check your password\n";
                        isPass = false;
                    }

                    if(isUser && isPass){
                        Intent i = new Intent(SignupActivity.this, MainActivity.class);
                        //signup data
                        editor.putBoolean("isLoggedIn", true);
                        editor.putBoolean("isUserInfoSaved", true);
                        editor.apply();
                        startActivity(i);
                    }
                }
                else {
                    err += "Please all the field\n";
                }


                if(err.length()>0){
                    showErrorDialog(err);
                }
                else{
                    //signup data
                    //TODO save data
                    editor.putString("name", name);
                    editor.putString("email", email);
                    editor.putString("phone", phone);
                    editor.putBoolean("isUserInfoSaved", true);
                    editor.apply();
                    isLogin = !isLogin;
                    changeView();
                }
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
            }
        });


        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin){
                    isLogin = false;
                }
                else {
                    isLogin = true;
                }
                changeView();

            }
        });


    }

    private void changeView(){
        if(isLogin){
            rowName.setVisibility(View.GONE);
            rowEmail.setVisibility(View.GONE);
            rowPhone.setVisibility(View.GONE);
            rowRepass.setVisibility(View.GONE);
            account.setText("Don't have an account?");
            loginRedirect.setText("Sign Up");
            title.setText("Login");
        }
        else{
            rowName.setVisibility(View.VISIBLE);
            rowEmail.setVisibility(View.VISIBLE);
            rowPhone.setVisibility(View.VISIBLE);
            rowRepass.setVisibility(View.VISIBLE);
            account.setText("Already have an account?");
            loginRedirect.setText("Login");
            title.setText("Signup");
        }
    }

    private void showErrorDialog(String errMsg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(errMsg);
        builder.setCancelable(true);

        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}