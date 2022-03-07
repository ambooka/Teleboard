package com.msah.teleboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.msah.teleboard.boards.SyncedBoardManager;
import com.msah.teleboard.bottomnavigation.BottomNavigationItem;
import com.msah.teleboard.bottomnavigation.BottomNavigationView;
import com.msah.teleboard.bottomnavigation.OnBottomNavigationItemClickListener;
import com.msah.teleboard.utils.SharedPreference;


public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment boardsFragment, notesFragment;

    private static final String TAG = "MainActivity";
    public static SharedPreference sharedPreference;
    private static FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        SyncedBoardManager.setContext(this);

        sharedPreference = new SharedPreference(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();




    int[] image = {R.drawable.ic_baseline_home_24, R.drawable.ic_baseline_sticky_note_2_24,};
       bottomNavigationView = findViewById(R.id.bottomNavigation);
        if(bottomNavigationView !=null){
        bottomNavigationView.isColoredBackground(false);
        // bottomNavigationView.activateTabletMode();
        bottomNavigationView.setTextActiveSize(getResources().getDimension(R.dimen.text_active));
        bottomNavigationView.setTextInactiveSize(getResources().getDimension(R.dimen.text_inactive));
    }

    BottomNavigationItem bottomNavigationItem = new BottomNavigationItem
            ("Home", R.color.colorAccent, image[0]);
    BottomNavigationItem bottomNavigationItem1 = new BottomNavigationItem
            ("Notes", R.color.colorAccent, image[1]);


        bottomNavigationView.addTab(bottomNavigationItem);
        bottomNavigationView.addTab(bottomNavigationItem1);

        //Get views
        boardsFragment = new BoardsFragment();
        notesFragment = new NotesFragment();
        if(null == savedInstanceState){
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack("BoardsFragment")
                    .replace(R.id.fragment_container, boardsFragment)
                    .commit();
        }


        bottomNavigationView.setOnBottomNavigationItemClickListener(new OnBottomNavigationItemClickListener() {
        @Override
        public void onNavigationItemClick ( int index){
            switch (index) {
                case 0:
                    replaceFragment(boardsFragment, "NotesFragment");
                    break;
                case 1:
                    replaceFragment(notesFragment, "NotesFragment");
                    break;
            }
        }
    });
}


    public void replaceFragment(Fragment fragment, String tag){
        // Get current fragment in container
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        //Prevent adding same fragment on top
        if(currentFragment.getClass() == fragment.getClass()){
            return;
        }
        //If fragment is already on the stack, we can pop back to it to prevent infinite growth of the stack
        if(getSupportFragmentManager().findFragmentByTag(tag) != null){
            getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        //Otherwise just replace the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(tag)
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        int fragmentInStack = getSupportFragmentManager().getBackStackEntryCount();
        if(fragmentInStack > 1) {
            // If  we have more than one Fragment, pop back stack
          //  bottomNavigationView.setSelectedItemId(R.id.boards_menu_item);
            bottomNavigationView.selectTab(0);

            getSupportFragmentManager().popBackStack();
        }else if(fragmentInStack == 1){
            // Finish activity, only if one fragment left to prevent leaving screen empty
        }else{

        }
    }
}