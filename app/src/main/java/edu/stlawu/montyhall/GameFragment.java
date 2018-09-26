package edu.stlawu.montyhall;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.prefs.Preferences;


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
    private int gameState = 0; // 0 is the state where the user is picking the door, 1 is the state where the user is asked about switching doors
    private int userIndex, openIndex;
    private Button newGameYes, newGameNo;
    private int wins, loss, total;
    private int[] valArray; // [0, 0, 0] randomly sets a 1, that represents a car
    private int carIndex; // carindex is the 1 above




    //setRetainInstance to save isntance data on rotation
    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // saves instance data
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
            this.total_loss = rootView.findViewById(R.id.total_loss);
            this.total_sum = rootView.findViewById(R.id.total_sum);
            this.percent_win = rootView.findViewById(R.id.percent_win);
            this.percent_loss = rootView.findViewById(R.id.percent_loss);
            this.newGameYes = rootView.findViewById(R.id.yes_button);
            this.newGameNo = rootView.findViewById(R.id.no_button);
            this.textPrompt = rootView.findViewById(R.id.prompt);

            this.doorArrayList.add(door1);
            this.doorArrayList.add(door2);
            this.doorArrayList.add(door3);
            refresh(0);

            gameState = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("gameState", 0);



            door1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (gameState == 0) {
                        GameFragment.this.aDoor = door1;
                        aDoorFirstClick();
                    }else if(gameState == 1){
                        GameFragment.this.aDoor = door1;
                        aDoorSecondClick();
                    }
                }
            });
            door2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (gameState == 0) {
                        GameFragment.this.aDoor = door2;
                        aDoorFirstClick();
                    }else if(gameState == 1) {
                        GameFragment.this.aDoor = door2;
                        aDoorSecondClick();

                    }
                }
            });
            door3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (gameState == 0) {
                        GameFragment.this.aDoor = door3;
                        aDoorFirstClick();
                    }else if(gameState == 1) {
                        GameFragment.this.aDoor = door3;
                        aDoorSecondClick();
                    }
                }
            });

            newGameYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refresh(0);

                }
            });

            newGameNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Objects.requireNonNull(getActivity()).finish();
                }
            });
        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //GameFragment.this.getFragmentManager().putFragment(outState, "GameFragment", myContent);
    }

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

                // save the gamestate,
//                                SharedPreferences.Editor edit = saved.edit();
//                                edit.putInt("gameState", gameState);
//                                edit.apply();
                // the switch or no switch door buttons
                textPrompt.setText(R.string.switch_prompt);


                gameState = 1;

                getActivity().getPreferences(Context.MODE_PRIVATE).edit().putInt("gameState", gameState).apply();



            }
        }, 1000);
    }

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
                                    total_sum.setText(String.valueOf(total));
                                    percent_win.setText(String.valueOf(((float)wins/total) * 100).concat("%"));
                                    percent_loss.setText(String.valueOf(((float)loss/total) * 100).concat("%"));

                                    // save the score
                                    refresh(1);

                                }
                            }, 1000);
                        }
                    }, 1000);
                }
            }, 1000);

        }
    }

    public void refresh(int gameState) {
        if (gameState == 0){
            this.textPrompt.setText(R.string.choose_a_door);
            this.newGameYes.setVisibility(View.INVISIBLE);
            this.newGameNo.setVisibility(View.INVISIBLE);
            // 0 represent goats in an array, change a random index to 1 to represent the car


            this.gameState = 0;
            getActivity().getPreferences(Context.MODE_PRIVATE).edit().putInt("gameState", gameState).apply();
            this.valArray = new int[]{0, 0, 0};
            carIndex = randomizer.nextInt(3);
            valArray[carIndex] = 1;
            // set up 3 closed doors
            for (ImageButton aDoor : doorArrayList) {
                aDoor.setImageResource(R.drawable.closed_door);
                aDoor.setEnabled(true);
            }
        }else if(gameState == 1){


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
    }

}
