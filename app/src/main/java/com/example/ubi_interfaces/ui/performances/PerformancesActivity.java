package com.example.ubi_interfaces.ui.performances;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.ubi_interfaces.R;
import com.example.ubi_interfaces.classes.Globals;
import com.example.ubi_interfaces.classes.Instrument;
import com.example.ubi_interfaces.classes.Performance;
import com.example.ubi_interfaces.CreatePerformance;
import com.example.ubi_interfaces.classes.User;
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

public class PerformancesActivity extends Fragment {
    View root;

    FirebaseFirestore db;

    RecyclerView rvPerf;
    private List<Performance> performances = new ArrayList<Performance>();
    /* Grab RecyclerView */
    private RecyclerPerformances perfAdapter;

    final private String perfTag = "perfTag";

    private PerformancesViewModel performancesViewModel;

    SearchView searchBox;
    User currentUser;
    public View onCreateView(@NonNull LayoutInflater inflater,
                                ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_performances, container, false);

        db = FirebaseFirestore.getInstance();

        // Write a message to the database
//        DatabaseReference myRef = database.getReference("message");
//        myRef.setValue("Hello, World!");

        // Catch Recycler View
        rvPerf = root.findViewById(R.id.rvPerf);
        rvPerf.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(root.getContext());
        rvPerf.setLayoutManager(llm);

        // Read from database
        // Vai ter que ser um filtro pelas que o user criou ou participou, e vamos ter que distinguir las
        db.collection("performances")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if(task.isSuccessful()) {
                           for (QueryDocumentSnapshot document : task.getResult()) {
                               Log.d("task", document.getId());
                               Performance perf = document.toObject(Performance.class);

                                Log.d("Dates Comparing", "Date: " + new Timestamp(perf.getDate()).toDate() + " ---- " + Timestamp.now().toDate().toString() + " --- "
                                        + (Long.parseLong(String.valueOf(new Timestamp(perf.getDate()).getSeconds())) > Long.parseLong(String.valueOf(Timestamp.now().getSeconds()))));

                               if(Long.parseLong(String.valueOf(new Timestamp(perf.getDate()).getSeconds())) > Long.parseLong(String.valueOf(Timestamp.now().getSeconds()))){
                                   perf.setId(document.getId());
                                   performances.add(perf);
                               }
                           }
                           perfAdapter = new RecyclerPerformances(root.getContext(), performances);

                           rvPerf.setAdapter(perfAdapter);
                       } else {
                           Log.w(perfTag, "Erro ao receber documents"
                           + task.getException());
                       }
                    }
                });

        final Button createPerf = root.findViewById(R.id.goToPerformancePage);
        createPerf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPerformance();
            }
        });

        final ImageView goBack = (ImageView) root.findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLogin();
            }
        });

        searchBox = root.findViewById(R.id.search);
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                perfAdapter.filterPerformances(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                perfAdapter.filterPerformances(newText);
                return false;
            }
        });

        /* Get All Instruments in the begining of the app*/
        getInstruments();

        return root;
    }



    public void getInstruments() {
        db.collection("instruments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Globals.instruments.clear(); // Limpar o array
                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                Instrument instr = doc.toObject(Instrument.class);
                                Log.d("Instrumentos", instr.getName());
                                Globals.instruments.add(instr);
                            }
                        }
                    }
                });
    }

    public void createPerformance() {

        FragmentManager fragM = getFragmentManager();
        Globals.goToFragment(new CreatePerformance(), fragM);
    }
    //Mudar o nome da funcção
    // Nem sempre vai ser para ir para o login....
    public void goLogin() { // é como se fosse um go back
        //Criar uma class global que tenha funções que vão ser usadas muitas vezes em diferentes sitios, como ir para a homePage, neste caso vai para o login porque testes...
//        Intent goLogin = new Intent(root.getContext(), MainActivity.class);
//        startActivity(goLogin);
        Log.d("Quem????", "Oh pá ta calado \n Joao Campos (99)");
    }
}
