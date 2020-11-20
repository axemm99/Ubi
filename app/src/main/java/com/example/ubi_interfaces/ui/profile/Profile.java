package com.example.ubi_interfaces.ui.profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ubi_interfaces.R;
import com.example.ubi_interfaces.classes.Globals;
import com.example.ubi_interfaces.classes.User;
import com.example.ubi_interfaces.ui.profile.tabLayout.ProfileAchievments;
import com.example.ubi_interfaces.ui.profile.tabLayout.ProfilePerformances;
import com.example.ubi_interfaces.ui.profile.tabLayout.ViewPageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Profile extends Fragment {

    ImageView profilePic;
    TextView name, username;

    FirebaseFirestore db;
    FirebaseStorage fs;
    StorageReference sr;

    User currentUser;


    // Tab Layout
    TabLayout tabLayout;
    ViewPager viewPager;

    private ProfileAchievments profileAchievments;
    private ProfilePerformances profilePerformances;


    // https://www.youtube.com/watch?v=eXK4VMI9XLI
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Registar instancia da firestore
        db = FirebaseFirestore.getInstance();
        fs = FirebaseStorage.getInstance();
        sr = fs.getReference();

        /* Ir buscar o currentUser*/
        currentUser = Globals.getCurrentUser();
        Log.d("USer No Profile", currentUser.getName() + " --- " + currentUser.getId());


        // Registar os pfu...
        profilePic = root.findViewById(R.id.profilePic);
        name = root.findViewById(R.id.name);
        username = root.findViewById(R.id.username);


        /* Tab Layout */
        tabLayout = root.findViewById(R.id.tabLayout);
        viewPager = root.findViewById(R.id.viewPager);

        profileAchievments = new ProfileAchievments();
        profilePerformances = new ProfilePerformances();

        tabLayout.setupWithViewPager(viewPager);


        assert getFragmentManager() != null; // Não sei o que isto faz, mas não tenho o
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getFragmentManager(), 0);

        viewPageAdapter.addFragment(profileAchievments, "Achievements");
        viewPageAdapter.addFragment(profilePerformances, "Performances");

        viewPager.setAdapter(viewPageAdapter);

        try {
            /* Get Current User Profile information */
            db.collection("users").document(currentUser.getId())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d("Depois de ir à BD", currentUser.getName() + " --- " + currentUser.getId()+ " -- " + currentUser.getAchievements());
                            Log.d("SUCCESS", String.valueOf(documentSnapshot));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("GetUSer", e);
                        }
                    }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        Log.d("SUCCESS ONCOMplete", String.valueOf(task.getResult().getData()));
                        DocumentSnapshot doc = task.getResult();
                        User us = doc.toObject(User.class);
                        us.setAchievements(us.getAchievements());
                        currentUser.setName(us.getName());
                        name.setText(currentUser.getName());

                        /* Get user image, tem que ser destinguido se é:
                        * Imagem que o user tem do face -> começa com https://graph.facebook.com/
                        * Url Externo -> começa com http://
                        * Imagem que está na Storage não começa com http://
                        * if (us.getPicture().startsWith("https://graph.facebook.com")) {
                            Picasso.get().load("https://graph.facebook.com/" + currentUser.getId() + "/picture?type=normal").resize(50, 50).into(profilePic);
                        }
                        else
                        */
                        if (us.getPicture().startsWith("http")) {
                            Picasso.get().load("https://graph.facebook.com/" + currentUser.getId() + "/picture?type=normal").fit().centerCrop().into(profilePic);
                        }
                        else {
                            sr.child(us.getPicture()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Got the download URL for 'users/me/profile.png'
                                    Picasso.get().load(uri.toString()).fit().centerCrop().into(profilePic);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors, podiamos por uma imagem de erro também
                                    exception.printStackTrace();
                                }
                            });
                        }
                    }
                }
            });

        } catch (Exception ex) {
            // Podiamos mandar o user de volta para a página de Login
            Log.w("Error in profile", ex);
            ex.printStackTrace();
        }

        return root;
    }
}
