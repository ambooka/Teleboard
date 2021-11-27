package com.msah.teleboard.notes.styles.toolitems;

import android.view.View;

import com.msah.teleboard.notes.styles.IStyle;


/**
 * The default tool item check status updater.
 */
public class ToolItem_UpdaterDefault implements IToolItem_Updater {

    private IToolItem mToolItem;

    private int mCheckedColor;

    private int mUncheckedColor;

    public ToolItem_UpdaterDefault(IToolItem toolItem, int checkedColor, int uncheckedColor) {
        mToolItem = toolItem;
        mCheckedColor = checkedColor;
        mUncheckedColor = uncheckedColor;
    }

    @Override
    public void onCheckStatusUpdate(boolean checked) {
        IStyle areStyle = mToolItem.getStyle();
        areStyle.setChecked(checked);
        View view = mToolItem.getView(null);
        int color = checked ? mCheckedColor : mUncheckedColor;
        view.setBackgroundColor(color);
    }
}

