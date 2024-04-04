package com.lazycoder.cakevpn.interfaces;

/**
 * Интерфейс для обработки нажатия на элемент навигации.
 */
public interface NavItemClickListener {

    /**
     * Обрабатывает нажатие на элемент навигации.
     *
     * @param index индекс элемента
     */
    void clickedItem(int index);
}
