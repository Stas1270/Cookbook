package com.ls.cookbook.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.ls.cookbook.BaseActivity;
import com.ls.cookbook.R;
import com.ls.cookbook.adapter.DrawerAdapter;
import com.ls.cookbook.interfaces.OnListItemClickListener;
import com.ls.cookbook.util.UserHelper;
import com.ls.cookbook.view.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.ls.cookbook.adapter.DrawerAdapter.NavigationItems.Home;
import static com.ls.cookbook.adapter.DrawerAdapter.NavigationItems.Logout;

public class MainActivity extends BaseActivity implements OnListItemClickListener<DrawerAdapter.NavigationItems> {

    private ActionBarDrawerToggle drawerToggle;
    private DrawerAdapter drawerAdapter;
    private int drawerAdapterSelectedItemPosition;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.rv_drawer)
    RecyclerView rvDrawer;

    public static Intent getMainActivityInstance(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    public int initContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void setDataToActivityViews() {
        initViews();
        initDrawer();
        onItemClick(0,Home);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setScrimColor(ContextCompat.getColor(this, R.color.black_transparent));
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                hideKeyboard();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void initDrawer() {
        List<DrawerAdapter.NavigationItems> drawerItems = new ArrayList<>();
        drawerItems.add(Home);
        drawerItems.add(Logout);
        if (rvDrawer != null) {
            rvDrawer.setLayoutManager(new LinearLayoutManager(this));
            drawerAdapter = new DrawerAdapter(drawerItems, this);
            rvDrawer.setAdapter(drawerAdapter);
        }
    }

    private void getLastDrawerPosition() {
        drawerAdapterSelectedItemPosition = drawerAdapter.getSelectedItemPosition();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        drawerToggle.syncState();
    }

    @Override
    public void onItemClick(int position, DrawerAdapter.NavigationItems object) {
        getLastDrawerPosition();
        if (position == drawerAdapterSelectedItemPosition) {
            return;
        }
        drawerAdapter.setSelected(position);
        switch (object) {
            case Home:
                changeFragment(R.id.fl_content, HomeFragment.newInstance(), false);
                drawerLayout.closeDrawers();
                break;

            case Logout:
                showMessage(getString(R.string.are_you_sure_to_log_out),
                        getString(R.string.logout), getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                UserHelper.getInstance().clearData();
                                startActivity(LoginActivity.getLoginActivityInstance(MainActivity.this));
                            }
                        });
                break;
        }
    }
}
