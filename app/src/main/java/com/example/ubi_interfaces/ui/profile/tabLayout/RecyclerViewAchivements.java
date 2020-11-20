package com.example.ubi_interfaces.ui.profile.tabLayout;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubi_interfaces.R;
import com.example.ubi_interfaces.classes.Achievement;
import com.example.ubi_interfaces.classes.Globals;
import com.example.ubi_interfaces.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAchivements extends RecyclerView.Adapter<RecyclerViewAchivements.ViewHolder> {

    List<Achievement> achievments;
    User user = new User("ai", "u", "2", "fa");
    Context context;

    FirebaseFirestore db;

    FirebaseStorage fs;
    StorageReference storageRef;



    public RecyclerViewAchivements(Context context, List<Achievement> achievements, final User us) {
        this.context = context;
        this.achievments = achievements;
        this.user = us;
        db = FirebaseFirestore.getInstance();
        fs = FirebaseStorage.getInstance();
        storageRef = fs.getReference();

        Log.d("Achiv!!!!!!!!!User", String.valueOf(this.user));
//        db.collection("users").document(us.getId())
//            .get()
//            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    Log.d("SUCCESS ONCOMplete", String.valueOf(task.getResult().getData()));
//                    DocumentSnapshot doc = task.getResult();
//                    User uss = doc.toObject(User.class);
//                    us.setAchievements(uss.getAchievements());
//                    Log.d("USS!!! ", String.valueOf(us));
//                }
//            });

    }

    @NonNull
    @Override
    public RecyclerViewAchivements.ViewHolder onCreateViewHolder(@NonNull ViewGroup vg, int i) {
        View itemView = LayoutInflater.from(vg.getContext()).inflate(R.layout.achievments_column, vg, false);
        return new RecyclerViewAchivements.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAchivements.ViewHolder holder, int position) {
        Achievement achievement = achievments.get(position);
        Map<String, Integer> userA = new HashMap<>();
        boolean found = false;
        //SET IMAGE
        String rank="unranked";
        holder.achivId.setText("???");
        //Log.d("Achiv...User", String.valueOf(achievement) + " - ");
        if(this.user.getAchievements() != null) {
            for (Map.Entry<String, Integer> entry : this.user.getAchievements().entrySet()) {
                Log.d("Achiv...User", entry.getKey() + " - " + entry.getValue() + " - " + String.valueOf(achievement));

                if(entry.getKey().equals(String.valueOf(achievement.getAchivId()))) {
                    Log.d("Achiv12...User!!!", entry.getKey() + " - " + entry.getValue() + " - " + String.valueOf(achievement));
                    if(entry.getValue() >= achievement.getRanks().get(2).getGoal()){
                        rank = "r3";
                        holder.achivId.setText(String.valueOf(achievement.getRanks().get(2).getName()));
                    }
                    else if(entry.getValue() >= achievement.getRanks().get(1).getGoal()){
                        rank = "r2";
                        holder.achivId.setText(String.valueOf(achievement.getRanks().get(1).getName()));
                    }
                    else if(entry.getValue() >= achievement.getRanks().get(0).getGoal()){
                        rank = "r1";
                        holder.achivId.setText(String.valueOf(achievement.getRanks().get(0).getName()));
                    }
                }
            }
        }
        storageRef.child("achievements/id" + achievement.getAchivId()+"_"+rank+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).fit().centerCrop().into(holder.achivImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                exception.printStackTrace();
                Log.d("Dubuggings", "ERROR: Couldn't retrieve Image.");
            }
        });

        Log.d("Dubuggings", String.valueOf(achievement) + " -- " + String.valueOf(achievement.getAchivId()));


        //SET ID
        //holder.achivId.setText(String.valueOf(achievement.getAchivId()));





    }

    @Override
    public int getItemCount() {
        return this.achievments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView achivId;
        ImageView achivImage;
        public ViewHolder(View itemView) {
            super(itemView);

            achivImage = itemView.findViewById(R.id.achivImage);
            achivId = itemView.findViewById(R.id.achivId);
        }
    }
}
