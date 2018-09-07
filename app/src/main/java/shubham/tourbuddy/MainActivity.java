package shubham.tourbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;


public class MainActivity extends AppCompatActivity {
   private SignInButton GoogleBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i=new Intent(MainActivity.this,SignIn.class);
                startActivity(i);
                finish();
            }
        },2000);





    }
}
