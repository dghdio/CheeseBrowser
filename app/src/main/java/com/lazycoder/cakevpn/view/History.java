package com.lazycoder.cakevpn.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.lazycoder.cakevpn.R;
import java.util.List;

/**
 * Активность для отображения истории посещенных сайтов.
 */
public class History extends AppCompatActivity {
    MydbHandler dbHandler = new MydbHandler(this, null, null, 1);
    WebView webView;

    /**
     * Метод, вызываемый при создании активности.
     *
     * @param savedInstanceState сохраненное состояние активности
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Button clear_history_button = (Button) findViewById(R.id.clear_history_button);
        final List<String> sites = this.dbHandler.databaseToString();
        if (sites.size() > 0) {
            ListView mylist = (ListView) findViewById(R.id.listviewHistory);
            ArrayAdapter myadapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, sites);
            mylist.setAdapter((ListAdapter) myadapter);
            mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                /**
                 * Метод, вызываемый при щелчке на элементе списка истории.
                 *
                 * @param parent   родительский AdapterView
                 * @param view     текущий просмотр элемента
                 * @param position позиция щелчка
                 * @param id       идентификатор элемента
                 */
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = (String) sites.get(position);
                    Intent intent = new Intent(view.getContext(), browser.class);
                    intent.putExtra("urls", url);
                    History.this.startActivity(intent);
                    History.this.finish();
                }
            });
        }
        clear_history_button.setOnClickListener(new View.OnClickListener() {
            /**
             * Метод, вызываемый при щелчке на кнопке очистки истории.
             *
             * @param v текущий просмотр кнопки
             */
            @Override
            public void onClick(View v) {
                ListView mylist2 = (ListView) History.this.findViewById(R.id.listviewHistory);
                mylist2.setAdapter((ListAdapter) null);
                History.this.dbHandler.deleteAllUrls();
            }
        });
    }
}
