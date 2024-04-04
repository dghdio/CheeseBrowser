package com.lazycoder.cakevpn.view;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.lazycoder.cakevpn.Chatgpt_Activity;
import com.lazycoder.cakevpn.R;

/**
 * Активность браузера, отображающая веб-содержимое и предоставляющая функции навигации и загрузки файлов.
 */
public class browser extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    MydbHandler dbHandler;
    myDbHandlerBook dbHandlerbook;
    EditText webAddressView;
    WebView webView;
    String url;
    static String webAddressesView = "";

    // Объект SharedPreferences для сохранения URL
    SharedPreferences sharedPreferences;

    /**
     * Создает активность браузера.
     *
     * @param savedInstanceState сохраненное состояние активности
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_view);
        Button goButton = (Button) findViewById(R.id.goButton);
        this.webAddressView = (EditText) findViewById(R.id.et_web_address);
        AdBlocker.init(this);
        WebView webView = (WebView) findViewById(R.id.webView);

        // Получение объекта SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        webAddressView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    url = webAddressView.getText().toString();
                    webAddressesView = "https://" + url;
                    if(url.contains("http://") || url.contains("https://")) {
                        webView.loadUrl(url);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(webAddressView.getWindowToken(), 0);
                    } else {
                        webView.loadUrl("https://" + url);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(webAddressView.getWindowToken(), 0);
                    }
                    saveData();
                    return true;
                }
                return false;
            }
        });

        this.webView = webView;
        webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebViewClient(new MyWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                browser.this.saveData();
                webAddressesView = url;
                webAddressView.setText(webAddressesView);
            }
        });

        // Загрузка сохраненного URL из SharedPreferences
        String savedUrl = sharedPreferences.getString("saved_url", "http://45.82.152.233:8080/");
        this.webView.loadUrl(savedUrl);

        if (getIntent().getStringExtra("urls") != null) {
            this.webView.loadUrl(getIntent().getStringExtra("urls"));
        }
        this.dbHandler = new MydbHandler(this, null, null, 1);
        this.dbHandlerbook = new myDbHandlerBook(this, null, null, 1);
        saveData();
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(browser.this, v);
                popup.setOnMenuItemClickListener(browser.this);
                popup.inflate(R.menu.menu_main);
                popup.show();
            }
        });
        this.webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                browser.this.saveData();
                if (Build.VERSION.SDK_INT >= 23) {
                    if (browser.this.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
                        Log.v("TAG", "Permission is granted");
                        browser.this.DownloadAlerter(url, userAgent, contentDisposition, mimetype);
                        browser.this.saveData();
                        return;
                    }
                    Log.v("TAG", "Permission revoked");
                    ActivityCompat.requestPermissions(browser.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
                    return;
                }
                Log.v("TAG", "Permission granted");
                browser.this.DownloadAlerter(url, userAgent, contentDisposition, mimetype);
            }
        });
    }

    /**
     * Обработчик события нажатия клавиши на клавиатуре.
     *
     * @param keyCode код нажатой клавиши
     * @param event   объект события KeyEvent
     * @return true, если событие обработано, false в противном случае
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                url = this.webAddressView.getText().toString();
                webAddressesView = "https://" + url;
                WebView webView = this.webView;
                if(url.contains("http://") || url.contains("https://")) {
                    webView.loadUrl(url);
                } else {
                    webView.loadUrl("https://" + url);
                }
                saveData();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }



    /**
     * Обработчик события выбора пункта меню.
     *
     * @param item выбранный пункт меню
     * @return true, если событие обработано успешно, иначе - false
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_bookmark:
                onBookPressed();
                Toast.makeText(this, "Page added to bookmarks", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_open_bookmarks:
                Intent book = new Intent(this, Bookmarks.class);
                startActivity(book);
                return true;
            case R.id.action_open_history:
                Intent history = new Intent(this, History.class);
                startActivity(history);
                return true;
            case R.id.action_vpn:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return true;
            case R.id.action_web_parsing:
                Intent parse = new Intent(this, WebParsing.class);
                startActivity(parse);
                return true;
            case R.id.action_yandex:
                this.webView.loadUrl("http://45.82.152.233:8080/");
                return true;
            case R.id.action_translator:
                Intent translatorIntent = new Intent(this, testyandexapi.class);
                startActivity(translatorIntent);
                return true;
            case R.id.action_pdf:
                Intent pdf = new Intent(this, PdfViewer.class);
                startActivity(pdf);
                return true;
            case R.id.action_chatgpt:
                Intent chatgpt = new Intent(this, Chatgpt_Activity.class);
                startActivity(chatgpt);
                return true;
            default:
                return false;
        }
    }

    /**
     * Обработчик события нажатия на кнопку "Назад".
     * Если веб-представление может перейти назад, переходим на предыдущую страницу.
     * Иначе, вызываем реализацию по умолчанию.
     */
    @Override
    public void onBackPressed() {
        WebView webView = (WebView) findViewById(R.id.webView);
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
        saveData();
    }

    /**
     * Обработчик события нажатия на кнопку добавления в закладки.
     * Создает объект Websites с текущим URL и добавляет его в базу данных закладок.
     */
    private void onBookPressed() {
        Websites web = new Websites(this.webView.getUrl());
        this.dbHandlerbook.addUrl(web);
        saveData();
    }

    /**
     * Сохраняет данные, включая текущий URL, в SharedPreferences и базу данных.
     */
    private void saveData() {
        Websites webv = new Websites(this.webView.getUrl());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("saved_url", url);
        editor.apply();

        this.dbHandler.addUrl(webv);
    }

    /**
     * Показывает диалоговое окно загрузки.
     *
     * @param url                URL файла для загрузки
     * @param userAgent          User-Agent приложения
     * @param contentDisposition значение заголовка Content-Disposition
     * @param mimetype           MIME-тип файла
     */
    public void DownloadAlerter(final String url, final String userAgent, String contentDisposition, String mimetype) {
        final String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View myView = getLayoutInflater().inflate(R.layout.custom_dialog_for_download, (ViewGroup) null);
        Button buttonYes = (Button) myView.findViewById(R.id.Button_Yes);
        Button buttonNo = (Button) myView.findViewById(R.id.Button_No);
        EditText EditTextFileName = (EditText) myView.findViewById(R.id.EditTextFile_Name);
        EditText EditTextFileUrl = (EditText) myView.findViewById(R.id.EditTextFile_Url);
        builder.setView(myView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        EditTextFileName.setText(filename);
        EditTextFileUrl.setText(url);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                String cookie = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("Cookie", cookie);
                request.addRequestHeader("User-Agent", userAgent);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                DownloadManager downloadManager = (DownloadManager) browser.this.getSystemService(Context.DOWNLOAD_SERVICE);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                downloadManager.enqueue(request);
                alertDialog.dismiss();
            }
        });
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}

