package com.example.sylla.trivialim;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import com.squareup.moshi.Json;

import java.util.ArrayList;

public class Authentification extends AppCompatActivity {
    private String pseudo;
    private String password;
    private CreationCompte compte;
    private Intent modeJeu ;
    private boolean trouve = false;
    private Joueur joueur;
    private FirebaseDatabase mDB;
    private DatabaseReference mDBRef;

    public String getPseudo() {
        return pseudo;
    }

    public String getPassword() {
        return password;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);
        modeJeu = new Intent(Authentification.this,ChoixModeJeu.class);
        mDB= FirebaseDatabase.getInstance();
    }

    public void authentification(View view){
        final EditText pseudo = (EditText) findViewById(R.id.edtPseudo);
        final EditText password = (EditText) findViewById(R.id.edtPassword);
        this.compte = new CreationCompte();
        mDB=FirebaseDatabase.getInstance();
        mDBRef=mDB.getReference();
        final String noeud = pseudo.getText().toString();
        mDBRef.child("Joueur").child(noeud).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                joueur= dataSnapshot.getValue(Joueur.class);
                if(joueur==null)
                    Toast.makeText(Authentification.this,"Aucun compte associé à ces identifiants, pensez à en créer",Toast.LENGTH_LONG).show();
                else if(!joueur.getPseudo().equals(pseudo.getText().toString()) || !joueur.getPassword().equals(compte.hachagePassword(password.getText().toString()))){
                    Toast.makeText(Authentification.this,"Pseudo ou mot de passe incorrect",Toast.LENGTH_LONG).show();
                }
                else { //si non on transmet l'objet joueur à l'activité suivante
                   modeJeu.putExtra("joueur", new Gson().toJson(joueur));
                   iAmChallenged(joueur.getPseudo());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void commencerCreationCompte(View view){
        Intent creationCompte = new Intent(Authentification.this,CreationCompte.class);
        startActivity(creationCompte);
    }

    //cette fonction compare le pseudo de user aux pseudos adverses dans la bases, si égalité, le joueur courant a été defié
    //donc il faut lui proposer s'il veut repondre au challeng

    public void iAmChallenged(final String pseudo){

          mDBRef=mDB.getReference().child("Défi");
            mDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DefiQuestionnaire dfi;
                    if (dataSnapshot.exists()){
                        for (DataSnapshot chil : dataSnapshot.getChildren()) {
                            dfi = chil.getValue(DefiQuestionnaire.class);
                            if(dfi.getPseudoAdversaire().equals(pseudo)){
                                trouve = true;
                                //Affichage de la notification de défi
                                AlertDialog.Builder builder = new AlertDialog.Builder(Authentification.this);
                                builder.setTitle("Notification de défi");
                                builder.setCancelable(true);
                                final TextView textView = new TextView(Authentification.this);
                                textView.setInputType(InputType.TYPE_CLASS_TEXT);
                                textView.setText("Vous avez été defié par "+dfi.getPseudoJoueur()+" répondre au défi ?");
                                builder.setView(textView);
                                final DefiQuestionnaire dfiFinal=dfi;
                                builder.setPositiveButton(
                                        "Oui",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //if faut lui envoyer des questions de l'adversaire
                                                Intent intent= new Intent(getApplicationContext(),EspaceJeu.class);
                                                intent.setAction("Defi");
                                                intent.putExtra("DefiQuestionnaire", new Gson().toJson(dfiFinal));
                                                intent.putExtra("joueur",new Gson().toJson(joueur));
                                                startActivity(intent);
                                            }
                                        });
                                builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        startActivity(modeJeu); //on affiche le plateau de jeux s'il veut pas repondre au défi
                                    }
                                });

                                builder.show();
                                break;
                            }
                        }
                    //si on trouve rien( il n'a pas été défié, il faut lui afficher le plateau de jeux comme d'hab
                      if(!trouve)
                          startActivity(modeJeu);


                    }
                    else{
                        startActivity(modeJeu);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        //modeJeu.putExtra("defi","hello defi");

    }

    public void challengrLifted(final String pseudo, final Context context){

        //liste devant contenir la liste des notification cocerant les challenges lévés
        FirebaseDatabase mDB = mDB=FirebaseDatabase.getInstance();;
        final DatabaseReference mDBRef=mDB.getReference().child("NotificationDefiReleve");
        mDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String chaine,tab[];
                if (dataSnapshot.exists()) {
                    for (final DataSnapshot chil : dataSnapshot.getChildren()) {
                        chaine = chil.getValue(String.class);
                        tab = chaine.split("_");
                        if (tab[0].equals(pseudo)) {
                           //Affichage de la notification de défi
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Notification de défi");
                            builder.setCancelable(true);
                            TextView textView = new TextView(context);
                            tab = tab[1].split("/");
                            textView.setText("");
                            textView.setTextSize(18);
                            for(String s : tab){
                                textView.append(s+"\n");
                            }
                            builder.setView(textView);
                            builder.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //on supprime la notification visualisée
                                         mDBRef.child(chil.getKey()).removeValue();
                                        }
                                    });

                            builder.show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
