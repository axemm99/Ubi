package com.example.ubi_interfaces;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ubi_interfaces.classes.Globals;
import com.example.ubi_interfaces.classes.Performance;
import com.example.ubi_interfaces.ui.performances.PerformancesActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;


public class CreatePerformance extends Fragment {

    private static final int CAMERA = 1;
    private static final int GALLERY = 0;
    Button saveNewPerf;
//    Globals globals;


    // Inputs
    EditText location, maxCapacity, timeStart, accessCode;
    Switch accessCodeSwitch;
    Calendar myCalendar;
    String imageNameGlobal = "";

    ImageView imageView;

    Button pickImage;

    // Firestore
    FirebaseFirestore db;

    // Firabase Storage
    FirebaseStorage fs;
    StorageReference imageRef;
    // Porque é um fragment
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_create_performance, container, false);

        db = FirebaseFirestore.getInstance();
        fs = FirebaseStorage.getInstance();




        location = root.findViewById(R.id.setLocation);
        maxCapacity = root.findViewById(R.id.maxCapacity);
        timeStart = root.findViewById(R.id.timeStart);
        accessCode = root.findViewById(R.id.accessCode);
        imageView = root.findViewById(R.id.selectedImage);

        // Calendário
        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Log.d("Calendar", String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear) + "/" + String.valueOf(year));
                updateLabel(String.valueOf(dayOfMonth), String.valueOf(monthOfYear), String.valueOf(year));
            }

        };

        timeStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Arranjar maneira de ter uma confirmação ao input que só deixasse por numeros e a cada dois numeros meter um "/"
                new DatePickerDialog(root.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        // Listener para clicar no botão.
        saveNewPerf = (Button) root.findViewById(R.id.createPerformance);
        saveNewPerf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Gravar a performance e sair
                // Aqui devia mandar alguma informação
                String locationValue = location.getText().toString(),
                accessCodeValue = accessCode.getText().toString(), maxCapacityValue = maxCapacity.getText().toString();
                Calendar date = Calendar.getInstance();
                try {
                    String[] dateArr = timeStart.getText().toString().split("/");

                    date.set(Calendar.YEAR, Integer.parseInt(dateArr[2]));
                    date.set(Calendar.MONTH, Integer.parseInt(dateArr[1]));
                    date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArr[0]));
                } catch (Exception ex) {
                    Log.e("Date Error", ex.toString());
                }

                Log.d("PASSOW ??? ", "Só para ver se passou - " + String.valueOf(accessCodeSwitch.isChecked()) + " - " + accessCodeValue + " - " + maxCapacityValue.toString());

                /*Max capacity*/
                if(maxCapacityValue.equals("")) {
                    maxCapacity.setError("Required... Hint: 999");
                    return;
                }
                /*password*/
                else if(accessCodeSwitch.isChecked() && accessCodeValue.equals("")) {
                    accessCode.setError("Needs to have a password");
                    return;
                }
                else {


                    // Default image once upon a time
                    // "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fcdn.vox-cdn.com%2Fthumbor%2FFgiZSpHjp1vcKkV0PrdRppJszhA%3D%2F0x0%3A960x960%2F1200x800%2Ffilters%3Afocal(404x404%3A556x556)%2Fcdn.vox-cdn.com%2Fuploads%2Fchorus_image%2Fimage%2F58799523%2F14915318_10155148305236754_7471955098066766739_n.0.png&f=1&nofb=1",
                    Performance newPerf = new Performance(new Timestamp(new Date(date.getTime().toString())),
                            accessCodeSwitch.isChecked(),
                            Integer.parseInt(maxCapacityValue),
                            locationValue,
                            imageNameGlobal == null || imageNameGlobal == "" ? "bar.jpg" : imageNameGlobal, // bar.jpg default para quando não há foto
                            accessCodeValue,
                            new ArrayList<String>());

                    Log.d("savePerf",
                            "Location: " + newPerf.getLocation() +
                                    "\n accessCodeSwitch: " + newPerf.getReqPass() +
                                    "\n accessCode: " +  newPerf.getPassword() +
                                    "\n timeStart: " + newPerf.getDate()); // Os valores estão corretos.


                    Map<String, Object> performance = new HashMap<>();

                    performance.put("active", false);
                    performance.put("adminId", "999"); //Ainda é preciso tratar do utilizador direito para fazer esta parte
                    performance.put("date", newPerf.getDate());
                    performance.put("createDate", Timestamp.now());
//                performance.put("duration", 999); Isto é um campo para meter depois?
//                performance.put("id", 999);
                    performance.put("location", newPerf.getLocation());
                    performance.put("picture", newPerf.getPicture());
                    performance.put("password", newPerf.getReqPass() ? newPerf.getPassword() : "");
                    performance.put("participantsId", newPerf.getParticipantsId()); // Vai ser sempre vazio mas é preciso mandar isto
                    performance.put("totalParticipants", newPerf.getTotalParticipants());


                    // Guardar a performance na Base de dados
                    db.collection("performances").document()
                            .set(performance)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // E isto só devia acontecer se tudo for gravado com sucesso
                                    Globals.goToFragment(new PerformancesActivity(), getFragmentManager());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("firestore", e.toString());
                            e.printStackTrace();
                        }
                    });

                }
            }
        });

        accessCodeSwitch = root.findViewById(R.id.accessCodeSwitch); //.isChecked();
        accessCode.setEnabled(accessCodeSwitch.isChecked());

        accessCodeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessCode.setEnabled(accessCodeSwitch.isChecked());
            }
        });


        // Botão de GoBack
        Button goBack = root.findViewById(R.id.goBackCreatePerformance);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.goToFragment(new PerformancesActivity(), getFragmentManager());
            }
        });


        // Escolher uma imagem (https://demonuts.com/pick-image-gallery-camera-android/)
        pickImage = root.findViewById(R.id.browseImage);

        // Inicio de escolher uma imagem
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageDialog();
            }

        });

        return root;
    }

    // Activity Result da escolha do user sobre de onde ir buscar a imagem
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // Alterei o valore da CAMERA para 1, se der merda pode ser por causa disso

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    // https://www.programcreek.com/java-api-examples/android.provider.MediaStore
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }


    // Mostrar o dialog com as opções para ir buscar a imagem
    private void pickImageDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");

        String[] dialogOptions = {
                "Select from gallery",
                "Take a picture"
        };
//        if(hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
//
//        }
        pictureDialog.setItems(dialogOptions,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0:
                                chooseFromGallery();
                                break;
                            case 1:
                                takePicture();
                                break;
                        }
                    }
                });

        pictureDialog.show();
    }

    // Funções para cada escolha do user sobre a imagem
    private void chooseFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePicture() {
        // Vou ter que usar o Dexter aqui provavelmeinte, para pedir permissão para a camera
        try {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);
        } catch (Exception ex) {
            Log.w("ohhhhhhhhhhhhhhhhhhSNAP", ex);
            ex.printStackTrace();
        }
    }

    // Guardar a imagem na BD Storage
    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);


        try {
            final String imageName = "perfimage_" + Calendar.getInstance().getTimeInMillis() + ".jpg";
            imageRef = fs.getReference("performances/" + imageName);
            // Get the data from an ImageView as bytes
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.w("ERro gravar imagem 1º!!", exception);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    Log.d("imageResultDetails", String.valueOf(taskSnapshot.getMetadata()));
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            imageNameGlobal = imageName;
                            Picasso.get().load(uri.toString()).resize(50, 50).into(imageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            imageNameGlobal = imageName;
                            Log.w("ERro gravar imagem 2º!!", exception);
                        }
                    });
                }
            });
            return imageName;
        } catch (Exception e1) {
            e1.printStackTrace();
    }
        return "";
    }

    // Mudar o texto do timeStart para a data recebida dp calendário
    private void updateLabel(String day, String month, String year) {
        // Mudar o valor do EditText (TimeStart)
        timeStart = root.findViewById(R.id.timeStart);
        month = String.valueOf(Integer.parseInt(month) + 1);
        timeStart.setText(day + "/" + month + "/" + year);
        Log.d("DATE updateLabel !!!", String.valueOf(new Date( month + "/" + day + "/" + year)));
    }
}
