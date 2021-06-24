package com.kptech.peps.customview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.kptech.peps.R;
import com.tokenautocomplete.TokenCompleteTextView;


import static java.security.AccessController.getContext;

/**
 * Created by Farman on 7/19/2017.
 */

public class ExpertAreaCompletionView extends TokenCompleteTextView<String>
{
    private Context context;
    public ExpertAreaCompletionView(Context context) {
        super(context);
        this.context=context;
    }
    public ExpertAreaCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    @Override
    protected View getViewForObject(String object) {
        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View  view = (View ) l.inflate(R.layout.interest_view, (ViewGroup) getParent(), false);
        TokenTextView tokenTextView=(TokenTextView)view.findViewById(R.id.name);
        tokenTextView.setText(object);

        return view;
    }

    @Override
    protected String defaultObject(String completionText) {
       return completionText;
    }


}
