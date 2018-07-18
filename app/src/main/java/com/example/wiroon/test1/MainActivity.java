package com.example.wiroon.test1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.wiroon.test1.Fragment.ChangeFragment;
import com.example.wiroon.test1.Fragment.CountingFragment;
import com.example.wiroon.test1.Fragment.HomeFragment;
import com.example.wiroon.test1.Fragment.InguiryFragment;
import com.example.wiroon.test1.Fragment.PickingFragment;
import com.example.wiroon.test1.Fragment.RecievingFragment;
import com.example.wiroon.test1.Fragment.ReturnFragment;
import com.example.wiroon.test1.Fragment.ShippingFragment;
import com.example.wiroon.test1.Fragment.TransitFragment;
import com.google.gson.Gson;
import com.example.wiroon.test1.Appconfig;
import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , AdapterView.OnItemSelectedListener
        , HomeFragment.OnClickMenu {

    //    Appconfig appconfig;
    Bundle bundle = new Bundle();
    Bundle extras;
    private NavigationView navigationView;
    private FragmentTransaction transaction;
    public static String FragmentName;
    public static ActionBarDrawerToggle toggle;
    public static DrawerLayout drawer;
    //spinner warehouse
    private Spinner spinner;

    public static boolean resetTransitFragment = false;
    public static boolean typingTransitFragment = false;
    public static boolean dialogTransit = false;

    public static void typingTransit(boolean st){typingTransitFragment=st;}
    public static void checkBackTransit(boolean st) {
        resetTransitFragment = st;
    }
    public static void setDialogTransit(boolean st) {
        dialogTransit = st;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!Appconfig.checkstate()) {
            restartApp();
        }
//            extras = getIntent().getExtras();
//            appconfig = (Appconfig) extras.getSerializable("Appconfig");
//            appconfig.setContext(this);

//            bundle.putSerializable("Appconfig", appconfig);
//            fragment.setArguments(bundle);
        Appconfig.setContext(this);
        HomeFragment fragment = new HomeFragment();
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content, fragment, "tag_transit").addToBackStack(null).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();

        Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu_home) {
//            FragmentManager fm = MainActivity.this.getSupportFragmentManager();
//            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
//                fm.popBackStack();
//            }
            HomeFragment fragment = new HomeFragment();
            transaction = getSupportFragmentManager().beginTransaction();
            fragment.setArguments(bundle);
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content, fragment, "tag_transit").addToBackStack(null).commit();
        } else if (id == R.id.nav_menu_receivng) {
            RecievingFragment fragment = new RecievingFragment();
//            bundle.putSerializable("Appconfig", appconfig);
//            fragment.setArguments(bundle);
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content, fragment, "tag_receiving").addToBackStack(null).commit();
        } else if (id == R.id.nav_menu_transit) {
            TransitFragment fragment = new TransitFragment();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content, fragment, "tag_transit").addToBackStack(null).commit();
        } else if (id == R.id.nav_menu_inguiry) {
            InguiryFragment fragment = new InguiryFragment();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content, fragment, "tag_inguiry").addToBackStack(null).commit();
        } else if (id == R.id.nav_menu_change) {
            ChangeFragment fragment = new ChangeFragment();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content, fragment, "tag_change").addToBackStack(null).commit();
        } else if (id == R.id.nav_menu_picking) {
            PickingFragment fragment = new PickingFragment();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content, fragment, "tag_picking").addToBackStack(null).commit();
        } else if (id == R.id.nav_menu_shipping) {
            ShippingFragment fragment = new ShippingFragment();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content, fragment, "tag_shipping").addToBackStack(null).commit();
        } else if (id == R.id.nav_menu_return) {
            ReturnFragment fragment = new ReturnFragment();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content, fragment, "tag_return").addToBackStack(null).commit();
        } else if (id == R.id.nav_menu_counting) {
            CountingFragment fragment = new CountingFragment();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content, fragment, "tag_counting").addToBackStack(null).commit();
        } else if (id == R.id.nav_menu_signout) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void Changefragment(int i) {
        navigationView.getMenu().getItem(i).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(i));
    }

    public void setNavMenuItemThemeColors(int color) {
        //Setting default colors for menu item Text and Icon
        int navDefaultTextColor = Color.parseColor("#202020");
        int navDefaultIconColor = Color.parseColor("#737373");

        //Defining ColorStateList for menu item Text
        ColorStateList navMenuTextList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[]{
                        color,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor
                }
        );

        //Defining ColorStateList for menu item Icon
        ColorStateList navMenuIconList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[]{
                        color,
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor
                }
        );

        navigationView.setItemTextColor(navMenuTextList);
        navigationView.setItemIconTintList(navMenuIconList);

    }

    public void restartApp() {
        Toast.makeText(this, "Session expire", Toast.LENGTH_SHORT).show();
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
    }

}
