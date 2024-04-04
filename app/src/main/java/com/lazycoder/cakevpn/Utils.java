package com.lazycoder.cakevpn;

import android.net.Uri;

/**
 * Утилитный класс для обработки различных операций.
 */
public class Utils {

    /**
     * Преобразует ресурс изображения в строку с путем к изображению.
     *
     * @param resourceId ресурс изображения
     * @return путь к изображению
     */
    public static String getImgURL(int resourceId) {

        // Используйте BuildConfig.APPLICATION_ID вместо R.class.getPackage().getName(), если они не совпадают
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }
}
