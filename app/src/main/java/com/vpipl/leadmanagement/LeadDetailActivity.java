package com.vpipl.leadmanagement;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.leadmanagement.Adapter.Lead_Detail_List_Adapter;
import com.vpipl.leadmanagement.Adapter.Lead_List_Adapter;
import com.vpipl.leadmanagement.Adapter.Staff_List_Adapter;
import com.vpipl.leadmanagement.Model.StackHelperStaffList;
import com.vpipl.leadmanagement.Utils.AppController;
import com.vpipl.leadmanagement.Utils.AppUtils;
import com.vpipl.leadmanagement.Utils.QueryUtils;
import com.vpipl.leadmanagement.Utils.SPUtils;
import com.vpipl.leadmanagement.firbase.Config;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import ru.nikartm.support.ImageBadgeView;

public class LeadDetailActivity extends Activity {
    private String TAG = "LeadDetailActivity";

    Activity act;
    ImageView img_nav_back;
    ImageBadgeView mukesh_begview;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ValueAnimator animator;

    /*Lead Details Data*/
    TextView txt_lead_title, txt_lead_time, txt_lead_status, txt_lead_description, txt_lead_updation, txt_lead_transfer, txt_transfer_to_mobno;

    public Lead_Detail_List_Adapter adapter;
    Staff_List_Adapter adapter_staff;
    RecyclerView recyclerView;
    LinearLayout ll_data_found, ll_no_data_found;
    public static ArrayList<HashMap<String, String>> array_list = new ArrayList<>();
    public static ArrayList<HashMap<String, String>> array_list_staff = new ArrayList<>();

    Spinner spinner_select_staff_list;
    List<StackHelperStaffList> stackHelperStaffLists = new ArrayList<>();
    JSONArray jsonarray_stafflist;
    EditText edtxt_remarks;
    String s_staff_id = "", s_staff_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_lead_detail);

            act = LeadDetailActivity.this;
            img_nav_back = findViewById(R.id.img_nav_back);
            mukesh_begview = findViewById(R.id.mukesh_begview);

            txt_lead_title = findViewById(R.id.txt_lead_title);
            txt_lead_time = findViewById(R.id.txt_lead_time);
            txt_lead_status = findViewById(R.id.txt_lead_status);
            txt_lead_description = findViewById(R.id.txt_lead_description);
            txt_lead_updation = findViewById(R.id.txt_lead_updation);
            txt_lead_transfer = findViewById(R.id.txt_lead_transfer);
            txt_transfer_to_mobno = findViewById(R.id.txt_transfer_to_mobno);

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
                    //Toast.makeText(LeadDetailActivity.this, AppController.getSpUserInfo().getString(SPUtils.notification_count, "0"), Toast.LENGTH_SHORT).show();
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

            //getIntent().getStringExtra("Leadid")
            txt_lead_title.setText("" + getIntent().getStringExtra("lead_title"));
            txt_lead_time.setText("" + getIntent().getStringExtra("lead_time"));
            txt_lead_status.setText("" + getIntent().getStringExtra("lead_status"));
            txt_lead_description.setText("" + getIntent().getStringExtra("lead_desc"));

            if (!getIntent().getStringExtra("TransferedMobileNo").equalsIgnoreCase("")) {
                txt_transfer_to_mobno.setVisibility(View.VISIBLE);
                txt_transfer_to_mobno.setText("Transfer To : " + getIntent().getStringExtra("TransferedMobileNo"));
            } else {
                txt_transfer_to_mobno.setVisibility(View.GONE);
            }

            if (getIntent().getStringExtra("lead_statusID").equalsIgnoreCase("1")
                    || getIntent().getStringExtra("lead_statusID").equalsIgnoreCase("2")) {
                txt_lead_updation.setVisibility(View.VISIBLE);
                txt_lead_transfer.setVisibility(View.VISIBLE);
            } else {
                txt_lead_updation.setVisibility(View.GONE);
                txt_lead_transfer.setVisibility(View.GONE);
            }

            txt_lead_updation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(act, LeadUpdateFormActivity.class);
                    intent.putExtra("Leadid", "" + getIntent().getStringExtra("Leadid"));
                    intent.putExtra("lead_title", "" + getIntent().getStringExtra("lead_title"));
                    startActivity(intent);
                }
            });
            txt_lead_transfer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTransferDialog(act);
                }
            });

            /*Lead Follow Up listing */
            ll_data_found = findViewById(R.id.ll_data_found);
            ll_no_data_found = findViewById(R.id.ll_no_data_found);

            recyclerView = (RecyclerView) findViewById(R.id.listView);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.setItemAnimator(new DefaultItemAnimator());

            if (AppUtils.isNetworkAvailable(act)) {
                executeLoadStaffList();
            } else {
                AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
            }
            /*array_list.clear();

            for (int i = 1; i <= 10; i++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("lead_title", "I want Oppo POCO M2 SmartPhone " + i);

                if (i % 2 == 0) {
                    map.put("call_type", "Incoming");
                    map.put("lead_status", "Pending");
                    map.put("repeat_call_type", "Y");
                } else {
                    map.put("call_type", "OutGoing");
                    map.put("lead_status", "Completed");
                    map.put("repeat_call_type", "N");
                }
                map.put("repeat_call_date_time", i + "1-10-2020");
                map.put("remarks", i + "Dummy text: Its function as a filler or as a tool for comparing the visual impression of different typefaces" + i);
                map.put("followup_date_and_time", i + "-10-2020");
                array_list.add(map);
            }
            showListView();*/

            // Staff list
           /* array_list_staff.clear();

            for (int i = 1; i <= 10; i++) {
                HashMap<String, String> map = new HashMap<>();
                map.put("StaffName", i + " StaffName");
                array_list_staff.add(map);
            }
            showListView();*/


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AppUtils.dismissProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(LeadDetailActivity.this);
        }
        System.gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showTransferDialog(Context activity) {
        try {
            Dialog cust_dialog = new Dialog(activity);
            cust_dialog = new Dialog(activity, R.style.CustomDialogTheme);
            cust_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //...set cancelable false so that it's never get hidden
            cust_dialog.setCancelable(false);
            //...that's the layout i told you will inflate later
            cust_dialog.setContentView(R.layout.custom_dialog_transfer_lead);

            //...initialize the imageView form infalted layout

            spinner_select_staff_list = cust_dialog.findViewById(R.id.spinner_select_staff_list);
            edtxt_remarks = cust_dialog.findViewById(R.id.edtxt_remarks);
            TextView txt_transfer_submit = cust_dialog.findViewById(R.id.txt_transfer_submit);
            TextView txt_transfer_cancel = cust_dialog.findViewById(R.id.txt_transfer_cancel);

            if (jsonarray_stafflist.length() > 0) {
                spinStaff();
            }
            spinner_select_staff_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                    if (jsonarray_stafflist.length() > 0) {
                        s_staff_name = ((StackHelperStaffList) spinner_select_staff_list.getSelectedItem()).getStateName();
                        s_staff_id = ((StackHelperStaffList) spinner_select_staff_list.getSelectedItem()).getCode();

                        if (s_staff_id.equalsIgnoreCase("")) {
                            s_staff_name = "";
                        }
                    }
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            final Dialog finalCust_dialog = cust_dialog;
            txt_transfer_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(s_staff_id)) {
                        Toasty.error(act, "Please Select Staff Member", Toast.LENGTH_SHORT, true).show();
                    } else {
                        finalCust_dialog.dismiss();
                        executeLeadTransferToStaff(s_staff_id, edtxt_remarks.getText().toString());
                    }
                }
            });
            txt_transfer_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalCust_dialog.dismiss();
                }
            });

            Bitmap map = takeScreenShot(LeadDetailActivity.this);

            Bitmap fast = fastblur(map, 10);
            final Drawable draw = new BitmapDrawable(getResources(), fast);
            cust_dialog.getWindow().setBackgroundDrawable(draw);
            //...finaly show it
            cust_dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public Bitmap fastblur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    /*Follow Up Report*/
    private void executeFollowupHistoryRequest() {
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
                            postParameters.add(new BasicNameValuePair("LeadID", "" + getIntent().getStringExtra("Leadid")));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToConfirmOrRejectStatus, TAG);
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
                                // ll_no_data_found.setVisibility(View.GONE);
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
                /*  map.put("lead_title", "I want Oppo POCO M2 SmartPhone " + i);
                    map.put("call_type", "OutGoing");
                    map.put("lead_status", "Completed");
                    map.put("repeat_call_type", "N");
                    map.put("repeat_call_date_time", i + "1-10-2020");
                    map.put("remarks", i + "Dummy text: Its function as a filler or as a tool for comparing the visual impression of different typefaces" + i);
                    map.put("followup_date_and_time", i + "-10-2020");*/

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put("LeadID", "" + jsonObject.getString("LeadID"));
                map.put("lead_title", "" + jsonObject.getString("LeadID"));
                map.put("call_type", "" + jsonObject.getString("calltype"));
                map.put("lead_status", "" + jsonObject.getString("LeadStatus"));
                map.put("repeat_call_type", "" + jsonObject.getString("Repeatcalltype"));
                map.put("repeat_call_date_time", "" + jsonObject.getString("RepeatCallDateTIME"));
                map.put("remarks", "" + jsonObject.getString("Remarks"));
                map.put("followup_date_and_time", "" + jsonObject.getString("followup_dateandtime"));
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
                adapter = new Lead_Detail_List_Adapter(act, array_list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);

                ll_data_found.setVisibility(View.VISIBLE);
                //  ll_no_data_found.setVisibility(View.GONE);
            } else {
                ll_data_found.setVisibility(View.GONE);
                //   ll_no_data_found.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    /*staff list for transfer */
    private void executeLoadStaffList() {
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
                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToSelect_AllStaff, TAG);
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
                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                        if (jsonArrayData.length() != 0) {
                            jsonarray_stafflist = jsonArrayData;
                            //  spinStaff();
                        } else {
                            jsonarray_stafflist = new JSONArray("[{\"EMPID\":0,\"FirstName\":\"No Member Found\"}]");
                            // spinStaff();
                        }
                    } else {
                        jsonarray_stafflist = new JSONArray("[{\"EMPID\":0,\"FirstName\":\"-- No Member Found --\"}]");
                        // spinStaff();
                    }
                    executeFollowupHistoryRequest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void spinStaff() {
        try {
            stackHelperStaffLists.clear();

            s_staff_id = "";
            s_staff_name = "--Select Staff Member--";

            StackHelperStaffList stackHelper = new StackHelperStaffList();
            stackHelper.setStateName(s_staff_name);
            stackHelper.setCode(s_staff_id);
            stackHelperStaffLists.add(stackHelper);

            for (int i = 0; i < jsonarray_stafflist.length(); i++) {
                JSONObject jsonobject = jsonarray_stafflist.getJSONObject(i);
                s_staff_id = jsonobject.getString("EMPID");
                s_staff_name = jsonobject.getString("FirstName");

                StackHelperStaffList stackHelper1 = new StackHelperStaffList();
                stackHelper1.setStateName(s_staff_name);
                stackHelper1.setCode(s_staff_id);
                stackHelperStaffLists.add(stackHelper1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<StackHelperStaffList> dataAdapter = new ArrayAdapter<StackHelperStaffList>(this, R.layout.sppiner_item, stackHelperStaffLists);
        dataAdapter.setDropDownViewResource(R.layout.sppiner_item);
        spinner_select_staff_list.setAdapter(dataAdapter);
    }

    /*Lead Transfer to another staff member*/
    private void executeLeadTransferToStaff(final String staffid, final String remarks) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                AppUtils.showProgressDialog(act);
            }

            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("LeadID", "" + getIntent().getStringExtra("Leadid")));
                    postParameters.add(new BasicNameValuePair("StaffID", "" + staffid));
                    postParameters.add(new BasicNameValuePair("TransferRemarks", "" + remarks));
                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToStaff_TransferLead, TAG);
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

                    if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                        JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                        if (jsonArrayData.length() != 0) {
                            AppUtils.alertDialogWithFinishHome(act, jsonObject.getString("Message"));
                        } else {
                            AppUtils.alertDialog(act, jsonObject.getString("Message"));
                        }
                    } else {
                        AppUtils.alertDialog(act, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}