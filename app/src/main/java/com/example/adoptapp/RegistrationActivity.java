package com.example.adoptapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private Button mRegister;
    private EditText mEmail, mPassword, mName;
    private RadioGroup mRadioGroup;
    private ImageView mProfileImage;
    private String profileImageUrl;
    private ProgressBar mProgressBar;
    private Uri resultUri;
    private int flag = 0;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null){
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        mRegister = (Button) findViewById(R.id.registerAnimal);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mName = (EditText) findViewById(R.id.name);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup2);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //uploadPhoto();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
                flag = 1;
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                int selectId = mRadioGroup.getCheckedRadioButtonId();
                final String sexo;
                final RadioButton radioButton = (RadioButton) findViewById(selectId);

                //if(selectId != -1){
                    //sexo = radioButton.getText().toString();
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();

                if(!email.equals("") && !password.equals("") && !name.equals("")){
                    if(selectId != -1){
                        sexo = radioButton.getText().toString();
                        if(flag == 1){
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    mProgressBar.setVisibility(View.GONE);
                                    if(!task.isSuccessful()){
                                        Toast.makeText(RegistrationActivity.this, "Erro ao se cadastrar!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        String userId = mAuth.getCurrentUser().getUid();
                                        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId); //child("People")
                                        Map userInfo = new HashMap<>();

                                        userInfo.put("profileImageUrl", "default");
                                        uploadPhoto();
                                        userInfo.put("name", name);
                                        userInfo.put("type", "people");
                                        userInfo.put("sexo", sexo);

                                        currentUserDb.updateChildren(userInfo);
                                        Toast.makeText(RegistrationActivity.this, "E-mail cadastrado!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegistrationActivity.this, "Selecione uma foto!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        sexo = "";
                        Toast.makeText(RegistrationActivity.this, "Selecione o sexo!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegistrationActivity.this, "Digite todos campos!", Toast.LENGTH_SHORT).show();
                }





            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    public void uploadPhoto(){
        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(mAuth.getCurrentUser().getUid());
        final DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = filepath.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();
                return;
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {
                        Map userInfo = new HashMap();
                        userInfo.put("profileImageUrl", downloadUrl.toString());
                        mCustomerDatabase.updateChildren(userInfo);
                        finish();
                        return;
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}

