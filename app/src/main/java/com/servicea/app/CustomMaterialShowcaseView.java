package com.servicea.app;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import ir.servicea.R;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class CustomMaterialShowcaseView extends MaterialShowcaseView {

    public CustomMaterialShowcaseView(Context context) {
        super(context);
        init();
    }

    public CustomMaterialShowcaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomMaterialShowcaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // Customize the text view here
        TextView contentTextView = findViewById(R.id.tv_content);
        if (contentTextView != null) {
            contentTextView.setTextSize(18); // Set your desired text size here
            contentTextView.setTextColor(Color.WHITE); // Set text color if needed
        }
    }
}
