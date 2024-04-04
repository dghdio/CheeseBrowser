package com.lazycoder.cakevpn.view;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceResponse;

import androidx.annotation.WorkerThread;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс AdBlocker предоставляет функциональность для блокировки рекламных ресурсов.
 */
public class AdBlocker {

    private static final String AD_HOSTS_FILE = "host.txt";
    private static final Set<String> AD_HOSTS = new HashSet<>();

    /**
     * Инициализирует блокировку рекламы.
     *
     * @param context контекст приложения
     */
    public static void init(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    loadFromAssets(context);
                } catch (IOException e) {
                    // noop
                }
                return null;
            }
        }.execute();
    }

    /**
     * Загружает список хостов из файла в ассетах.
     *
     * @param context контекст приложения
     * @throws IOException при ошибке чтения файла
     */
    @WorkerThread
    private static void loadFromAssets(Context context) throws IOException {
        InputStream stream = context.getAssets().open(AD_HOSTS_FILE);
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            AD_HOSTS.add(line);
        }
        bufferedReader.close();
        inputStreamReader.close();
        stream.close();
    }

    /**
     * Проверяет, является ли указанный URL рекламным ресурсом.
     *
     * @param url URL для проверки
     * @return true, если URL является рекламным ресурсом, иначе false
     */
    public static boolean isAd(String url) {
        try {
            return isAdHost(getHost(url)) || AD_HOSTS.contains(Uri.parse(url).getLastPathSegment());
        } catch (MalformedURLException e) {
            Log.d("Ind", e.toString());
            return false;
        }
    }

    /**
     * Проверяет, является ли указанный хост рекламным хостом.
     *
     * @param host хост для проверки
     * @return true, если хост является рекламным хостом, иначе false
     */
    private static boolean isAdHost(String host) {
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        int index = host.indexOf(".");
        return index >= 0 && (AD_HOSTS.contains(host) ||
                index + 1 < host.length() && isAdHost(host.substring(index + 1)));
    }

    /**
     * Возвращает хост указанного URL.
     *
     * @param url URL для получения хоста
     * @return хост URL
     * @throws MalformedURLException при некорректном URL
     */
    public static String getHost(String url) throws MalformedURLException {
        return new URL(url).getHost();
    }

    /**
     * Создает пустой ресурс веб-страницы.
     *
     * @return пустой ресурс веб-страницы
     */
    public static WebResourceResponse createEmptyResource() {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
    }
}
