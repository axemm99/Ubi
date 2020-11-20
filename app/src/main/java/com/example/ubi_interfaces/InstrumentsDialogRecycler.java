package com.example.ubi_interfaces;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubi_interfaces.classes.Globals;
import com.example.ubi_interfaces.classes.Instrument;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class InstrumentsDialogRecycler extends RecyclerView.Adapter<InstrumentsDialogRecycler.ViewHolder>  {

    /* Instument List */
    List<Instrument> instruments;

    /* Previous Context */
    Context context;
    /* Dialog that shows this recycler view */
    Dialog dialog;

    TextView currentInst;

    /* FireStorage */
    FirebaseStorage fs;
    StorageReference sr;

    /* Constructor */
    public InstrumentsDialogRecycler(Context context, List<Instrument> instruments, Dialog dialog, TextView currentInst) {
        this.context = context;
        this.instruments = instruments;
        this.dialog = dialog;
        this.currentInst = currentInst;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup vg, int viewType) {
        View itemView = LayoutInflater.from(vg.getContext()).inflate(R.layout.recyclerview_instruments, vg, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder vh, int position) {
        final Instrument instrument = instruments.get(position);

        fs = FirebaseStorage.getInstance();
        sr = fs.getReference();

        /* NAME */
        vh.name.setText(instrument.getName());
        Log.d("Name", instrument.getName());
        /* ID (for later) */
        vh.instId.setText(String.valueOf(instrument.getInstrumentId()));

        if(instrument.getImage() != null) {
            sr.child("instruments/" + instrument.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Picasso.get().load(uri.toString()).resize(90, 80).into(vh.image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    exception.printStackTrace();
                }
            });
        }
        else {
            // Default image if there isnt one, TODO depois temos que mudar a default image
            sr.child("instruments/maintenance.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Picasso.get().load(uri.toString()).resize(90, 80).into(vh.image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    exception.printStackTrace();
                }
            });
        }

        vh.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView id = v.findViewById(R.id.instrument_id);
                Log.d("Instrument ID", id.getText().toString());

                for(int i = 0; i< instruments.size(); i++) {
                    if(instruments.get(i).getInstrumentId() == Integer.parseInt(id.getText().toString())) {
                        Globals.setInstrument(instruments.get(i));
                        Log.d("Instrument FINAL!!!", instruments.get(i).toString());
                        currentInst.setText(instruments.get(i).getName());
                    }
                }

                dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.instruments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, instId;
        LinearLayout container;

        public ViewHolder(final View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.instrument_image);
            name = itemView.findViewById(R.id.instrument_name);
            container = itemView.findViewById(R.id.instrument_container);
            instId = itemView.findViewById(R.id.instrument_id);
        }
    }
}
