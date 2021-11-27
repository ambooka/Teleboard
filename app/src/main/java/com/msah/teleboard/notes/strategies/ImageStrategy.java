package com.msah.teleboard.notes.strategies;


import android.net.Uri;

import com.msah.teleboard.notes.styles.toolitems.styles.Style_Image;

public interface ImageStrategy {

    /**
     * Upload the video to server and return the url of the video at server.
     * After that done, you need to call
     * {@link Style_Image#insertImage(Object, com.msah.teleboard.notes.spans.CustomImageSpan.ImageType)}
     * to insert the url on server to ARE
     *
     * @param uri
     * @param areStyleImage used to insert the url on server to ARE
     */
    void uploadAndInsertImage(Uri uri, Style_Image areStyleImage);
}

