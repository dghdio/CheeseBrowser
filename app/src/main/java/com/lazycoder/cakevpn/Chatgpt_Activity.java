package com.lazycoder.cakevpn;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Активность, отображающая экран чата с помощником ChatGPT.
 */
public class Chatgpt_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText messageEditText;
    private ImageView sendImageView;
    private List<MessageMode> messageList;
    private MessageAdapter messageAdapter;
    private SharedPreferences sharedPreferences;

    private Button clear_Button;

    private static final String PREFS_NAME = "ChatPrefs";
    private static final String PREF_CHAT_MESSAGES = "chatMessages";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client = new OkHttpClient();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatgpt);

        recyclerView = findViewById(R.id.recycleView);
        messageEditText = findViewById(R.id.message);
        sendImageView = findViewById(R.id.send);

        clear_Button = findViewById(R.id.clear_btn);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        messageList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);

        clear_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearChat();
            }
        });

        sendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = messageEditText.getText().toString().trim();

                if (question.isEmpty()) {
                    Toast.makeText(Chatgpt_Activity.this, "Input request", Toast.LENGTH_SHORT).show();
                } else {
                    addToChat(question, MessageMode.SENT_BY_ME);
                    messageEditText.setText("");

                    callAPI(question);
                }
            }
        });

        // Загрузка сохраненного чата
        loadChatMessages();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Сохранение чата при приостановке активности
        saveChatMessages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_clear_chat) {
            clearChat();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Вызывает API для отправки вопроса и получения ответа от ChatGPT.
     *
     * @param question вопрос, отправляемый в API
     */
    private void callAPI(String question) {
        messageList.add(new MessageMode("Wait, please...", MessageMode.SENT_BY_BOT));
        messageAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());

        JSONArray messagesArray = getMessagesArray(question);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("model", "gpt-3.5-turbo");
            jsonObject.put("messages", messagesArray);
            jsonObject.put("max_tokens", 1000);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        client = client.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Fail: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject1.getJSONArray("choices");

                        String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    addResponse("Fail: " + response.body().string());
                }
            }
        });
    }

    /**
     * Создает JSONArray с предыдущими сообщениями пользователя и системным сообщением.
     *
     * @param question вопрос, отправленный пользователем
     * @return JSONArray со всеми сообщениями
     */
    private JSONArray getMessagesArray(String question) {
        JSONArray jsonArray = new JSONArray();

        // Добавляем системное сообщение
        JSONObject systemMessage = new JSONObject();
        try {
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a helpful assistant.");
            jsonArray.put(systemMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Добавляем предыдущие сообщения
        for (MessageMode message : messageList) {
            JSONObject messageObject = new JSONObject();
            try {
                messageObject.put("role", "user");
                messageObject.put("content", message.getMessage());
                jsonArray.put(messageObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Добавляем новое сообщение пользователя
        JSONObject userMessage = new JSONObject();
        try {
            userMessage.put("role", "user");
            userMessage.put("content", question);
            jsonArray.put(userMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    /**
     * Добавляет ответ в чат от ChatGPT.
     *
     * @param response ответ от ChatGPT
     */
    private void addResponse(String response) {
        messageList.remove(messageList.size() - 1);
        addToChat(response, MessageMode.SENT_BY_BOT);
    }

    /**
     * Добавляет сообщение в чат.
     *
     * @param message   текст сообщения
     * @param sentByMe  флаг, указывающий, отправлено ли сообщение пользователем
     */
    private void addToChat(String message, String sentByMe) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new MessageMode(message, sentByMe));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    /**
     * Загружает сохраненные сообщения чата.
     */
    private void loadChatMessages() {
        String savedChatMessages = sharedPreferences.getString(PREF_CHAT_MESSAGES, null);

        if (savedChatMessages != null) {
            try {
                JSONArray jsonArray = new JSONArray(savedChatMessages);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject messageObject = jsonArray.getJSONObject(i);
                    String role = messageObject.getString("role");
                    String content = messageObject.getString("content");

                    messageList.add(new MessageMode(content, role));
                }

                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Сохраняет сообщения чата.
     */
    private void saveChatMessages() {
        JSONArray jsonArray = new JSONArray();

        for (MessageMode message : messageList) {
            JSONObject messageObject = new JSONObject();
            try {
                messageObject.put("role", message.getSentBy());
                messageObject.put("content", message.getMessage());
                jsonArray.put(messageObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_CHAT_MESSAGES, jsonArray.toString());
        editor.apply();
    }

    /**
     * Очищает чат.
     */
    private void clearChat() {
        messageList.clear();
        messageAdapter.notifyDataSetChanged();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PREF_CHAT_MESSAGES);
        editor.apply();

        Toast.makeText(Chatgpt_Activity.this, "Chat cleared", Toast.LENGTH_SHORT).show();
    }
}
