package com.smokebreaker.www;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.util.Util;
import com.smokebreaker.www.bl.Settings;
import com.smokebreaker.www.bl.Statistics;
import com.smokebreaker.www.bl.models.Statistic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;
import jonathanfinerty.once.Once;

public class SetupActivity extends AppCompatActivity {

    @Inject
    Settings settings;

    @Inject
    Statistics statistics;

    @BindView(R.id.coldTurkeySection)
    View coldTurkeySection;

    @BindView(R.id.graduallySection)
    View graduallySection;

    @BindView(R.id.quittingMethod)
    Spinner quittingMethod;

    @BindView(R.id.packQuantity)
    EditText packQuantity;

    @BindView(R.id.packPrice)
    EditText packPrice;

    @BindView(R.id.currency)
    EditText currency;

    @BindView(R.id.quitDate)
    EditText quitDate;

    @BindView(R.id.initialCigarettesPerDay)
    EditText initialCigarettesPerDay;

    @BindView(R.id.todaysCigarettes)
    EditText todaysCigarettes;

    @BindView(R.id.finishButton)
    Button finishButton;

    @BindView(R.id.coldTurkeyExplanation)
    View coldTurkeyExplanation;

    @BindView(R.id.graduallyExplanation)
    View graduallyExplanation;
    private int todaysCigarettesNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        App.getAppComponent(this).inject(this);
        ButterKnife.bind(this);

        statistics.clear();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        adapter.add("Quit cold turkey");
        adapter.add("Quit gradually");
        quittingMethod.setAdapter(adapter);
        quittingMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                settings.setColdTurkey(i == 0);
                Utils.show(coldTurkeySection,i == 0);
                Utils.show(coldTurkeyExplanation,i == 0);
                Utils.show(graduallySection,i != 0);
                Utils.show(graduallyExplanation,i != 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        packQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validate();
            }
        });
        initialCigarettesPerDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validate();
            }
        });
        todaysCigarettes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validate();
            }
        });
        packPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validate();
            }
        });
        currency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currency.setEms(editable.length());
            }
        });
        currency.setOnFocusChangeListener((view1, b) -> {
            if(b)
                onCurrencyClick(currency);
        });
        currency.setOnClickListener(view1 -> onCurrencyClick(currency));
        quitDate.setOnFocusChangeListener((view1, b) -> {
            if(b)
                onQuitDateClick(quitDate);
        });
        quitDate.setOnClickListener(view1 -> onQuitDateClick(quitDate));

        packQuantity.setText(""+settings.getPackQuantity());
        packPrice.setText(""+settings.getPackPrice());
        initialCigarettesPerDay.setText(""+settings.getInitialCigarettesPerDay());
        todaysCigarettes.setText(""+0);
        currency.setText(settings.getCurrency().getSymbol());
        quitDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new Date(settings.getQuitDate())));
    }

    public void onCurrencyClick(View v){
        List<Currency> currencies = new ArrayList<>();
        currencies.addAll(Currency.getAvailableCurrencies());

        List<String> titles = new ArrayList<>();
        for(Currency c : currencies)
            titles.add(String.format("%s (%s)",c.getDisplayName(),c.getSymbol()));

        Collections.sort(titles,String::compareToIgnoreCase);

        new MaterialDialog.Builder(this)
                .items(titles)
                .itemsCallbackSingleChoice(currencies.indexOf(settings.getCurrency()),(dialog, itemView, position, text) -> {
                    settings.setCurrency(currencies.get(position));
                    currency.setText(settings.getCurrency().getSymbol());
                    return true;
                })
                /*.negativeText(R.string.cancel)
                .positiveText(R.string.ok)*/
                .cancelable(true)
                .autoDismiss(true)
                .show();
    }

    @OnClick(R.id.finishButton)
    public void onFinishButtonClick(View v){
        if(!settings.isColdTurkey()){
            statistics.clear();
            statistics.smoke(todaysCigarettesNumber);
        }
        Once.markDone("setup");
        startActivity(new Intent(this,SplashScreenActivity.class));
        finish();
    }

    private void validate(){
        boolean isGood = true;
        try{
            int q = Integer.parseInt(packQuantity.getText().toString());
            if(q > 0){
                settings.setPackQuantity(q);
                packQuantity.setEms(packQuantity.getText().length());
                packQuantity.setError(null);
            }
            else{
                packQuantity.setError(getString(R.string.non_positive_number_error));
                isGood = false;
            }
        }
        catch (NumberFormatException e){
            packQuantity.setError(getString(R.string.required_field));
            isGood = false;
        }

        try{
            float q = Float.parseFloat(packPrice.getText().toString());
            if(q > 0){
                settings.setPackPrice(q);
                packPrice.setEms(packPrice.getText().length());
                packPrice.setError(null);
            }
            else{
                packPrice.setError(getString(R.string.non_positive_number_error));
                isGood = false;
            }
        }
        catch (NumberFormatException e){
            packPrice.setError(getString(R.string.required_field));
            isGood = false;
        }

        try{
            int q = Integer.parseInt(initialCigarettesPerDay.getText().toString());
            if(q > 0){
                settings.setInitialCigarettesPerDay(q);
                initialCigarettesPerDay.setEms(initialCigarettesPerDay.getText().length());
                initialCigarettesPerDay.setError(null);
            }
            else{
                initialCigarettesPerDay.setError(getString(R.string.non_positive_number_error));
                isGood = false;
            }
        }
        catch (NumberFormatException e){
            initialCigarettesPerDay.setError(getString(R.string.required_field));
            isGood = false;
        }

        try{
            int q = Integer.parseInt(todaysCigarettes.getText().toString());
            if(q >= 0){
                todaysCigarettesNumber = q;
                todaysCigarettes.setEms(todaysCigarettes.getText().length());
                todaysCigarettes.setError(null);
            }
            else{
                todaysCigarettes.setError(getString(R.string.non_positive_number_error));
                isGood = false;
            }
        }
        catch (NumberFormatException e){
            todaysCigarettes.setError(getString(R.string.required_field));
            isGood = false;
        }


        finishButton.setEnabled(isGood);
    }

    private void onQuitDateClick(EditText currency) {
        long time = settings.getQuitDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        DatePickerDialog dialog = new DatePickerDialog(this, (datePicker, i, i1, i2) -> {
            calendar.set(Calendar.YEAR,i);
            calendar.set(Calendar.MONTH,i1);
            calendar.set(Calendar.DAY_OF_MONTH,i2);
            settings.setQuitDate(calendar.getTimeInMillis());
            quitDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new Date(calendar.getTimeInMillis())));
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.show();
    }
}
