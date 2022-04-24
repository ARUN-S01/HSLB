package com.example.hslb;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class MainActivity extends AppCompatActivity {
    public static AtomicReference<User> user;
    public static App app;
    private EditText mEditText;
    private String m_Text = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this);
//        if(internet_enabled("www.google.com",80)) {
//            login();
//        }
//        else{
//            app = null;
//        }
        login();

        setContentView(R.layout.activity_main);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view);

        final MyAdapter adapter = new MyAdapter(this,getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    public void login(){
        String app_id = "hms-hrujn";
        app = new App(new AppConfiguration.Builder(app_id).build());
        Credentials emailPasswordCredentials = Credentials.emailPassword("hslb@gmail.com", "hslb123");
        user = new AtomicReference<User>();

        app.loginAsync(emailPasswordCredentials, it -> {
            if (it.isSuccess()) {
                Log.v("AUTH", "Successfully authenticated using an email and password.");
                user.set(app.currentUser());
                //  Toast.makeText(LoginActivity.this,customUserData.toString(), Toast.LENGTH_SHORT).show();
                //  Toast.makeText(LoginActivity.this,"Login Success",Toast.LENGTH_SHORT).show();
//                if(app.currentUser().getId().equals("62507e2b8039fe7bc176d9f3")){
//                    startActivity(new Intent(LoginActivity.this,AdminHomeActivity.class));
//                }
//                else{
//                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
//                }
                Toast.makeText(this,"Connection Sucess",Toast.LENGTH_SHORT).show();

                //Toast.makeText(LoginActivity.this, app.currentUser().toString(),Toast.LENGTH_SHORT).show();
            } else {
               // Toast.makeText(LoginActivity.this,it.getError().toString(),Toast.LENGTH_SHORT).show();
                Log.e("AUTH", it.getError().toString());
                Toast.makeText(this,"Connection Refused",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public Boolean internet_enabled(String host,int port){
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 2000);
                return true;
            } catch (IOException e) {
                // Either we have a timeout or unreachable host or failed DNS lookup
                System.out.println(e);
                return false;
            }
    }
}