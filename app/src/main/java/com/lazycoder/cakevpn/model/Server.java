package com.lazycoder.cakevpn.model;

/**
 * Модель представления сервера.
 */
public class Server {
    private String country; // Название страны
    private String flagUrl; // URL-адрес флага страны
    private String ovpn; // Содержимое конфигурационного файла OpenVPN
    private String ovpnUserName; // Имя пользователя OpenVPN (опционально)
    private String ovpnUserPassword; // Пароль пользователя OpenVPN (опционально)

    /**
     * Создает объект Server с указанными параметрами.
     *
     * @param country           название страны
     * @param flagUrl           URL-адрес флага страны
     * @param ovpn              содержимое конфигурационного файла OpenVPN
     */
    public Server(String country, String flagUrl, String ovpn) {
        this.country = country;
        this.flagUrl = flagUrl;
        this.ovpn = ovpn;
    }

    /**
     * Создает объект Server с указанными параметрами.
     *
     * @param country           название страны
     * @param flagUrl           URL-адрес флага страны
     * @param ovpn              содержимое конфигурационного файла OpenVPN
     * @param ovpnUserName      имя пользователя OpenVPN (опционально)
     * @param ovpnUserPassword  пароль пользователя OpenVPN (опционально)
     */
    public Server(String country, String flagUrl, String ovpn, String ovpnUserName, String ovpnUserPassword) {
        this.country = country;
        this.flagUrl = flagUrl;
        this.ovpn = ovpn;
        this.ovpnUserName = ovpnUserName;
        this.ovpnUserPassword = ovpnUserPassword;
    }

    /**
     * Возвращает название страны.
     *
     * @return название страны
     */
    public String getCountry() {
        return country;
    }

    /**
     * Устанавливает название страны.
     *
     * @param country название страны
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Возвращает URL-адрес флага страны.
     *
     * @return URL-адрес флага страны
     */
    public String getFlagUrl() {
        return flagUrl;
    }

    /**
     * Устанавливает URL-адрес флага страны.
     *
     * @param flagUrl URL-адрес флага страны
     */
    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }

    /**
     * Возвращает содержимое конфигурационного файла OpenVPN.
     *
     * @return содержимое конфигурационного файла OpenVPN
     */
    public String getOvpn() {
        return ovpn;
    }

    /**
     * Устанавливает содержимое конфигурационного файла OpenVPN.
     *
     * @param ovpn содержимое конфигурационного файла OpenVPN
     */
    public void setOvpn(String ovpn) {
        this.ovpn = ovpn;
    }

    /**
     * Возвращает имя пользователя OpenVPN.
     *
     * @return имя пользователя OpenVPN
     */
    public String getOvpnUserName() {
        return ovpnUserName;
    }

    /**
     * Устанавливает имя пользователя OpenVPN.
     *
     * @param ovpnUserName имя пользователя OpenVPN
     */
    public void setOvpnUserName(String ovpnUserName) {
        this.ovpnUserName = ovpnUserName;
    }

    /**
     * Возвращает пароль пользователя OpenVPN.
     *
     * @return пароль пользователя OpenVPN
     */
    public String getOvpnUserPassword() {
        return ovpnUserPassword;
    }

    /**
     * Устанавливает пароль пользователя OpenVPN.
     *
     * @param ovpnUserPassword пароль пользователя OpenVPN
     */
    public void setOvpnUserPassword(String ovpnUserPassword) {
        this.ovpnUserPassword = ovpnUserPassword;
    }
}
