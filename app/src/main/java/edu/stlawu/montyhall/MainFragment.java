package edu.stlawu.montyhall;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MainFragment extends Fragment {

    public static final String PREF_NAME = "MontyHall";
    public static final String NEW_CLICKED = "NEWCLICKED";

    public AudioAttributes aa = null;
    public SoundPool soundPool = null;
    public int beepSound = 0;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    // onCreate gets called when the fragment is created
    // before the UI views are constructed
    // Initialize data needed for the fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        this.aa = new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        this.soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(aa)
                .build();

        this.beepSound = this.soundPool.load(
                MainFragment.this.getActivity(), R.raw.beep, 1);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        // about button is clicked
        final View aboutButton = rootView.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   AlertDialog.Builder builder =
                           new AlertDialog.Builder(getActivity());
                   builder.setTitle(R.string.about_text);
                   builder.setMessage(R.string.about);
                   builder.setPositiveButton(R.string.ok,
                           new DialogInterface.OnClickListener(){
                               @Override
                               public void onClick(DialogInterface dialog,
                                                   int which) {
                                   return;
                               }
                           });
                   builder.show();

                   Animator anim = AnimatorInflater
                           .loadAnimator(getActivity(), R.animator.buttons);
                   anim.setTarget(aboutButton);
                   anim.start();
               }
           }
        );

        // new button is clicked
        final View newButton = rootView.findViewById(R.id.new_button);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences clicked = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = clicked.edit();
                edit.putBoolean(NEW_CLICKED, true);
                edit.apply();

                Intent intent = new Intent(getActivity(), GameActivity.class);
                getActivity().startActivity(intent);

                Animator anim = AnimatorInflater
                        .loadAnimator(getActivity(), R.animator.buttons);
                anim.setTarget(newButton);
                anim.start();

            }

        });

        // continue button is clicked
        final View contButton = rootView.findViewById(R.id.continue_button);
        contButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences clicked = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = clicked.edit();
                edit.putBoolean(NEW_CLICKED, false);
                edit.apply();

                Intent intent = new Intent(getActivity(), GameActivity.class);

                getActivity().startActivity(intent);

                Animator anim = AnimatorInflater
                        .loadAnimator(getActivity(), R.animator.buttons);
                anim.setTarget(contButton);
                anim.start();
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
