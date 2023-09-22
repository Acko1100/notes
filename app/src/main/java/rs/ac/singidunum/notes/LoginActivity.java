package rs.ac.singidunum.notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    ////Deklaracija komponenti korisnickog interfejsa
    EditText emailEditText, passwordEditText;
    Button loginButton;
    ProgressBar progressBar;
    TextView createAccountTextViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Inicijalizacija komponenti prema njihovim ID-jevima
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        createAccountTextViewButton = findViewById(R.id.create_account_text_view_button);
        progressBar = findViewById(R.id.progress_bar);


        loginButton.setOnClickListener((v) -> LoginUser());
        createAccountTextViewButton.setOnClickListener((v) -> startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class)));


    }
    void LoginUser(){
        //Uzimanje unetih podataka
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        //Validacija podataka
        boolean isValidated = dataValidation(email,password);
        if (!isValidated){
            return;
        }

        //Prijavljivanje korisnika putem Firebase autentikacije
        loginAccountFirebase(email,password);
        changeInProgress(true);
    }

    void changeInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    void loginAccountFirebase(String email, String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            changeInProgress(false);
            if (task.isSuccessful()){
                //uspesno logovanje
                if (firebaseAuth.getCurrentUser().isEmailVerified()){
                    //ide na glavnu aktivnost (prosao email verifikaciju)
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }else{
                    Utility.showToast(LoginActivity.this,"Email is not verified.");
                }
            }else{
                //neuspesno logovanje
                Utility.showToast(LoginActivity.this,task.getException().getLocalizedMessage());

            }

        });

    }

    boolean dataValidation(String email, String password){
        //validacija unetih podataka
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email address is not valid. ");
            return false ;
        }
        if(password.length()<6){
            passwordEditText.setError("Password must contain more than 6 characters");
            return false;
        }
        return true;
    }
}