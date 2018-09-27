package edu.stlawu.montyhall;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.prefs.Preferences;

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
    private int[] valArray; // [0, 0, 0] randomly sets a 1, that represents a car
    private int carIndex; // carindex is the 1 above
    private boolean clicked_state;

    private static DecimalFormat df2 = new DecimalFormat(".##");

    //setRetainInstance to save instance data on rotation
    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // saves instance data

        // check clicked shared variable
        SharedPreferences clicked_check = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        clicked_state = clicked_check.getBoolean(NEW_CLICKED, true);

        Log.i("CLICKED STATE BOOL", Boolean.toString(clicked_state));

        // change instance variables before UI is created

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
            this.percent_win.setText(String.valueOf(win_percent));
            this.percent_loss = rootView.findViewById(R.id.percent_loss);
            this.percent_loss.setText(String.valueOf(loss_percent));


            this.newGameYes = rootView.findViewById(R.id.yes_button);
            this.newGameNo = rootView.findViewById(R.id.no_button);
            this.textPrompt = rootView.findViewById(R.id.prompt);
            this.doorArrayList.add(door1);
            this.doorArrayList.add(door2);
            this.doorArrayList.add(door3);

            // initialize refresh
            refresh();

            // refresh
            // 1st click
            // second click
            // end state

            door1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    GameFragment.this.aDoor = door1;

                    if (gameState == 0)
                        refresh();
                    else if (gameState == 1)
                        aDoorFirstClick();
                    else if (gameState == 2)
                        aDoorSecondClick();

                }
            });
            door2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    GameFragment.this.aDoor = door2;

                    if (gameState == 0)
                        refresh();
                    else if (gameState == 1)
                        aDoorFirstClick();
                    else if (gameState == 2)
                        aDoorSecondClick();
                }
            });
            door3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    GameFragment.this.aDoor = door3;

                    if (gameState == 0)
                        refresh();
                    else if (gameState == 1)
                        aDoorFirstClick();
                    else if (gameState == 2)
                        aDoorSecondClick();

                }
            });

        return rootView;
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
        // set up 3 closed doors
        for (ImageButton aDoor : doorArrayList) {
            aDoor.setImageResource(R.drawable.closed_door);
            aDoor.setEnabled(true);
        }

        gameState = 1;

    }



    // gamestate 1
    //this method is called each time a door is clicked
    // it represents a door click listener but in a general form
    public void aDoorFirstClick(){

        aDoor.setImageResource(R.drawable.closed_door_chosen);
        userIndex = doorArrayList.indexOf(aDoor);

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
                // find which door to open in our doorArrayList
                doorArrayList.get(openIndex).setImageResource(R.drawable.goat);
                // enable back the two doors that are closed
                for (ImageButton aDoor : doorArrayList) {
                    if (doorArrayList.indexOf(aDoor) != openIndex)
                        aDoor.setEnabled(true);
                }

                textPrompt.setText(R.string.switch_prompt);



            }
        }, 1000);

        gameState = 2;

    }

    // gamestate 2
    public void aDoorSecondClick(){
        {

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

                                    } else if (valArray[doorArrayList.indexOf(aDoor)] == 0) {
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

                                    newGameYes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            refresh();

                                        }
                                    });

                                    newGameNo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Objects.requireNonNull(getActivity()).finish();
                                            //ameFragment.this.onDetach();
                                        }
                                    });

                                }
                            }, 1000);
                        }
                    }, 1000);
                }
            }, 1000);

        }

        // gameState = 3;

    }

}
