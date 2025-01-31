package com.example.rundumdog;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
public class EntryCardDesign extends RecyclerView.ItemDecoration {
    private int largePadding;
    private int smallPadding;

    //initialises card padding for Entry card layout
    public EntryCardDesign(int largePadding, int smallPadding) {
        this.largePadding = largePadding;
        this.smallPadding = smallPadding;
    }

    //generates Entry card offsets
    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = smallPadding;
        outRect.right = smallPadding;
        outRect.top = largePadding;
        outRect.bottom = largePadding;
    }
}