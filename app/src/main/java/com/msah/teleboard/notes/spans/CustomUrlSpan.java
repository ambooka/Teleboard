package com.msah.teleboard.notes.spans;

import android.text.style.URLSpan;


public class CustomUrlSpan extends URLSpan implements Clickable_Span {

    public CustomUrlSpan(String url) {
        super(url);
    }

}
