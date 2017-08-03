package com.huihong.healthydiet.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huihong.healthydiet.R;
import com.huihong.healthydiet.activity.RecipesDetailsActivity;
import com.huihong.healthydiet.bean.TitlePage;
import com.huihong.healthydiet.mInterface.ItemOnClickListener;
import com.joooonho.SelectableRoundedImageView;

import java.util.List;

/**
 * Created by zangyi_shuai_ge on 2017/5/16
 * 首页推荐饮食 RecyclerView
 */

public class RvRecommendAdapter extends RecyclerView.Adapter<RvRecommendViewHolder> {


    private LayoutInflater mInflater;
    private Context mContext;
    private List<TitlePage.ListData2Bean> mList;

    private ItemOnClickListener mItemOnClickListener;

    public void setItemOnClickListener(ItemOnClickListener pItemOnClickListener) {
        mItemOnClickListener = pItemOnClickListener;
    }


    public RvRecommendAdapter(Context pContext, List<TitlePage.ListData2Bean> pList) {
        mList = pList;
        mContext = pContext;
        mInflater = LayoutInflater.from(mContext);
    }


    @Override
    public RvRecommendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = mInflater.inflate(R.layout.rv_recommend_item_02, parent, false);

        return new RvRecommendViewHolder(mView);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final RvRecommendViewHolder holder, final int position) {

        holder.tvName.setText(mList.get(position).getName());

        Glide
                .with(mContext)
                .load(mList.get(position).getTitleImage())
                .asBitmap()
                .error(R.mipmap.error_photo)
                .into(holder.ivHead);
//        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mItemOnClickListener!=null){
//                    mItemOnClickListener.onClick( holder.tvTitle,holder.getAdapterPosition());
//                }
//            }
//        });
        holder.ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIn=new Intent(mContext, RecipesDetailsActivity.class);
                mIn.putExtra("RecipeId",mList.get(position).getId()+"");
                mContext.startActivity(mIn);
            }
        });



        holder.tvPrice.setText("￥"+mList.get(position).getPrice());

        int percentage = mList.get(position).getConstitutionPercentage();

        holder.tvConstitutionPercentage.setText(percentage+"%");

        if (percentage > 90) {
           holder.  tvConstitutionPercentage.setTextColor(mContext.getResources().getColor(R.color.percentage_color_9));
        } else if (percentage > 80 & percentage <= 90) {
          holder.   tvConstitutionPercentage.setTextColor(mContext.getResources().getColor(R.color.percentage_color_8));
        } else if (percentage > 70 & percentage <= 80) {
          holder.   tvConstitutionPercentage.setTextColor(mContext.getResources().getColor(R.color.percentage_color_7));
        } else if (percentage > 60 & percentage <= 70) {
          holder.   tvConstitutionPercentage.setTextColor(mContext.getResources().getColor(R.color.percentage_color_6));
        } else {
          holder.   tvConstitutionPercentage.setTextColor(mContext.getResources().getColor(R.color.percentage_color_5));
        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}


class RvRecommendViewHolder extends RecyclerView.ViewHolder {

    TextView tvName, tvTitle,tvPrice,tvConstitutionPercentage;
    LinearLayout mLinearLayout;
    SelectableRoundedImageView ivHead;

    RvRecommendViewHolder(View itemView) {
        super(itemView);
        ivHead = (SelectableRoundedImageView) itemView.findViewById(R.id.ivHead);
        tvName = (TextView) itemView.findViewById(R.id.tvName);
        tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
        tvConstitutionPercentage = (TextView) itemView.findViewById(R.id.tvConstitutionPercentage);
//        mLinearLayout= (LinearLayout) itemView.findViewById(R.id.mLinearLayout);
    }
}