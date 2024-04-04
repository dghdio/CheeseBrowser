package com.lazycoder.cakevpn.view;

import static com.lazycoder.cakevpn.Utils.getImgURL;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.lazycoder.cakevpn.R;
import com.lazycoder.cakevpn.adapter.ServerListRVAdapter;
import com.lazycoder.cakevpn.interfaces.ChangeServer;
import com.lazycoder.cakevpn.interfaces.NavItemClickListener;
import com.lazycoder.cakevpn.model.Server;

import java.util.ArrayList;

import com.lazycoder.cakevpn.Utils;


/**
 * Основная активность приложения.
 */
public class MainActivity extends AppCompatActivity implements NavItemClickListener {
    private FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    private Fragment fragment;
    private RecyclerView serverListRv;
    private ArrayList<Server> serverLists;
    private ServerListRVAdapter serverListRVAdapter;
    private DrawerLayout drawer;
    private ChangeServer changeServer;

    public static final String TAG = "CakeVPN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация переменных
        initializeAll();

        ImageButton menuRight = findViewById(R.id.navbar_right);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        menuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });

        transaction.add(R.id.container, fragment);
        transaction.commit();

        // Инициализация списка серверов в RecyclerView
        if (serverLists != null) {
            serverListRVAdapter = new ServerListRVAdapter(serverLists, this);
            serverListRv.setAdapter(serverListRVAdapter);
        }

    }

    /**
     * Инициализация всех объектов и слушателей.
     */
    private void initializeAll() {
        drawer = findViewById(R.id.drawer_layout);

        fragment = new MainFragment();
        serverListRv = findViewById(R.id.serverListRv);
        serverListRv.setHasFixedSize(true);

        serverListRv.setLayoutManager(new LinearLayoutManager(this));

        serverLists = getServerList();
        changeServer = (ChangeServer) fragment;

    }

    /**
     * Закрыть боковую панель навигации (Navigation Drawer).
     */
    public void closeDrawer(){
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            drawer.openDrawer(GravityCompat.END);
        }
    }

    /**
     * Список серверов/локаций
     */
    private ArrayList<Server> getServerList() {

        ArrayList<Server> servers = new ArrayList<>();
        servers.add(new Server("Netherlands", getImgURL(R.drawable.nederlands), "ovpn-nl-1.vpnv.cc-udp-1194.ovpn", "freevpn.us-restartdmg", "andrewrestart46"));
       // servers.add(new Server("Canada", getImgURL(R.drawable.canada), "cao-1.optnl.com-udp-1194.ovpn", "opentunnel.net-restartdmg", "andrewrestart46"));
        servers.add(new Server("USA", getImgURL(R.drawable.usaa), "ovpn-us-1.vpnv.cc-udp-1194.ovpn", "freevpn.us-restartdmg1", "andrewrestart46"));
        /*servers.add(new Server("France", getImgURL(R.drawable.fr_flag), "fro-1.opensvr.net-udp-1194.ovpn", "opentunnel.net-restartdmg", "andrewrestart46"));
        servers.add(new Server("Germany", getImgURL(R.drawable.germany), "deo-1.opensvr.net-udp-1194.ovpn", "opentunnel.net-restartdmg", "andrewrestart46"));
        servers.add(new Server("UK", getImgURL(R.drawable.uk_flag), "uko-1.opensvr.net-udp-1194.ovpn", "opentunnel.net-restartdmg", "andrewrestart46"));
        */return servers;
    }

    /**
     * Обработчик нажатия на элемент навигационного меню.
     * Закрыть боковую панель навигации и изменить сервер.
     *
     * @param index: индекс сервера
     */
    @Override
    public void clickedItem(int index) {
        closeDrawer();
        changeServer.newServer(serverLists.get(index));
    }
}
