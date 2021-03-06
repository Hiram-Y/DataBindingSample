package com.example.zhanghongqiang.databindingsample.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhanghongqiang.databindingsample.R;
import com.example.zhanghongqiang.databindingsample.api.API;
import com.example.zhanghongqiang.databindingsample.databinding.ActivityMainBinding;
import com.example.zhanghongqiang.databindingsample.databinding.ItemMovieBinding;
import com.example.zhanghongqiang.databindingsample.databinding.ViewEmptyBinding;
import com.example.zhanghongqiang.databindingsample.model.HttpResult;
import com.example.zhanghongqiang.databindingsample.model.Movie;
import com.example.zhanghongqiang.databindingsample.presenter.XRecyclerViewPresenter;
import com.example.zhanghongqiang.databindingsample.presenter.XRecyclerviewContract;
import com.example.zhanghongqiang.databindingsample.subscribers.OnNextOnErrorNoMatch;

import java.util.List;

/**
 * Created by zhanghongqiang on 16/4/5  下午2:35
 * ToDo:
 */
public class MainActivity extends BaseActivity implements XRecyclerviewContract.IFListview<Movie> {

    ActivityMainBinding binding;
    //布局填充器
    LayoutInflater inflater;
    //列表代理
    XRecyclerViewPresenter recyclerViewPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        inflater = getLayoutInflater();

        recyclerViewPresenter = XRecyclerViewPresenter.with(this)
                .recyclerView(binding.XRecyclerViewLayout)
                .emptyTip("no  data")
                .build();
        //空布局
        initEmptyView();
        //加载数据
        loadData();
    }

    private void initEmptyView() {
        //获取空布局
        ViewEmptyBinding emptyBinding = recyclerViewPresenter.getEmptyBinding();
        //空布局的实现
        if (emptyBinding != null) {
            emptyBinding.button.setVisibility(View.VISIBLE);
            emptyBinding.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewPresenter.reLoadData();
                }
            });
        }
    }

    @Override
    public int getViewType(int position) {
        return 0;
    }

    @Override
    public void loadData() {
        subscriptionArrayList.add(API.toSubscribe(this, API.getInstance().getRest().getTopMovie(recyclerViewPresenter.nextPage(), recyclerViewPresenter.getPageSize()), new OnNextOnErrorNoMatch<HttpResult<List<Movie>>>() {


            @Override
            public void onNext(HttpResult<List<Movie>> listHttpResult) {
                recyclerViewPresenter.success(listHttpResult.getSubjects());
            }

            @Override
            public void notMatch(HttpResult<List<Movie>> listHttpResult) {
                Log.i("123","result not match");

            }


            @Override
            public void onError(Throwable e) {
                recyclerViewPresenter.refreshComplete();
            }
        }));
    }

    @Override
    public void updateView(@NonNull Movie data, @NonNull ViewDataBinding binding, int position) {
        ItemMovieBinding b = (ItemMovieBinding) binding;
        b.setMovie(data);

    }

    @Override
    public ViewDataBinding createView(ViewGroup parent, int viewType) {
        return DataBindingUtil.inflate(inflater, R.layout.item_movie, parent, false);
    }
}
