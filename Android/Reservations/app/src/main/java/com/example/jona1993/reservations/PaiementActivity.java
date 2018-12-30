package com.example.jona1993.reservations;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Scanner;

import network.request.ROMPResponse;
import servicesocket.SocketUtility;

public class PaiementActivity extends AppCompatActivity {
    private SocketUtility soc;
    private String username;

    @Override
    protected void onResume() {

        super.onResume();
        try {
            soc = SocketUtility.getInstance();

            if (soc.IsAuthenticated()) {
                username = SocketUtility.LOGIN;
            } else {
                finish();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paiement);

        try {
            soc = SocketUtility.getInstance();

            if(soc.IsAuthenticated())
                username = SocketUtility.LOGIN;

            LinearLayout ll;

            ll = findViewById(R.id.ReservedLL);

            CheckBox r;
            final ArrayList<CheckBox> listreserv = new ArrayList<>();

            soc.sendRequest(username + "#NONE", "LMYROOMS");

            ROMPResponse rep = soc.getResponse();

            Scanner scan = new Scanner(rep.getChargeUtile());
            scan.useDelimiter("#");

            if(rep.getCode() == ROMPResponse.OK) {
                while (scan.hasNext()) {
                    r = new CheckBox(this);
                    String s = scan.next();
                    r.setText(s.split(" IDVOY")[0].split("ID: ")[1]);
                    listreserv.add(r);
                }

                for (CheckBox cb : listreserv) {
                    ll.addView(cb);
                }
            }

            Button payer, annuler;

            payer = findViewById(R.id.PayerFinalButton);
            annuler = findViewById(R.id.CancelButton);

            payer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText numcarte;
                    numcarte = findViewById(R.id.NumCarteTF);

                    LinearLayout ll = findViewById(R.id.ReservedLL);

                    ArrayList<CheckBox> acb = new ArrayList<>(listreserv);

                    for(CheckBox cb : acb){
                        if(cb.isChecked())
                        {

                            try {
                                soc.sendRequest(username + "#" + cb.getText() + "#" + numcarte.getText(), "PROOM");

                                ROMPResponse rep = soc.getResponse();

                                if(rep.getCode() == ROMPResponse.OK) {
                                    listreserv.remove(cb);
                                    ll.removeView(cb);
                                }

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                }
            });

            annuler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LinearLayout ll = findViewById(R.id.ReservedLL);

                    ArrayList<CheckBox> acb = new ArrayList<>(listreserv);



                    for(CheckBox cb : acb) {
                        if(cb.isChecked())
                        {
                            try {
                                soc.sendRequest(username + "#" + cb.getText(), "CROOM");

                                ROMPResponse rep = soc.getResponse();

                                if(rep.getCode() == ROMPResponse.OK) {
                                    listreserv.remove(cb);
                                    ll.removeView(cb);
                                }

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Annulation !", Toast.LENGTH_LONG).show();

                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
