package com.msah.teleboard.notes.styles.toolitems;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.msah.teleboard.notes.models.AtItem;
import com.msah.teleboard.notes.styles.IStyle;
import com.msah.teleboard.notes.styles.toolitems.styles.Style_At;
import com.msah.teleboard.notes.views.CustomEditText;


public class ToolItem_At extends ToolItem_Abstract {

    @Override
    public IToolItem_Updater getToolItemUpdater() {
        return null;
    }

    @Override
    public IStyle getStyle() {
        if (mStyle == null) {
            CustomEditText editText = this.getEditText();
            mStyle = new Style_At(editText);
        }
        return mStyle;
    }

    @Override
    public View getView(Context context) {
        return null;
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        return;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (Style_At.REQUEST_CODE == requestCode) {
                AtItem atItem = (AtItem) data.getSerializableExtra(Style_At.EXTRA_TAG);
                if (null == atItem) { return; }
                ((Style_At) this.getStyle()).insertAt(atItem);
            }
        }
    }
}

