package com.example.hslb;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hslb.placeorder.OrderAdapter;
import com.example.hslb.placeorder.Order;

import java.util.ArrayList;

public class MyAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;


    public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                OrderPlaced orderPlaced = new OrderPlaced();

                return orderPlaced;
            case 1:
                OrderFinished orderFinished = new OrderFinished();
                return orderFinished;
            case 2:
                Delivered delivered = new Delivered();
                return delivered;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}