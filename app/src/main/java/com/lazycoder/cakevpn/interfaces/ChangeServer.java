package com.lazycoder.cakevpn.interfaces;

import com.lazycoder.cakevpn.model.Server;

/**
 * Интерфейс для обновления текущего сервера.
 */
public interface ChangeServer {

    /**
     * Обновляет текущий сервер.
     *
     * @param server новый сервер
     */
    void newServer(Server server);
}
