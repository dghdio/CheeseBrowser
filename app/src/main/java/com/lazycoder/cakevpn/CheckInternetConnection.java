package com.lazycoder.cakevpn;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Класс отвечает за проверку статуса интернет-соединения.
 */
public class CheckInternetConnection {

    /**
     * Проверяет статус интернет-соединения.
     * @param context контекст приложения
     * @return статус соединения: true, если есть подключение к интернету, иначе false
     */
    public boolean netCheck(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();

        boolean isConnected = nInfo != null && nInfo.isConnectedOrConnecting();
        return isConnected;
    }
}
