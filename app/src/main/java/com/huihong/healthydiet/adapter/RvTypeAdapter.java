package com.huihong.healthydiet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huihong.healthydiet.R;
import com.huihong.healthydiet.mInterface.ItemOnClickListener;
import com.huihong.healthydiet.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zangyi_shuai_ge on 2017/5/16
 */

public class RvTypeAdapter extends RecyclerView.Adapter<RvTypeAdapter.RvTypeViewHolder> {


    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mList;

    private ItemOnClickListener mItemOnClickListener;

    public void setItemOnClickListener(ItemOnClickListener pItemOnClickListener) {
        mItemOnClickListener = pItemOnClickListener;
    }

    public RvTypeAdapter(Context pContext, List<String> pList) {
        if(pList!=null){
            mList = pList;
        }else {
            mList = new ArrayList<>();
        }
        mContext = pContext;
        mInflater = LayoutInflater.from(mContext);
    }


    @Override
    public RvTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = mInflater.inflate(R.layout.rv_type_item, parent, false);

        return new RvTypeViewHolder(mView);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final RvTypeViewHolder holder, int position) {
        String type = mList.get(position);
        MyUtils.setImageViewType(holder.ivType,type);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class RvTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivType;

        RvTypeViewHolder(View itemView) {
            super(itemView);
            ivType = (ImageView) itemView.findViewById(R.id.ivType);
        }
    }
}

