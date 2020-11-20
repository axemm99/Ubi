package com.example.ubi_interfaces.ui.performances;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubi_interfaces.PlayPerformance;
import com.example.ubi_interfaces.R;
import com.example.ubi_interfaces.classes.Globals;
import com.example.ubi_interfaces.classes.Performance;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;



public class RecyclerPerformances extends RecyclerView.Adapter<RecyclerPerformances.ViewHolder> {

    private String rvAdapter = "RvAdapter";
    private List<Performance> performances;
    private List<Performance> performancesCopy = new ArrayList<Performance>();
    private Context context;
    private int index;

    private FirebaseStorage fs;
    private StorageReference storageRef;

    /* Dialog */
    public static Dialog dialog;

    public RecyclerPerformances(Context context, List<Performance> performances) {
        this.performances  = performances;
        this.context = context;
        this.performancesCopy.addAll(this.performances);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup vg, int i) {
        View itemView = LayoutInflater.from(vg.getContext()).inflate(R.layout.performances_row, vg, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int i) {
        Performance perf = performances.get(i);

        fs = FirebaseStorage.getInstance();
        storageRef = fs.getReference();
        this.index = i;
        final ViewHolder auxVh = vh;

        if(perf.getPicture() == null) {
            // Default image if there isnt one, TODO depois temos que mudar a default image
            storageRef.child("performances/bar.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Picasso.get().load(uri.toString()).resize(50, 50).into(auxVh.picture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    exception.printStackTrace();
                }
            });
        }
        else if(perf.getPicture().startsWith("http")){
            Picasso.get().load(perf.getPicture()).resize(50, 50).into(auxVh.picture);
        }
        else {
            storageRef.child("performances/" + perf.getPicture()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Picasso.get().load(uri.toString()).resize(50, 50).into(auxVh.picture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    exception.printStackTrace();
                }
            });

        }

        // Mostrar uma data mais adequada
        String total = String.valueOf(perf.getParticipantsId() == null ? 0 : perf.getParticipantsId().size()) + "/" + perf.getTotalParticipants();
        Log.d("TOTAL!!!!!!!", total);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        vh.date.setText(sfd.format(perf.getDate()));
        vh.location.setText(String.format("Location %s", perf.getLocation()));
        vh.reqPass.setText(perf.getReqPass() ? "Yes" : "No");
        vh.totalParticipants.setText(total);
        vh.btnParticipate.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = new Integer(index);

                Log.d("Button goToPerf", performances.get(position).getLocation()+ " -- " + position);
                if(performances.get(position).getReqPass()) {
                    Globals.perf = performances.get(position);

                    showDialog();
                }
                else {
                    Log.d("Button goToPerf !!!!!", performances.get(position).getLocation() + " -- " + position);

                    Globals.perf = performances.get(position);

                    goToPerformance(performances.get(position));

                    // Falta fazer Udpate À lista de performances
                    // Enviar para o ecrã de tocar perfomance
                    Log.d("btn performnce!!!", performances.get(position).getReqPass().toString() + " - " + performances.get(position).getPassword());
                }
              }
        }));
    }
    @Override
    public int getItemCount() {
        return performances.size();
    }

    public void filterPerformances(String text) {
        List<Performance> newList = new ArrayList<Performance>();
        if(!text.isEmpty()) {
            performances.clear();


            for(Performance perf : performancesCopy) {
               if(perf.getLocation().toUpperCase().contains(text.toUpperCase())) {
                    performances.add(perf);
               }
            }
        }
        else {
            performances.addAll(performancesCopy);
        }
        notifyDataSetChanged();
    }
    // Ir para uma performance
    public void goToPerformance(Performance perf) {

        Log.d("Extras Perf Id", perf.getId());

        // Esta funcção pode dar jeito porque vai ser preciso enviar informação para a página de play performance
        Intent playPerf = new Intent(context, PlayPerformance.class);
        playPerf.putExtra("id", perf.getId());
        playPerf.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Sem isto vai dar um erro
        context.startActivity(playPerf);
    }

    public void insertUserInPerformance(FirebaseFirestore db) {
        /* Inserir aqui o user na performance */


    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView date, reqPass, totalParticipants; // Por agora é só esta
        protected ImageView picture;
        protected Button btnParticipate;
        protected TextView location;
        // Usei aqui o final para poder usar ao clicar no participante
        // Mas não sei é grande ideia ter um final num parametro
        public ViewHolder(final View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.hora);
            picture = itemView.findViewById(R.id.performacePicture);
            btnParticipate = itemView.findViewById(R.id.participate);
            location = itemView.findViewById(R.id.localization);
            reqPass = itemView.findViewById(R.id.reqPassText);
            totalParticipants = itemView.findViewById(R.id.totalParticipants);
        }

    }

    public void showDialog() {
        dialog = new Dialog(context);
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.password_dialog);

        final EditText pass = dialog.findViewById(R.id.passCheck);



        /* Confrim */
        Button confirmDialogBtn = dialog.findViewById(R.id.confirm);
        confirmDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Log.d("Pass check", "PassVal: " + pass.getText().toString().equals("") + "/ Per pass: " + Globals.perf.getPassword() + " ---- " + String.valueOf(pass));

                        if(pass.getText().toString().equals("")) {
                                pass.setError("Required");
                        }
                        else if (pass.getText().toString().equals(Globals.perf.getPassword())) {
                            /* campeão */
                            Log.d("Campeão", "champ");
                            goToPerformance(Globals.perf);
                        }
                        else {
                                pass.setError("Not valid!!!!!");
                        }

                    }

        });

        /* Cancel */
        Button cancelDialogBtn = (Button) dialog.findViewById(R.id.cancel);
        cancelDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

//        RecyclerView recyclerView = dialog.findViewById(R.id.recycler);
//        AdapterRe adapterRe = new AdapterRe(MainActivity.this,myImageNameList);
//        recyclerView.setAdapter(adapterRe);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

//        recyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        dialog.show();
    }
}
