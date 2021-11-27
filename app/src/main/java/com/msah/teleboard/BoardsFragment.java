package com.msah.teleboard;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.msah.teleboard.boards.BoardActivity;
import com.msah.teleboard.boards.SyncedBoardManager;
import com.msah.teleboard.boards.adapters.FirebaseListAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BoardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoardsFragment extends Fragment {

    public static final String TAG = "AndroidDrawing";
    private DatabaseReference mRef;
    private DatabaseReference mBoardsRef, audioStreamRef;
    private DatabaseReference mSegmentsRef;
    private DatabaseReference   userReference;
    private FirebaseListAdapter<HashMap> mBoardListAdapter;
    private ValueEventListener mConnectedListener;
    View view;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BoardsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SolutionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BoardsFragment newInstance(String param1, String param2) {
        BoardsFragment fragment = new BoardsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*TODO:note it */
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRef = FirebaseDatabase.getInstance().getReference("Boards");
        userReference= FirebaseDatabase.getInstance().getReference("Users");
        mBoardsRef = mRef.child("boardmetas");
        audioStreamRef = mRef.child("audioStream");
        audioStreamRef.keepSynced(true);
        mBoardsRef.keepSynced(true); // keep the board list in sync
        mSegmentsRef = mRef.child("boardsegments");
        SyncedBoardManager.restoreSyncedBoards(mSegmentsRef);
        view = inflater.inflate(R.layout.fragment_boards, container, false);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();

        // Set up a notification to let us know when we're connected or disconnected from the Firebase servers
        mConnectedListener = mRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(getContext(), "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Disconnected from Firebase", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                // No-op
            }
        });
        final ListView boardList = (ListView) view.findViewById(R.id.BoardList);
        mBoardListAdapter = new FirebaseListAdapter<HashMap>(mBoardsRef, HashMap.class, R.layout.board_item, getActivity()) {
            @Override
            protected void populateView(View v, HashMap model) {
                final String key = mBoardListAdapter.getModelKey(model);
                ((TextView)v.findViewById(R.id.board_title)).setText(key);

                // show if the board is synced and listen for clicks to toggle that state
                CheckBox checkbox = (CheckBox) v.findViewById(R.id.keepSynced);
                checkbox.setChecked(SyncedBoardManager.isSynced(key));
                checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SyncedBoardManager.toggle(mSegmentsRef, key);
                    }
                });

                // display the board's thumbnail if it is available
                ImageView thumbnailView = (ImageView) v.findViewById(R.id.board_thumbnail);
                if (model.get("thumbnail") != null){
                    try {
                        thumbnailView.setImageBitmap(BoardActivity.decodeFromBase64(model.get("thumbnail").toString()));
                        thumbnailView.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    thumbnailView.setVisibility(View.INVISIBLE);
                }
            }
        };
        boardList.setAdapter(mBoardListAdapter);
        boardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openBoard(mBoardListAdapter.getModelKey(position));
            }
        });
        mBoardListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                boardList.setSelection(mBoardListAdapter.getCount() - 1);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        // Clean up our listener so we don't have it attached twice.
        mRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mBoardListAdapter.cleanup();

    }

    private void createBoard() {
        // create a new board
        String userId = MainActivity.sharedPreference.getIdUser();
        Map<String, Object> newStream = new HashMap<>();


        final DatabaseReference newBoardRef = mBoardsRef.push();
        Map<String, Object> newBoardValues = new HashMap<>();
        newBoardValues.put("Creator", userId);
        newBoardValues.put("createdAt", ServerValue.TIMESTAMP);
        android.graphics.Point size = new android.graphics.Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        newBoardValues.put("width", size.x);
        newBoardValues.put("height", size.y);
        newBoardRef.setValue(newBoardValues, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference ref) {
                if (firebaseError != null) {
                    Log.e(TAG, firebaseError.toString());
                    throw firebaseError.toException();
                } else {
                    // once the board is created, start a DrawingActivity on it
                    openBoard(newBoardRef.getKey());
                }
            }
        });
    }

    private void openBoard(String key) {
        Toast.makeText(getContext(), "Opening board: "+key, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getContext(), BoardActivity.class);
        intent.putExtra("BOARD_ID", key);
        startActivity(intent);

    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();


        getActivity().getMenuInflater().inflate(R.menu.menu_board_list, menu);

        // Inflate the menu; this adds items to the action bar if it is present.


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_new_board) {
            createBoard();
        }
        return super.onOptionsItemSelected(item);
    }

}
