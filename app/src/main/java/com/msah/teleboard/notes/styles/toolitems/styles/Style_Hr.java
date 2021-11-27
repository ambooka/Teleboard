package com.msah.teleboard.notes.styles.toolitems.styles;


import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.teleboard.notes.spans.HrSpan;
import com.msah.teleboard.notes.styles.ABS_FreeStyle;
import com.msah.teleboard.utils.Constants;
import com.msah.teleboard.notes.views.CustomEditText;

public class Style_Hr extends ABS_FreeStyle {

    private CustomEditText mEditText;

    public Style_Hr(CustomEditText editText, ImageView imageView) {
        super(editText.getContext());
        this.mEditText = editText;
        setListenerForImageView(imageView);
    }

    @Override
    public EditText getEditText() {
        return this.mEditText;
    }

    public void setEditText(CustomEditText editText) {
        this.mEditText = editText;
    }

    @Override
    public void setListenerForImageView(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editable = mEditText.getEditableText();
                int start = mEditText.getSelectionStart();
                int end = mEditText.getSelectionEnd();

                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(Constants.CHAR_NEW_LINE);
                ssb.append(Constants.CHAR_NEW_LINE);
                ssb.append(Constants.ZERO_WIDTH_SPACE_STR);
                ssb.append(Constants.CHAR_NEW_LINE);
                ssb.setSpan(new HrSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                editable.replace(start, end, ssb);
            }
        });
    }

    @Override
    public void applyStyle(Editable editable, int start, int end) {
        // Do nothing
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

