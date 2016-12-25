package com.smokebreaker.www.pl.breaks;


import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smokebreaker.www.App;
import com.smokebreaker.www.R;
import com.smokebreaker.www.Utils;
import com.smokebreaker.www.bl.SmokeBreaksManager;
import com.smokebreaker.www.bl.Statistics;
import com.trello.rxlifecycle.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class BreaksFragment extends RxFragment {

    @Inject
    SmokeBreaksManager smokeBreaksManager;

    @Inject
    Statistics statistics;

    @BindView(R.id.breaksPerDay)
    EditText breaksPerDay;

    @BindView(R.id.firstCigaretteTime)
    TextView firstCigaretteTime;

    @BindView(R.id.lastCigaretteTime)
    TextView lastCigaretteTime;

    @BindView(R.id.autoBreaks)
    View autoBreaks;

    @BindView(R.id.autoFirstCigarette)
    View autoFirstCigarette;

    @BindView(R.id.autoLastCigarette)
    View autoLastCigarette;

    @BindView(R.id.resetButton)
    Button resetButton;

    public BreaksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.breaks, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent(getContext()).inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        init(true);
    }

    private void init(boolean b) {
        breaksPerDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals(smokeBreaksManager.getNumberOfBreaks()+""))
                    return;
                try{
                    int q = Integer.parseInt(editable.toString());
                    if(q > 0){
                        smokeBreaksManager.setNumberOfBreaks(q);
                        init(false);
                    }
                    else
                        breaksPerDay.setError("Please enter a positive number");
                }
                catch (NumberFormatException e){
                    breaksPerDay.setError(getContext().getString(R.string.required_field));
                }
            }
        });
        if(b)
            breaksPerDay.setText(smokeBreaksManager.getNumberOfBreaks()+"");

        firstCigaretteTime.setText(smokeBreaksManager.getStart() != null ? getTimeString(smokeBreaksManager.getStart()) : "NOT SET");
        lastCigaretteTime.setText(smokeBreaksManager.getEnd() != null ? getTimeString(smokeBreaksManager.getEnd()) : "NOT SET");

        Utils.show(autoBreaks,smokeBreaksManager.getNumberOfBreaks() == statistics.cigarettesPerDay() - 1);
        Utils.show(autoFirstCigarette,smokeBreaksManager.getStart() != null && smokeBreaksManager.getStart() == statistics.firstCigaretteTime());
        Utils.show(autoLastCigarette,smokeBreaksManager.getEnd() != null && smokeBreaksManager.getEnd() == statistics.lastCigaretteTime());

        resetButton.setEnabled((
                smokeBreaksManager.getNumberOfBreaks() != statistics.cigarettesPerDay() - 1 ||
                        smokeBreaksManager.getStart() == null ||
                        smokeBreaksManager.getStart() != statistics.firstCigaretteTime() ||
                        smokeBreaksManager.getEnd() == null ||
                        smokeBreaksManager.getEnd() != statistics.lastCigaretteTime()) && statistics.isReady());
    }

    private String getTimeString(int mins) {
        int hour = mins/60;
        int minutes = mins - (hour*60);

        return String.format("%02d:%02d",hour,minutes);
    }

    private int getHours(int mins) {
        return mins/60;
    }

    private int getMinutes(int mins) {
        int hour = mins/60;
        return mins - (hour*60);
    }

    @OnClick(R.id.firstCigaretteTimeButton)
    public void onFirstCigaretteTimeButtonClick(View view){
        new TimePickerDialog(getContext(),
                (view1, hourOfDay, minute) ->{
                    int minutes = (hourOfDay*60)+minute;
                    smokeBreaksManager.setStart(minutes);
                    init(false);
                },
                smokeBreaksManager.getStart() != null ? getHours(smokeBreaksManager.getStart()) : 8,
                smokeBreaksManager.getStart() != null ? getMinutes(smokeBreaksManager.getStart()) : 30,
                true)
                .show();
    }

    @OnClick(R.id.lastCigaretteTimeButton)
    public void onLastCigaretteTimeButtonClick(View view){
        new TimePickerDialog(getContext(),
                (view1, hourOfDay, minute) ->{
                    int minutes = (hourOfDay*60)+minute;
                    smokeBreaksManager.setEnd(minutes);
                    init(false);
                },
                smokeBreaksManager.getEnd() != null ? getHours(smokeBreaksManager.getEnd()) : 21,
                smokeBreaksManager.getEnd() != null ? getMinutes(smokeBreaksManager.getEnd()) : 00,
                true)
                .show();
    }

    @OnClick(R.id.switchTimesButton)
    public void onSwitchTimesButtonClick(View view){
        Integer start = smokeBreaksManager.getStart();
        Integer end = smokeBreaksManager.getEnd();

        smokeBreaksManager.setStart(end);
        smokeBreaksManager.setEnd(start);
        init(false);
    }

    @OnClick(R.id.resetButton)
    public void onResetButtonClick(View view){
        smokeBreaksManager.reset();
        init(false);
    }
}
