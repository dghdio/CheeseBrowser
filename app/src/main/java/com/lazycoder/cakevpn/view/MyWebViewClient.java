package com.lazycoder.cakevpn.view;

import android.annotation.TargetApi;
import android.net.http.SslError;
import android.os.Build;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс-наследник WebViewClient, используемый для настройки WebView.
 * Он обрабатывает различные события, связанные с загрузкой веб-страницы.
 */
public class MyWebViewClient extends WebViewClient {

    /**
     * Метод, вызываемый при получении ошибки SSL.
     * Он игнорирует ошибки сертификата SSL.
     *
     * @param view    WebView, в котором произошла ошибка
     * @param handler обработчик ошибки SSL
     * @param error   информация об ошибке SSL
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed(); // Игнорировать ошибки сертификата SSL
    }

    /**
     * Метод, вызываемый для перехвата и обработки URL-адресов при загрузке веб-страницы.
     * Он загружает URL-адрес в WebView и возвращает значение true для указания, что перехват был выполнен.
     *
     * @param view    WebView, в котором выполняется загрузка
     * @param request информация о запросе загрузки веб-ресурса
     * @return true, если перехват выполнен успешно; false в противном случае
     */
    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        view.loadUrl(request.getUrl().toString());
        return true;
    }

    private Map<String, Boolean> loadedUrls = new HashMap<>();

    /**
     * Метод, вызываемый для перехвата и обработки запросов ресурсов, связанных с загружаемой веб-страницей.
     * Он проверяет, является ли URL-адрес рекламным и возвращает соответствующий ресурс.
     *
     * @param view WebView, в котором выполняется загрузка
     * @param url  URL-адрес ресурса
     * @return WebResourceResponse с ресурсом или null, если ресурс не перехватывается
     */
    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        boolean ad;
        if (!loadedUrls.containsKey(url)) {
            ad = AdBlocker.isAd(url);
            loadedUrls.put(url, ad);
        } else {
            ad = loadedUrls.get(url);
        }
        return ad ? AdBlocker.createEmptyResource() :
                super.shouldInterceptRequest(view, url);
    }
}
