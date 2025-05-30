package com.msah.teleboard.notes;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.msah.teleboard.R;
import com.msah.teleboard.notes.views.CustomTextView;

public class TextViewActivity extends AppCompatActivity {
    public static final String HTML_TEXT = "html_text";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);
        CustomTextView customTextView = findViewById(R.id.custom_textView);
        String s = getIntent().getStringExtra(HTML_TEXT);
        if (null == s) {
            s =  "<html><body><p><b>aaaa</b></p><p><i>bbbb</i></p>\n" +
                    "    <p><u>cccc</u></p>\n" +
                    "    <p><span style=\"text-decoration:line-through;\">dddd</span></p>\n" +
                    "    <p style=\"text-align:start;\">Alignleft</p>\n" +
                    "    <p style=\"text-align:center;\">Align center</p>\n" +
                    "    <p style=\"text-align:end;\">Align right</p>\n" +
                    "    <p style=\"text-align:start;\">Align left</p>\n" +
                    "    <p style=\"text-align:start;\">Hello left<span style=\"background-color:#FFFF00;\"> good?</span> yes</p>\n" +
                    "    <p style=\"text-align:start;\">Text color <span style=\"color:#FF5722;\">red </span><span style=\"color:#4CAF50;\">green </span><span style=\"color:#2196F3;\">blue </span><span style=\"color:#9C27B0;\">purple</span><span style=\"color:#000000;\"> normal black</span></p>\n" +
                    "    <br>\n" +
                    "    <p style=\"text-align:start;\">Click to open <a href=\"http://www.qq.com\">QQ</a> website</p>\n" +
                    "    <br><br>\n" +
                    "    <blockquote><p style=\"text-align:start;\">Quote</p>\n" +
                    "    <p style=\"text-align:start;\">Quote 2nd line</p>\n" +
                    "    <br>\n" +
                    "    </blockquote>\n" +
                    "    <br><br>\n" +
                    "    <p style=\"text-align:start;\">2X<sub>1</sub><sup>2 </sup>+3X<sub>1</sub><sup>2</sup>=5X<sub>1</sub><sup>2</sup></p>\n" +
                    "    <br>\n" +
                    "    <br>\n" +
                    "    <p style=\"text-align:start;\"><hr /> </p>\n" +
                    "    <p style=\"text-align:start;\">Text <span style=\"font-size:32px\";>SIZE </span><span style=\"font-size:18px\";><span style=\"font-size:21px\";>normal</span></span></p>\n" +
                    "    <br>\n" +
                    "    <p style=\"text-align:center;\"><img src=\"emoji|2131230945\"></p>\n" +
                    "    <p style=\"text-align:start;\">Image:</p>\n" +
                    "    <p style=\"text-align:start;\"><img src=\"http://d.hiphotos.baidu.com/image/pic/item/6159252dd42a2834171827b357b5c9ea14cebfcf.jpg\"></p>\n" +
                    "    <p style=\"text-align:start;\"></p>\n" +
                    "    </body></html>";
        }
        customTextView.fromHtml(s);
    }
}
