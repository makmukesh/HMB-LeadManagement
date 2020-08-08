package com.vpipl.leadmanagement;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.leadmanagement.Adapter.Lead_List_Adapter;
import com.vpipl.leadmanagement.Adapter.Notification_History_Adapter;
import com.vpipl.leadmanagement.Utils.AppController;
import com.vpipl.leadmanagement.Utils.AppUtils;
import com.vpipl.leadmanagement.Utils.QueryUtils;
import com.vpipl.leadmanagement.Utils.SPUtils;
import com.vpipl.leadmanagement.firbase.Config;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.nikartm.support.ImageBadgeView;

public class LeadListActivity extends Activity {
    private String TAG = "LeadListActivity";

    Activity act;
    ImageView img_nav_back;
    ImageBadgeView mukesh_begview;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ValueAnimator animator;

    public Lead_List_Adapter adapter;
    RecyclerView recyclerView;
    LinearLayout ll_data_found, ll_no_data_found;
    public static ArrayList<HashMap<String, String>> array_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_lead_list);

            act = LeadListActivity.this;
            img_nav_back = findViewById(R.id.img_nav_back);
            mukesh_begview = findViewById(R.id.mukesh_begview);

            img_nav_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) > 0) {
                animator = ValueAnimator.ofFloat(0f, 1f);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mukesh_begview.setAlpha((Float) animation.getAnimatedValue());
                    }
                });
                animator.setDuration(500);
                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.setRepeatCount(-1);
                animator.start();
            }

            mukesh_begview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppController.getSpUserInfo().edit().putString(SPUtils.notification_count, "0").commit();
                    //Toast.makeText(LeadListActivity.this, AppController.getSpUserInfo().getString(SPUtils.notification_count, "0"), Toast.LENGTH_SHORT).show();
                    // startActivity(new Intent(MainActivity.this, AddCartCheckOut_Activity.class));
                }
            });
            mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));

            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e("count", AppController.getNotification_count().getString(SPUtils.notification_count, "0"));
                    mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));
                    if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) > 0) {
                        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                mukesh_begview.setAlpha((Float) animation.getAnimatedValue());
                            }
                        });
                        animator.setDuration(500);
                        animator.setRepeatMode(ValueAnimator.REVERSE);
                        animator.setRepeatCount(-1);
                        animator.start();
                    }
                }
            };

            /*Home Today Leads */
            ll_data_found = findViewById(R.id.ll_data_found);
            ll_no_data_found = findViewById(R.id.ll_no_data_found);

            recyclerView = (RecyclerView) findViewById(R.id.listView);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.setItemAnimator(new DefaultItemAnimator());

            if (AppUtils.isNetworkAvailable(act)) {
                String Str_lead_title = "";
                Str_lead_title = getIntent().getStringExtra("Keyword");
                if (Str_lead_title.equalsIgnoreCase("")) {
                    executeLeadHistoryRequest();
                } else {
                    executeLeadFilterRequest("", "", Str_lead_title);
                }
            } else {
                AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));
        super.onRestart();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    private void executeLeadHistoryRequest() {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("StaffID", "" + AppController.getSpUserInfo().getString(SPUtils.Member_ID, "")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToShowLeadAndDetails, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);
                            array_list.clear();
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                ll_data_found.setVisibility(View.VISIBLE);
                                ll_no_data_found.setVisibility(View.GONE);
                                if (jsonObject.getJSONArray("Data").length() > 0) {
                                    getAllActivityListResult(jsonObject.getJSONArray("Data"));
                                }
                            } else {
                                showListView();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void getAllActivityListResult(JSONArray jsonArray) {
        try {
            array_list.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("Leadid", "" + jsonObject.getString("LeadID"));
                map.put("lead_title", "" + jsonObject.getString("LeadTitle"));
                map.put("lead_desc", "" + jsonObject.getString("LeadDescription"));
                map.put("cust_name", "" + jsonObject.getString("CustomerName"));
                map.put("lead_time", "" + jsonObject.getString("RTS"));
                map.put("cust_mobileno", "" + jsonObject.getString("Mobile"));
                map.put("lead_status", "" + jsonObject.getString("LeadStatus"));
                map.put("lead_statusID", "" + jsonObject.getString("LeadStatusID"));
                map.put("StaffID", "" + AppController.getSpUserInfo().getString(SPUtils.Member_ID, ""));
                map.put("StaffName", "" + AppController.getSpUserInfo().getString(SPUtils.MemberName, ""));
                map.put("isTransfer", "" + jsonObject.getString("isTransfer"));
                map.put("TransferredID", "" + jsonObject.getString("TransferredID"));
                map.put("TransferRemark", "" + jsonObject.getString("TransferRemark"));
                map.put("TransferedLeadID", "" + jsonObject.getString("TransferedLeadID"));
                map.put("TransferedMobileNo", "" + jsonObject.getString("MobileNo")); //MobileNo":"7894561235","StaffName
                map.put("TransferedStaffName", "" + jsonObject.getString("StaffName"));
                array_list.add(map);
            }

            showListView();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void getAllActivityListResultFilter(JSONArray jsonArray) {
        try {
            array_list.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("Leadid", "" + jsonObject.getString("LeadID"));
                map.put("lead_title", "" + jsonObject.getString("LeadTitle"));
                map.put("lead_desc", "" + jsonObject.getString("LeadDescription"));
                map.put("cust_name", "" + jsonObject.getString("CustomerName"));
                map.put("lead_time", "" + jsonObject.getString("RTS"));
                map.put("cust_mobileno", "" + jsonObject.getString("Mobile"));
                map.put("lead_status", "" + jsonObject.getString("LeadStatusName"));
                map.put("lead_statusID", "" + jsonObject.getString("LeadStatus"));
                map.put("StaffID", "" + AppController.getSpUserInfo().getString(SPUtils.Member_ID, ""));
                map.put("StaffName", "" + AppController.getSpUserInfo().getString(SPUtils.MemberName, ""));
                map.put("TransferredID", "" + jsonObject.getString("TransferredID"));
                map.put("TransferRemark", "" + jsonObject.getString("TransferRemark"));
                map.put("TransferedLeadID", "" + jsonObject.getString("TransferedLeadID"));
                map.put("TransferedMobileNo", "" + jsonObject.getString("MobileNo")); //MobileNo":"7894561235","StaffName
                map.put("TransferedStaffName", "" + jsonObject.getString("StaffName"));

                array_list.add(map);
            }

            showListView();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private void showListView() {
        try {
            if (array_list.size() > 0) {
                adapter = new Lead_List_Adapter(act, array_list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);

                ll_data_found.setVisibility(View.VISIBLE);
                ll_no_data_found.setVisibility(View.GONE);
            } else {
                ll_data_found.setVisibility(View.GONE);
                ll_no_data_found.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(LeadListActivity.this);
        }
        System.gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeLeadFilterRequest(final String FromDate, final String ToDate, final String LeadTitle) {
        try {
            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            /*FromDate:ToDate:StaffID:LeadTitle:*/
                            postParameters.add(new BasicNameValuePair("AdminID", "1"));
                            postParameters.add(new BasicNameValuePair("FromDate", "" + FromDate));
                            postParameters.add(new BasicNameValuePair("ToDate", "" + ToDate));
                            postParameters.add(new BasicNameValuePair("StaffID", "" + AppController.getSpUserInfo().getString(SPUtils.Member_ID, "")));
                            postParameters.add(new BasicNameValuePair("LeadTitle", "" + LeadTitle));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToAdmin_LeadFilteration, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);
                            array_list.clear();
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                ll_data_found.setVisibility(View.VISIBLE);
                                ll_no_data_found.setVisibility(View.GONE);
                                if (jsonObject.getJSONArray("Data").length() > 0) {
                                    getAllActivityListResultFilter(jsonObject.getJSONArray("Data"));
                                }
                            } else {
                                showListView();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(act);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

}