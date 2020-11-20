package com.example.ubi_interfaces.ui.profile.tabLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubi_interfaces.R;
import com.example.ubi_interfaces.classes.Achievement;
import com.example.ubi_interfaces.classes.Globals;
import com.example.ubi_interfaces.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ProfileAchievments extends Fragment {

    FirebaseFirestore db;

    RecyclerView rvAchiv;

    /* Porque era ganda ideia termos as imagens ou svg's dos ircones
    * dos achievments na Storage do Firebase */
    FirebaseStorage fs;

    List<Achievement> achievements = new ArrayList<>();
    RecyclerViewAchivements achivAdapter;

    User currentUser;
    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_profile_achievements, container, false);

        db = FirebaseFirestore.getInstance();
        rvAchiv = root.findViewById(R.id.rvProfileAchievements);

        rvAchiv.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(root.getContext());
        rvAchiv.setLayoutManager(llm);

        currentUser = Globals.getCurrentUser();

        db.collection("achievements")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d("Profile Achivs", "Done");

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Achievement achiv = document.toObject(Achievement.class);
                                Log.d("Profile Achivs", String.valueOf(achiv.getRanks().get(0)));

                                achievements.add(achiv);
                            }

                            db.collection("users").document(currentUser.getId())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            Log.d("SUCCESS ONCOMplete", String.valueOf(task.getResult().getData()));
                                            DocumentSnapshot doc = task.getResult();
                                            User uss = doc.toObject(User.class);
//                                            us.setAchievements(uss.getAchievements());
//                                            Log.d("USS!!! ", String.valueOf(us));

                                            achivAdapter = new RecyclerViewAchivements(root.getContext(), achievements, Globals.getCurrentUser(uss.getAchievements()));
                                            rvAchiv.setAdapter(achivAdapter);
                                        }
                                    });

                        }
                        else {
                            Log.w("Profile Achivs", "Erro ao receber documents"
                                    + task.getException());
                        }
                    }
                });
        return root;
    }
}
