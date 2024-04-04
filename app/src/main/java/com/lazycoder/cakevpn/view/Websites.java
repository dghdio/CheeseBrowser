package com.lazycoder.cakevpn.view;

import android.widget.ImageView;

/**
 * Класс, представляющий веб-сайты.
 */
public class Websites {
    private int _id;                // Идентификатор
    private String _url;            // URL адрес
    private ImageView image;        // Изображение
    private String title;           // Название

    /**
     * Конструктор без параметров.
     */
    public Websites() {
    }

    /**
     * Конструктор с параметром URL адреса.
     *
     * @param url URL адрес веб-сайта.
     */
    public Websites(String url) {
        this._url = url;
    }

    /**
     * Конструктор с параметрами URL адреса и названия.
     *
     * @param url   URL адрес веб-сайта.
     * @param title Название веб-сайта.
     */
    public Websites(String url, String title) {
        this._url = url;
        this.title = title;
    }

    /**
     * Возвращает идентификатор веб-сайта.
     *
     * @return Идентификатор веб-сайта.
     */
    public int get_id() {
        return this._id;
    }

    /**
     * Устанавливает идентификатор веб-сайта.
     *
     * @param _id Идентификатор веб-сайта.
     */
    public void set_id(int _id) {
        this._id = _id;
    }

    /**
     * Возвращает URL адрес веб-сайта.
     *
     * @return URL адрес веб-сайта.
     */
    public String get_url() {
        return this._url;
    }

    /**
     * Устанавливает URL адрес веб-сайта.
     *
     * @param _url URL адрес веб-сайта.
     */
    public void set_url(String _url) {
        this._url = _url;
    }

    /**
     * Возвращает изображение веб-сайта.
     *
     * @return Изображение веб-сайта.
     */
    public ImageView getImage() {
        return this.image;
    }

    /**
     * Устанавливает изображение веб-сайта.
     *
     * @param image Изображение веб-сайта.
     */
    public void setImage(ImageView image) {
        this.image = image;
    }

    /**
     * Возвращает название веб-сайта.
     *
     * @return Название веб-сайта.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Устанавливает название веб-сайта.
     *
     * @param title Название веб-сайта.
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
