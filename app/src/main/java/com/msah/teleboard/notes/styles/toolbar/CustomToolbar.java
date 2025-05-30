package com.msah.teleboard.notes.styles.toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.text.Layout.Alignment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.msah.teleboard.R;
import com.msah.teleboard.colorpicker.ColorPickerListener;
import com.msah.teleboard.colorpicker.ColorPickerView;
import com.msah.teleboard.notes.models.AtItem;
import com.msah.teleboard.notes.spans.CustomImageSpan;
import com.msah.teleboard.notes.styles.At;
import com.msah.teleboard.notes.styles.BackgroundColor;
import com.msah.teleboard.notes.styles.Bold;
import com.msah.teleboard.notes.styles.CustomAlignment;
import com.msah.teleboard.notes.styles.CustomQuote;
import com.msah.teleboard.notes.styles.FontColor;
import com.msah.teleboard.notes.styles.FontSize;
import com.msah.teleboard.notes.styles.Fontface;
import com.msah.teleboard.notes.styles.Hr;
import com.msah.teleboard.notes.styles.IStyle;
import com.msah.teleboard.notes.styles.Image;
import com.msah.teleboard.notes.styles.IndentLeft;
import com.msah.teleboard.notes.styles.IndentRight;
import com.msah.teleboard.notes.styles.Italic;
import com.msah.teleboard.notes.styles.Link;
import com.msah.teleboard.notes.styles.ListBullet;
import com.msah.teleboard.notes.styles.ListNumber;
import com.msah.teleboard.notes.styles.Strikethrough;
import com.msah.teleboard.notes.styles.Subscript;
import com.msah.teleboard.notes.styles.Superscript;
import com.msah.teleboard.notes.styles.Underline;
import com.msah.teleboard.utils.Util;
import com.msah.teleboard.notes.views.CustomEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * A fixed toolbar, for including only static tool items.
 * Not friendly for extending.
 *
 */
public class CustomToolbar extends LinearLayout {

    /**
     * Request code for selecting an image.
     */
    public static final int REQ_IMAGE = 1;

    /**
     * Request code for choosing a people to @.
     */
    public static final int REQ_AT = 2;

    /**
     * Request code for choosing a video.
     */
    public static final int REQ_VIDEO_CHOOSE = 3;

    /**
     * Request code for inserting a video
     */
    public static final int REQ_VIDEO = 4;


    private Activity mContext;

    private CustomEditText mEditText;

    /**
     * Supported styles list.
     */
    private ArrayList<IStyle> mStylesList = new ArrayList<>();

    /**
     * Video Style
     */

    /**
     * Emoji Style
     */

    /**
     * Font-size Style
     */
    private FontSize mFontsizeStyle;

    /**
     * Font-face Style
     */
    private Fontface mFontfaceStyle;

    /**
     * Bold Style
     */
    private Bold mBoldStyle;

    /**
     * Italic Style
     */
    private Italic mItalicStyle;

    /**
     * Underline Style
     */
    private Underline mUnderlineStyle;

    /**
     * Strikethrough Style
     */
    private Strikethrough mStrikethroughStyle;

    /**
     * Horizontal rule style
     */
    private Hr mHrStyle;

    /**
     * Subscript Style
     */
    private Subscript mSubscriptStyle;

    /**
     * Superscript Style
     */
    private Superscript mSuperscriptStyle;

    /**
     * Quote style
     */
    private CustomQuote mQuoteStyle;

    /**
     * Font color Style
     */
    private FontColor mFontColorStyle;

    /**
     * Background color Style
     */
    private BackgroundColor mBackgroundColoStyle;

    /**
     * Link Style
     */
    private Link mLinkStyle;

    /**
     * List number Style
     */
    private ListNumber mListNumberStyle;

    /**
     * List bullet Style
     */
    private ListBullet mListBulletStyle;

    /**
     * Indent to right Style.
     */
    private IndentRight mIndentRightStyle;

    /**
     * Indent to left Style.
     */
    private IndentLeft mIndentLeftStyle;

    /**
     * Align left.
     */
    private CustomAlignment mAlignLeft;

    /**
     * Align center.
     */
    private CustomAlignment mAlignCenter;

    /**
     * Align right.
     */
    private CustomAlignment mAlignRight;

    /**
     * Insert image style.
     */
    private Image mImageStyle;

    /**
     * At @ mention.
     */
    private At mAtStyle;

    /**
     * Emoji button.
     */
    private ImageView mEmojiImageView;

    /**
     * Absolute font size button.
     */
    private ImageView mFontsizeImageView;

    /**
     * Absolute font face button.
     */
    private ImageView mFontfaceImageView;

    /**
     * Bold button.
     */
    private ImageView mBoldImageView;

    /**
     * Italic button.
     */
    private ImageView mItalicImageView;

    /**
     * Underline button.
     */
    private ImageView mUnderlineImageView;

    /**
     * Strikethrough button.
     */
    private ImageView mStrikethroughImageView;

    /**
     * Horizontal rule button.
     */
    private ImageView mHrImageView;

    /**
     * Subscript button.
     */
    private ImageView mSubscriptImageView;

    /**
     * Superscript button.
     */
    private ImageView mSuperscriptImageView;

    /**
     * Quote button.
     */
    private ImageView mQuoteImageView;

    /**
     * The color palette.
     */
    private ColorPickerView mColorPalette;

    /**
     * The emoji panel
     */
    private View mEmojiPanelContainer;

    /**
     * Foreground color image view.
     */
    private ImageView mFontColorImageView;

    /**
     * Background button.
     */
    private ImageView mBackgroundImageView;

    /**
     * Add link button.
     */
    private ImageView mLinkImageView;

    /**
     * List number button.
     */
    private ImageView mRteListNumber;

    /**
     * List bullet button.
     */
    private ImageView mRteListBullet;

    /**
     * Increase. Indent to right.
     */
    private ImageView mRteIndentRight;

    /**
     * Align left.
     */
    private ImageView mRteAlignLeft;

    /**
     * Align center.
     */
    private ImageView mRteAlignCenter;

    /**
     * Align right.
     */
    private ImageView mRteAlignRight;

    /**
     * Decrease. Indent to left.
     */
    private ImageView mRteIndentLeft;

    /**
     * Insert image button.
     */
    private ImageView mRteInsertImage;

    /**
     * Insert video button.
     */
    private ImageView mRteInsertVideo;

    /**
     * @ mention image button.
     */
    private ImageView mRteAtImage;

    public CustomToolbar(Context context) {
        this(context, null);
    }

    public CustomToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomToolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = (Activity) context;
        init();
    }

    private void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(this.mContext);
        layoutInflater.inflate(getLayoutId(), this, true);
        this.setOrientation(LinearLayout.VERTICAL);
        initViews();
        initStyles();
        initKeyboard();
    }

    private int getLayoutId() {
        return R.layout.custom_toolbar;
    }

    private void initViews() {

        this.mEmojiImageView = this.findViewById(R.id.rteEmoji);
        this.mFontsizeImageView = this.findViewById(R.id.rteFontsize);

        this.mFontfaceImageView = this.findViewById(R.id.rteFontface);

        this.mBoldImageView = this.findViewById(R.id.rteBold);

        this.mItalicImageView = this.findViewById(R.id.rteItalic);

        this.mUnderlineImageView = this.findViewById(R.id.rteUnderline);

        this.mQuoteImageView = this.findViewById(R.id.rteQuote);

        this.mColorPalette = this.findViewById(R.id.rteColorPalette);

        this.mEmojiPanelContainer = this.findViewById(R.id.rteEmojiPanel);

        this.mFontColorImageView = this.findViewById(R.id.rteFontColor);

        this.mStrikethroughImageView = this.findViewById(R.id.rteStrikethrough);

        this.mHrImageView = this.findViewById(R.id.rteHr);

        this.mSubscriptImageView = this.findViewById(R.id.rteSubscript);

        this.mSuperscriptImageView = this.findViewById(R.id.rteSuperscript);

        this.mBackgroundImageView = this.findViewById(R.id.rteBackground);

        this.mLinkImageView = this.findViewById(R.id.rteLink);

        this.mRteListNumber = this.findViewById(R.id.rteListNumber);

        this.mRteListBullet = this.findViewById(R.id.rteListBullet);

        this.mRteIndentRight = this.findViewById(R.id.rteIndentRight);

        this.mRteIndentLeft = this.findViewById(R.id.rteIndentLeft);

        this.mRteAlignLeft = this.findViewById(R.id.rteAlignLeft);

        this.mRteAlignCenter = this.findViewById(R.id.rteAlignCenter);

        this.mRteAlignRight = this.findViewById(R.id.rteAlignRight);

        this.mRteInsertImage = this.findViewById(R.id.rteInsertImage);

        this.mRteInsertVideo = this.findViewById(R.id.rteInsertVideo);

        this.mRteAtImage = this.findViewById(R.id.rteAt);

    }

    /**
     *
     */
    private void initStyles() {
        this.mFontsizeStyle = new FontSize(this.mFontsizeImageView, this);
        this.mFontfaceStyle = new Fontface(this.mFontfaceImageView, this);
        this.mBoldStyle = new Bold(this.mBoldImageView);
        this.mItalicStyle = new Italic(this.mItalicImageView);
        this.mUnderlineStyle = new Underline(this.mUnderlineImageView);
        this.mStrikethroughStyle = new Strikethrough(this.mStrikethroughImageView);
        this.mHrStyle = new Hr(this.mHrImageView, this);
        this.mSubscriptStyle = new Subscript(this.mSubscriptImageView);
        this.mSuperscriptStyle = new Superscript(this.mSuperscriptImageView);
        this.mQuoteStyle = new CustomQuote(this.mQuoteImageView);
        this.mFontColorStyle = new FontColor(this.mFontColorImageView, this);
        this.mBackgroundColoStyle = new BackgroundColor(this.mBackgroundImageView, Color.YELLOW);
        this.mLinkStyle = new Link(this.mLinkImageView, this);
        this.mListNumberStyle = new ListNumber(this.mRteListNumber, this);
        this.mListBulletStyle = new ListBullet(this.mRteListBullet, this);
        this.mIndentRightStyle = new IndentRight(this.mRteIndentRight, this);
        this.mIndentLeftStyle = new IndentLeft(this.mRteIndentLeft, this);
        this.mAlignLeft = new CustomAlignment(this.mRteAlignLeft, Alignment.ALIGN_NORMAL, this);
        this.mAlignCenter = new CustomAlignment(this.mRteAlignCenter, Alignment.ALIGN_CENTER, this);
        this.mAlignRight = new CustomAlignment(this.mRteAlignRight, Alignment.ALIGN_OPPOSITE, this);
        this.mImageStyle = new Image(this.mRteInsertImage);
        this.mAtStyle = new At(this);

        this.mStylesList.add(this.mFontsizeStyle);
        this.mStylesList.add(this.mFontfaceStyle);
        this.mStylesList.add(this.mBoldStyle);
        this.mStylesList.add(this.mItalicStyle);
        this.mStylesList.add(this.mUnderlineStyle);
        this.mStylesList.add(this.mStrikethroughStyle);
        this.mStylesList.add(this.mHrStyle);
        this.mStylesList.add(this.mSubscriptStyle);
        this.mStylesList.add(this.mSuperscriptStyle);
        this.mStylesList.add(this.mQuoteStyle);
        this.mStylesList.add(this.mFontColorStyle);
        this.mStylesList.add(this.mBackgroundColoStyle);
        this.mStylesList.add(this.mLinkStyle);
        this.mStylesList.add(this.mListNumberStyle);
        this.mStylesList.add(this.mListBulletStyle);
        this.mStylesList.add(this.mIndentRightStyle);
        this.mStylesList.add(this.mIndentLeftStyle);
        this.mStylesList.add(this.mAlignLeft);
        this.mStylesList.add(this.mAlignCenter);
        this.mStylesList.add(this.mAlignRight);
        this.mStylesList.add(this.mImageStyle);
        this.mStylesList.add(this.mAtStyle);
    }

    public void setUseEmoji(boolean useEmoji) {
        if (useEmoji) {
            mEmojiImageView.setVisibility(View.VISIBLE);
        } else {
            mEmojiImageView.setVisibility(View.GONE);
        }
    }

    public void setEditText(CustomEditText editText) {
        this.mEditText = editText;
        bindToolbar();
    }

    private void bindToolbar() {
        this.mFontsizeStyle.setEditText(this.mEditText);
        this.mBoldStyle.setEditText(this.mEditText);
        this.mItalicStyle.setEditText(this.mEditText);
        this.mUnderlineStyle.setEditText(this.mEditText);
        this.mStrikethroughStyle.setEditText(this.mEditText);
        this.mHrStyle.setEditText(this.mEditText);
        this.mSubscriptStyle.setEditText(this.mEditText);
        this.mSuperscriptStyle.setEditText(this.mEditText);
        this.mQuoteStyle.setEditText(this.mEditText);
        this.mFontColorStyle.setEditText(this.mEditText);
        this.mBackgroundColoStyle.setEditText(this.mEditText);
        this.mLinkStyle.setEditText(this.mEditText);
        this.mImageStyle.setEditText(this.mEditText);
        this.mAtStyle.setEditText(this.mEditText);
    }

    public CustomEditText getEditText() {
        return this.mEditText;
    }

    public IStyle getBoldStyle() {
        return this.mBoldStyle;
    }

    public Italic getItalicStyle() {
        return this.mItalicStyle;
    }

    public Underline getUnderlineStyle() {
        return mUnderlineStyle;
    }

    public Strikethrough getStrikethroughStyle() {
        return mStrikethroughStyle;
    }

    public Hr getHrStyle() {
        return mHrStyle;
    }

    public Subscript getSubscriptStyle() {
        return this.mSubscriptStyle;
    }

    public Superscript getSuperscriptStyle() {
        return this.mSuperscriptStyle;
    }

    public CustomQuote getQuoteStyle() {
        return mQuoteStyle;
    }

    public FontColor getTextColorStyle() { return  this.mFontColorStyle; }

    public BackgroundColor getBackgroundColoStyle() {
        return mBackgroundColoStyle;
    }

    public Image getImageStyle() {
        return mImageStyle;
    }

    public List<IStyle> getStylesList() {
        return this.mStylesList;
    }

    public void toggleColorPalette(ColorPickerListener colorPickerListener) {
        int visibility = this.mColorPalette.getVisibility();
        this.mColorPalette.setColorPickerListener(colorPickerListener);
        if (View.VISIBLE == visibility) {
            this.mColorPalette.setVisibility(View.GONE);
        } else {
            this.mColorPalette.setVisibility(View.VISIBLE);
        }
    }

    public void setColorPaletteColor(int color) {
        this.mColorPalette.setColor(color);
    }



    /**
     * On activity result.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mEmojiPanelContainer.setVisibility(View.GONE);
        mEmojiShownNow = false;
        if (resultCode == Activity.RESULT_OK) {
            if (REQ_IMAGE == requestCode) {
                Uri uri = data.getData();
                this.mImageStyle.insertImage(uri, CustomImageSpan.ImageType.URI);
            } else if (REQ_AT == requestCode) {
                AtItem atItem = (AtItem) data.getSerializableExtra(At.EXTRA_TAG);
                if (null == atItem) { return; }
                this.mAtStyle.insertAt(atItem);

            }
        }
    }

    /* -------- START: Keep it at the bottom of the class.. Keyboard and emoji ------------ */
    /* -------- START: Keep it at the bottom of the class.. Keyboard and emoji ------------ */
    private void initKeyboard() {
        final Window window = mContext.getWindow();
        final View rootView = window.getDecorView().findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (mLayoutDelay == 0) {
                            init();
                            return;
                        }
                        rootView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                init();
                            }
                        }, mLayoutDelay);

                    }

                    private void init() {
                        Rect r = new Rect();
                        View view = window.getDecorView();
                        view.getWindowVisibleDisplayFrame(r);
                        int[] screenWandH = Util.getScreenWidthAndHeight(mContext);
                        int screenHeight = screenWandH[1];
                        final int keyboardHeight = screenHeight - r.bottom;

                        if (mPreviousKeyboardHeight != keyboardHeight) {
                            if (keyboardHeight > 100) {
                                mKeyboardHeight = keyboardHeight;
                                onKeyboardShow();
                            } else {
                                onKeyboardHide();
                            }
                        }
                        mPreviousKeyboardHeight = keyboardHeight;
                    }
                });
    }

    private void onKeyboardShow() {
        mKeyboardShownNow = true;
        toggleEmojiPanel(false);
        mEmojiShownNow = false;
        mLayoutDelay = 100;
    }

    private void onKeyboardHide() {
        mKeyboardShownNow = false;
        if (mHideEmojiWhenHideKeyboard) {
            toggleEmojiPanel(false);
        } else {
            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHideEmojiWhenHideKeyboard = true;
                }
            }, 100);
        }
    }

    private int mLayoutDelay = 0;
    private int mPreviousKeyboardHeight = 0;
    private boolean mKeyboardShownNow = true;
    private boolean mEmojiShownNow = false;
    private boolean mHideEmojiWhenHideKeyboard = true;
    private int mKeyboardHeight = 0;
    private View mEmojiPanel;


    private void initEmojiPanelHeight(int expectHeight) {
        int emojiHeight = this.mEmojiPanelContainer.getHeight();
        if (emojiHeight != expectHeight) {
            LayoutParams layoutParams = (LayoutParams) mEmojiPanelContainer.getLayoutParams();
            layoutParams.height = expectHeight;
            mEmojiPanelContainer.setLayoutParams(layoutParams);
            if (mEmojiPanel != null) {
                LayoutParams emojiPanelLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                mEmojiPanel.setLayoutParams(emojiPanelLayoutParams);
                ((ViewGroup) mEmojiPanelContainer).addView(mEmojiPanel);
            }
            mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    public void toggleEmojiPanel(boolean byClickEmoji) {
        if (mKeyboardShownNow) {
            if (byClickEmoji) {
                // Keyboard is shown now
                // Click emoji icon we should hide keyboard and keep emoji panel open

                // 1. Set keyboard as hide
                mKeyboardShownNow = false;

                // 2. When hide keyboard, don't need to hide emoji
                // as we need to open emoji
                mHideEmojiWhenHideKeyboard = false;

                // 3. Hide keyboard
                View view = mContext.getCurrentFocus();
                Util.hideKeyboard(view, mContext);

                // 4. Show emoji
                initEmojiPanelHeight(mKeyboardHeight);
                mEmojiPanelContainer.setVisibility(View.VISIBLE);

                // 5. Set emoji is shown now
                mEmojiShownNow = true;

                // 6. Change emoji icon to keyboard
                mEmojiImageView.setImageResource(R.drawable.ic_baseline_keyboard_24);
            } else {
                // Keyboard is shown now
                // Toggle emoji panel to make the layout looks well for adjustPan

                // 1. Sets emoji panel to visible so it takes up height
                mEmojiPanelContainer.setVisibility(View.VISIBLE);

                // 2. Although emoji panel takes up height but it is not shown now
                // (NOT VISIBLE TO USER, AS KEYBOARD IS BEING SHOWN)
                mEmojiShownNow = false;
            }
        } else {
            if (byClickEmoji) {
                // Keyboard is hide, then use clicks emoji
                // We should consider two cases
                // 1. keyboard is hidden but emoji is shown
                // 2. keyboard is hidden and Emoji is hidden too


                if (mEmojiShownNow) {
                    // Case 1: keyboard is hidden but emoji is shown
                    // And user clicks emoji icon
                    //
                    // We should show keyboard and hide emoji

                    // 1. Set keyboard as shown now
                    mKeyboardShownNow = true;

                    // 1.1. Show keyboard
                    View view = getEditText();
                    showKeyboard(view);

                    // 2. Set emoji as hide now
                    mEmojiShownNow = false;

                    // 3. Change emoji icon to emoji
                    mEmojiImageView.setImageResource(R.drawable.ic_baseline_insert_emoticon_24);
                } else {
                    // Case 2: keyboard is hidden and Emoji is hidden too
                    // And user clicks emoji icon
                    //
                    // We should show emoji panel

                    // 1. Show emoji panel
                    initEmojiPanelHeight(mKeyboardHeight);
                    mEmojiPanelContainer.setVisibility(View.VISIBLE);
                    // 1.1 Set emoji panel as shown now
                    mEmojiShownNow = true;
                    // 1.2 Change emoji icon to keyboard
                    mEmojiImageView.setImageResource(R.drawable.ic_baseline_keyboard_24);
                }

            } else {
                // User clicks the virtual button to hide keyboard
                // We should hide emoji panel
                mEmojiPanelContainer.setVisibility(View.GONE);
                mEmojiShownNow = false;
                mEmojiImageView.setImageResource(R.drawable.ic_baseline_insert_emoticon_24);
            }
        }
    }

    protected void showKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                view.requestFocus();
                imm.showSoftInput(view, 0);
            }
        }
    }

    public void setEmojiPanel(View emojiPanel) {
        mEmojiPanel = emojiPanel;
    }
    /* -------- END: Keep it at the bottom of the class.. Keyboard and emoji ------------ */
    /* -------- END: Keep it at the bottom of the class.. Keyboard and emoji ------------ */
}

