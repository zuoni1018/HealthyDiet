package com.huihong.healthydiet.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.gson.Gson;
import com.huihong.healthydiet.AppUrl;
import com.huihong.healthydiet.MyApplication;
import com.huihong.healthydiet.R;
import com.huihong.healthydiet.adapter.LvPopTypeAdapter;
import com.huihong.healthydiet.adapter.RvRecommendNearbyAdapter;
import com.huihong.healthydiet.mInterface.HttpUtilsListener;
import com.huihong.healthydiet.model.gsonbean.DataDictionary;
import com.huihong.healthydiet.model.gsonbean.RestaurantList;
import com.huihong.healthydiet.model.httpmodel.RestaurantInfo;
import com.huihong.healthydiet.model.httpmodel.ScreenType;
import com.huihong.healthydiet.utils.common.LogUtil;
import com.huihong.healthydiet.utils.current.HttpUtils;
import com.huihong.healthydiet.utils.current.ListPopupUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * Created by zangyi_shuai_ge on 2017/7/12
 * 附近餐厅列表界面
 */

public class RecommendNearbyListFragment extends Fragment {
    @BindView(R.id.tvType01)
    TextView tvType01;
    @BindView(R.id.tvType02)
    TextView tvType02;
    @BindView(R.id.tvType03)
    TextView tvType03;
    @BindView(R.id.tvType04)
    TextView tvType04;
    @BindView(R.id.ivType)
    ImageView ivType;
    @BindView(R.id.layoutType04)
    LinearLayout layoutType04;
    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    LRecyclerView recyclerView;
    private View mView;
    //GroupBy 类型
    private final String SCREEN_TYPE_DISTANCE = "Distance";
    private final String SCREEN_TYPE_SALES_VOLUME = "SalesVolume";
    private final String SCREEN_TYPE_SUIT_ME = "SuitMe";
    private final String SCREEN_TYPE_TYPE = "Type";
    private final String SCREEN_TYPE_NONE = "";
    //当前GroupBy 类型
    private String GroupBy = "";
    private String TypeValue = "";
    private ListPopupWindow mListPopupWindow;//类型列表

    private List<ScreenType> mScreenTypeList;


    //列表加载页数
    private int num = 1;
    private boolean isOpen = false;
    private ImageView ivTest;
    private View mButtonView;
    private LRecyclerView.LScrollListener mLScrollListener;


    private ProgressDialog progressDialog;

    public void setLScrollListener(LRecyclerView.LScrollListener mLScrollListener) {
        this.mLScrollListener = mLScrollListener;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScreenTypeList = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在获取数据...");
        GroupBy = SCREEN_TYPE_NONE;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_recommend_nearby_list, null);
            unbinder = ButterKnife.bind(this, mView);
            initUI();
            getScreenInfo(false);//获取筛选列表
        }


        return mView;
    }

    //获取筛选列表
    private void getScreenInfo(final boolean isReGet) {


        Map<String, String> map = new HashMap<>();
        map.put("Type_Like", "");

        HttpUtils.sendHttpAddToken(getActivity(), AppUrl.DATA_DICTIONARY
                , map
                , new HttpUtilsListener() {
                    @Override
                    public void onResponse(String response, int id) {
                        LogUtil.i("获取餐厅类型", response);
                        LogUtil.i("接口，获取餐厅类型:", response);
                        Gson gson = new Gson();
                        DataDictionary DataDictionary = gson.fromJson(response, DataDictionary.class);
                        if (DataDictionary.getHttpCode() == 200) {
                            mScreenTypeList.clear();
                            mScreenTypeList.addAll(DataDictionary.getListData());
                            if (isReGet) {

                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.i("获取餐厅类型", e.toString());
                    }
                });


    }

    private void initUI() {
        initRecyclerView();
    }


    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private List<RestaurantInfo> recommendList;

    private void initRecyclerView() {
        recommendList = new ArrayList<>();
        RvRecommendNearbyAdapter mRvRecommendAdapter = new RvRecommendNearbyAdapter(getActivity(), recommendList);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mRvRecommendAdapter);
        recyclerView.setAdapter(mLRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                num = 1;
                getInfo(num,true);
            }
        });

        //加载更多
        recyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getInfo(num,false);
            }
        });
        recyclerView.refresh();
        if (mLScrollListener != null) {
            recyclerView.setLScrollListener(mLScrollListener);
        }
    }

    private void showListPopup(LinearLayout mTextView) {

        if (mScreenTypeList.size() > 0) {

            if (mListPopupWindow == null) {
                LvPopTypeAdapter mAdapter = new LvPopTypeAdapter(getActivity(), mScreenTypeList);
                mListPopupWindow = ListPopupUtil.showListPopup(getActivity(), mTextView, mAdapter, R.drawable.bg_03, 0, 2, 100, 0);
                mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        tvType04.setText(mScreenTypeList.get(position).getTypeValue());
                        GroupBy = SCREEN_TYPE_TYPE;
                        tvType04.setTextColor(getResources().getColor(R.color.recommend_type_text_select));
                        TypeValue = mScreenTypeList.get(position).getId() + "";
                        mListPopupWindow.dismiss();
//                        getInfo(num,true);
                        recyclerView.refresh();
                    }
                });

                mListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ivType.setImageResource(R.mipmap.up);
                        layoutType04.setClickable(false);
                        layoutType04.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                              layoutType04.setClickable(true);
                            }
                        },50);
                    }
                });
                mListPopupWindow.setForceIgnoreOutsideTouch(false);
//                mListPopupWindow.setModal(false);
//                mListPopupWindow.ou
            }
            ivType.setImageResource(R.mipmap.down);

        }

    }

    //获取餐厅列表信息
    private void getInfo(int num, final boolean needClear) {
//        progressDialog.show();
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
                        RestaurantList mRestaurantList = gson.fromJson(response, RestaurantList.class);
                        int code = mRestaurantList.getHttpCode();
                        if (code == 200) {
                            //是否需要清除之前的数据
                            if (needClear) {
                                recommendList.clear();
                            }
                            RecommendNearbyListFragment.this.num++;
                            recommendList.addAll(mRestaurantList.getListData());
                            mLRecyclerViewAdapter.notifyDataSetChanged();
                        } else if (code == 300) {
                            Toast.makeText(getActivity(), R.string.no_more_date, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tvType01, R.id.tvType02, R.id.tvType03, R.id.layoutType04})
    public void onViewClicked(View view) {
        //点击4个筛选框将重新刷新这个界面所以先要把集合清空
        num = 1;
        //重置字的颜色
        tvType01.setTextColor(getResources().getColor(R.color.recommend_type_text_normal));
        tvType02.setTextColor(getResources().getColor(R.color.recommend_type_text_normal));
        tvType03.setTextColor(getResources().getColor(R.color.recommend_type_text_normal));
        tvType04.setTextColor(getResources().getColor(R.color.recommend_type_text_normal));

        GroupBy = SCREEN_TYPE_NONE;

        //第4个
        TypeValue = "";
//        tvType04.setText("类型");
        ivType.setImageResource(R.mipmap.up);

        switch (view.getId()) {
            case R.id.tvType01:
                GroupBy = SCREEN_TYPE_SUIT_ME;
                tvType01.setTextColor(getResources().getColor(R.color.recommend_type_text_select));
//                getInfo(num,true);
                recyclerView.refresh();
                break;
            case R.id.tvType02:
                GroupBy = SCREEN_TYPE_SALES_VOLUME;
                tvType02.setTextColor(getResources().getColor(R.color.recommend_type_text_select));
//                getInfo(num,true);
                recyclerView.refresh();
                break;
            case R.id.tvType03:
                GroupBy = SCREEN_TYPE_DISTANCE;
//                tvType03.setTextColor(getResources().getColor(R.color.recommend_type_text_select));
                recyclerView.refresh();
                getInfo(num,true);
                break;
            case R.id.layoutType04:

                //点的时候初始化
                showListPopup(layoutType04);
                if (mListPopupWindow != null && !mListPopupWindow.isShowing()) {
                    mListPopupWindow.show();
                }
                break;
        }
    }
}
