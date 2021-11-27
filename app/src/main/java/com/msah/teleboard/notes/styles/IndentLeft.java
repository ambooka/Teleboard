package com.msah.teleboard.notes.styles;


import android.text.Editable;
import android.text.Spannable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.teleboard.notes.spans.CustomLeadingMarginSpan;
import com.msah.teleboard.notes.styles.toolbar.CustomToolbar;
import com.msah.teleboard.utils.Util;

public class IndentLeft extends ABS_FreeStyle {

    public IndentLeft(ImageView indentImageView, CustomToolbar toolbar) {
        super(toolbar);
        setListenerForImageView(indentImageView);
    }

    @Override
    public void setListenerForImageView(ImageView imageView) {
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = getEditText();
                int currentLine = Util.getCurrentCursorLine(editText);
                int start = Util.getThisLineStart(editText, currentLine);
                int end = Util.getThisLineEnd(editText, currentLine);

                Editable editable = editText.getText();
                CustomLeadingMarginSpan[] existingLMSpans = editable.getSpans(start, end, CustomLeadingMarginSpan.class);
                if (null != existingLMSpans && existingLMSpans.length == 1) {
                    CustomLeadingMarginSpan currentLeadingMarginSpan = existingLMSpans[0];
                    int originalEnd = editable.getSpanEnd(currentLeadingMarginSpan);
                    editable.removeSpan(currentLeadingMarginSpan);
                    int currentLevel = currentLeadingMarginSpan.decreaseLevel();
                    if (currentLevel > 0) {
                        editable.setSpan(currentLeadingMarginSpan, start, originalEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                }
                else {
                    // No leading margin span found 
                    // Do nothing
                }
            }
        });
    }

    @Override
    public void applyStyle(Editable editable, int start, int end) {
        // TODO Auto-generated method stub

    }

    @Override
    public ImageView getImageView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setChecked(boolean isChecked) {
        // TODO Auto-generated method stub

    }
}
