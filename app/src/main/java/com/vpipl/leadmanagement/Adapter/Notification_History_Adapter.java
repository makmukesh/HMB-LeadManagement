package com.vpipl.leadmanagement.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.vpipl.leadmanagement.R;
import com.vpipl.leadmanagement.Utils.TextDrawable;

import java.util.ArrayList;
import java.util.HashMap;

public class Notification_History_Adapter extends RecyclerView.Adapter<Notification_History_Adapter.MyViewHolder> {
    public ArrayList<HashMap<String, String>> array_List;
    LayoutInflater inflater = null;
    Context context;
    private TextDrawable.IBuilder mDrawableBuilder;

    public Notification_History_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        array_List = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
        mDrawableBuilder = TextDrawable.builder().round();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_notification_history, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            holder.txt_title.setText(array_List.get(position).get("Title"));
            holder.txt_desc.setText(array_List.get(position).get("Desc"));
            holder.txt_date_time.setText(array_List.get(position).get("Date_time"));

            holder.txt_title.setSelected(true);

            holder.txt_title.setSingleLine(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return array_List.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_title,txt_desc ,txt_date_time;
        LinearLayout ll_main;

        public MyViewHolder(View view) {
            super(view);
            txt_title = (TextView) view.findViewById(R.id.txt_title);
            txt_desc = (TextView) view.findViewById(R.id.txt_desc);
            txt_date_time = (TextView) view.findViewById(R.id.txt_date_time);
            ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        }
    }
}