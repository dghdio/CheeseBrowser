package com.lazycoder.cakevpn;

/**
 * Класс, представляющий PDF-файл.
 */
public class pdfClass {

    public String name; // Имя файла
    public String url; // URL-адрес файла

    /**
     * Конструктор без параметров.
     */
    public pdfClass() {}

    /**
     * Конструктор с параметрами.
     *
     * @param name Имя файла
     * @param url  URL-адрес файла
     */
    public pdfClass(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * Получает имя файла.
     *
     * @return Имя файла
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя файла.
     *
     * @param name Имя файла
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Получает URL-адрес файла.
     *
     * @return URL-адрес файла
     */
    public String getUrl() {
        return url;
    }

    /**
     * Устанавливает URL-адрес файла.
     *
     * @param url URL-адрес файла
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
