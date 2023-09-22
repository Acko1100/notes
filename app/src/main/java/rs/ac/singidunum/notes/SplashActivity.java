package rs.ac.singidunum.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            //Provera da li je ulogovan korisnik
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null){
                //Ako nije preusmerava ga na LoginActivity
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }else{
                //Ako jeste preusmerava ga na MainActivity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            finish();
        },2000);
    }
}