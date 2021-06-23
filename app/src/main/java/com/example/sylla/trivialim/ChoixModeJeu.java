package com.example.sylla.trivialim;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.*;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ChoixModeJeu extends AppCompatActivity {

    private FirebaseDatabase mDB;
    private DatabaseReference mDBRef;
    private String pseudo;
    private Intent intent,authentification;
    private Authentification auth;
    private Joueur joueurConnecte, joueur; //joueurConnecté est renseigné à l'authen. et joueur est l'instance qui maintient le session ouverte tant que l'appli est ouverte....
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_mode_jeu);
        mDB=FirebaseDatabase.getInstance();
        mDBRef=mDB.getReference();
        intent=new Intent(ChoixModeJeu.this,EspaceJeu.class);
        authentification=new Intent(ChoixModeJeu.this,Authentification.class);
        //recupère ici le joueur connecté
        this.joueurConnecte = new Gson().fromJson(getIntent().getStringExtra("joueur"),Joueur.class);
        this.joueur = new Gson().fromJson(getIntent().getStringExtra("joueurSession"),Joueur.class);
       // Log.d("joueur: ", joueurConnecte.getPseudo());
        if(this.joueurConnecte==null && this.joueur==null) //si acun joueur n'est connecté, on relance la page de connexion
            startActivity(authentification);
        else if(joueurConnecte==null)
            joueurConnecte = joueur; // si l'activité choixModeJeu est relancée par ScoreBoard
        if(joueurConnecte!=null){
            TextView txt = (TextView) findViewById(R.id.txtVUser);
            txt.setText(joueurConnecte.getPseudo());
        }

        if(this.joueurConnecte.getLimCoin()<100){
            AlertDialog.Builder builder = new AlertDialog.Builder(ChoixModeJeu.this);
            builder.setTitle("Notifocation Limcoins");
            builder.setCancelable(true);
            final TextView textView = new TextView(ChoixModeJeu.this);
            textView.setText("Vous êtes ruinés, vous allez repartir à 500 Limcoins");
            textView.setTextSize(18);
            builder.setView(textView);
            builder.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            joueurConnecte.setLimCoin(500);
                        }
                    });

            builder.show();
        }
       this.auth = new Authentification();
        //à chaque lancement du choix mode de jeu, on verifie si le joueur n'a pas de notification défi
        auth.challengrLifted(this.joueurConnecte.getPseudo(), ChoixModeJeu.this);
        //auth.iAmChallenged(joueurConnecte.getPseudo(),ChoixModeJeu.this);

    }

    public void StartClassique(View view){

        //la partie lui coûte -100 limcoins
        this.joueurConnecte.setLimCoin(this.joueurConnecte.getLimCoin()-100);
        //this.joueur.setLimCoin(this.joueur.getLimCoin()-100);
        //choix de la difficulté
        int difficulte=((RadioGroup)findViewById(R.id.DifficulteRadioGroup)).getCheckedRadioButtonId();
        if(difficulte==R.id.DifficulteMedium){
            intent.putExtra("Difficulte","medium");
        }
        else if(difficulte==R.id.DifficulteEasy){
            intent.putExtra("Difficulte","easy");
        }
        else{
            intent.putExtra("Difficulte","hard");
        }
        intent.putExtra("modeJeu","classic");
        intent.putExtra("joueur",new Gson().toJson(this.joueurConnecte)); //transmettre ensuite le joueur connecté à l'espac de jeu
        startActivity(intent);
    }

    public void StartDefi(View view) {

        this.joueurConnecte.setLimCoin(this.joueurConnecte.getLimCoin()-100);
        //le formulaire de saisie d'adversaire
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Saisir le pseudo de votre adversaire");
        builder.setCancelable(true);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        pseudo = input.getText().toString();
                       while(pseudo.equals("")){
                           pseudo = input.getText().toString();
                       }
                        mDBRef.child("Joueur").child(pseudo).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Joueur joueur= dataSnapshot.getValue(Joueur.class);
                                if(joueur==null)
                                    Toast.makeText(ChoixModeJeu.this,"Ce joueurn'existe pas",Toast.LENGTH_LONG).show();
                                else if(pseudo.equals(joueurConnecte.getPseudo()))
                                    Toast.makeText(ChoixModeJeu.this,"Vous ne pouvez pas vous défier vous même!",Toast.LENGTH_LONG).show();
                                else {
                                   // Intent espaceJeu = new Intent(ChoixModeJeu.this,EspaceJeu.class);
                                    intent.putExtra("pseudoAdversaire",pseudo);//transmettre le pseudo de l'adversaire choisi
                                    intent.putExtra("joueur",new Gson().toJson(joueurConnecte));
                                    intent.putExtra("modeJeu","defi");
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    public void disconnect(View view){
        this.joueurConnecte = null;
        this.joueur = null;
        startActivity(new Intent(ChoixModeJeu.this,Authentification.class));
    }
}
