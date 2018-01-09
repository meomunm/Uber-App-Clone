package techkids.vn.uberclone;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.w3c.dom.Text;

import techkids.vn.uberclone.models.User;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.toString();
    Button btnRegister, btnSignIn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setFont("fonts/Arkhip_font.ttf");
        this.init();
    }
    private void setFont(String fontPath){
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(fontPath)            //thay thế font chữ mặc định thành font chữ mới step 2
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    @Override //thay thế font chữ mặc định thành font chữ mới step 1
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void init() {
        btnRegister = findViewById(R.id.btn_register);
        btnSignIn = findViewById(R.id.btn_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference("Users");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_in:
                Log.d(TAG, "onClick: in button sign in");
                showSignInDialog();
                break;
            case R.id.btn_register:
                Log.d(TAG, "onClick: in button register");
                showRegisterDialog();
                break;
        }
    }

    private void showSignInDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use email to sign in");

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View signInLayout = layoutInflater.inflate(R.layout.layout_sign_in, null);

        final MaterialEditText edtPasswordSignIn = signInLayout.findViewById(R.id.edt_sign_in_password);
        final MaterialEditText edtMailSignIn = signInLayout.findViewById(R.id.edt_sign_in_email);

        dialog.setView(signInLayout);
        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //xử lý input khi register account
                if (TextUtils.isEmpty(edtMailSignIn.getText().toString())) {
                    Snackbar.make(findViewById(R.id.root_layout), "Please enter your email", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (edtPasswordSignIn.getText().length() < 6) {
                    Snackbar.make(findViewById(R.id.root_layout), "Password too short!!!", Snackbar.LENGTH_SHORT).show();
                }
                firebaseAuth.signInWithEmailAndPassword(edtMailSignIn.getText().toString().trim(), edtPasswordSignIn.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(MainActivity.this, Welcome.class));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(findViewById(R.id.root_layout), "Login fail " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                Log.d(TAG, "onFailure: login fail - "+e.getMessage());
                            }
                        });
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    private void showRegisterDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email to register");

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View register_layout = layoutInflater.inflate(R.layout.layout_register, null);

        final MaterialEditText edtNameRegister = register_layout.findViewById(R.id.edt_register_name);
        final MaterialEditText edtPhoneRegister = register_layout.findViewById(R.id.edt_register_phone);
        final MaterialEditText edtPasswordRegister = register_layout.findViewById(R.id.edt_register_password);
        final MaterialEditText edtMailRegister = register_layout.findViewById(R.id.edt_register_email);

        dialog.setView(register_layout);
        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //xử lý input khi register account
                if (TextUtils.isEmpty(edtMailRegister.getText().toString())) {
                    Snackbar.make(findViewById(R.id.root_layout), "Please enter your email", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (edtPasswordRegister.getText().length() < 6) {
                    Snackbar.make(findViewById(R.id.root_layout), "Password too short!!!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtNameRegister.getText().toString())) {
                    Snackbar.make(findViewById(R.id.root_layout), "Please enter your name", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtPhoneRegister.getText().toString())) {
                    Snackbar.make(findViewById(R.id.root_layout), "Please enter your phone", Snackbar.LENGTH_SHORT).show();
                }
                // Xác thực đăng nhập với google authentication
                firebaseAuth.createUserWithEmailAndPassword(edtMailRegister.getText().toString(), edtPasswordRegister.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //Nếu đăng nhập thành công thì save user vào firebase database
                                User user = new User(edtMailRegister.getText().toString(), edtPasswordRegister.getText().toString(), edtNameRegister.getText().toString(), edtPhoneRegister.getText().toString());
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(findViewById(R.id.root_layout), "Register success fully !!!", Snackbar.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(findViewById(R.id.root_layout), "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(findViewById(R.id.root_layout), "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();

        Log.d(TAG, "showRegisterDialog: show register dialog");
    }
}
