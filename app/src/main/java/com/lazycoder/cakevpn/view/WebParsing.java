package com.lazycoder.cakevpn.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lazycoder.cakevpn.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

/**
 * Класс WebParsing отвечает за парсинг веб-страницы.
 */
public class WebParsing extends Activity {
    Context context = this;
    private ArrayAdapter<String> adapter;
    public Elements content, content2;
    private ListView lv;

    public boolean checker = false;
    public ArrayList<String> titleList = new ArrayList<>();
    public EditText webAddressView;
    public WebView webvieww;
    public String translated, translated2;

    FirebaseTranslator englishRussianTranslator, englishRussianTranslator2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_parsing);
        this.lv = (ListView) findViewById(R.id.listview_parsing);
        this.webAddressView = (EditText) findViewById(R.id.et_web_address);
        this.webvieww = (WebView) findViewById(R.id.webView);
        Toast.makeText(WebParsing.this, "Please wait language modal is being downloaded.", Toast.LENGTH_LONG).show();
        new NewThread().execute(new String[0]);
        this.adapter = new ArrayAdapter<>(this, (int) R.layout.list_item, (int) R.id.pro_item, this.titleList);
    }

    /**
     * Класс NewThread отвечает за выполнение парсинга в фоновом потоке.
     */
    public class NewThread extends AsyncTask<String, Void, String> {
        public NewThread() {
        }

        /**
         * Метод doInBackground выполняет парсинг веб-страницы и переводит текст на русский язык.
         */
        @Override
        protected String doInBackground(String... arg) {
            try {
                Document doc = Jsoup.connect(browser.webAddressesView).get();
                content = doc.select("head");
                content2 = doc.select("body");
                titleList.clear();
                Iterator<Element> it = content.iterator();
                while (it.hasNext()) {
                    FirebaseTranslatorOptions options =
                            new FirebaseTranslatorOptions.Builder()
                                    // указываем исходный язык.
                                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                    // указываем целевой язык.
                                    .setTargetLanguage(FirebaseTranslateLanguage.RU)
                                    // создаем объект опций.
                                    .build();
                    englishRussianTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
                    Element contents = it.next();
                    translated = contents.text();
                    myThread.start();
                }
                Iterator<Element> it2 = content2.iterator();

                while (it2.hasNext())
                {
                    FirebaseTranslatorOptions options =
                            new FirebaseTranslatorOptions.Builder()
                                    // указываем исходный язык.
                                    .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                    // указываем целевой язык.
                                    .setTargetLanguage(FirebaseTranslateLanguage.RU)
                                    // создаем объект опций.
                                    .build();
                    englishRussianTranslator2 = FirebaseNaturalLanguage.getInstance().getTranslator(options);
                    Element contents2 = it2.next();
                    translated2 = contents2.text();
                    myThread2.start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    Thread myThread = new Thread(new Runnable() {
        @Override
        public void run() {
            downloadModal(translated);
        }
    });

    Thread myThread2 = new Thread(new Runnable() {
        @Override
        public void run() {
            downloadModal(translated2);
        }
    });

    /**
     * Метод downloadModal загружает языковую модель для перевода текста.
     */
    public void downloadModal(String input) {
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
        englishRussianTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                translateLanguage(input);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WebParsing.this, "Fail to download modal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Метод translateLanguage выполняет перевод текста на русский язык.
     */
    private void translateLanguage(String input) {
        englishRussianTranslator.translate(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                translated = s;
                titleList.add(translated);
                WebParsing.this.lv.setAdapter((ListAdapter) adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WebParsing.this, "Fail to translate", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
