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
 * Активность Bookmarks отображает список закладок и предоставляет функциональность для их управления.
 */
public class Bookmarks extends AppCompatActivity {
    myDbHandlerBook dbHandlerBook = new myDbHandlerBook(this, null, null, 1);
    WebView webView;

    /**
     * Создает активность Bookmarks.
     *
     * @param savedInstanceState сохраненное состояние активности
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        Button clear_bookmarks = (Button) findViewById(R.id.clear_bookmarks_button);
        final List<String> books = this.dbHandlerBook.databaseToString();
        if (books.size() > 0) {
            ArrayAdapter myadapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, books);
            ListView mylist = (ListView) findViewById(R.id.listviewBookmarks);
            mylist.setAdapter((ListAdapter) myadapter);
            mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = (String) books.get(position);
                    Intent intent = new Intent(view.getContext(), browser.class);
                    intent.putExtra("urls", url);
                    Bookmarks.this.startActivity(intent);
                    Bookmarks.this.finish();
                }
            });
        }

        /**
         * Очищает список закладок
         */
        clear_bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView mylist2 = (ListView) Bookmarks.this.findViewById(R.id.listviewBookmarks);
                mylist2.setAdapter((ListAdapter) null);
                Bookmarks.this.dbHandlerBook.deleteAllBooks();
            }
        });
    }
}
