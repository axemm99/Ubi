package com.example.ubi_interfaces.classes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ubi_interfaces.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.internal.instrument.InstrumentUtility;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

// Também é  preciso importar a da __google__

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Globals {

    private static final String logTag = "Globals Tag";
    public static Instrument instrument = new Instrument();
    public static List<Instrument> instruments = new ArrayList<>();

    // Vai ser preciso mandar o contexto de certas páginas para aqui
    private Context ctx;

    //Inicializar sem nada
    public void Globals() {
    }

    public void Globals(Context ctx) {

    }

    public static Performance perf;

    public static void goToActivity(Context ctx, Class targetClass) {
        Intent goback = new Intent(ctx, targetClass);
        goback.setFlags(FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(goback);
    }

    // Como substituir o fragment Manager
    public static void goToFragment(Fragment TargetClass, FragmentManager manager)  {
        FragmentManager fragmentManager = manager;
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, TargetClass);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
//    public User setUser(User user) {
//        return user;
//    }

    //Não tou a coseguit.........................................................................................................
    public static User getCurrentUser(Map<String, Integer>... achis) {
        /* Get firebase instance */
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();
        /* Get Facebook user (if any) */
        Profile fbUser = Profile.getCurrentProfile();

        FirebaseFirestore db;

        User user;
        String id = null, email = null, name = null, authType = null;


        Log.d("User firebase Globals", String.valueOf(fUser)); // Depois de estar logado isto fica preenchido (fAuth.getCurrentUserProfile)
        Log.d("User Facebook Globals", String.valueOf(Profile.getCurrentProfile())); // Depois de estar logado isto fica preenchido (Profile.getCurrentProfile)

        /* Get user from firebase (if any) */
        if(fAuth.getCurrentUser() != null) {
            Log.d("User firebase Globals", "é firebase " + String.valueOf(fUser));
            id = fUser.getUid();
            email = fUser.getEmail();
            name = fUser.getDisplayName();
            authType = "firebase";
        }
        /* Get USer Facebook */
        else if(fbUser != null) {
            Log.d("User facebook Globals", "é facebook " + String.valueOf(fbUser.getId()) + " -- " + fbUser.getFirstName() + " -- " + fbUser.getName()); // Não tem email...
            id = fbUser.getId();
            name = fbUser.getFirstName();
            authType = "facebook";
            /* Para o email devo precisar de usar o graphRequest */
        }

        db = FirebaseFirestore.getInstance();
        boolean done = false;
        boolean started = false;
//        final User[] anodaOne = new User[1]; que caralhos é isto...

        return new User(id, email, name, authType, achis.length != 0 ? achis[0] : null);
    }

    public static Instrument currentInstrument() {
        Log.d("Instrument Globals", String.valueOf(Globals.instrument));
        return Globals.instrument;
    }

    public static void setInstrument(Instrument inst) {
        Globals.instrument = inst;
    }
}
