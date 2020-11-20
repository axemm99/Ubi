package com.example.ubi_interfaces;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ubi_interfaces.classes.Globals;
import com.example.ubi_interfaces.ui.performances.PerformancesActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SettingsAccount extends Fragment {
    View root;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    LoginManager fbUser;
    PopupWindow confirm;
    boolean confirmLogout = true;

    RelativeLayout logout;
    Button confirmChanges, goBack;

    EditText username, confirmPass, newPass;
     public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container, Bundle savedInstanceState) {
         root = inflater.inflate(R.layout.fragment_settings_account, container, false);



         // --------------------------------------------
         // Firebase USer
         fAuth = FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();
         logout = root.findViewById(R.id.logoutBtn);

         // Logout Button
         logout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                // Pop Up
                 confirm = new PopupWindow(getContext());
                 LinearLayout placeholder = new LinearLayout(getContext());
                 LinearLayout buttons = new LinearLayout(getContext());

                 TextView message = new TextView(getContext());
                 message.setText("Do you really WANT IT");

                 Button yes = new Button(getContext()), no = new Button(getContext());
                 yes.setText("YES");
                 no.setText("NO");
                 yes.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {

                         // Fazer signOut pelo Facebook, working
                         if(LoginManager.getInstance() != null) {
                             LoginManager.getInstance().logOut();
                         }

                         // Fazer signOut pelo firebase (email, password)
                         if (fAuth != null) {
                             fAuth.signOut();
                         }


                         // Enviar para a página de login
                         Globals.goToActivity(getContext(), Login.class);
                         confirm.dismiss();
                     }
                 });
                 no.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         confirmLogout = false;
                         confirm.dismiss();
                     }
                 });

                 ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                 placeholder.setOrientation(LinearLayout.VERTICAL);
                 placeholder.addView(message, params);

                 buttons.setOrientation(LinearLayout.HORIZONTAL);
                 buttons.addView(no, params);
                 buttons.addView(yes, params);
                 placeholder.addView(buttons, params);

                 confirm.setContentView(placeholder);

                 // Show Pop Up
                 confirm.showAtLocation(placeholder, Gravity.BOTTOM, 10, 10);
             }
         });


         // Save Profile Changes Button
         confirmChanges = root.findViewById(R.id.confirmButton);
         confirmChanges.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 username = root.findViewById(R.id.usernameSettings);
                 confirmPass = root.findViewById(R.id.confirmPasswordSettings);
                 newPass = root.findViewById(R.id.newPasswordSettings);

                 String name = username.getText().toString(),
                         confPass = confirmPass.getText().toString(),
                         nPass = newPass.getText().toString();
                if(!name.isEmpty() || !confPass.isEmpty() || !nPass.isEmpty()) {
                        // Só dá para alterar o username, e pass?
                        Map<String, Object> user = new HashMap<>();
                        if(!name.isEmpty()) user.put("name", name);
//                        if(!confPass.isEmpty() && !nPass.isEmpty())

                        db.collection("users").document(fAuth.getUid())
                                .update(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                }
                else {
                    Snackbar.make(root.findViewById(R.id.accountSettings),"Pelo menos um campo tem que estar preenchido",
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
             }
         });

         goBack = root.findViewById(R.id.imageView4);
         goBack.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Globals.goToFragment(new PerformancesActivity(), getFragmentManager());
             }
         });
         return root;
    }
}
