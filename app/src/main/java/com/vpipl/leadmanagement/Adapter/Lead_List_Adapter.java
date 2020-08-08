package com.vpipl.leadmanagement.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.vpipl.leadmanagement.LeadDetailActivity;
import com.vpipl.leadmanagement.R;
import com.vpipl.leadmanagement.Utils.TextDrawable;

import java.util.ArrayList;
import java.util.HashMap;

public class Lead_List_Adapter extends RecyclerView.Adapter<Lead_List_Adapter.MyViewHolder> {
    public ArrayList<HashMap<String, String>> array_List;
    LayoutInflater inflater = null;
    Context context;
    private TextDrawable.IBuilder mDrawableBuilder;

    public Lead_List_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        array_List = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
        mDrawableBuilder = TextDrawable.builder().round();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_lead_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            holder.txt_lead_title.setText(array_List.get(position).get("lead_title"));
            holder.txt_cust_name.setText(array_List.get(position).get("cust_name"));
            holder.txt_lead_time.setText(array_List.get(position).get("lead_time"));
            holder.txt_cust_mobileno.setText(array_List.get(position).get("cust_mobileno"));
            holder.txt_lead_status.setText(array_List.get(position).get("lead_status"));

            holder.ll_transfer_name.setVisibility(View.GONE);
            /*if(array_List.get(position).get("isTransfer").equalsIgnoreCase("Yes")){
                holder.ll_transfer_name.setVisibility(View.VISIBLE);
                holder.txt_lead_status.setText(array_List.get(position).get("TransferRemark"));
            }
            else {
                holder.ll_transfer_name.setVisibility(View.GONE);
                holder.txt_lead_status.setText(array_List.get(position).get("TransferRemark"));
            }*/

            holder.txt_lead_first_letter.setText(array_List.get(position).get("lead_title").substring(0, 1));

            holder.txt_lead_title.setSelected(true);
            holder.txt_cust_name.setSelected(true);
            holder.txt_lead_time.setSelected(true);
            holder.txt_cust_mobileno.setSelected(true);
            holder.txt_lead_status.setSelected(true);

            holder.txt_lead_title.setSingleLine(true);
            holder.txt_cust_name.setSingleLine(true);
            holder.txt_lead_time.setSingleLine(true);
            holder.txt_cust_mobileno.setSingleLine(true);
            holder.txt_lead_status.setSingleLine(true);

            holder.ll_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context , LeadDetailActivity.class);
                    intent.putExtra("Leadid" , "" + array_List.get(position).get("Leadid"));
                    intent.putExtra("lead_title" , "" + array_List.get(position).get("lead_title"));
                    intent.putExtra("lead_desc" , "" + array_List.get(position).get("lead_desc"));
                    intent.putExtra("lead_time" , "" + array_List.get(position).get("lead_time"));
                    intent.putExtra("lead_status" , "" + array_List.get(position).get("lead_status"));
                    intent.putExtra("lead_statusID" , "" + array_List.get(position).get("lead_statusID"));
                    intent.putExtra("TransferedMobileNo" , "" + array_List.get(position).get("TransferedMobileNo"));
                    intent.putExtra("TransferedStaffName" , "" + array_List.get(position).get("TransferedStaffName"));
                    context.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return array_List.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_lead_first_letter,txt_lead_title ,txt_cust_name, txt_lead_time,txt_cust_mobileno,txt_lead_status,txt_transfer_name,txt_transfer_remarks;
        ImageView iv_lead_photo;
        LinearLayout ll_main ,ll_transfer_name;
        LottieAnimationView lottie_icon_mobile ;
        TextDrawable drawable123;

        public MyViewHolder(View view) {
            super(view);
            txt_lead_first_letter = (TextView) view.findViewById(R.id.txt_lead_first_letter);
            iv_lead_photo = (ImageView) view.findViewById(R.id.iv_lead_photo);
            lottie_icon_mobile = (LottieAnimationView) view.findViewById(R.id.lottie_icon_mobile);
            txt_lead_title = (TextView) view.findViewById(R.id.txt_lead_title);
            txt_cust_name = (TextView) view.findViewById(R.id.txt_cust_name);
            txt_lead_time = (TextView) view.findViewById(R.id.txt_lead_time);
            txt_cust_mobileno = (TextView) view.findViewById(R.id.txt_cust_mobileno);
            txt_lead_status = (TextView) view.findViewById(R.id.txt_lead_status);
            ll_transfer_name = (LinearLayout) view.findViewById(R.id.ll_transfer_name);
            ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
            txt_transfer_name = (TextView) view.findViewById(R.id.txt_transfer_name);
            txt_transfer_remarks = (TextView) view.findViewById(R.id.txt_transfer_remarks);
        }
    }
}