package com.servicea.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
        SpacesItemDecoration(spacing);
    }

    private int halfSpace;

    public void SpacesItemDecoration(int space) {
        this.halfSpace = space / 2;
    }

    @Override
    public void getItemOffsets(
            @NonNull Rect outRect,
            @NonNull View view,
            @NonNull RecyclerView parent,
            @NonNull RecyclerView.State state
    ) {
        if (parent.getPaddingLeft() != halfSpace) {
            parent.setPadding(halfSpace, halfSpace, halfSpace, halfSpace);
            parent.setClipToPadding(false);
        }

        outRect.top = halfSpace;
        outRect.bottom = halfSpace;
        outRect.left = halfSpace;
        outRect.right = halfSpace;
    }
}
