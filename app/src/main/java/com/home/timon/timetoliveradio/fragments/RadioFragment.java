package com.home.timon.timetoliveradio.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.home.timon.timetoliveradio.MainActivity;
import com.home.timon.timetoliveradio.R;

public class RadioFragment extends Fragment{

    //region Vars

    private static final String TAG = RadioFragment.class.getName();

    private boolean paused;
    private Button bt_play_pause;
    private View viewFragment;

    //endregion


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        paused = true;
        viewFragment = inflater.inflate(R.layout.fragment_radio, container, false);

        //region Button Play/Pause logic
        bt_play_pause = viewFragment.findViewById(R.id.radio_button_play_pause);
        bt_play_pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24px);
        bt_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.radio_button_play_pause:
                        if (paused) {
                            bt_play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_24px);
                            ((MainActivity)getActivity()).startPlayer();
                            paused = false;
                        } else {
                            bt_play_pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24px);
                            paused = true;
                            ((MainActivity)getActivity()).pausePlayer();
                        }
                }
            }
        });
        //endregion

        return viewFragment;
    }
}
