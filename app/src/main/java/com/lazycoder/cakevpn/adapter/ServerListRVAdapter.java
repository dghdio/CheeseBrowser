package com.lazycoder.cakevpn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lazycoder.cakevpn.R;
import com.lazycoder.cakevpn.interfaces.NavItemClickListener;
import com.lazycoder.cakevpn.model.Server;

import java.util.ArrayList;

/**
 * Адаптер для отображения списка серверов в RecyclerView.
 */
public class ServerListRVAdapter extends RecyclerView.Adapter<ServerListRVAdapter.MyViewHolder> {

    private ArrayList<Server> serverLists;
    private Context mContext;
    private NavItemClickListener listener;

    /**
     * Конструктор адаптера для списка серверов.
     *
     * @param serverLists список серверов
     * @param context     контекст приложения
     */
    public ServerListRVAdapter(ArrayList<Server> serverLists, Context context) {
        this.serverLists = serverLists;
        this.mContext = context;
        listener = (NavItemClickListener) context;
    }

    /**
     * Создает новый объект ViewHolder при необходимости.
     *
     * @param parent   родительская ViewGroup
     * @param viewType тип представления элемента
     * @return новый объект ViewHolder
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.server_list_view, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Привязывает данные сервера к элементу списка.
     *
     * @param holder   ViewHolder, представляющий элемент списка
     * @param position позиция элемента в списке
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.serverCountry.setText(serverLists.get(position).getCountry());
        Glide.with(mContext)
                .load(serverLists.get(position).getFlagUrl())
                .into(holder.serverIcon);

        holder.serverItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.clickedItem(position);
            }
        });
    }

    /**
     * Возвращает количество элементов в списке.
     *
     * @return количество элементов в списке
     */
    @Override
    public int getItemCount() {
        return serverLists.size();
    }

    /**
     * ViewHolder, содержащий представление элемента списка.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout serverItemLayout;
        ImageView serverIcon;
        TextView serverCountry;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            serverItemLayout = itemView.findViewById(R.id.serverItemLayout);
            serverIcon = itemView.findViewById(R.id.iconImg);
            serverCountry = itemView.findViewById(R.id.countryTv);
        }
    }
}
