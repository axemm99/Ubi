package com.example.ubi_interfaces.ui.profile.tabLayout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubi_interfaces.R;
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

public class RecyclerProfilePerformances extends RecyclerView.Adapter<RecyclerProfilePerformances.ViewHolder> {

    List<Performance> performances = new ArrayList<>();
    Context context;

    FirebaseStorage fs;
    StorageReference storageRef;

    public RecyclerProfilePerformances(Context context, List<Performance> performances) {
        this.performances  = performances;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerProfilePerformances.ViewHolder onCreateViewHolder(@NonNull ViewGroup vg, int i) {
        View itemView = LayoutInflater.from(vg.getContext()).inflate(R.layout.profile_performances_row, vg, false);
        return new RecyclerProfilePerformances.ViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull RecyclerProfilePerformances.ViewHolder vh, int position) {
        Performance perf = performances.get(position);

        fs = FirebaseStorage.getInstance();
        storageRef = fs.getReference();
//        index = position;

        final RecyclerProfilePerformances.ViewHolder auxVh = vh;

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
        String total = String.valueOf(perf.getParticipantsId() == null ? 0 : perf.getParticipantsId().size()) + "/" + String.valueOf(perf.getTotalParticipants());
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        Log.d("DATE", String.valueOf(vh.date) + String.valueOf(vh.location) + String.valueOf(vh.totalParticipants) + String.valueOf(vh.picture));
        vh.date.setText(sfd.format(perf.getDate()));
        vh.location.setText("Location " + perf.getLocation());
//        vh.reqPass.setText(perf.getReqPass() ? "Yes" : "No");
        vh.totalParticipants.setText(total);

    }

    @Override
    public int getItemCount() {
        return performances.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        /* aQUI É ONDE SE VÃO BUSCAR TODOS OS ELEMENTOS NECESSÁRIOS DO ui*/
        ImageView picture;
        TextView location, date, totalParticipants;

        public ViewHolder(final View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.profile_performance_image);
            location = itemView.findViewById(R.id.profile_performances_location);
            date = itemView.findViewById(R.id.profile_performances_date);
            totalParticipants = itemView.findViewById(R.id.profile_performance_totalParticipants);

        }

    }
}
