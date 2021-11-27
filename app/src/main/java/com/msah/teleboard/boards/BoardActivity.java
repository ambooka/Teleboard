package com.msah.teleboard.boards;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msah.teleboard.R;
import com.msah.teleboard.boards.adapters.boardUsersAdapter;
import com.msah.teleboard.boards.models.UserModel;
import com.msah.teleboard.boards.views.CanvasView;
import com.msah.teleboard.boards.views.Segment;
import com.msah.teleboard.colorpicker.ColorPickerListener;
import com.msah.teleboard.colorpicker.ColorPickerView;
import com.msah.teleboard.notes.styles.toolbar.IToolbar;


import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;


public class BoardActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    public static final int THUMBNAIL_SIZE = 256;
    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int CLEAR_MENU_ID = COLOR_MENU_ID + 1;
    private static final int PIN_MENU_ID = CLEAR_MENU_ID + 1;

    public static final int REQUEST_IMAGE = 2;
    public static final int REQUEST_BACKGROUND = 3;
    public static final String TAG = "AndroidDrawing";
    private ArrayList<String> mSelectPath;

    int strokeType;

    private CanvasView mDrawingView;
    private DatabaseReference mFirebaseRef; // Firebase base URL
    private DatabaseReference mMetadataRef;
    private DatabaseReference mSegmentsRef, audioStreamRef;
    private ValueEventListener mConnectedListener;
    private String mBoardId;
    public static int mBoardWidth;
    public static int mBoardHeight;
    LinearLayout containerMain;
    private ColorPickerView colorPickerView;

    private BottomSheetBehavior mBehavior;
    private View bottom_sheet;
    private ImageView showBottomSheet;
    ListView listView;
    ArrayList<String> listUsers;
    ArrayAdapter arrayAdapter;
    IToolbar illustratorToolbar;

    private Animation moveLeft;
    private Animation moveRight;
    FloatingActionButton clearFab;

    //Audio Settings
    private final int source = MediaRecorder.AudioSource.DEFAULT;
    private final int channel_in = AudioFormat.CHANNEL_IN_MONO;
    private final int channel_out = AudioFormat.CHANNEL_OUT_MONO;
    private final int format = AudioFormat.ENCODING_PCM_16BIT;

    int read = 0, write = 0;
    private boolean isRecording;
    private AudioRecord record;
    private AudioTrack player;
    private AudioManager manager;
    private int recordState, playerState;
    private int minBuffer;

    FloatingActionButton fab, fab1, fab2, fab3;
    Boolean isFABOpen = false;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_board);
        containerMain = findViewById(R.id.container_main);
        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinator_main);
        //    moveLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left);
        //    moveRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right);
        clearFab = (FloatingActionButton) findViewById(R.id.clear_fab);

        isRecording = false;
        manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        manager.setMode(AudioManager.MODE_NORMAL);
        initAudio();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });


        clearFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.removeListener();
                mDrawingView.clear();
                /* Only admins */
                mSegmentsRef.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            throw firebaseError.toException();
                        }
                    }
                });
            }
        });
        colorPickerView = (ColorPickerView) findViewById(R.id.rteColorPalette);
        colorPickerView.setColorPickerListener(new ColorPickerListener() {
            @Override
            public void onPickColor(int color) {
                Toast.makeText(getApplication(), "Color " + color, Toast.LENGTH_SHORT).show();
                if (color == -1) {
                    mDrawingView.setStrokeSize(30);
                }
                mDrawingView.setStrokeSize(10);
                mDrawingView.setColor(color);
            }
        });
        bottom_sheet = findViewById(R.id.simple_bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);
        listView = findViewById(R.id.bottom_sheet_list);
        showBottomSheet = findViewById(R.id.show_bottom_sheet);


        showBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    clearFab.startAnimation(moveLeft);
                    clearFab.setVisibility(View.VISIBLE);
                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    //showBottomSheet.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
                    mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    clearFab.startAnimation(moveRight);
                    clearFab.setVisibility(View.GONE);


                }
            }

        });

        listUsers = new ArrayList<>();

        readUsers();

        Intent intent = getIntent();
        // final String url = intent.getStringExtra("FIREBASE_URL");
        final String boardId = intent.getStringExtra("BOARD_ID");
        mFirebaseRef = FirebaseDatabase.getInstance().getReference("Boards");
        mBoardId = boardId;
        mMetadataRef = mFirebaseRef.child("boardmetas").child(boardId);
        audioStreamRef = mFirebaseRef.child("audioStream").child(mBoardId);
        mSegmentsRef = mFirebaseRef.child("boardsegments").child(mBoardId);
        mMetadataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (mDrawingView != null) {
                    ((ViewGroup) mDrawingView.getParent()).removeView(mDrawingView);
                    mDrawingView.removeListener();
                    mDrawingView = null;
                }

                Map<String, Object> boardValues = (Map<String, Object>) dataSnapshot.getValue();

                if (boardValues != null && boardValues.get("width") != null && boardValues.get("height") != null) {
                    /// mBoardWidth = ((Long) boardValues.get("width")).intValue();
                    // mBoardHeight = ((Long) boardValues.get("height")).intValue();

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    mBoardHeight = displayMetrics.heightPixels;
                    mBoardWidth = displayMetrics.widthPixels;

                    mDrawingView = new CanvasView(BoardActivity.this, mFirebaseRef.child("boardsegments").child(boardId), mBoardWidth, mBoardHeight);
                    mDrawingView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT));
                    mDrawingView.setStrokeSize(10);
                    containerMain.addView(mDrawingView);
                    // containerMain.scrollTo(1000,1000);


                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError firebaseError) {
                // No-op
            }
        });

        MaterialButtonToggleGroup materialButtonToggleGroup =
                findViewById(R.id.illustrator_toggle_group);

        materialButtonToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.btn_illustrator_toolbar) {
                        colorPickerView.setVisibility(View.VISIBLE);
                    }
                    if (checkedId == R.id.btn_mic) {
                        (new Thread() {
                            @Override
                            public void run() {
                                startAudio();
                            }
                        }).start();
                    }
                    if (checkedId == R.id.btn_illustrator_attach) {
                        startMultiImageSelector(REQUEST_IMAGE);
                    }
                }
                if (!isChecked) {
                    if (checkedId == R.id.btn_illustrator_toolbar) {
                        colorPickerView.setVisibility(View.GONE);
                    }
                    if (checkedId == R.id.btn_mic) {
                        endAudio();
                    }
                }
            }
        });
    }

    private void readUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    assert firebaseUser != null;
                    assert user != null;
                    if (!user.getId().equals(firebaseUser.getUid()))
                        listUsers.add(user.getUserName());

                }

                listView.setAdapter(new boardUsersAdapter(BoardActivity.this, listUsers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Set up a notification to let us know when we're connected or disconnected from the Firebase servers
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(BoardActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BoardActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                // No-op
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        // Clean up our listener so we don't have it attached twice.
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        if (mDrawingView != null) {
            mDrawingView.removeListener();
        }
        this.updateThumbnail(mBoardWidth, mBoardHeight, mSegmentsRef, mMetadataRef);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // getMenuInflater().inflate(R.menu.menu_drawing, menu);

        menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c').setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, CLEAR_MENU_ID, 2, "Clear").setShortcut('5', 'x');
        menu.add(0, PIN_MENU_ID, 3, "Keep in sync").setShortcut('6', 's').setIcon(android.R.drawable.ic_lock_lock)
                .setCheckable(true).setChecked(SyncedBoardManager.isSynced(mBoardId));

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == COLOR_MENU_ID) {
            return true;
        } else if (item.getItemId() == CLEAR_MENU_ID) {
            mDrawingView.removeListener();
            mSegmentsRef.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                    if (firebaseError != null) {
                        throw firebaseError.toException();
                    }
                    mDrawingView = new CanvasView(BoardActivity.this, mFirebaseRef.child("boardsegments").child(mBoardId), mBoardWidth, mBoardHeight);
                    containerMain.addView(mDrawingView);
                    //mDrawingView.clear();
                }
            });

            return true;
        } else if (item.getItemId() == PIN_MENU_ID) {
            SyncedBoardManager.toggle(mFirebaseRef.child("boardsegments"), mBoardId);
            item.setChecked(SyncedBoardManager.isSynced(mBoardId));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public static void updateThumbnail(int boardWidth, int boardHeight, DatabaseReference segmentsRef, final DatabaseReference metadataRef) {
        final float scale = Math.min(1.0f * THUMBNAIL_SIZE / boardWidth, 1.0f * THUMBNAIL_SIZE / boardHeight);
        final Bitmap b = Bitmap.createBitmap(Math.round(boardWidth * scale), Math.round(boardHeight * scale), Bitmap.Config.ARGB_8888);
        final Canvas buffer = new Canvas(b);

        buffer.drawRect(0, 0, b.getWidth(), b.getHeight(), CanvasView.paintFromColor(Color.WHITE, Paint.Style.FILL_AND_STROKE));
        Log.i(TAG, "Generating thumbnail of " + b.getWidth() + "x" + b.getHeight());

        segmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot segmentSnapshot : dataSnapshot.getChildren()) {
                    Segment segment = segmentSnapshot.getValue(Segment.class);
                    buffer.drawPath(
                            CanvasView.getPathForPoints(segment.getPoints(), scale),
                            CanvasView.paintFromColor(segment.getColor())
                    );
                }
                String encoded = encodeToBase64(b);
                metadataRef.child("thumbnail").setValue(encoded, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Log.e(TAG, "Error updating thumbnail", firebaseError.toException());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NotNull DatabaseError firebaseError) {

            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mDrawingView != null) {
            mDrawingView.addListener(mFirebaseRef.child("boardsegments").child(mBoardId));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);

    }

    public static Bitmap decodeFromBase64(String input) throws IOException {
        byte[] decodedByte = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public void initAudio() {
        //Tests all sample rates before selecting one that works
        int sample_rate = getSampleRate();
        minBuffer = AudioRecord.getMinBufferSize(sample_rate, channel_in, format);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        record = new AudioRecord(source, sample_rate, channel_in, format, minBuffer);
            recordState = record.getState();
            int id = record.getAudioSessionId();
            Log.d("Record", "ID: " + id);
            playerState = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                player = new AudioTrack(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                        new AudioFormat.Builder().setEncoding(format)
                                .setSampleRate(sample_rate)
                                .setChannelMask(channel_out)
                                .build(), minBuffer, AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE);
                playerState = player.getState();
                // Formatting Audio

            }
        }

        public void startAudio() {

            if (recordState == AudioRecord.STATE_INITIALIZED && playerState == AudioTrack.STATE_INITIALIZED) {
                record.startRecording();
                player.play();
                isRecording = true;
            }
            while (isRecording) {
                short[] audioData = new short[minBuffer];
                if (record != null)


                    read = record.read(audioData, 0, minBuffer);
                else break;
                if (player != null) {
                    write = player.write(audioData, 0, read);

                } else break;
            }
        }

        public void endAudio() {
            if (record != null) {
                if (record.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
                    record.stop();
                isRecording = false;
            }
            if (player != null) {
                if (player.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
                    player.stop();
                isRecording = false;
            }
        }

        public int getSampleRate() {
            //Find a sample rate that works with the device
            for (int rate : new int[]{8000, 11025, 16000, 22050, 44100, 48000}) {
                int buffer = AudioRecord.getMinBufferSize(rate, channel_in, format);
                if (buffer > 0)
                    return rate;
            }
            return -1;
        }




        // checking the result(data) from the intent and setting that to imageView
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_IMAGE) {
                if (resultCode == this.RESULT_OK) {
                    mSelectPath = data.getStringArrayListExtra(ImageSelector.EXTRA_RESULT);
                    String path = "";
                    if (mSelectPath.size() == 1) {
                        path = mSelectPath.get(0);
                    } else if (mSelectPath == null || mSelectPath.size() == 0) {
                    }
                    //加载图片
                    mDrawingView.addPhotoByPath(path);
                    mDrawingView.setEditMode(CanvasView.EDIT_PHOTO);
                }

            }
        }

        public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
            int width = image.getWidth();
            int height = image.getHeight();

            float bitmapRatio = (float) width / (float) height;
            if (bitmapRatio > 1) {
                width = maxSize;
                height = (int) (width / bitmapRatio);
            } else {
                height = maxSize;
                width = (int) (height * bitmapRatio);
            }
            return Bitmap.createScaledBitmap(image, width, height, true);
        }


        private void startMultiImageSelector(int request) {
            ImageSelector selector = ImageSelector.create(getApplicationContext());
            selector.showCamera(true);
            selector.count(9);
            selector.single();
            selector.origin(mSelectPath);
            Bundle boundsBundle = new Bundle();
            Rect rect = new Rect();
            mDrawingView.getLocalVisibleRect(rect);
            int[] boundsInts = new int[4];
            //noinspection Range
            mDrawingView.getLocationInWindow(boundsInts);

            selector.start(this, request);
        }

    private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
    }

    }
