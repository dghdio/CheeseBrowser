package com.lazycoder.cakevpn.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;
import com.lazycoder.cakevpn.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Активность для отображения PDF-файла в виде веб-просмотра.
 */
public class PdfWebView extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_web_view);
        pdfView = findViewById(R.id.pdfView);

        // Получение URL PDF-файла из интента
        Intent intent = getIntent();
        String Url = intent.getStringExtra("PdfURL");
        Log.i("PDFF", Url);

        // Запуск асинхронной задачи для загрузки и отображения PDF-файла
        new PdfWebView.Retrivepdf().execute(Url);
    }

    /**
     * Асинхронная задача для загрузки PDF-файла.
     */
    class Retrivepdf extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL urll = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // Загрузка и отображение PDF-файла в PDFView
            pdfView.fromStream(inputStream).load();
        }
    }
}
