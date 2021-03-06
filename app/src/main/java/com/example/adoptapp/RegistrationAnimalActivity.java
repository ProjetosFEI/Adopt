package com.example.adoptapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationAnimalActivity extends AppCompatActivity {

    private Button mRegister;
    private EditText mEmail, mPassword, mName;
    private RadioGroup mRadioGroup, mRadioGroup2;
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
        setContentView(R.layout.activity_registrationanimal);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null){
                    Intent intent = new Intent(RegistrationAnimalActivity.this, MainActivity.class);
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
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mRadioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);


        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                int selectPorte = mRadioGroup2.getCheckedRadioButtonId();
                final String sexo, porte;
                final RadioButton radioButton = (RadioButton) findViewById(selectId);
                final RadioButton radioButton2 = (RadioButton) findViewById(selectPorte);




                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();

                if(!email.equals("") && !password.equals("")  && !name.equals("")){
                    if(selectId != -1 && selectPorte != -1){
                        sexo = radioButton.getText().toString();
                        porte = radioButton2.getText().toString();
                        if(flag == 1){
                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationAnimalActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    mProgressBar.setVisibility(View.GONE);
                                    if(!task.isSuccessful()){
                                        Toast.makeText(RegistrationAnimalActivity.this, "Erro ao se cadastrar!", Toast.LENGTH_SHORT).show();
                                    }else{
                                        String userId = mAuth.getCurrentUser().getUid();
                                        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                                        Map userInfo = new HashMap<>();

                                        userInfo.put("profileImageUrl", "default");
                                        uploadPhoto();
                                        userInfo.put("name", name);
                                        userInfo.put("sexo", sexo);
                                        userInfo.put("type", "pet");
                                        userInfo.put("porte", porte);
                                        //userInfo.put("profileImageUrl", profileImageUrl);
                                        currentUserDb.updateChildren(userInfo);
                                        Toast.makeText(RegistrationAnimalActivity.this, "E-mail cadastrado!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegistrationAnimalActivity.this, "Selecione uma foto!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        sexo = "";
                        porte = "";
                        Toast.makeText(RegistrationAnimalActivity.this, "Selecione o sexo e porte!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegistrationAnimalActivity.this, "Digite todos campos!", Toast.LENGTH_SHORT).show();
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
            bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);  //aqui===================
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
