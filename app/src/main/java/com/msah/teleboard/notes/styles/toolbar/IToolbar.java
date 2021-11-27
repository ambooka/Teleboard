package com.msah.teleboard.notes.styles.toolbar;

import android.content.Intent;


import com.msah.teleboard.notes.styles.toolitems.IToolItem;
import com.msah.teleboard.notes.views.CustomEditText;

import java.util.List;


public interface IToolbar {

    public void addToolbarItem(IToolItem toolbarItem);

    public List<IToolItem> getToolItems();

    public void setEditText(CustomEditText editText);

    public CustomEditText getEditText();

    public void onActivityResult(int requestCode, int resultCode, Intent data);

    public default void setVisibility(int visibility){

    }
}

