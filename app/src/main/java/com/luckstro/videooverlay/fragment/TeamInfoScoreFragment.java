package com.luckstro.videooverlay.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luckstro.videooverlay.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeamInfoScoreFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeamInfoScoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeamInfoScoreFragment extends Fragment {
    private static final String TEAM_TYPE = "TEAM_TYPE";
    public static final String AWAY_TEAM = "awayTeam";
    public static final String HOME_TEAM = "homeTeam";

    private String teamType;

    private OnFragmentInteractionListener mListener;

    public TeamInfoScoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param teamType The type of team.  Either away or home.
     *
     * @return A new instance of fragment TeamInfoScoreFragment.
     */
    public static TeamInfoScoreFragment newInstance(String teamType) {
        TeamInfoScoreFragment fragment = new TeamInfoScoreFragment();
        Bundle args = new Bundle();
        args.putString(TEAM_TYPE, teamType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            teamType = getArguments().getString(TEAM_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_team_info_score, container, false);
        TextView textView = (TextView) view.findViewById(R.id.team_fragment_title);
        if (HOME_TEAM.equals(teamType))
            textView.setText(R.string.home_team);
        else
            textView.setText(R.string.away_team);
        return view;
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
