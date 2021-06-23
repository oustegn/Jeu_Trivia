package com.example.sylla.trivialim;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.google.firebase.database.*;
import okio.Utf8;

import java.security.*;

public class CreationCompte extends AppCompatActivity {

    private FirebaseDatabase mDB;
    private DatabaseReference mDBRef;
    private Joueur joueur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_compte);
        mDB = FirebaseDatabase.getInstance();
       // mDBRef=mDB.getReference ();
       this.joueur=new Joueur();

    }

    //methode d'ajout d'un user dans la base
    public void signUp(View view){

        EditText nom=(EditText) findViewById(R.id.edtNom);
        EditText prenom=(EditText) findViewById(R.id.edtPrenom);
        EditText pseud=(EditText) findViewById(R.id.edtPseudo);
        EditText password = (EditText) findViewById(R.id.edtPass);
        String pass = password.getText().toString();
        this.joueur = new Joueur(pseud.getText().toString(),hachagePassword(pass),nom.getText().toString(),prenom.getText().toString());

        //le pseudo est l'identifiant unique des joueur
        mDBRef=mDB.getReference().child("Joueur").child(this.joueur.getPseudo());
        mDBRef.setValue(this.joueur);
        Intent authentification = new Intent(CreationCompte.this,Authentification.class);
       startActivity(authentification);
    }

    public String hachagePassword(String pass){
        String password="";
        byte[] hash;
        StringBuilder sb = new StringBuilder();
        byte[]  bytesChaine = pass.getBytes();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            hash = md.digest(bytesChaine);

            for (byte byt : hash) {
                String hex = Integer.toHexString(byt);
                if (hex.length() == 1) {
                    sb.append(0);
                    sb.append(hex.charAt(hex.length() - 1));
                } else {
                    sb.append(hex.substring(hex.length() - 2));
                }
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return sb.toString();
    }

}
