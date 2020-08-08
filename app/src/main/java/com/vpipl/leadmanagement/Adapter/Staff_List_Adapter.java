package com.vpipl.leadmanagement.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.leadmanagement.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Staff_List_Adapter extends RecyclerView.Adapter<Staff_List_Adapter.MyViewHolder> {
    public ArrayList<HashMap<String, String>> array_List;
    LayoutInflater inflater = null;
    Context context;
    private int selectedPosition = -1;// no selection by default

    public Staff_List_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        array_List = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.adapter_staff_list_transfer, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
           /* holder.txt_staff_member_name.setText(array_List.get(position).get("StaffName"));
           //  holder.txt_lead_first_letter.setText(array_List.get(position).get("lead_title").substring(0, 1));
            holder.txt_lead_first_letter.setText("" + position );

            holder.txt_staff_member_name.setSelected(true);
            holder.txt_staff_member_name.setSingleLine(true);*/

            holder.txt_staff_member_name.setText(array_List.get(position).get("StaffName"));

            holder.cb_staff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (selectedPosition == position) {
                        if(isChecked){
                            holder.cb_staff.setSelected(true);
                        }else {
                            holder.cb_staff.setSelected(false);
                        }
                    } else {
                        if(isChecked){
                            holder.cb_staff.setSelected(true);
                        }else {
                            holder.cb_staff.setSelected(false);
                        }
                    }
                    selectedPosition = holder.getAdapterPosition();
                }
            });

           /* holder.ll_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context , PhotoGalleryDetailsActivity.class);
                   // intent.putExtra("id" , array_List.get(position).get("id"));
                    intent.putExtra("id" , "" + position);
                    intent.putExtra("List" , array_List);
                    context.startActivity(intent);
                }
            });*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return array_List.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_lead_first_letter,txt_staff_member_name ;
        LinearLayout ll_main;
        CheckBox cb_staff ;

        public MyViewHolder(View view) {
            super(view);
            txt_lead_first_letter = (TextView) view.findViewById(R.id.txt_lead_first_letter);
            txt_staff_member_name = (TextView) view.findViewById(R.id.txt_staff_member_name);
            ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
            cb_staff = (CheckBox) view.findViewById(R.id.cb_staff);
        }
    }
}