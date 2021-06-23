package com.example.sylla.trivialim;

import android.support.annotation.NonNull;

import java.util.Comparator;

public class Score implements Comparable{
    private String pseudo;
    private int nbQuestions;
    private int temps;

    public Score(String pseudo, int nbQuestions, int temps) {
        this.pseudo = pseudo;
        this.nbQuestions = nbQuestions;
        this.temps = temps;
    }

    public Score() { }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getNbQuestions() {
        return nbQuestions;
    }

    public void setNbQuestions(int nbQuestions) {
        this.nbQuestions = nbQuestions;
    }

    public int getTemps() {
        return temps;
    }

    public void setTemps(int temps) {
        this.temps = temps;
    }

    @Override
    public int compareTo(@NonNull Object o) throws ClassCastException{
        if(o instanceof Score) {
            Score s=(Score) o;
            if (this.nbQuestions == s.getNbQuestions()) {
                return (this.temps - s.getTemps());
            } else {
                return (s.getNbQuestions() - this.nbQuestions );
            }
        }
        else{
            throw new ClassCastException();
        }
    }
}
