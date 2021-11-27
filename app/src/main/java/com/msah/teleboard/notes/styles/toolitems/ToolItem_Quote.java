package com.msah.teleboard.notes.styles.toolitems;

import android.content.Context;
import android.text.Editable;
import android.text.style.QuoteSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.msah.teleboard.notes.spans.CustomQuoteSpan;
import com.msah.teleboard.notes.styles.IStyle;
import com.msah.teleboard.notes.styles.toolitems.styles.Style_Quote;
import com.msah.teleboard.utils.Constants;
import com.msah.teleboard.R;
import com.msah.teleboard.utils.Util;
import com.msah.teleboard.notes.views.CustomEditText;


public class ToolItem_Quote extends ToolItem_Abstract {

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
            mStyle = new Style_Quote(editText, (ImageView) mToolItemView, toolItemUpdater);
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
            int size = Util.getPixelByDp(context, 40);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.ic_baseline_format_quote_24);
            imageView.bringToFront();
            mToolItemView = imageView;
        }

        return mToolItemView;
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        boolean quoteExists = false;

        CustomEditText editText = this.getEditText();
        Editable editable = editText.getEditableText();
        printSpans(CustomQuoteSpan.class);
        if (selStart > 0 && selStart == selEnd) {
            QuoteSpan[] quoteSpans = editable.getSpans(selStart - 1, selStart, QuoteSpan.class);
            if (quoteSpans != null && quoteSpans.length > 0) {
                quoteExists = true;
            }
        } else {
            QuoteSpan[] quoteSpans = editable.getSpans(selStart, selEnd, QuoteSpan.class);
            if (quoteSpans != null && quoteSpans.length > 0) {
                if (editable.getSpanStart(quoteSpans[0]) <= selStart
                        && editable.getSpanEnd(quoteSpans[0]) >= selEnd) {
                    quoteExists = true;
                }
            }
        }


        mToolItemUpdater.onCheckStatusUpdate(quoteExists);
    }
}
