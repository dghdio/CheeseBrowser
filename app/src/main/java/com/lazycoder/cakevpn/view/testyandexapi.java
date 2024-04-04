package com.lazycoder.cakevpn.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.lazycoder.cakevpn.R;

/**
 * Это активность, которая демонстрирует перевод текста на различные языки.
 */
public class testyandexapi extends AppCompatActivity {

    private EditText edtLanguage;
    private EditText translateLanguageTV;
    private Button translateLanguageBtn;
    private Drawable drawableRenamed, from_notRenamed, to_notRenamed;
    private Spinner fromSpinner, toSpinner;
    String[] fromLanguages = {"", "English", "Russian", "Belarusian", "Czech", "Hindi", "Arabic"};
    String[] toLanguages = {"", "English", "Russian", "Belarusian", "Czech", "Hindi", "Arabic"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    int languageCode, fromLanguageCode, toLanguageCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testyandexapi);
        Resources res = getResources();
        drawableRenamed = ResourcesCompat.getDrawable(res, R.drawable.empty, null);
        from_notRenamed = ResourcesCompat.getDrawable(res, R.drawable.from, null);
        to_notRenamed = ResourcesCompat.getDrawable(res, R.drawable.to, null);
        edtLanguage = findViewById(R.id.idEdtLanguage);
        translateLanguageTV = findViewById(R.id.idTVTranslatedLanguage);
        translateLanguageBtn = findViewById(R.id.idBtnTranslateLanguage);
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.ToSpinner);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode = getLanguageCode(fromLanguages[position]);
                if(fromLanguageCode != 0)
                {
                    fromSpinner.setBackground(drawableRenamed);
                } else{
                    fromSpinner.setBackground(from_notRenamed);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter fromAdapter = new ArrayAdapter(this, R.layout.spinner_item, fromLanguages);

        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode = getLanguageCode(fromLanguages[position]);
                if(toLanguageCode != 0)
                {
                    toSpinner.setBackground(drawableRenamed);
                } else{
                    toSpinner.setBackground(to_notRenamed);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter toAdapter = new ArrayAdapter(this, R.layout.spinner_item, toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        translateLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateLanguageTV.setText("");
                if(edtLanguage.getText().toString().isEmpty())
                {
                    Toast.makeText(testyandexapi.this, "Please, input text to translate!", Toast.LENGTH_SHORT).show();
                }
                else if(fromLanguageCode==0)
                {
                    Toast.makeText(testyandexapi.this, "Please, select from language to translate!", Toast.LENGTH_SHORT).show();
                }
                else if(toLanguageCode==0)
                {
                    Toast.makeText(testyandexapi.this, "Please, select language to translate!", Toast.LENGTH_SHORT).show();
                }
                else{
                    translateText(fromLanguageCode, toLanguageCode, edtLanguage.getText().toString());
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PERMISSION_CODE)
        {
            if(resultCode == RESULT_OK && data!=null)
            {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                edtLanguage.setText(result.get(0));
            }
        }
    }

    /**
     * Метод, который выполняет перевод текста
     *
     * @param fromLanguageCode код языка исходного текста
     * @param toLanguageCode код языка, на который нужно перевести текст
     * @param source исходный текст
     */
    private void translateText(int fromLanguageCode, int toLanguageCode, String source)
    {
        translateLanguageTV.setText("Downloading model...");
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translateLanguageTV.setText("Translating...");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translateLanguageTV.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(testyandexapi.this, "Fail translate: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(testyandexapi.this, "Fail download model: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Метод, который возвращает код языка в соответствии с его названием.
     *
     * @param language название языка
     * @return код языка
     */
    public int getLanguageCode(String language) {
        int languageCode = 0;
        switch (language){
            case "English":
                languageCode = FirebaseTranslateLanguage.EN;
                break;
            case "Russian":
                languageCode = FirebaseTranslateLanguage.RU;
                break;
            case "Belarusian":
                languageCode = FirebaseTranslateLanguage.BE;
                break;
            case "Czech":
                languageCode = FirebaseTranslateLanguage.CS;
                break;
            case "Hindi":
                languageCode = FirebaseTranslateLanguage.HI;
                break;
            case "Arabic":
                languageCode = FirebaseTranslateLanguage.AR;
                break;
            default:
                languageCode = 0;
        }


        return languageCode;
    }
}
