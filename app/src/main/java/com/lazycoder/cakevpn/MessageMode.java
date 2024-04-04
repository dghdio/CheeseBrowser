package com.lazycoder.cakevpn;

/**
 * Класс, представляющий режим сообщения.
 */
public class MessageMode {

    /**
     * Константа, указывающая, что сообщение отправлено мной.
     */
    static String SENT_BY_ME = "me";

    /**
     * Константа, указывающая, что сообщение отправлено ботом.
     */
    static String SENT_BY_BOT = "bot";

    private String message; // Текст сообщения
    private String sentBy; // Отправитель сообщения

    /**
     * Конструктор класса MessageMode.
     *
     * @param message Текст сообщения
     * @param sentBy  Отправитель сообщения
     */
    public MessageMode(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }

    /**
     * Получает текст сообщения.
     *
     * @return Текст сообщения
     */
    public String getMessage() {
        return message;
    }

    /**
     * Устанавливает текст сообщения.
     *
     * @param message Текст сообщения
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Получает отправителя сообщения.
     *
     * @return Отправитель сообщения
     */
    public String getSentBy() {
        return sentBy;
    }

    /**
     * Устанавливает отправителя сообщения.
     *
     * @param sentBy Отправитель сообщения
     */
    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }
}
