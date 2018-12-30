package com.example.jona1993.reservations;

import android.content.Intent;
import android.opengl.EGLExt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import network.request.ROMPResponse;
import servicesocket.SocketUtility;

public class ClientActivity extends AppCompatActivity {
    private SocketUtility soc;
    private String username;
    private EditText dateDebut;
    private EditText dateFin;
    private Spinner as;
    ROMPResponse rep;
    LinearLayout ll;
    Scanner scan;

    @Override
    protected void onResume() {
        super.onResume();

        try {
            soc = SocketUtility.getInstance();

            if(soc.IsAuthenticated())
            {
                username = SocketUtility.LOGIN;
            }
            else
            {
                finish();
            }

            soc.sendRequest(username + "#ALL", "LMYROOMS");

            rep = soc.getResponse();

            TextView v;

            ll = findViewById(R.id.LogementsLL);

            ll.removeAllViews();

            scan = new Scanner(rep.getChargeUtile());
            scan.useDelimiter("#");

            while(scan.hasNext())
            {
                v = new TextView(this);
                v.setText(scan.next());
                ll.addView(v);
            }

            soc.sendRequest(null, "GetFreeRooms");

            rep = soc.getResponse();

            scan = new Scanner(rep.getChargeUtile());
            scan.useDelimiter("#");
            ArrayList<String> al = new ArrayList<>();

            as = findViewById(R.id.AvalibleSpinner);
            final SpinnerAdapter SpinAd;


            while(scan.hasNext())
            {
                al.add(scan.next());
            }

            SpinAd = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, al);

            as.setAdapter(SpinAd);




            dateDebut = findViewById(R.id.DateDebutTF);

            dateFin = findViewById(R.id.DateFinTF);

            Button reserverButton, payerButton;

            reserverButton = findViewById(R.id.ReserverButton);

            reserverButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        System.err.println("ENVOI: " + username + "#" + as.getSelectedItem().toString().split("/ ")[0].split("°")[1] + "#" + dateDebut.getText() + "#" + dateFin.getText());
                        soc.sendRequest(username + "#" + as.getSelectedItem().toString().split("/ ")[0].split("°")[1] + "#" + dateDebut.getText() + "#" + dateFin.getText(), "BROOM");
                        ROMPResponse repo = soc.getResponse();

                        LinearLayout ll = findViewById(R.id.LogementsLL);


                        soc.sendRequest(username + "#ALL", "LMYROOMS");

                        ROMPResponse rep = soc.getResponse();

                        TextView tv;

                        Scanner scan = new Scanner(rep.getChargeUtile());
                        scan.useDelimiter("#");

                        ll.removeAllViews();

                        while(scan.hasNext())
                        {
                            tv = new TextView(ClientActivity.this);
                            tv.setText(scan.next());
                            ll.addView(tv);
                        }

                        if(repo.getCode() == ROMPResponse.OK)
                        {
                            Toast.makeText(getApplicationContext(), "Réservé avec succès !", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "ERROR: " + repo.getChargeUtile(), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            payerButton = findViewById(R.id.PayerButton);

            payerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(ClientActivity.this, PaiementActivity.class);
                    ClientActivity.this.startActivity(myIntent);
                }
            });

        } catch (IOException e1) {
            Toast.makeText(getApplicationContext(), "ERROR: " + e1.getMessage(), Toast.LENGTH_LONG).show();
        } catch (ClassNotFoundException e1) {
            Toast.makeText(getApplicationContext(), "ERROR: " + e1.getMessage(), Toast.LENGTH_LONG).show();
        } catch (InterruptedException e1) {
            Toast.makeText(getApplicationContext(), "ERROR: " + e1.getMessage(), Toast.LENGTH_LONG).show();
        } catch(Exception e1) {
            Toast.makeText(getApplicationContext(), "ERROR: " + e1.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

    }




    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}
