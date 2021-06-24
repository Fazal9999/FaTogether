package com.kptech.peps.recycler;

import android.view.View;

/**
 * Created by suchandra on 12/3/2017.
 */

public interface ClickListener {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}
