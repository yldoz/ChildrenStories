package com.example.alamaudioplayer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alamaudioplayer.databinding.ActivityLoginBinding;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

public class Login extends AppCompatActivity {
    String TAG = "login";
    ArrayList<Lang> langList = new ArrayList<>();
    DatePickerDialog.OnDateSetListener dateSetListener1, dateSetListener2;
    String birthDayDate, todayDate;
    int totalAge;

    private void getAllLang() {
      /*  SortedSet<String> allLanguages = new TreeSet<String>();
        String[] languages = Locale.getISOLanguages();
        for (int i = 0; i < languages.length; i++) {
            Locale loc = new Locale(languages[i]);
            Log.d(TAG, "getAllLang: " + loc.getDisplayLanguage());
            if(loc.getDisplayLanguage().toLowerCase().equals("english") || loc.getDisplayLanguage().toLowerCase().equals("german")){
                langList.add(new Lang(loc));
                Log.d(TAG, "getAllLang: " + loc.getLanguage());
                allLanguages.add(loc.getDisplayLanguage());
            }

        }*/
        langList.add(new Lang(Locale.ENGLISH));
        langList.add(new Lang(Locale.GERMAN));
        Collections.sort(langList, new Comparator<Lang>() {
            @Override
            public int compare(Lang lang, Lang t1) {
                return lang.getLocale().getDisplayLanguage().compareTo(t1.getLocale().getDisplayLanguage());
            }
        });
    }


    ActivityLoginBinding binding;
    LocalListDialogFragment bottomSheet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String language = Locale.getDefault().getDisplayLanguage();

        if (language != null) {
            binding.editLange.setText(language);
        }
        PrefManager prefManager = new PrefManager(this);
        boolean first = prefManager.getBoolean("save");
        if (first) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();

        } else {
            getAllLang();

            binding.getBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (binding.getBirthday.getText().toString().isEmpty()) {
                        binding.getBirthday.setError("Enter Age");
                        return;
                  /*  } else if (binding.toage.getText().toString().isEmpty()) {
                        binding.toage.setError("Enter age");
                        return;*/
                    } else if (binding.editLange.getText().toString().isEmpty()) {
                        binding.editLange.setError("Select language");
                        return;
                    }
                    prefManager.setInt("age", totalAge);
                    prefManager.setBoolean("save", true);


                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();

                }
            });

            // age calculation

            // calendar format is imported to pick date
            Calendar calendar = Calendar.getInstance();

            // for year
            int year = calendar.get(Calendar.YEAR);

            // for month
            int month = calendar.get(Calendar.MONTH);

            // for date
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            // to set the current date as by default
            todayDate = simpleDateFormat.format(Calendar.getInstance().getTime());


            // action to be performed when button 1 is clicked
            binding.getBirthday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // date picker dialog is used
                    // and its style and color are also passed
                    DatePickerDialog datePickerDialog = new DatePickerDialog(Login.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener1, year, month, day
                    );
                    // to set background for datepicker
                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    datePickerDialog.show();
                }
            });

            // it is used to set the date which user selects
            dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    // here month+1 is used so that
                    // actual month number can be displayed
                    // otherwise it starts from 0 and it shows
                    // 1 number less for every month
                    // example- for january month=0
                    month = month + 1;
                    String date = day + "/" + month + "/" + year;
                    birthDayDate = date;
                    binding.getBirthday.setText(birthDayDate);
                    // converting the inputted date to string
                    String sDate = birthDayDate;
                    String eDate = todayDate;
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        // converting it to date format
                        Date date1 = simpleDateFormat1.parse(sDate);
                        Date date2 = simpleDateFormat1.parse(eDate);

                        long startdate = date1.getTime();
                        long endDate = date2.getTime();

                        // condition
                        if (startdate <= endDate) {
                            org.joda.time.Period period = new Period(startdate, endDate, PeriodType.yearMonthDay());
                            int years = period.getYears();
                            int months = period.getMonths();
                            int days = period.getDays();

                            // show the final output
                            // binding.getBirthday.setText(years + " Years |" + months + "Months |" + days + "Days");
                            totalAge = years;
                        } else {
                            // show message
                            Toast.makeText(Login.this, "BirthDate should not be larger than today's date!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        binding.editLange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet = LocalListDialogFragment.newInstance(langList, new OnLangSelect() {
                    @Override
                    public void onLangSelectListener(Lang lang) {
                        try {
                            bottomSheet.dismiss();
                        } catch (Exception e) {

                        }
                        binding.editLange.setText(lang.getLocale().getDisplayLanguage(Locale.ENGLISH));
                        prefManager.setString("lang", lang.getLocale().getDisplayLanguage(Locale.ENGLISH).toLowerCase());
                        prefManager.setString("langcode", lang.getLocale().getLanguage().toLowerCase());

                    }
                });
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");
            }
        });


    }

    public interface OnLangSelect {


        void onLangSelectListener(Lang lang);
    }
}