package app.racdeveloper.com.bookmybook;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import app.racdeveloper.com.bookmybook.admin.DataBaseBookList;
import app.racdeveloper.com.bookmybook.ebookList.EBookActivity;
import app.racdeveloper.com.bookmybook.userLogin.LoginActivity;
import app.racdeveloper.com.bookmybook.userProfile.BookDetailsFrag;
import app.racdeveloper.com.bookmybook.userProfile.BookListFrag;

/**
 * Created by Rachit on 4/15/2017.
 */

public class ProfileActivity extends AppCompatActivity implements BookListFrag.BookListListener, NavigationView.OnNavigationItemSelectedListener {

    private String resultViewerUrl = "http://www.facebook.com/readersguild/";
    DrawerLayout drawerLayout;
    private TextView tvName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Constants.isAdmin = false;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        String username = QueryPreferences.getUserName(ProfileActivity.this);
        tvName = (TextView) header.findViewById(R.id.tvUserName);
        tvName.setText(username);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(ProfileActivity.this, drawerLayout, toolbar, 0, 0);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        if (id == R.id.nav_book_list) {
            startActivity(new Intent(ProfileActivity.this, DataBaseBookList.class));
        } else if (id == R.id.nav_pdfs) {
            startActivity(new Intent(ProfileActivity.this, EBookActivity.class));
        } else if (id == R.id.nav_issue) {
            startActivity(new Intent(ProfileActivity.this, IssueBookActivity.class));
        } else if (id == R.id.nav_logout) {
            finish();
//            FirebaseAuth auth;
//            auth = FirebaseAuth.getInstance();
//            auth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        } else if (id == R.id.nav_webview) {
            startActivity(new Intent(ProfileActivity.this, WebActivity.class).putExtra("uri", resultViewerUrl));
        } else if (id == R.id.nav_aboutUs) {
            startActivity(new Intent(ProfileActivity.this, AboutUsActivity.class));
        }
        return true;
    }

    @Override
    public void itemClicked(int id) {
        BookDetailsFrag details= new BookDetailsFrag();
        FragmentTransaction transaction= getFragmentManager().beginTransaction();
        details.setId(id);
        transaction.replace(R.id.lFrame,details,null);
        transaction.addToBackStack(null);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.commit();
    }
}
