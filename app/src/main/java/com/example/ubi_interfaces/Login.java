package com.example.ubi_interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ubi_interfaces.classes.Globals;
import com.example.ubi_interfaces.classes.User;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {

    TextView createAccount;
    EditText addPassword;
    EditText addEmail;
    Button login;
    TextView changePassword;

    /* Autenticações */
    FirebaseAuth fAuth;
    LoginButton login_button_facebook;
    CallbackManager callbackManager;
    LoginManager facebookLogin;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signinGoogle;

    /* Base de Dados */
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Base de Dados */
        db = FirebaseFirestore.getInstance();

        /* Get firebase instance */
        fAuth = FirebaseAuth.getInstance();

        /* Qualquer cena para o facebook */
        AppEventsLogger.activateApp(getApplication());
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create(); // Para que é que serve

        /* Qualquer cena para a autenticação pelo google */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        /* Ir buscar os elementos */
        addPassword = findViewById(R.id.addPassword);
        addEmail = findViewById(R.id.addEmail);
        changePassword = findViewById(R.id.changePassword);
        login = findViewById(R.id.login);
        createAccount = findViewById(R.id.createAccount);
        signinGoogle = findViewById(R.id.sign_in_button_google);

        // O resto tá nos Globals
        // Há uma class user para o face ou para o firebase
        User newUser = Globals.getCurrentUser();
        Log.d("User Ubi Interfaces", String.valueOf(newUser.getName()) + " -- " + newUser.getAuthType());
        Log.d("User Google", String.valueOf(GoogleSignIn.getLastSignedInAccount(this))); // Não estou a  conseguir fazer login por aqui

        // isto tem que ficar aqui porque é no login que começa a APP
        /* Apenas login com mail e password */
        Log.d("Globlas.GetCurrentUser", "Globals: " + String.valueOf(Globals.getCurrentUser().getId()));
        if (Globals.getCurrentUser().getId() != null){ // É sito que vou usar para saber se o user está autenticado pelo firebase
            Globals.goToActivity(getApplicationContext(), BottomNav.class);
            finish();

            // Fazer signOut
//                Log.d("Fazer Logout", "Logout");
//             FirebaseAuth.getInstance().signOut();
        }


        /* Apenas para o signin da google */
        signinGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.sign_in_button_google:
                        signIn();
                        break;
                }
            }
        });


        // If you are using in a fragment, call loginButton.setFragment(this); pode ser importante, mas não vamos usar num fragment
        // Só para o login com o facebook
        login_button_facebook = findViewById(R.id.login_button_facebook);
        login_button_facebook.setReadPermissions(Arrays.asList("email"));

        // Callback registration
        login_button_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.w("LoginActivity", response.toString());
                                /* Na response vem o
                                * id
                                * name
                                * email*/

                                // Application code
                                try {
                                    final String email = object.getString("email");
                                    final String name = object.getString("name");
                                    final String id = object.getString("id");
                                    Log.d("Login Facebook", loginResult.getAccessToken().getUserId() +
                                    " --- Id:" + id +
                                    " Email: "  + email +
                                    " Public profile: " + name);

                                    /* Ques esto */
                                    LoginManager logged = facebookLogin.getInstance();
                                    Log.d("Login Facebook", String.valueOf(logged));

                                    // New User
                                    DocumentReference docRef = db.collection("users").document(id);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()) {
                                                DocumentSnapshot doc = task.getResult();
                                                Log.d("Login Facebook", "Resultado -- " + doc.getData());

                                                if(doc.exists()) {
                                                    Log.d("Login facebook", "O utilizador já existe " + String.valueOf(doc.getData()));
                                                }
                                                else {
                                                    /* Guardar o user na Base de Dados */
                                                    Log.d("Login Facebook", "O User não existe por isso é preciso guarda lo na Basd de Dados");
                                                    // Adicionar o user à BD, mas primeiro confirmar se ele já não existe (pelo id).
                                                    Map<String, Object> newUserData = new HashMap<>();
                                                    newUserData.put("name", name);
                                                    newUserData.put("email", email);
//                                                    newUserData.put("id", id);
                                                    newUserData.put("achievments", new HashMap<String, String>());

                                                    // https://stackoverflow.com/questions/19855072/android-get-facebook-profile-picture
                                                    newUserData.put("picture", "https://graph.facebook.com/" + id + "/picture?type=normal"); // Aqui devia ser a fotografia que o user tem no face, mas ele pode mudar depois...
                                                    newUserData.put("performanceId", -1);
                                                    newUserData.put("authType", "facebook");
                                                    Log.d("Login Facebook", String.valueOf(newUserData));

                                                    db.collection("users").document(id)
                                                            .set(newUserData)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()) {
                                                                        Log.d("Login Facebook", "User Inserido na BD depois de login pelo Face !!!");
                                                                    }
                                                                    else {
                                                                        Log.d("ERROR Login Facebook", "O user Não fi Inserido !!!!");
                                                                    }
                                                                }
                                                            });

                                                }
                                            }
                                        }
                                    });

                                    Globals.goToActivity(getApplicationContext(), BottomNav.class);
                                } catch (Exception ex) {
                                    Log.w("Error Getting User Data", ex);
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // Evento para quando o user cancela a meio da autenticação
            }

            @Override
            public void onError(FacebookException exception) {
                Log.w("LoginFacebook", exception);
            }
        });

        /* Login com email e password ATENÇÂO: o firebase não guarda a pass ... Update: em texto e visivel, mas acaba por guardar.... */
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addEmail.getText().toString().isEmpty() && !addPassword.getText().toString().isEmpty()){
                    //Importante: https://firebase.google.com/docs/auth/web/manage-users
                    Log.d("Fields", "email: " + addEmail.getText().toString() + "\npasswrod: " + addPassword.getText().toString());

                    fAuth.signInWithEmailAndPassword(addEmail.getText().toString(), addPassword.getText().toString())
                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = fAuth.getCurrentUser();
//                                        Log.d("SUCCESS", "signInWithEmail:success" + user.toString());

                                        Globals.goToActivity(getApplicationContext(), BottomNav.class);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("FAILURE", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(Login.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(Login.this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* Só para ir para o signUp */
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, com.example.ubi_interfaces.Signup.class);
                startActivity(intent);
            }
        });
    } // Fim do OnCreate


    int RC_SIGN_IN = 0;

    @Override
    protected void onStart() {
        super.onStart();

        // Ir buscar o ultimo user que fez login pela google
        /* Daqui para baixo é só autenticação pela google
        * É isto que vou usar para saber se o user está autenticado pela google ..... MAs lastusersignin, e o current???? */
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Log.d("OnStart GoogleSignin", String.valueOf(account));
        //updateUI(account);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { // 0 = Sucesso

            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("Google account", String.valueOf(account));
            // Guardar o user na BD

            // Signed in successfully, show authenticated UI.
            Globals.goToActivity(getApplicationContext(), BottomNav.class);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
            e.printStackTrace();
            // updateUI(null);
        }
    }

    /* Funções auxiliares ao login com o face ou google */
    private void checkIfUserExists(String id) {

    }
}