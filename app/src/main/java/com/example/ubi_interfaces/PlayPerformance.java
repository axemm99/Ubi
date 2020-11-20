package com.example.ubi_interfaces;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubi_interfaces.classes.Globals;
import com.example.ubi_interfaces.classes.Performance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class PlayPerformance extends AppCompatActivity {

    String logTag = "Log Tag";
    LinearLayout checkTouch;
    private String socketLabelError = "Error in Socket";
    int contador = 0;
    TextView numberOfUsers, currentInst;
    /* Usar uma imageView para mostrar o instrumento selecionado */
    Button chooseInstrument;
    ImageView imageView;

    /* Firebase */
    FirebaseFirestore db;

    // Socekt
    private Socket socket;
    private String uri = "http://192.168.1.4:3001";
    private String username = "user1";
    private String perfId;

    private Performance performance;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_performance);

        db = FirebaseFirestore.getInstance();
        numberOfUsers = findViewById(R.id.numberOfUsers);
        chooseInstrument = findViewById(R.id.instruments);
        currentInst = findViewById(R.id.current_instrument);
        currentInst.setText(Globals.instrument == null ? "None" : Globals.instrument.getName());

        imageView = findViewById(R.id.imageView8);
//        imageView.getBackground().setAlpha(45);

        /* Get Performance Information */
        perfId = getIntent().getStringExtra("id");
        db.collection("performances").document(perfId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();

                        performance = doc.toObject(Performance.class);

                        /* Para não ser null \*/
                        assert performance != null;
                        performance.setId(doc.getId());

                        Log.d("PErf Play Perf!", performance.getLocation() + " -- " + performance.getId());
                    }
                });
        // Inicializar Socket
        try {
            socket = IO.socket(uri);
        } catch (Exception ex) {
            Log.w(socketLabelError, ex);
            ex.printStackTrace();
        }

        Map<String, Object> updatePerf = new HashMap<>();
        List<String> newId = Globals.perf.getParticipantsId();
        Boolean save = true;
        for(String id : Globals.perf.getParticipantsId()) {
            if(Globals.getCurrentUser().getId().equals(id)) {
                save = false;
            }
        }

        if(save) {
            newId.add(Globals.getCurrentUser().getId());
            updatePerf.put("participantsId", newId);
            db.collection("performances").document(perfId)
                    .update(updatePerf)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("TA", "MOS");

                            if(task.isSuccessful()) {
                                Log.d("Play", "Funcionou");
                            }
                        }
                    });
        }

        /* Sitio para tocar */
        checkTouch = findViewById(R.id.playit);

        // Connectar
        socket.connect();

        // Avisar quem entrou, Vai ser substituido pelo nome do utilizador
        socket.emit("join", Globals.getCurrentUser().getId());

        /* Aqui é onde estamos à escuta de Eventos
        * Depois deve poder ser manda para fora do onCreate */
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
            }
        }).on("newConnection", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    db.collection("perfromances").document(Globals.perf.getId())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();
                                        Performance perf = doc.toObject(Performance.class);
                                        Globals.perf = perf;

                                        Boolean save = true;
                                        for(String id : perf.getParticipantsId()) {
                                            if(Globals.getCurrentUser().getId().equals(id)) {
                                                save = false;
                                            }
                                        }

                                        if(save) {
                                            Map<String, Object> updatePerf = new HashMap<>();
                                            List<String> newId = Globals.perf.getParticipantsId();
                                            newId.add(Globals.getCurrentUser().getId());
                                            updatePerf.put("participantsId", newId);
                                            db.collection("performances").document(perfId)
                                                    .update(updatePerf)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Log.d("TA", "MOS");

                                                            if(task.isSuccessful()) {
                                                                Log.d("Play", "Funcionou");
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                }
                            });

                } catch (Exception ex) {
                    Log.w("Error !!!", ex);
                    ex.printStackTrace();
                }
            }
        }).on("totalUsers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    String nUsers = String.valueOf(args);
                    Log.d("Number of USers", nUsers + " -- " + Arrays.toString(args) + " -- " + args[0].toString());


                } catch (Exception ex) {
                    Log.w("Error !!!", ex);
                    ex.printStackTrace();
                }
            }
        });

        // Listener para o toque
        checkTouch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent eve) {

                int x = (int) eve.getX();
                int y = (int) eve.getY();
                switch (eve.getAction()) {
                    // Para quando se arrasta o dedo
                    case MotionEvent.ACTION_MOVE:
                        contador++;

                        if(contador > 5) { // Para determinar o "ritmo" podemos fazer por contador ou por timeouts mas JAVA.....
                            contador = 0;
                            Log.d("notas", x*0.1 + ":" + y *0.1);
                            socket.emit("musicnote", x *0.1 + ":" + y* 0.1);
                        }
                    break;
                        // Estes os dois ainda não tenho a certeza de como funceminam
                    case MotionEvent.ACTION_DOWN:
                        contador++;
                        break;
                    case MotionEvent.ACTION_UP:
                        contador++;
                    break;
//                    default:
//                        throw new IllegalStateException("Unexpected value: " + eve.getAction());
                }

                // Só returnando true é que é possivel detetar o arraste de dedo
                return true;
            }
        });
        Button goBack = findViewById(R.id.goPerfs);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.goToActivity(getApplicationContext(), BottomNav.class);
            }
        });
        /* Show instruments Dialog */
        chooseInstrument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog choose = new Dialog(PlayPerformance.this);
                choose.setCancelable(false);
                choose.setCanceledOnTouchOutside(true);
                choose.setContentView(R.layout.instruments_dialog);

                RecyclerView recyclerView = choose.findViewById(R.id.instruments_recycler);
                InstrumentsDialogRecycler adapterRe = new InstrumentsDialogRecycler(PlayPerformance.this, Globals.instruments, choose, currentInst);
                recyclerView.setAdapter(adapterRe);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

                choose.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                /* Desenhar Linhas */
                Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), 5, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(Color.WHITE);
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(8);
                paint.setAntiAlias(true);
                int offset = 50;
                canvas.drawLine(
                        offset, canvas.getHeight() / 2, canvas.getWidth() - offset, canvas.getHeight() / 2, paint);

                canvas.drawLine(
                        offset, 0, 100, 100, paint);
                imageView.setImageBitmap(bitmap);

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}
