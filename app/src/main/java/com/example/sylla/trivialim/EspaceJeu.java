package com.example.sylla.trivialim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class EspaceJeu extends AppCompatActivity {

    private static final String Tag="EspaceJeu";
    private List<Result> Questions;
    private int numQuestion;
    private GridView gridView;
    private TextView question;
    private ProgressBar progressBar;
    private final int tempsImparti=90000;
    private Joueur user;
    private DefiQuestionnaire defiQuestionnaire;
    private int scoreAdversaire;
    private long tempsAdversaire;
    private  String pseudoAdversaire;
    private String modeJeu;
    //parce qu'on fait pas new sur list
    private ArrayList<String> listAnswerUser;

    private class QuestionReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Enonce enonce=(new Gson()).fromJson(intent.getStringExtra("Questions"),Enonce.class);
            Questions=enonce.getResults();
            //Log.d(Tag,"QuestionsReceiver"+Questions.toString());
            demarrageCompteARebours();
            getNextQuestion();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espace_jeu);

        //Initialisation des variables
        gridView=findViewById(R.id.Reponses);
        question=findViewById(R.id.Question);
        progressBar=findViewById(R.id.ProgressBar);
        numQuestion=0;
        this.listAnswerUser = new ArrayList<String>();

        //Acquisition des questions
        if(getIntent().getAction()=="Defi"){
            defiQuestionnaire=new Gson().fromJson(getIntent().getStringExtra("DefiQuestionnaire"),DefiQuestionnaire.class);
            Questions=defiQuestionnaire.getListeQuestion();
            scoreAdversaire=defiQuestionnaire.getNbrQuestionReussie();
            tempsAdversaire=defiQuestionnaire.getTempsMis();
            pseudoAdversaire=defiQuestionnaire.getPseudoJoueur();
            demarrageCompteARebours();
            getNextQuestion();
        }
        else {
            IntentFilter filter = new IntentFilter();
            filter.addAction("GetQuestions");
            registerReceiver(new QuestionReceiver(), filter);
            Intent intent = new Intent(this, ServiceQuestionnaire.class);
            intent.putExtra("Difficulte", getIntent().getStringExtra("Difficulte"));
            startService(intent);
        }

        //Ajout d'un listener sur le GridView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String userAnswer=((TextView)view).getText().toString();
                listAnswerUser.add(userAnswer);//enregistre la reponse du joueur, qu'elle soit vraie ou fausse
                if(userAnswer.equals(Questions.get(numQuestion).getCorrectAnswer())){
                    //on test s'il a repondu à 3 bonnes questions, il a +30 limcoins
                    if(numQuestion!=0 && numQuestion%3==0)
                        user.setLimCoin(user.getLimCoin()+30);
                    numQuestion++;
                    getNextQuestion();
                }
                else{
                    ouvrirScoreBoard(ScoreBoard.USER_RESPONSE_WRONG);
                }
            }
        });
      this.user = new Gson().fromJson(getIntent().getStringExtra("joueur"),Joueur.class);
      TextView cptLimcoin = (TextView) findViewById(R.id.CompteurLimcoin);
      cptLimcoin.setText("LimCoins: "+this.user.getLimCoin());

      modeJeu = getIntent().getStringExtra("modeJeu");
      if(modeJeu!=null && modeJeu.equals("defi")){ //on istancie defiQuestionnaire seulement quand ils'agit du mode defi
          defiQuestionnaire = new DefiQuestionnaire();
          defiQuestionnaire.setPseudoJoueur(this.user.getPseudo());
          //Log.d("advers: ",""+intent1.getStringExtra("pseudoAdversaire"));
          defiQuestionnaire.setPseudoAdversaire(getIntent().getStringExtra("pseudoAdversaire"));
      }


    }

    //Paramétrage du compte à rebours
    private void demarrageCompteARebours() {
        //Log.d(Tag,"Démarrage compte à rebours");
        final TextView compteARebours=findViewById(R.id.CompteARebours);
        new CountDownTimer(tempsImparti, 1000) {
            @Override
            public void onTick(long l) {
                compteARebours.setText(String.valueOf(l/1000));
            }

            @Override
            public void onFinish() {
                    ouvrirScoreBoard(ScoreBoard.END_OF_TIME);
            }
        }.start();
    }

    private void getNextQuestion(){
        if(numQuestion<Questions.size()){
            ReponseAdapter adapter=new ReponseAdapter(this,Questions.get(numQuestion).getAnswers());
            gridView.setAdapter(adapter);
            question.setText(Questions.get(numQuestion).getQuestion());
            //defiQuestionnaire.getListeQuestion().add(Questions.get(numQuestion).getQuestion());
            progressBar.incrementProgressBy(100/Questions.size());
        }
        else{
            ouvrirScoreBoard(ScoreBoard.USER_WIN);
        }
    }

    private void ouvrirScoreBoard(int endingCode) {
        if (!this.isDestroyed()) {
            int temps = Integer.parseInt(((TextView) findViewById(R.id.CompteARebours)).getText().toString());
            Intent intent = new Intent(this, ScoreBoard.class);
            //Extras à ajouter (score, nb de questions répondues, limcoins gagnés...)
            intent.putExtra("NbQuestions", numQuestion);
            intent.putExtra("Temps", tempsImparti / 1000 - temps);
            intent.putExtra("joueur", new Gson().toJson(this.user));

            if(getIntent().getAction()=="Defi"){
                if(numQuestion>scoreAdversaire || (numQuestion==scoreAdversaire && tempsImparti / 1000 - temps<tempsAdversaire)){
                    intent.putExtra("EndingCode",ScoreBoard.USER_WIN_DEFI);
                }
                else if(numQuestion==scoreAdversaire && tempsImparti / 1000 - temps==tempsAdversaire){
                    intent.putExtra("EndingCode",ScoreBoard.DEFI_DRAW);
                }
                else{
                    intent.putExtra("EndingCode",ScoreBoard.USER_LOOSE_DEFI);
                }
                intent.putExtra("ScoreAdversaire",scoreAdversaire);
                intent.putExtra("TempsAdversaire",tempsAdversaire);
                intent.putExtra("PseudoAdversaire",pseudoAdversaire);
            }
            else {
                intent.putExtra("EndingCode", endingCode);
                if (this.modeJeu.equals("defi")) {
                    defiQuestionnaire.getListeQuestion().addAll(Questions);
                    defiQuestionnaire.getReponse().addAll(this.listAnswerUser);
                    defiQuestionnaire.setTempsMis(tempsImparti / 1000 - temps);
                    defiQuestionnaire.setNbrQuestionReussie(numQuestion);
                    FirebaseDatabase mDB = FirebaseDatabase.getInstance();
                    DatabaseReference mDBRef;
                    mDBRef = mDB.getReference().child("Défi").child(defiQuestionnaire.getPseudoJoueur() + "_" + defiQuestionnaire.getPseudoAdversaire());
                    mDBRef.setValue(this.defiQuestionnaire);
                }
            }
            intent.putExtra("DéfiQuestionnaire",new Gson().toJson(defiQuestionnaire));
            startActivity(intent);
        }
    }

}
