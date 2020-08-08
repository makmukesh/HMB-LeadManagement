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

public class Lead_Detail_List_Adapter extends RecyclerView.Adapter<Lead_Detail_List_Adapter.MyViewHolder> {
    public ArrayList<HashMap<String, String>> array_List;
    LayoutInflater inflater = null;
    Context context;
    private TextDrawable.IBuilder mDrawableBuilder;

    public Lead_Detail_List_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        array_List = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
        mDrawableBuilder = TextDrawable.builder().round();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_lead_detail_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            holder.txt_lead_title.setText(array_List.get(position).get("lead_title"));

            if (!array_List.get(position).get("call_type").equalsIgnoreCase("")) {
                holder.txt_call_type.setVisibility(View.VISIBLE);
                holder.txt_call_type.setText(array_List.get(position).get("call_type"));
            } else {
                holder.txt_call_type.setVisibility(View.GONE);
            }
            if (!array_List.get(position).get("lead_status").equalsIgnoreCase("")) {
                holder.txt_lead_status.setVisibility(View.VISIBLE);
                holder.txt_lead_status.setText(array_List.get(position).get("lead_status"));
            } else {
                holder.txt_lead_status.setVisibility(View.GONE);
            }

            if (!array_List.get(position).get("remarks").equalsIgnoreCase("")) {
                holder.txt_remarks.setVisibility(View.VISIBLE);
                holder.txt_remarks.setText(array_List.get(position).get("remarks"));
            } else {
                holder.txt_remarks.setVisibility(View.GONE);
            }
            if (!array_List.get(position).get("followup_date_and_time").equalsIgnoreCase("")) {
                holder.txt_followup_date_and_time.setVisibility(View.VISIBLE);
                holder.txt_followup_date_and_time.setText(array_List.get(position).get("followup_date_and_time"));
            } else {
                holder.txt_followup_date_and_time.setVisibility(View.GONE);
            }

            if (array_List.get(position).get("repeat_call_type").equalsIgnoreCase("Yes")) {
                if (!array_List.get(position).get("repeat_call_date_time").equalsIgnoreCase("")) {
                    holder.txt_repeat_call_date_time.setVisibility(View.VISIBLE);
                    holder.txt_repeat_call_date_time.setText("Repeat Call Date/Time : " + array_List.get(position).get("repeat_call_date_time"));
                } else {
                    holder.txt_repeat_call_date_time.setVisibility(View.GONE);
                }
            } else {
                holder.txt_repeat_call_date_time.setVisibility(View.GONE);
            }

            holder.txt_lead_title.setSelected(true);
            holder.txt_call_type.setSelected(true);
            holder.txt_lead_status.setSelected(true);
            holder.txt_repeat_call_date_time.setSelected(true);
            holder.txt_followup_date_and_time.setSelected(true);

            holder.txt_lead_title.setSingleLine(true);
            holder.txt_call_type.setSingleLine(true);
            holder.txt_lead_status.setSingleLine(true);
            holder.txt_repeat_call_date_time.setSingleLine(true);
            holder.txt_followup_date_and_time.setSingleLine(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return array_List.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_lead_title, txt_call_type, txt_lead_status, txt_repeat_call_date_time, txt_remarks, txt_followup_date_and_time;
        LinearLayout ll_main;

        public MyViewHolder(View view) {
            super(view);
            txt_lead_title = (TextView) view.findViewById(R.id.txt_lead_title);
            txt_call_type = (TextView) view.findViewById(R.id.txt_call_type);
            txt_lead_status = (TextView) view.findViewById(R.id.txt_lead_status);
            txt_repeat_call_date_time = (TextView) view.findViewById(R.id.txt_repeat_call_date_time);
            txt_remarks = (TextView) view.findViewById(R.id.txt_remarks);
            txt_followup_date_and_time = (TextView) view.findViewById(R.id.txt_followup_date_and_time);
            ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        }
    }
}