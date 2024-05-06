package com.saveetha.tutorfinder;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static java.security.AccessController.getContext;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.razorpay.PaymentResultListener;
import com.saveetha.tutorfinder.databinding.ActivityMainBinding;
import com.saveetha.tutorfinder.student.ListPackagesFragment;
import com.saveetha.tutorfinder.student.MyBookingsFragment;
import com.saveetha.tutorfinder.student.StudentHomeFragment;
import com.saveetha.tutorfinder.student.StudentProfileFragment;
import com.saveetha.tutorfinder.student.SubscriptionFragment;
import com.saveetha.tutorfinder.student.TransactionFragment;
import com.saveetha.tutorfinder.tutor.LocationFragment;
import com.saveetha.tutorfinder.tutor.TextEditorFragment;
import com.saveetha.tutorfinder.tutor.TutorHomeFragment;
import com.saveetha.tutorfinder.tutor.TutorProfileFragment;

import org.w3c.dom.Text;

import java.util.Random;

public class MainActivity extends AppCompatActivity   {

    SharedPreferences sf;
    private long backPressedTime = 0;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sf = getSharedPreferences(AppConstants.SHARED_PREF_NAME, MODE_PRIVATE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().clear();

        // The callback can be enabled or disabled here or in handleOnBackPressed()
//        EditText editText=new EditText(this);
//        editText.append();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toolbar.setTitleTextColor(Color.WHITE);

        toggle.syncState();

        String user_type = sf.getString(AppConstants.KEY_TYPE, null);
        String is_user_logged = sf.getString(AppConstants.KEY_ID, null);
        String is_user_email = sf.getString(AppConstants.KEY_EMAIL, null);
        String userName = sf.getString(AppConstants.KEY_NAME, null);
        String profile = sf.getString(AppConstants.KEY_IMAGE, null);
        View view= navigationView.inflateHeaderView(R.layout.header_main);
        ShapeableImageView siv=null;
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        if(is_user_logged != null)
        {
            TextView text=view.findViewById(R.id.textView123);

            text.setText(userName);
             siv= view.findViewById(R.id.imageView2);
             siv.setOnClickListener(v -> {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayout,new StudentProfileFragment(view));
                transaction.commit();
                getSupportActionBar().setTitle("Profile");
                 drawer.postDelayed(new Runnable() {
                     @Override public void run() {
                         // ... navigate away, e.g. startActivity(...)
                         drawer.closeDrawers();
                     }
                 }, 500);
            });
            Glide.with(this).load(profile).apply(new RequestOptions().placeholder(R.drawable.error2) // Placeholder image
                            .error(R.drawable.error1))// Error image in case of loading failure
                    .into(siv);
            if(user_type.equals(AppConstants.Student))
            {
                navigationView.inflateMenu(R.menu.student_menu);
                navigationView.getMenu().findItem(R.id.student_profile).setVisible(true);
                navigationView.getMenu().findItem(R.id.student_bookings).setVisible(true);
                navigationView.getMenu().findItem(R.id.student_packages).setVisible(true);
                navigationView.getMenu().findItem(R.id.subscription).setVisible(true);
                navigationView.getMenu().findItem(R.id.transaction).setVisible(true);
                tx.replace(R.id.frameLayout, new StudentHomeFragment());
                tx.commit();

            }else if(user_type.equals(AppConstants.Tutor)){
                navigationView.inflateMenu(R.menu.tutor_menu);
                navigationView.getMenu().findItem(R.id.transaction).setVisible(true);
                navigationView.getMenu().findItem(R.id.student_bookings).setVisible(true);
                navigationView.getMenu().findItem(R.id.tutor_profile).setVisible(true);
                navigationView.getMenu().findItem(R.id.location).setVisible(true);
                tx.replace(R.id.frameLayout, new TutorHomeFragment());
                tx.commit();
            }
            navigationView.getMenu().findItem(R.id.logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.login).setVisible(false);
            navigationView.getMenu().findItem(R.id.signup).setVisible(false);

        }else{
            navigationView.inflateMenu(R.menu.student_menu);
            navigationView.getMenu().findItem(R.id.logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.login).setVisible(true);
            navigationView.getMenu().findItem(R.id.signup).setVisible(true);

            tx.replace(R.id.frameLayout, new StudentHomeFragment());
            tx.commit();
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                int id = item.getItemId();
                FragmentManager fm = getSupportFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                if(id == R.id.student_home){
                    getSupportActionBar().setTitle("Home");
                    transaction.replace(R.id.frameLayout,new StudentHomeFragment());
                    transaction.commit();
                }else if(id == R.id.tutor_home){
                    transaction.replace(R.id.frameLayout,new TutorHomeFragment());
                    transaction.commit();
                    getSupportActionBar().setTitle("Home");
                }else if(id == R.id.tutor_profile){
                    transaction.replace(R.id.frameLayout,new StudentProfileFragment(view));
                    transaction.commit();
                    getSupportActionBar().setTitle("Profile");
                }else if(id == R.id.location){
                    transaction.replace(R.id.frameLayout,new LocationFragment());
                    transaction.commit();
                    getSupportActionBar().setTitle("Location");
                }else if(id == R.id.student_packages){
                    transaction.replace(R.id.frameLayout,new ListPackagesFragment());
                    transaction.commit();
                    getSupportActionBar().setTitle("Packages");
                }else if(id == R.id.student_profile){
                    transaction.replace(R.id.frameLayout,new StudentProfileFragment(view));
                    transaction.commit();
                    getSupportActionBar().setTitle("Profile");
                }else if(id == R.id.student_bookings){
                    transaction.replace(R.id.frameLayout,new MyBookingsFragment());
                    transaction.commit();
                    getSupportActionBar().setTitle("Bookings");
                }else if(id == R.id.subscription){
                    transaction.replace(R.id.frameLayout,new SubscriptionFragment());
                    transaction.commit();
                    getSupportActionBar().setTitle("Subscription");
                }else if(id == R.id.transaction){
                    transaction.replace(R.id.frameLayout,new TransactionFragment());
                    transaction.commit();
                    getSupportActionBar().setTitle("Transaction");
                }else if(id == R.id.login){
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    i.putExtra("fromMainActivity","ma");
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                    finish();
                }else if(id == R.id.signup){
                    Intent i = new Intent(MainActivity.this, SignupActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                    finish();
                }else if(id == R.id.logout){
                    SharedPreferences.Editor editor= sf.edit();
                    editor.clear();
                    editor.commit();
                    Intent mIntent = getIntent();
                    finish();
                    startActivity(mIntent);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                }
                drawer.postDelayed(new Runnable() {
                    @Override public void run() {
                        // ... navigate away, e.g. startActivity(...)
                        drawer.closeDrawers();
                    }
                }, 500);
                return true;
            }
        });
    }
}