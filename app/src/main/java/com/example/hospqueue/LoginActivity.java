package com.example.hospqueue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    TextView textViewlogin;
    EditText ettphno,etcode;
    Button buttoncode,verify;
    private FirebaseAuth mAuth;
    String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textViewlogin=findViewById(R.id.phonenum_tv);
        mAuth = FirebaseAuth.getInstance();
        buttoncode=findViewById(R.id.code_buttton);
        ettphno=findViewById(R.id.login_phone);
        verify=findViewById(R.id.code_verify);
        etcode=findViewById(R.id.otp_et);
        buttoncode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificattionCode();
            }
        });
        checkUser();
    }

    private void checkUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();
                        Log.i("JWT_Token:: ",idToken);
                    }
                }
            });
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            // User is signed out
        }
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Log.i("CODE","IS SENT ");
            verificationId=s;
            textViewlogin.setText("Enter the code sent");
            ettphno.setVisibility(View.INVISIBLE);
            etcode.setVisibility(View.VISIBLE);
            buttoncode.setVisibility(View.INVISIBLE);
            verify.setVisibility(View.VISIBLE);
            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyPhoneNumberWithCode(verificationId,etcode.getText().toString());
                }
            });

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.i("CODE","NOt SENT "+e);

        }
    };
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(),"login success",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        String idToken = task.getResult().getToken();
                                        Log.i("JWT_Token:: ",idToken);
                                    }
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("IT IS", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(),"login invalid",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void sendVerificattionCode() {
        String num="+91"+ettphno.getText().toString();
        Log.i("Number",num);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                num,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }
}