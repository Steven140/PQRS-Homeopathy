package com.example.finalehomeopathy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatientRegistrationActivity extends AppCompatActivity {

    private EditText editTextName, editTextAge, editTextEmail,
            editTextPassword, editTextConfirmPassword, editTextContactNo;

    private RadioButton radioButtonMale, radioButtonFemale;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_patient_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextName = findViewById(R.id.editText_name);
        editTextAge = findViewById(R.id.editText_age);
        editTextEmail = findViewById(R.id.editText_email);
        editTextPassword = findViewById(R.id.editText_password);
        editTextConfirmPassword =
                findViewById(R.id.editText_confirm_password);
        editTextContactNo = findViewById(R.id.editText_contact_no);

        radioButtonMale = findViewById(R.id.radioButton_male);
        radioButtonFemale = findViewById(R.id.radioButton_female);

        findViewById(R.id.button_register_submit)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        registerDoctor();
                    }
                });
    
    private void registerDoctor() {
        String name = editTextName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword =
                editTextConfirmPassword.getText().toString().trim();
        String contactNo = editTextContactNo.getText().toString().trim();
        String gender = radioButtonMale.isChecked() ? "Male" : "Female";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age)
                || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirmPassword)
                || TextUtils.isEmpty(contactNo)) {
            Toast.makeText(this, "All fields are required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match",
                    Toast.LENGTH_SHORT).show();
            return;
        }
                mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        String userId = user.getDisplayName();

                        DatabaseReference currentUserDb =
                                mDatabase.child("users").child(user.getUid());

                        currentUserDb.child("name").setValue(name);
                        currentUserDb.child("age").setValue(age);
                        currentUserDb.child("email").setValue(email);
                        currentUserDb.child("gender").setValue(gender);
                        currentUserDb.child("contactNo").setValue(contactNo);
                                                Toast.makeText(PatientRegistrationActivity.this,
                                "Registration successful",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(
                                PatientRegistrationActivity.this,
                                PatientLoginActivity.class));
                        finish();

                    } else {
                        Toast.makeText(PatientRegistrationActivity.this,
                                "Registration failed",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Log the exception or show a message to the user
                    Log.e("Registration Error", e.getMessage());

                    Toast.makeText(PatientRegistrationActivity.this,
                            "Registration failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}
    }
