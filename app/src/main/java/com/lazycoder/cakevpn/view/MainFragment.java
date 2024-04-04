package com.lazycoder.cakevpn.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.lazycoder.cakevpn.CheckInternetConnection;
import com.lazycoder.cakevpn.R;
import com.lazycoder.cakevpn.SharedPreference;
import com.lazycoder.cakevpn.databinding.FragmentMainBinding;
import com.lazycoder.cakevpn.interfaces.ChangeServer;
import com.lazycoder.cakevpn.model.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;
import de.blinkt.openvpn.core.VpnStatus;

import static android.app.Activity.RESULT_OK;

/**
 * Фрагмент главного экрана.
 */
public class MainFragment extends Fragment implements View.OnClickListener, ChangeServer {

    private Server server;
    private CheckInternetConnection connection;

    private OpenVPNThread vpnThread = new OpenVPNThread();
    private OpenVPNService vpnService = new OpenVPNService();
    boolean vpnStart = false;
    private SharedPreference preference;

    private FragmentMainBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        View view = binding.getRoot();
        initializeAll();

        return view;
    }

    /**
     * Инициализация переменных и объектов.
     */
    private void initializeAll() {
        preference = new SharedPreference(getContext());
        server = preference.getServer();

        // Обновляем иконку текущего выбранного сервера
        updateCurrentServerIcon(server.getFlagUrl());

        connection = new CheckInternetConnection();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.vpnBtn.setOnClickListener(this);

        // Проверяем, работает ли VPN-соединение

        isServiceRunning();
        VpnStatus.initLogCache(getActivity().getCacheDir());
    }

    /**
     * Обработчик нажатий на элементы интерфейса.
     *
     * @param v: View, на которую был совершен клик
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vpnBtn:
                // Vpn is running, user would like to disconnect current connection.
                if (vpnStart) {
                    confirmDisconnect();
                }else {
                    prepareVpn();
                }
        }
    }

    /**
     * Отображение диалога подтверждения отключения.
     */
    public void confirmDisconnect(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(R.string.connection_close_confirm));

        builder.setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stopVpn();
            }
        });
        builder.setNegativeButton(getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Пользователь отменил диалог
            }
        });

        // Создание диалога
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Подготовка к установлению VPN-соединения с необходимыми разрешениями.
     */
    private void prepareVpn() {
        if (!vpnStart) {
            if (getInternetStatus()) {

                // Проверка разрешений для мониторинга сети
                Intent intent = VpnService.prepare(getContext());

                if (intent != null) {
                    startActivityForResult(intent, 1);
                } else startVpn();// Разрешения уже есть

                // Обновление статуса соединения
                status("connecting");

            } else {

                // Нет доступа к интернету
                showToast("you have no internet connection !!");
            }

        } else if (stopVpn()) {

            // VPN отключен, показываем всплывающее сообщение.
            showToast("Disconnect Successfully");
        }
    }

    /**
     * Остановка VPN-соединения.
     *
     * @return boolean: статус VPN-соединения
     */
    public boolean stopVpn() {
        try {
            vpnThread.stop();

            status("connect");
            vpnStart = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Запрос разрешений на доступ к сети.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // Разрешения получены, запускаем VPN
            startVpn();
        } else {
            showToast("Permission Deny !! ");
        }
    }

    /**
     * Проверка состояния подключения к интернету.
     */
    public boolean getInternetStatus() {
        return connection.netCheck(getContext());
    }

    /**
     * Получение статуса сервиса.
     */
    public void isServiceRunning() {
        setStatus(vpnService.getStatus());
    }

    /**
     * Запуск VPN.
     */
    private void startVpn() {
        try {
            // Файл .ovpn
            InputStream conf = getActivity().getAssets().open(server.getOvpn());
            InputStreamReader isr = new InputStreamReader(conf);
            BufferedReader br = new BufferedReader(isr);
            String config = "";
            String line;

            while (true) {
                line = br.readLine();
                if (line == null) break;
                config += line + "\n";
            }

            br.readLine();
            OpenVpnApi.startVpn(getContext(), config, server.getCountry(), server.getOvpnUserName(), server.getOvpnUserPassword());

            // Обновление лога
            binding.logTv.setText("Connecting...");
            vpnStart = true;

        } catch (IOException | RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Изменение статуса соединения VPN.
     *
     * @param connectionState: текущий статус VPN-соединения
     */
    public void setStatus(String connectionState) {
        if (connectionState!= null)
        switch (connectionState) {
            case "DISCONNECTED":
                status("connect");
                vpnStart = false;
                vpnService.setDefaultStatus();
                binding.logTv.setText("");
                break;
            case "CONNECTED":
                vpnStart = true;
                status("connected");
                binding.logTv.setText("");
                break;
            case "WAIT":
                binding.logTv.setText("waiting for server connection!!");
                break;
            case "AUTH":
                binding.logTv.setText("server authenticating!!");
                break;
            case "RECONNECTING":
                status("connecting");
                binding.logTv.setText("Reconnecting...");
                break;
            case "NONETWORK":
                binding.logTv.setText("No network connection");
                break;
        }

    }

    /**
     * Изменение цвета фона и текста кнопки.
     *
     * @param status: текущий статус VPN
     */
    public void status(String status) {

        if (status.equals("connect")) {
            binding.vpnBtn.setText(getContext().getString(R.string.connect));
        } else if (status.equals("connecting")) {
            binding.vpnBtn.setText(getContext().getString(R.string.connecting));
        } else if (status.equals("connected")) {

            binding.vpnBtn.setText(getContext().getString(R.string.disconnect));

        } else if (status.equals("tryDifferentServer")) {

            binding.vpnBtn.setBackgroundResource(R.drawable.button_connected);
            binding.vpnBtn.setText("Try Different\nServer");
        } else if (status.equals("loading")) {
            binding.vpnBtn.setBackgroundResource(R.drawable.button);
            binding.vpnBtn.setText("Loading Server..");
        } else if (status.equals("invalidDevice")) {
            binding.vpnBtn.setBackgroundResource(R.drawable.button_connected);
            binding.vpnBtn.setText("Invalid Device");
        } else if (status.equals("authenticationCheck")) {
            binding.vpnBtn.setBackgroundResource(R.drawable.button_connecting);
            binding.vpnBtn.setText("Authentication \n Checking...");
        }

    }

    /**
     * Получение сообщения.
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                setStatus(intent.getStringExtra("state"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                String duration = intent.getStringExtra("duration");
                String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
                String byteIn = intent.getStringExtra("byteIn");
                String byteOut = intent.getStringExtra("byteOut");

                if (duration == null) duration = "00:00:00";
                if (lastPacketReceive == null) lastPacketReceive = "0";
                if (byteIn == null) byteIn = " ";
                if (byteOut == null) byteOut = " ";
                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * Обновление интерфейса статуса.
     *
     * @param duration: время работы
     * @param lastPacketReceive: время последнего получения пакета данных
     * @param byteIn: входящий трафик
     * @param byteOut: исходящий трафик
     */
    public void updateConnectionStatus(String duration, String lastPacketReceive, String byteIn, String byteOut) {
        binding.durationTv.setText("Duration: " + duration);
        binding.lastPacketReceiveTv.setText("Packet Received: " + lastPacketReceive + " second ago");
        binding.byteInTv.setText("Bytes In: " + byteIn);
        binding.byteOutTv.setText("Bytes Out: " + byteOut);
    }

    /**
     * Показывает всплывающее сообщение
     * @param message: toast message
     */
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Обновление иконки текущего выбранного сервера.
     * @param serverIcon: icon URL
     */
    public void updateCurrentServerIcon(String serverIcon) {
        Glide.with(getContext())
                .load(serverIcon)
                .into(binding.selectedServerIcon);
    }

    /**
     * Метод интерфейса ChangeServer для обновления текущего сервера.
     *
     * @param server: объект Server с новыми данными сервера
     */
    @Override
    public void newServer(Server server) {
        this.server = server;
        updateCurrentServerIcon(server.getFlagUrl());

        // Stop previous connection
        if (vpnStart) {
            stopVpn();
        }

        prepareVpn();
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));

        if (server == null) {
            server = preference.getServer();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    /**
     * Сохранение выбранного сервера в SharedPreference
     */
    @Override
    public void onStop() {
        if (server != null) {
            preference.saveServer(server);
        }

        super.onStop();
    }
}
