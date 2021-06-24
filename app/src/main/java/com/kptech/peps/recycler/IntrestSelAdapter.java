package com.kptech.peps.recycler;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kptech.peps.R;
import com.kptech.peps.utils.DataValidator;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by suchandra on 12/3/2017.
 */

public class IntrestSelAdapter extends RecyclerView.Adapter<IntrestSelAdapter.MyViewHolder> {
    private static final String TAG = IntrestSelAdapter.class.getName();

    private List<String> itemdata = new ArrayList<>();
    private Context mContext;
    private boolean mShowAllComments;

    public void setItemList(List<String> data){
        if(data != null) {
            itemdata = data;
        }else{
            itemdata = new ArrayList<>();
        }
    }

    public String getSelectedPositionItem(int pos){
        if(pos < itemdata.size()) {
            return itemdata.get(pos);
        }else{
            return null;
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       // Log.d(TAG,"OnCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sel_places_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
      //  Log.d(TAG,"onBind Position is "+position);
        String data = itemdata.get( position);//reverse the list
        if(DataValidator.isValid(data)){
            holder.name.setText(data);
        }


    }

    @Override
    public int getItemCount() {
        if(itemdata != null){
                return itemdata.size();

        }else {
            return 0;
        }
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public boolean ismShowAllComments() {
        return mShowAllComments;
    }

    public void setmShowAllComments(boolean mShowAllComments) {
        this.mShowAllComments = mShowAllComments;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, commentTime,commentText;
        CircleImageView user_img;
        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);

        }
    }


}
