package com.lazycoder.cakevpn;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Адаптер для отображения сообщений в RecyclerView.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<MessageMode> modeList;
    private Context context;

    /**
     * Конструктор адаптера.
     *
     * @param modeList Список сообщений для отображения.
     */
    public MessageAdapter(List<MessageMode> modeList) {
        this.modeList = modeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageMode mode = modeList.get(position);

        if (mode.getSentBy().equals(MessageMode.SENT_BY_ME)) {
            // Сообщение отправлено мной, скрываем левую часть разговора и показываем правую часть
            holder.leftChat.setVisibility(View.GONE);
            holder.rightChat.setVisibility(View.VISIBLE);
            holder.rightText.setText(mode.getMessage());

            // Добавляем долгое нажатие для копирования текста
            holder.rightText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copyTextToClipboard(mode.getMessage());
                    showToast("Text copied to clipboard");
                    return true;
                }
            });
        } else {
            // Сообщение отправлено собеседником, скрываем правую часть разговора и показываем левую часть
            holder.rightChat.setVisibility(View.GONE);
            holder.leftChat.setVisibility(View.VISIBLE);
            holder.leftText.setText(mode.getMessage());

            // Добавляем долгое нажатие для копирования текста
            holder.leftText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copyTextToClipboard(mode.getMessage());
                    showToast("Text copied to clipboard");
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return modeList.size();
    }

    /**
     * ViewHolder для отдельного элемента RecyclerView.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout leftChat, rightChat;
        TextView leftText, rightText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChat = itemView.findViewById(R.id.left_chat);
            rightChat = itemView.findViewById(R.id.right_chat);
            leftText = itemView.findViewById(R.id.left_text);
            rightText = itemView.findViewById(R.id.right_text);
        }
    }

    /**
     * Копирует текст в буфер обмена.
     *
     * @param text Текст для копирования.
     */
    private void copyTextToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", text);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Отображает всплывающее сообщение.
     *
     * @param message Сообщение для отображения.
     */
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
