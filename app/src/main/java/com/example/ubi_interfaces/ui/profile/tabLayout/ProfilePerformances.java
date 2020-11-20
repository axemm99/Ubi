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
import com.example.ubi_interfaces.classes.Globals;
import com.example.ubi_interfaces.classes.Performance;
import com.example.ubi_interfaces.classes.User;
import com.example.ubi_interfaces.ui.profile.tabLayout.RecyclerProfilePerformances;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfilePerformances extends Fragment {

    FirebaseFirestore db;
    RecyclerView rvPerf;

    List<Performance> performances = new ArrayList<>();
    RecyclerProfilePerformances perfAdapter;

    User currentUser;

    View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile_performances, container, false);

        db = FirebaseFirestore.getInstance();
        rvPerf = root.findViewById(R.id.rvProfilePerformances);

        rvPerf.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(root.getContext());
        rvPerf.setLayoutManager(llm);

        currentUser = Globals.getCurrentUser();
        // Read from database
        Log.d("USER", String.valueOf(currentUser));
        db.collection("performances")
                .whereEqualTo("adminId", currentUser.getId().toString())
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            // Working til here...
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                               Log.d(perfTag, document.getId() + "=>"
//                               + document.getData());
                                    Performance perf = document.toObject(Performance.class);

                                Log.d("Dates Comparing", "Date: " + new Timestamp(perf.getDate()).toDate() + " ---- " + Timestamp.now().toDate().toString());
                                performances.add(perf);
                            }
                            Log.d("ProfilePerformance.size", "" + performances.size());

                            db.collection("performances")
                                    .whereArrayContainsAny("participantsId", Arrays.asList(currentUser.getId().toString()))
                                    .orderBy("date", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Performance perf2 = document.toObject(Performance.class);
                                                        performances.add(perf2);
                                                }
                                                Log.d("ProfilePerformance.size", "" + performances.size());

                                                perfAdapter = new RecyclerProfilePerformances(root.getContext(), performances);

                                                //perfAdapter = new RecyclerPerformances(performances);
                                                rvPerf.setAdapter(perfAdapter);
                                            }
                                        }
                                    });
                        } else {
                            Log.w("Profile Performances!!!", "Erro ao receber documents"
                                    + task.getException());
                        }
                    }
                });
        return root;
    }
}
