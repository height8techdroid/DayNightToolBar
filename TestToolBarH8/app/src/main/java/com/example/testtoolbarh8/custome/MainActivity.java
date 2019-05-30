package com.example.testtoolbarh8.custome;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testtoolbarh8.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, CustomToolbarView.CustomListener {


    private AppBarLayout appBar;
    private CustomToolbarView mCustomToolbarView;

    private float scrollRange = -1;
    private float timeScale = 0F;

    private SharedPreferences mSharedPreferences;
    public static final int PERMISSION_REQUEST_CODE = 234;

    String appPermisions[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkAndRequestPermissions()) {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }
        updateView();

    }

    public boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> listPermissionNeed = new ArrayList<>();
            for (String appPerm : appPermisions) {
                if (ContextCompat.checkSelfPermission(this, appPerm) != PackageManager.PERMISSION_GRANTED) {
                    listPermissionNeed.add(appPerm);
                }
            }
            if (!listPermissionNeed.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionNeed.toArray(new String[listPermissionNeed.size()]),
                        PERMISSION_REQUEST_CODE);
                return false;
            }
            return true;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                HashMap<String, Integer> permissionResult = new HashMap<>();
                int deniedCount = 0;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissionResult.put(permissions[i], grantResults[i]);
                        deniedCount++;
                    }
                }
                if (deniedCount == 0) {
                    Toast.makeText(this, "Permisiions grant Success", Toast.LENGTH_SHORT).show();
                } else {
                    for (Map.Entry<String, Integer> entity : permissionResult.entrySet()) {
                        String permiName = entity.getKey();
                        int permiResult = entity.getValue();

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiName)) {

                            checkAndRequestPermissions();
                        } else {
                            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                break;
        }
    }

    public void updateView() {
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        mSharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);

        mCustomToolbarView = findViewById(R.id.custom_toolbar);
        appBar = findViewById(R.id.app_bar);


        //Adding OnOffsetChangedListener to AppBarLayout
        appBar.addOnOffsetChangedListener(this);
        mCustomToolbarView.setCustomListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setQueryHint("Search here");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);// Do not iconify the widget; expand it by defaul

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // This is your adapter that will be filtered

                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                // **Here you can get the value "query" which is entered in the search box.**

                Toast.makeText(getApplicationContext(), "searchvalue :" + query, Toast.LENGTH_LONG).show();

                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        /*
         * Since the totalScrollRange is not going to change in runtime,
         * we getTotalScrollRange() only if the totalScrollRange equals -1
         * which is the initial value, if its not then it means that we already called it
         * and therefore we don't need to keep doing it.
         */
        if (scrollRange == -1) scrollRange = appBarLayout.getTotalScrollRange();

        /*
         * Simple Maths here, we divide verticalOffset we get from the listener
         * by the scrollRange and we add 1 to it to prevent the scale from being -1
         * when the AppBar is completely collapsed.
         */
        float scale = 1 + verticalOffset / scrollRange;

        //We add the elevation to the AppBarLayout only if its collapsed.
        if (scale <= 0.0F) appBar.setElevation(10);
        else appBar.setElevation(0);

        //We update the scale in our custom view
        mCustomToolbarView.setScale(scale);


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onValuesUpdated(boolean isNight, float timeScale) {

    }
}