package com.example.sylla.trivialim;

import android.os.Parcel;
import android.os.Parcelable;

public class Joueur{
    private String pseudo;
    private int limCoin;
    private String password;
    private String nom,prenom;
    private int niveau;

    public Joueur(String pseudo, String password, String nom, String prenom) {
        this.pseudo = pseudo;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.limCoin = 500;
        this.niveau = 1;
    }

    public Joueur() {
        this.limCoin=500;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setLimCoin(int limCoin) {
        this.limCoin = limCoin;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPseudo() {
        return pseudo;
    }

    public int getLimCoin() {
        return limCoin;
    }

    public String getPassword() {
        return password;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getNiveau() { return niveau; }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }
}
