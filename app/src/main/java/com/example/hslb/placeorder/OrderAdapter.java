package com.example.hslb.placeorder;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hslb.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;

    private boolean isLoading;
    private Activity activity;
    private List<Order> orders;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }

    private class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView order_name;
        public TextView order_status;
//        public TextView date;
        public MaterialCardView orderCard;
//        public Button btnHallTicket;

        public OrderViewHolder(View view) {
            super(view);
            order_name = (TextView) view.findViewById(R.id.orderName);
            order_status = (TextView) view.findViewById(R.id.orderStatus);
            orderCard =  view.findViewById(R.id.cardUsers);
//            date = (TextView) view.findViewById(R.id.txtLastDate);
//            btnHallTicket = (Button) view.findViewById(R.id.btnHallTicket);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public OrderAdapter(RecyclerView recyclerView, List<Order> order, Activity activity){
        this.orders = order;
        this.activity = activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return orders.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.order_list, parent, false);
            return new OrderViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OrderViewHolder) {

            Order exam = orders.get(position);
            OrderViewHolder examViewHolder = (OrderViewHolder) holder;
            examViewHolder.order_name.setText(exam.getOrder_Name());
            examViewHolder.order_status.setText(exam.getOrder_Status());
            //examViewHolder.date.setText(exam.getLastDate());

            examViewHolder.orderCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    clicked_exam_name = exam.getExamName();
//                    clicked_exam_date = exam.getExamDate();
//                    clicked_exam_eli = exam.getEligibility();
//                    clicked_exam_fee = exam.getFee();
//                    clicked_exam_last_date =  exam.getLastDate();
                    //Intent in = new Intent(activity.getApplicationContext(),ExamDetails.class);
//                    activity.startActivity(new Intent(activity,ExamDetails.class));
                    //Toast.makeText(activity.getApplicationContext(),"clicked "+exam.getExamName(),Toast.LENGTH_SHORT).show();
                }
            });


        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return orders == null ? 0 : orders.size();
    }
}
