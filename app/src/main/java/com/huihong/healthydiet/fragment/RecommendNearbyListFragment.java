package com.huihong.healthydiet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.gson.Gson;
import com.huihong.healthydiet.AppUrl;
import com.huihong.healthydiet.MyApplication;
import com.huihong.healthydiet.R;
import com.huihong.healthydiet.activity.RecommendActivity;
import com.huihong.healthydiet.adapter.RvRecommendNearbyAdapter;
import com.huihong.healthydiet.mInterface.HttpUtilsListener;
import com.huihong.healthydiet.mInterface.ScreenTypeListener;
import com.huihong.healthydiet.model.gsonbean.RestaurantList;
import com.huihong.healthydiet.model.httpmodel.RestaurantInfo;
import com.huihong.healthydiet.utils.common.LogUtil;
import com.huihong.healthydiet.utils.current.HttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by zangyi_shuai_ge on 2017/7/12
 * 附近餐厅列表界面
 */

public class RecommendNearbyListFragment extends Fragment {
    private View mView;
    //列表加载页数
    private int num = 1;
    private boolean isOpen = false;
    private ImageView ivTest;
    private View mButtonView;
    private LRecyclerView.LScrollListener mLScrollListener;

    public void setLScrollListener(LRecyclerView.LScrollListener mLScrollListener) {
        this.mLScrollListener = mLScrollListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_recommend_nearby_list, null);
            initUI();
        }
        return mView;
    }

    private void initUI() {
        initRecyclerView();
        //所在activity对期发起请求
        //只有在
        RecommendActivity.mRecommendActivity.setLeftScreenTypeListener(new ScreenTypeListener() {
            @Override
            public void screenType(boolean isRight, String type, int typeId, boolean isSwitch) {
                if (!isRight) {
                    LogUtil.i("附近餐厅","type="+type+"typeId"+"isSwitch"+isSwitch);

                    if (!isSwitch) {
                        num = 1;
                        recommendList.clear();
                        mLRecyclerViewAdapter.notifyDataSetChanged();
                        GroupBy = type;
                        TypeValue = typeId + "";
                        getInfo(num);
                    }
                }
            }
        });

    }

    //列表
    private LRecyclerView recyclerView;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private RvRecommendNearbyAdapter mRvRecommendAdapter;
    private List<RestaurantInfo> recommendList;

    private void initRecyclerView() {
        recommendList = new ArrayList<>();
        recyclerView = (LRecyclerView) mView.findViewById(R.id.recyclerView);
        mRvRecommendAdapter = new RvRecommendNearbyAdapter(getActivity(), recommendList);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mRvRecommendAdapter);
        recyclerView.setAdapter(mLRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                num = 1;
                recommendList.clear();
                mLRecyclerViewAdapter.notifyDataSetChanged();
                getInfo(num);
            }
        });

        //加载更多
        recyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getInfo(num);
            }
        });
        recyclerView.refresh();
        if (mLScrollListener != null) {
            recyclerView.setLScrollListener(mLScrollListener);
        }
    }

    private String GroupBy = "";
    private String TypeValue = "";

    //获取餐厅列表信息
    private void getInfo(int num) {

        Map<String, String> map = new HashMap<>();
        map.put("CoordY", MyApplication.Latitude + "");
        map.put("CoordX", MyApplication.Longitude + "");
        map.put("GroupBy", GroupBy);
        map.put("PageNo", num + "");
        map.put("TypeValue", TypeValue);

        HttpUtils.sendHttpAddToken(getActivity(), AppUrl.GET_RESTAURANT_LIST_INFO
                , map
                , new HttpUtilsListener() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        recyclerView.refreshComplete(1);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("接口，餐厅列表:", response);
                        recyclerView.refreshComplete(1);
                        Gson gson = new Gson();
                        RestaurantList RestaurantList = gson.fromJson(response, RestaurantList.class);
                        int code = RestaurantList.getHttpCode();
                        if (code == 200) {
                            RecommendNearbyListFragment.this.num++;
                            List<RestaurantInfo> mListData = RestaurantList.getListData();//拿到餐厅列表
                            recommendList.addAll(mListData);
                            mLRecyclerViewAdapter.notifyDataSetChanged();
                        } else if (code == 300) {
                            Toast.makeText(getActivity(), R.string.no_more_date, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
