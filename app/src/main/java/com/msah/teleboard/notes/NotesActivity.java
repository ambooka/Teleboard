package com.msah.teleboard.notes;


import static com.msah.teleboard.notes.TextViewActivity.HTML_TEXT;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.msah.teleboard.R;
import com.msah.teleboard.notes.helpers.ImageStrategyHelper;
import com.msah.teleboard.notes.strategies.ImageStrategy;
import com.msah.teleboard.notes.styles.toolbar.IToolbar;
import com.msah.teleboard.notes.styles.toolitems.IToolItem;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_AlignmentCenter;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_AlignmentLeft;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_AlignmentRight;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_At;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_BackgroundColor;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_Bold;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_FontColor;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_FontSize;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_Hr;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_Image;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_Italic;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_Link;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_ListBullet;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_ListNumber;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_Quote;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_Strikethrough;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_Subscript;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_Superscript;
import com.msah.teleboard.notes.styles.toolitems.ToolItem_Underline;
import com.msah.teleboard.notes.views.CustomEditText;
import com.msah.teleboard.utils.SaveUtil;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NotesActivity extends AppCompatActivity {

    public static final String NOTE_TITLE = "Note_Title";
    private IToolbar mToolbar;
    File directory;
    private CustomEditText mEditText;

    private boolean scrollerAtEnd;

    private ImageStrategy imageStrategy = new ImageStrategyHelper();
    public String fileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);


        final Intent intent = getIntent();
        fileName = intent.getStringExtra(NOTE_TITLE) + ".html";
        getSupportActionBar().setTitle(fileName.replace(".html", ""));

       // directory = new File(Environment.getExternalStorageDirectory()+File.separator+ "Project Study"+ File.separator+ "Documents" + File.separator + ".Html notes" + File.separator + fileName);
        directory = new File(this.getExternalFilesDir(null) + File.separator + "Notes" + File.separator + fileName);

        try {
            InputStream inputStream = new FileInputStream(directory);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            String filecontent = stringBuilder.toString();
            Toast.makeText(this, filecontent, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initToolbar();

    }

    private void initToolbar() {
        mToolbar = this.findViewById(R.id.default_toolbar);

        IToolItem bold = new ToolItem_Bold();
        IToolItem italic = new ToolItem_Italic();
        IToolItem underline = new ToolItem_Underline();
        IToolItem strikethrough = new ToolItem_Strikethrough();
        IToolItem fontSize = new ToolItem_FontSize();
        IToolItem fontColor = new ToolItem_FontColor();
        IToolItem backgroundColor = new ToolItem_BackgroundColor();
        IToolItem quote = new ToolItem_Quote();
        IToolItem listNumber = new ToolItem_ListNumber();
        IToolItem listBullet = new ToolItem_ListBullet();
        IToolItem hr = new ToolItem_Hr();
        IToolItem link = new ToolItem_Link();
        IToolItem subscript = new ToolItem_Subscript();
        IToolItem superscript = new ToolItem_Superscript();
        IToolItem left = new ToolItem_AlignmentLeft();
        IToolItem center = new ToolItem_AlignmentCenter();
        IToolItem right = new ToolItem_AlignmentRight();
        IToolItem image = new ToolItem_Image();
        IToolItem at = new ToolItem_At();

        mToolbar.addToolbarItem(bold);
        mToolbar.addToolbarItem(italic);
        mToolbar.addToolbarItem(underline);
        mToolbar.addToolbarItem(strikethrough);
        mToolbar.addToolbarItem(fontSize);
        mToolbar.addToolbarItem(fontColor);
        mToolbar.addToolbarItem(backgroundColor);
        mToolbar.addToolbarItem(quote);
        mToolbar.addToolbarItem(listNumber);
        mToolbar.addToolbarItem(listBullet);
        mToolbar.addToolbarItem(hr);
        mToolbar.addToolbarItem(link);
        mToolbar.addToolbarItem(subscript);
        mToolbar.addToolbarItem(superscript);
        mToolbar.addToolbarItem(left);
        mToolbar.addToolbarItem(center);
        mToolbar.addToolbarItem(right);
        mToolbar.addToolbarItem(image);
        mToolbar.addToolbarItem(at);

        mEditText = this.findViewById(R.id.arEditText);
        mEditText.setToolbar(mToolbar);
        mEditText.setImageStrategy(imageStrategy);


        try {
            InputStream inputStream = new FileInputStream(directory);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            String filecontent = stringBuilder.toString();
            setHtml(filecontent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setHtml(String fileContent) {
        String html = fileContent;

        mEditText.fromHtml(html);
    }
    /**
     private void initToolbarArrow() {
     final ImageView imageView = this.findViewById(R.id.arrow);
     if (this.mToolbar instanceof DefaultToolbar) {
     ((DefaultToolbar) mToolbar).getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
    @Override
    public void onScrollChanged() {
    int scrollX = ((DefaultToolbar) mToolbar).getScrollX();
    int scrollWidth = ((DefaultToolbar) mToolbar).getWidth();
    int fullWidth = ((DefaultToolbar) mToolbar).getChildAt(0).getWidth();

    if (scrollX + scrollWidth < fullWidth) {
    imageView.setImageResource(R.drawable.arrow_mini);
    scrollerAtEnd = false;
    } else {
    imageView.setImageResource(R.drawable.arrow_mini);
    scrollerAtEnd = true;
    }
    }
    });
     }

     imageView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    if (scrollerAtEnd) {
    ((DefaultToolbar) mToolbar).smoothScrollBy(-Integer.MAX_VALUE, 0);
    scrollerAtEnd = false;
    } else {
    int hsWidth = ((DefaultToolbar) mToolbar).getChildAt(0).getWidth();
    ((DefaultToolbar) mToolbar).smoothScrollBy(hsWidth, 0);
    scrollerAtEnd = true;
    }
    }
    });
     }
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        if (menuId == R.id.action_save) {
            String html = this.mEditText.getHtml();
            SaveUtil.saveHtml(this, html, fileName);
            return true;
        }
        if (menuId == R.id.action_show_tv) {
            String html = this.mEditText.getHtml();
            Intent intent = new Intent(this, TextViewActivity.class);
            intent.putExtra(HTML_TEXT, html);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mToolbar.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();
        CharSequence selectedText = getIntent()
                .getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        if(selectedText == null){

        }else {

            setHtml(selectedText.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SaveUtil.saveHtml(this, mEditText.getHtml(), fileName);
    }
}


