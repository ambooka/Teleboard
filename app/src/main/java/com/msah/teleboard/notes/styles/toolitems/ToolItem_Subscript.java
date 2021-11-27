package com.msah.teleboard.notes.styles.toolitems;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.msah.teleboard.notes.spans.CustomSubscriptSpan;
import com.msah.teleboard.notes.styles.IStyle;
import com.msah.teleboard.notes.styles.toolitems.styles.Style_Subscript;
import com.msah.teleboard.utils.Constants;
import com.msah.teleboard.R;
import com.msah.teleboard.utils.Util;
import com.msah.teleboard.notes.views.CustomEditText;

public class ToolItem_Subscript extends ToolItem_Abstract {

    @Override
    public IToolItem_Updater getToolItemUpdater() {
        if (mToolItemUpdater == null) {
            mToolItemUpdater = new ToolItem_UpdaterDefault(this, Constants.CHECKED_COLOR, Constants.UNCHECKED_COLOR);
            setToolItemUpdater(mToolItemUpdater);
        }
        return mToolItemUpdater;
    }

    @Override
    public IStyle getStyle() {
        if (mStyle == null) {
            CustomEditText editText = this.getEditText();
            IToolItem_Updater toolItemUpdater = getToolItemUpdater();
            mStyle = new Style_Subscript(editText, (ImageView) mToolItemView, toolItemUpdater);
        }
        return mStyle;
    }

    @Override
    public View getView(Context context) {
        if (null == context) {
            return mToolItemView;
        }
        if (mToolItemView == null) {
            ImageView imageView = new ImageView(context);
            int size = Util.getPixelByDp(context, 30);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.ic_baseline_subscript_24);
            imageView.bringToFront();
            mToolItemView = imageView;
        }

        return mToolItemView;
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {

		boolean subscriptExists = false;

		//
		// Two cases:
		// 1. Selection is just a pure cursor
		// 2. Selection is a range
		Editable editable = this.getEditText().getEditableText();
		if (selStart > 0 && selStart == selEnd) {
			CustomSubscriptSpan[] subscriptSpans = editable.getSpans(selStart - 1, selStart, CustomSubscriptSpan.class);
			if (subscriptSpans != null && subscriptSpans.length > 0) {
                subscriptExists = true;
			}
		} else {
            CustomSubscriptSpan[] subscriptSpans = editable.getSpans(selStart, selEnd, CustomSubscriptSpan.class);
            if (subscriptSpans != null && subscriptSpans.length > 0) {
                if (editable.getSpanStart(subscriptSpans[0]) <= selStart
                        && editable.getSpanEnd(subscriptSpans[0]) >= selEnd) {
                    subscriptExists = true;
                }
            }
        }

        mToolItemUpdater.onCheckStatusUpdate(subscriptExists);
    }
}
