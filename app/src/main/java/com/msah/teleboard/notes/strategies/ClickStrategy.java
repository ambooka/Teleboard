package com.msah.teleboard.notes.strategies;


import android.content.Context;
import android.text.style.URLSpan;

import com.msah.teleboard.notes.spans.AtSpan;
import com.msah.teleboard.notes.spans.CustomImageSpan;


public interface ClickStrategy {


    // boolean onClick(Context context, ARE_Clickable_Span areClickableSpan);

    /**
     * Do your actions upon span clicking {@link com.msah.teleboard.notes.spans.AtSpan}
     *
     * @param context
     * @param atSpan
     * @return handled return true; or else return false
     */
    boolean onClickAt(Context context, AtSpan atSpan);

    /**
     * Do your actions upon span clicking {@link com.msah.teleboard.notes.spans.CustomImageSpan}
     *
     * @param context
     * @param imageSpan
     * @return handled return true; or else return false
     */
    boolean onClickImage(Context context, CustomImageSpan imageSpan);

    /**
     * Do your actions upon span clicking {@link URLSpan}
     *
     * @param context
     * @param urlSpan
     * @return handled return true; or else return false
     */
    boolean onClickUrl(Context context, URLSpan urlSpan);

    /**
     *Do your actions upon span clicking {@link com.chinalwb.are.spans.AreVideoSpan}
     *
     * @param context
     * @param videoSpan
     * @return handled return true; or else return false
     */
}

