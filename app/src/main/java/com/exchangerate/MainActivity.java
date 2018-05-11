package com.exchangerate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.recyclerview.CommenAdapter;
import com.adapter.recyclerview.base.DividerItemDecoration;
import com.adapter.recyclerview.base.ItemViewDelegate;
import com.adapter.recyclerview.base.OnItemClickListener;
import com.adapter.recyclerview.base.ViewHolder;
import com.exchangerate.view.VirtualKeyboardEditTextView;
import com.exchangerate.view.VirtualKeyboardView;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    Activity that;
    RequestCall mCall = OkHttpUtils.get().url("https://api.fixer.io/latest").build();
    @BindView(R.id.recycleView)
    RecyclerView mRecyclerView;
    List<ExchangeRateBean.Item> mList;
    CommenAdapter<ExchangeRateBean.Item> mCommenAdapter;
    ItemTouchHelper mItemTouchHelper;


    @BindView(R.id.virtualKeyboardView)
    VirtualKeyboardView mVirtualKeyboardView;

    ExchangeRateBean mExchangeRateBean;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        that = this;
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mList = new ArrayList<>();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mCommenAdapter = new CommenAdapter(that, mList).addItemViewDelegate(new ItemViewDelegate<ExchangeRateBean.Item>(R.layout.item_rate) {
            @Override
            public void convert(final ViewHolder holder, ExchangeRateBean.Item o, final int position) {
                holder.setText(R.id.key, o.getKey());
                final VirtualKeyboardEditTextView editText = (VirtualKeyboardEditTextView) holder.getView(R.id.rate);
                editText.initTextView(o.getShowValueString());
                editText.setTextChangeListener(new VirtualKeyboardEditTextView.TextChangeListener() {
                    @Override
                    public void afterTextChanged() {
                        changeAllRates(position, editText.getText());
                    }

                });
                mVirtualKeyboardView.monitorEditViewFouse(editText);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(that));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.list_divider));
        mRecyclerView.setAdapter(mCommenAdapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
            }

            @Override
            public void onLongPress(RecyclerView.ViewHolder holder, int position) {
                    mItemTouchHelper.startDrag(holder);

            }
        });
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                final int dragFlags;
                final int swipeFlags;
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    swipeFlags = 0;
                } else {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                }
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                mList.add(toPosition, mList.remove(fromPosition));
                mCommenAdapter.notifyItemMoved(fromPosition, toPosition);
                mExchangeRateBean.setListRates(mList);
                SpUtil.getInstance().saveRates(mExchangeRateBean);
                return false;
            }



            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mCommenAdapter.notifyItemRemoved(position);
                mList.remove(position);
                mExchangeRateBean.setListRates(mList);
                SpUtil.getInstance().saveRates(mExchangeRateBean);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                }
                Vibrator vibrator = (Vibrator) that.getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(70);
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundResource(0);
            }
        });
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        getData();

    }

    private void changeAllRates(int position, Editable editable) {
        float rate = 1.0f;
        try{
            rate = Float.valueOf(editable.toString().trim());
        }catch (Exception e){
            e.printStackTrace();
            rate = 0;
        }
        //无效数据
        if (rate <= 0){
            return;
        }
        float a = rate / mList.get(position).getRate();
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setShowValue(a * mList.get(i).getRate());
        }
        mCommenAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.refresh)
    public void refresh(View view) {
        refresh();
    }

    void refresh() {
        mProgressDialog.show();
        mCall.execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e, int id) {
                mProgressDialog.dismiss();
                Toast.makeText(that,"更新失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                mProgressDialog.dismiss();
                ExchangeRateBean exchangeRateBean = null;
                try {
                    exchangeRateBean = new Gson().fromJson(response, ExchangeRateBean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (exchangeRateBean != null && exchangeRateBean.getRates() != null && exchangeRateBean.getRates().size() > 0) {
                    exchangeRateBean.addBase();
                    exchangeRateBean.baseUSD();
                    exchangeRateBean.fillList(mList);
                    mCommenAdapter.notifyDataSetChanged();
                    mExchangeRateBean = exchangeRateBean;
                    SpUtil.getInstance().saveRates(mExchangeRateBean);
                } else {
                    Toast.makeText(that,"更新失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCall != null) {
            mCall.cancel();
        }
    }

    public void getData() {
        mExchangeRateBean = SpUtil.getInstance().getRates();
        if (mExchangeRateBean == null){
            refresh();
        }else {
            mExchangeRateBean.baseUSD();
            mExchangeRateBean.fillList(mList);
            mCommenAdapter.notifyDataSetChanged();
            SpUtil.getInstance().saveRates(mExchangeRateBean);
        }

    }
}
