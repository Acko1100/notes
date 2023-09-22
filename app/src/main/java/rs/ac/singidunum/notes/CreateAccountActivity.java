package rs.ac.singidunum.notes;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {


    //Deklaracija komponenti korisnickog interfejsa
    EditText emailEditText, passwordEditText,repeatPasswordEditText;
    Button createAccountButton;
    ProgressBar progressBar;
    TextView loginButtonTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


        //Inicijalizacija komponenti prema njihovim ID-jevima
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        repeatPasswordEditText = findViewById(R.id.repeat_password_edit_text);
        createAccountButton = findViewById(R.id.create_account_button);
        loginButtonTextView = findViewById(R.id.login_text_view_button);
        progressBar = findViewById(R.id.progress_bar);


        createAccountButton.setOnClickListener((v) -> createAccount());
        loginButtonTextView.setOnClickListener(v -> finish());
    }


    void createAccount(){
        //Uzimanje unetih podataka
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String repeatPassword = repeatPasswordEditText.getText().toString();

        //Validacija podataka
        boolean isValidated = dataValidation(email,password,repeatPassword);
        if (!isValidated){
            return;
        }

        changeInProgress(true);

        //Kreiranje naloga putem Firebase autentikacije
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                task -> {
                    //Obrada rezultata
                    changeInProgress(false);
                    if (task.isSuccessful()){
                        //napravljen nalog
                        Utility.showToast(CreateAccountActivity.this,"Account successfully created," +
                                " Check your email inbox");
                        firebaseAuth.getCurrentUser().sendEmailVerification();
                        firebaseAuth.signOut();
                        finish();
                    }else{
                        //nije napravljen nalog
                        Utility.showToast(CreateAccountActivity.this,task.getException().getLocalizedMessage());
                    }
                });
    }

    //Promena prikaza dok traje proces
    void changeInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccountButton.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            createAccountButton.setVisibility(View.VISIBLE);
        }
    }

    boolean dataValidation(String email, String password ,String repeatPassword){
        //validacija unetih podataka
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email address is not valid. ");
            return false ;
        }
        if(password.length()<6){
            passwordEditText.setError("Password must contain more than 6 characters");
            return false;
        }
        if (!password.equals(repeatPassword)){
            repeatPasswordEditText.setError("Passwords do not match. ");
            return false;
        }
        return true;
    }

}