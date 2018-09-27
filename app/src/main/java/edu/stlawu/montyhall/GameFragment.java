package edu.stlawu.montyhall;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;


import static edu.stlawu.montyhall.MainFragment.NEW_CLICKED;
import static edu.stlawu.montyhall.MainFragment.PREF_NAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {


    private Random randomizer = new Random();
    private ImageButton door1, door2, door3;
    private ImageButton aDoor;
    private ArrayList<ImageButton> doorArrayList = new ArrayList<>();
    private TextView textPrompt = null;
    private TextView total_wins, total_loss, total_sum, percent_win, percent_loss;
    private int gameState; // 0 is the state where the user is picking the door, 1 is the state where the user is asked about switching doors
    private int userIndex, openIndex;
    private Button newGameYes, newGameNo;
    private int wins, loss, total;
    private float loss_percent, win_percent;
    private int[] valArray = new int[] {0,0,0}; // [0, 0, 0] randomly sets a 1, that represents a car
    private int carIndex; // carindex is the 1 above
    private boolean clicked_state;

    public AudioAttributes aa = null;
    public SoundPool soundPool = null;
    public int goatSound, doorSound, wonSound, crySound, musicSound = 0;


    //setRetainInstance to save instance data on rotation
    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setRetainInstance(true); // saves instance data

        // check clicked shared variable
        SharedPreferences clicked_check = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        clicked_state = clicked_check.getBoolean(NEW_CLICKED, true);

        Log.i("CLICKED STATE BOOL", Boolean.toString(clicked_state));

        // change instance variables before UI is created

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // wins
        SharedPreferences wins_check = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        wins = wins_check.getInt("wins", 0);

        // loss
        SharedPreferences loss_check = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loss = loss_check.getInt("loss", 0);

        // total
        SharedPreferences total_check = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        total = total_check.getInt("total", 0);

        // win percent
        SharedPreferences win_percent_check = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        win_percent = win_percent_check.getFloat("win_percent", 0);

        // loss percent
        SharedPreferences loss_percent_check = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loss_percent = loss_percent_check.getFloat("loss_percent", 0);

        // set gamestate
        SharedPreferences gamestate_check = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gameState = gamestate_check.getInt("gamestate", 0);
        Log.i("GAMESTATE VAL", Integer.toString(gameState));


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        this.aa = new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        this.soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(aa)
                .build();

        this.goatSound = this.soundPool.load(
                GameFragment.this.getActivity(), R.raw.goat, 1);

        this.doorSound = this.soundPool.load(
                GameFragment.this.getActivity(), R.raw.door, 1);

        this.wonSound = this.soundPool.load(
                GameFragment.this.getActivity(), R.raw.fireworks, 1);

        this.crySound = this.soundPool.load(
                GameFragment.this.getActivity(), R.raw.cry, 1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_game, container, false);



        // initialize imagebuttons
        this.door1 = rootView.findViewById(R.id.door1);
        this.door2 = rootView.findViewById(R.id.door2);
        this.door3 = rootView.findViewById(R.id.door3);


        this.total_wins = rootView.findViewById(R.id.total_wins);
        this.total_wins.setText(String.valueOf(wins));
        this.total_loss = rootView.findViewById(R.id.total_loss);
        this.total_loss.setText(String.valueOf(loss));
        this.total_sum = rootView.findViewById(R.id.total_sum);
        this.total_sum.setText(String.valueOf(total));

        this.percent_win = rootView.findViewById(R.id.percent_win);
        this.percent_win.setText(String.valueOf(String.format("%.2f",win_percent)).concat("%"));
        this.percent_loss = rootView.findViewById(R.id.percent_loss);
        this.percent_loss.setText(String.valueOf(String.format("%.2f",loss_percent)).concat("%"));


        this.newGameYes = rootView.findViewById(R.id.yes_button);
        this.newGameNo = rootView.findViewById(R.id.no_button);
        this.textPrompt = rootView.findViewById(R.id.prompt);
        this.doorArrayList.add(door1);
        this.doorArrayList.add(door2);
        this.doorArrayList.add(door3);

        if (clicked_state){
            refresh();
        }else{
            if (gameState == 0){

                refresh();
                newGameYes.setVisibility(View.INVISIBLE);
                newGameNo.setVisibility(View.INVISIBLE);
            }else if(gameState == 1){

                SharedPreferences openindex_check = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                openIndex = openindex_check.getInt("openindex",0 );

                SharedPreferences userindex_check = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                userIndex = userindex_check.getInt("userindex",0 );

                SharedPreferences carindex_check = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                carIndex = carindex_check.getInt("carindex",0 );

                valArray[carIndex] = 1;
                doorArrayList.get(openIndex).setImageResource(R.drawable.goat);

                doorArrayList.get(userIndex).setImageResource(R.drawable.closed_door_chosen);

                newGameYes.setVisibility(View.INVISIBLE);
                newGameNo.setVisibility(View.INVISIBLE);
            }else if(gameState == 2){


                SharedPreferences carindex_check = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                carIndex = carindex_check.getInt("carindex",0 );

                if (userIndex == carIndex){
                    textPrompt.setText(R.string.won);
                }else{
                    textPrompt.setText(R.string.lost);
                }
                for(ImageButton aDoor: doorArrayList){
                    aDoor.setImageResource(R.drawable.goat);
                }
                doorArrayList.get(carIndex).setImageResource(R.drawable.car);

                newGameYes.setVisibility(View.VISIBLE);
                newGameNo.setVisibility(View.VISIBLE);

            }
        }


        // initialize refresh

        // refresh
        // 1st click
        // second click
        // end state

        door1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                soundPool.play(doorSound, 1f,
                        1f, 1, 0, 1f);

                GameFragment.this.aDoor = door1;

                if (gameState == 0) {
                    aDoorFirstClick();
                }else if(gameState == 1){
                    aDoorSecondClick();
                }

            }
        });
        door2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                soundPool.play(doorSound, 1f,
                        1f, 1, 0, 1f);

                GameFragment.this.aDoor = door2;

                if (gameState == 0) {
                    aDoorFirstClick();
                }else if(gameState == 1){
                    aDoorSecondClick();
                }
            }
        });
        door3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                soundPool.play(doorSound, 1f,
                        1f, 1, 0, 1f);

                GameFragment.this.aDoor = door3;

                if (gameState == 0) {
                    aDoorFirstClick();
                }else if(gameState == 1){
                    aDoorSecondClick();
                }

            }
        });

        newGameYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();

                soundPool.autoPause();

            }
        });

        newGameNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                soundPool.autoPause();

                Objects.requireNonNull(getActivity()).finish();

            }
        });

        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        soundPool.autoPause();

    }

    // gamestate 0
    // before doors are clicked
    public void refresh() {



        this.textPrompt.setText(R.string.choose_a_door);
        this.newGameYes.setVisibility(View.INVISIBLE);
        this.newGameNo.setVisibility(View.INVISIBLE);
        // 0 represent goats in an array, change a random index to 1 to represent the car

        this.valArray = new int[]{0, 0, 0};
        carIndex = randomizer.nextInt(3);
        valArray[carIndex] = 1;

        SharedPreferences carindex = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit_carindex = carindex.edit();
        edit_carindex.putInt("carindex", carIndex);
        edit_carindex.apply();

        // set up 3 closed doors
        for (ImageButton aDoor : doorArrayList) {
            aDoor.setImageResource(R.drawable.closed_door);
            aDoor.setEnabled(true);
        }

        gameState = 0;

        SharedPreferences gamestate = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit_gamestate = gamestate.edit();
        edit_gamestate.putInt("gamestate", gameState);
        edit_gamestate.apply();

    }



    // gamestate 1
    //this method is called each time a door is clicked
    // it represents a door click listener but in a general form
    public void aDoorFirstClick(){

        newGameNo.setVisibility(View.INVISIBLE);
        newGameNo.setVisibility(View.INVISIBLE);

        gameState = 1;

        SharedPreferences gamestate = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit_gamestate = gamestate.edit();
        edit_gamestate.putInt("gamestate", gameState);
        edit_gamestate.apply();



        aDoor.setImageResource(R.drawable.closed_door_chosen);
        userIndex = doorArrayList.indexOf(aDoor);

        SharedPreferences userchoice = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit_userchoice = userchoice.edit();
        edit_userchoice.putInt("userindex", userIndex);
        edit_userchoice.apply();

        //disable all doors after click
        door1.setEnabled(false);
        door2.setEnabled(false);
        door3.setEnabled(false);


        openIndex = randomizer.nextInt(3);
        aDoor.postDelayed(new Runnable() {
            @Override
            public void run() {
                // open a goat door thats not the users

                while (openIndex == userIndex || openIndex == carIndex)
                    openIndex = randomizer.nextInt(3);

                SharedPreferences openindex = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit_openindex = openindex.edit();
                edit_openindex.putInt("openindex", openIndex);
                edit_openindex.apply();

                // find which door to open in our doorArrayList
                doorArrayList.get(openIndex).setImageResource(R.drawable.goat);
                // enable back the two doors that are closed
                for (ImageButton aDoor : doorArrayList) {
                    if (doorArrayList.indexOf(aDoor) != openIndex)
                        aDoor.setEnabled(true);
                }

                textPrompt.setText(R.string.switch_prompt);

                soundPool.play(goatSound, 1f,
                        1f, 1, 0, 1f);





            }
        }, 1000);




    }

    // gamestate 2
    public void aDoorSecondClick(){
        {
            gameState = 2;

            SharedPreferences gamestate = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit_gamestate = gamestate.edit();
            edit_gamestate.putInt("gamestate", gameState);
            edit_gamestate.apply();

            for (ImageButton theDoor : doorArrayList) {
                theDoor.setEnabled(false);
            }
            // if a door is open and the game state has been changed to 1 then show
            doorArrayList.get(userIndex).setImageResource(R.drawable.closed_door);
            aDoor.setImageResource(R.drawable.closed_door);
            // if user is here then door 1 has been clicked so do the 3..2..1 animation
            aDoor.setImageResource(R.drawable.three);
            aDoor.postDelayed(new Runnable() {
                @Override
                public void run() {
                    aDoor.setImageResource(R.drawable.two);
                    aDoor.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            aDoor.setImageResource(R.drawable.one);
                            aDoor.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (valArray[doorArrayList.indexOf(aDoor)] == 1) {
                                        textPrompt.setText(R.string.won);
                                        aDoor.setImageResource(R.drawable.car);
                                        wins++;
                                        total_wins.setText(String.valueOf(wins));
                                        total++;

                                        soundPool.play(wonSound, 1f,
                                                1f, 1, 0, 1f);

                                    } else if (valArray[doorArrayList.indexOf(aDoor)] == 0) {

                                        soundPool.play(crySound, 1f,
                                                1f, 1, 0, 1f);

                                        textPrompt.setText(R.string.lost);
                                        aDoor.setImageResource(R.drawable.goat);
                                        loss++;
                                        total_loss.setText(String.valueOf(loss));
                                        total++;

                                    }

                                    loss_percent = ((float)loss/total) * 100;
                                    win_percent = ((float)wins/total) *  100;

                                    total_sum.setText(String.valueOf(total));
                                    percent_win.setText(String.valueOf(String.format("%.2f", win_percent)).concat("%"));
                                    percent_loss.setText(String.valueOf(String.format("%.2f",loss_percent)).concat("%"));

                                    // save the score
                                    SharedPreferences wins = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor edit_wins = wins.edit();
                                    edit_wins.putInt("wins", GameFragment.this.wins);
                                    edit_wins.apply();

                                    SharedPreferences loss = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor edit_loss = loss.edit();
                                    edit_loss.putInt("loss", GameFragment.this.loss);
                                    edit_loss.apply();

                                    SharedPreferences total = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor edit_total = total.edit();
                                    edit_total.putInt("total", GameFragment.this.total);
                                    edit_total.apply();

                                    SharedPreferences win_percent = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor edit_win_percent = win_percent.edit();
                                    edit_win_percent.putFloat("win_percent", GameFragment.this.win_percent);
                                    edit_win_percent.apply();

                                    SharedPreferences loss_percent = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor edit_loss_percent = loss_percent.edit();
                                    edit_loss_percent.putFloat("loss_percent", GameFragment.this.loss_percent);
                                    edit_loss_percent.apply();

                                    //make doors unclickable
                                    for (ImageButton aDoor : doorArrayList) {
                                        aDoor.setEnabled(false);
                                        if (valArray[doorArrayList.indexOf(aDoor)] == 0) {
                                            aDoor.setImageResource(R.drawable.goat);
                                        } else {
                                            aDoor.setImageResource(R.drawable.car);
                                        }
                                    }

                                    newGameNo.setVisibility(View.VISIBLE);
                                    newGameYes.setVisibility(View.VISIBLE);

                                }
                            }, 1000);
                        }
                    }, 1000);
                }
            }, 1000);

        }

    }

}