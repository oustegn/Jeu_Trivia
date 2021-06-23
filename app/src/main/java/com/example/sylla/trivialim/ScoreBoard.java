package com.example.sylla.trivialim;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;

public class ScoreBoard extends AppCompatActivity {

    private Joueur joueur;

    public static final int USER_WIN=1;//"Félicitations !";
    public static final int USER_RESPONSE_WRONG=2;//"Mauvaise Réponse !";
    public static final int END_OF_TIME=3;//"Fin du temps imparti !";
    public static final int USER_WIN_DEFI=4;//Le joueur a gagné un défi
    public static final int USER_LOOSE_DEFI=5;//Le joueur a perdu
    public static final int DEFI_DRAW=6;//Égalité
    private FirebaseDatabase mDB;
    private DatabaseReference mDBRef;
    private DefiQuestionnaire defi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        //on recupere le défi
        this.defi = new Gson().fromJson(getIntent().getStringExtra("DéfiQuestionnaire"),DefiQuestionnaire.class);
        this.joueur=new Gson().fromJson(getIntent().getStringExtra("joueur"), Joueur.class);
        //chaine du score des deux joueur;
        String chn="";
        if(defi!=null)
          chn =defi.getPseudoAdversaire()+" : "+getIntent().getIntExtra("NbQuestions",0)+" questions en "+getIntent().getIntExtra("Temps",0)+" secondes/"+
                defi.getPseudoJoueur()+" : "+getIntent().getIntExtra("ScoreAdversaire",0)+" questions en "+getIntent().getIntExtra("TempsAdversaire",0)+" secondes\n";
        String chaine;
        Intent intent=getIntent();
        TextView textView=findViewById(R.id.Score);
        mDB = FirebaseDatabase.getInstance();
        switch(intent.getIntExtra("EndingCode",0)){
            case USER_WIN:
                textView.setText("Félicitations!\n");
                textView.append("Vous avez répondu juste à toutes les questions\n");
                textView.append("en "+intent.getIntExtra("Temps",0)+" secondes !\n");
                //il a +200 limcoins dans ce cas
                this.joueur.setLimCoin(this.joueur.getLimCoin()+200);
                break;
            case USER_RESPONSE_WRONG:
                textView.setText("Mauvaise Réponse !\n");
                textView.append("Vous avez répondu juste à "+intent.getIntExtra("NbQuestions",0)+" questions\n");
                textView.append("en "+intent.getIntExtra("Temps",0)+" secondes !\n");
                break;
            case END_OF_TIME:
                textView.setText("Fin du temps imparti !\n");
                textView.append("Mais vous avez répondu juste à "+intent.getIntExtra("NbQuestions",0)+" questions\n");
                break;
            case USER_WIN_DEFI:
                textView.setText("Félicitations!\n");
                textView.append("Vous avez gagné votre défi contre "+intent.getStringExtra("PseudoAdversaire")+"\n");
                textView.append(joueur.getPseudo()+" : "+intent.getIntExtra("NbQuestions",0)+" questions en "+intent.getIntExtra("Temps",0)+" secondes\n");
                textView.append(intent.getStringExtra("PseudoAdversaire")+" : "+intent.getIntExtra("ScoreAdversaire",0)+" questions en "+intent.getIntExtra("TempsAdversaire",0)+" secondes\n");
                //on insert une chaine dans la base avec pour clé le pseudo du joueur à notifer et on la supprime quand le joueur aura été notifié
                mDBRef = mDB.getReference().child("NotificationDefiReleve").child(this.defi.getPseudoJoueur()+"_"+defi.getPseudoAdversaire());
                chaine = this.defi.getPseudoJoueur()+"_Vous avez perdu le défi contre "+defi.getPseudoAdversaire()+"/"+ chn;
                mDBRef.setValue(chaine);
                //on supprime le défi
                mDBRef = mDB.getReference().child("Défi").child(this.defi.getPseudoJoueur()+"_"+defi.getPseudoAdversaire());
                mDBRef.removeValue();

                break;
            case USER_LOOSE_DEFI:
                textView.setText("Vous avez perdu votre défi contre "+intent.getStringExtra("PseudoAdversaire")+"\n");
                textView.append(intent.getStringExtra("PseudoAdversaire")+" : "+intent.getIntExtra("ScoreAdversaire",0)+" questions en "+intent.getIntExtra("TempsAdversaire",0)+" secondes\n");
                textView.append(joueur.getPseudo()+" : "+intent.getIntExtra("NbQuestions",0)+" questions en "+intent.getIntExtra("Temps",0)+" secondes\n");
                mDBRef = mDB.getReference().child("NotificationDefiReleve").child(this.defi.getPseudoJoueur()+"_"+defi.getPseudoAdversaire());
                chaine = this.defi.getPseudoJoueur()+"_Félicitation!/Vous avez gagné le défi contre "+defi.getPseudoAdversaire()+"/"+ chn;
                mDBRef.setValue(chaine);
                //on supprime le défi
                mDBRef = mDB.getReference().child("Défi").child(this.defi.getPseudoJoueur()+"_"+defi.getPseudoAdversaire());
                mDBRef.removeValue();
                break;
            case DEFI_DRAW:
                textView.setText("Égalité!\n");
                textView.append("Vous avez fait le même score que "+intent.getStringExtra("PseudoAdversaire")+"\n");
                textView.append("Vous avez répondu juste à "+intent.getIntExtra("NbQuestions",0)+" questions\n");
                textView.append("en "+intent.getIntExtra("Temps",0)+" secondes !\n");
                mDBRef = mDB.getReference().child("NotificationDefiReleve").child(this.defi.getPseudoJoueur()+"_"+defi.getPseudoAdversaire());
                chaine = this.defi.getPseudoJoueur()+"_Égalité!/Vous avez fait le même score que "+defi.getPseudoAdversaire()+"/"+ chn;
                mDBRef.setValue(chaine);
                //on supprime le défi
                mDBRef = mDB.getReference().child("Défi").child(this.defi.getPseudoJoueur()+"_"+defi.getPseudoAdversaire());
                mDBRef.removeValue();
                break;
            default:
                textView.setText("Désolé nous faisons face à une erreure\n:(");
                break;

        }

        //mettre à jour le nombre de limcoin du joueur
        mDBRef=mDB.getReference().child("Joueur").child(this.joueur.getPseudo());
        mDBRef.setValue(this.joueur);

        //on créé l'objet score associé aux resultat du joueur
        final Score score=new Score(joueur.getPseudo(),intent.getIntExtra("NbQuestions",0),intent.getIntExtra("Temps",0));

        mDBRef=mDB.getReference().child("Meilleurs_Scores").child(this.joueur.getPseudo());
       // mDBRef.setValue(score);
        //on compare son score actuel à son précedent score si y a besoin de mettre à jour

        //recuperation de son ancien meilleur score

       mDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Score s= dataSnapshot.getValue(Score.class);
                if(s==null) { //c'est sa premiere fois de jouer, alors on enregistre son score
                    mDBRef.child(joueur.getPseudo()).setValue(score);
                }
                else { //il faut comparer s et score
                    if(score.compareTo(s)<0)
                        mDBRef.child(joueur.getPseudo()).setValue(score);

                    //si non y a pas besoin de mettre à jour
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ListView listView = findViewById(R.id.listScore);
        scoreList(listView);
    }

    public void rejouer(View view) {
        Intent intent=new Intent(this,ChoixModeJeu.class);
        intent.putExtra("joueurSession", new Gson().toJson(this.joueur));
        startActivity(intent);
    }

    //fct d'affichage des meilleurs scores

    public void scoreList(final ListView listViews){
        mDBRef=mDB.getReference().child("Meilleurs_Scores");
        mDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList <Score> listAux = new ArrayList<>();
                Score score;
                if (dataSnapshot.exists()){
                    for (DataSnapshot chil : dataSnapshot.getChildren()) {
                        score = chil.getValue(Score.class);
                        listAux.add(score);
                    }
                    //il faut faire de telle sorte que la liste soir par ordre decroissant du ratio(nbr question/temps mis)
                    //donc on extrait le nbrQuestion et le temps dans la chaine
                    Collections.sort(listAux);

                    //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ScoreBoard.this, android.R.layout.simple_list_item_multiple_choice, listAux);
                    listViews.setAdapter(new ScoreAdapter(getApplicationContext(),listAux));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
