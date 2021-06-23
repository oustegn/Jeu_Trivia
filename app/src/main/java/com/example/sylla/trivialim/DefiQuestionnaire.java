package com.example.sylla.trivialim;

import java.util.ArrayList;
import java.util.List;

public class DefiQuestionnaire {

    private String pseudoJoueur;
    private String pseudoAdversaire;
    private ArrayList<Result> listeQuestion;
    private ArrayList<String> reponse;
    private long tempsMis;
    private int nbrQuestionReussie;

    public DefiQuestionnaire() {
        this.listeQuestion = new ArrayList<Result>();
        this.reponse = new ArrayList<String>();
    }

    public String getPseudoJoueur() {
        return pseudoJoueur;
    }

    public String getPseudoAdversaire() {
        return pseudoAdversaire;
    }

    public ArrayList<Result> getListeQuestion() {
        return listeQuestion;
    }

    public long getTempsMis() {
        return tempsMis;
    }

    public void setPseudoJoueur(String pseudoJoueur) {
        this.pseudoJoueur = pseudoJoueur;
    }

    public void setPseudoAdversaire(String pseudoAdversaire) {
        this.pseudoAdversaire = pseudoAdversaire;
    }

    public void setTempsMis(long tempsMis) {
        this.tempsMis = tempsMis;
    }

    public List<String> getReponse() {
        return reponse;
    }

    public int getNbrQuestionReussie() {
        return nbrQuestionReussie;
    }

    public void setNbrQuestionReussie(int nbrQuestion) {
        this.nbrQuestionReussie = nbrQuestion;
    }
}
