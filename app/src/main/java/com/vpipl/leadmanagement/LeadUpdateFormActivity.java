package com.vpipl.leadmanagement;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.vpipl.leadmanagement.Adapter.Lead_Detail_List_Adapter;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import ru.nikartm.support.ImageBadgeView;

public class LeadUpdateFormActivity extends Activity {
    private String TAG = "LeadUpdateFormActivity";

    Activity act;
    ImageView img_nav_back;
    ImageBadgeView mukesh_begview;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ValueAnimator animator;

    /*Lead Details Data*/
    TextView txt_lead_id, txt_repeat_date_and_time, txt_lead_updation_submit;
    EditText edtxt_lead_title, edtxt_remarks;
    RadioGroup rg_call_type, rg_repeat_call;
    RadioButton rb_incoming, rb_outgoing, rb_repeat_call_yes, rb_repeat_call_no;
    LinearLayout ll_repeat_date_time;
    Spinner spinner_selectlead_status;
    JSONArray jsonarray_stafflist;
    List<StackHelperStaffList> stackHelperStaffLists = new ArrayList<>();

    private RadioButton rb_call_type;

    private Calendar myCalendar;
    private SimpleDateFormat sdf;
    private String whichdate = "";

    private final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (new Date().before(myCalendar.getTime())) {
                if (whichdate.equalsIgnoreCase("txt_repeat_date_and_time"))
                    txt_repeat_date_and_time.setText(sdf.format(myCalendar.getTime()));
            } else {
                AppUtils.alertDialog(LeadUpdateFormActivity.this, "Selected Date Can't be Before today");
            }
        }
    };
    private DatePickerDialog datePickerDialog;

    private void showdatePicker() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, date1, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
       // datePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        datePickerDialog.show();
    }

    private void showtimePicker() {
        final Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;

        // mTimePicker.setIs24HourView(true);

        mTimePicker = new TimePickerDialog(LeadUpdateFormActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                String am_pm = "";

                String strHrsToShow = "";
                if ((selectedHour > 12)) {
                    am_pm = "PM";
                    strHrsToShow = String.valueOf(selectedHour - 12);
                } else {
                    am_pm = "AM";
                    strHrsToShow = String.valueOf(selectedHour);
                }
                //  String strHrsToShow = (selectedHour == 0) ?"12":selectedHour+"";

                if (whichdate.equalsIgnoreCase("txt_repeat_date_and_time")) {
                    String str = "" + txt_repeat_date_and_time.getText().toString();
                    txt_repeat_date_and_time.setText(str + " " + strHrsToShow + ":" + selectedMinute + " " + am_pm);
                }

                //    txt_follow_time.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (whichdate.equalsIgnoreCase("txt_repeat_date_and_time")) {
                txt_repeat_date_and_time.setText(sdf.format(myCalendar.getTime()));
            }
            showtimePicker();
        }
    };
    String s_lead_id = "", s_lead_title = "", s_lead_status_id = "", s_lead_status_name = "", s_calltype = "", s_repeatcallsts = "", s_repeatcall_datetime = "", s_remarks = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_lead_update_form);

            act = LeadUpdateFormActivity.this;
            img_nav_back = findViewById(R.id.img_nav_back);
            mukesh_begview = findViewById(R.id.mukesh_begview);

            txt_lead_id = findViewById(R.id.txt_lead_id);
            txt_repeat_date_and_time = findViewById(R.id.txt_repeat_date_and_time);
            edtxt_lead_title = findViewById(R.id.edtxt_lead_title);
            edtxt_remarks = findViewById(R.id.edtxt_remarks);
            rg_call_type = findViewById(R.id.rg_call_type);
            rg_repeat_call = findViewById(R.id.rg_repeat_call);
            rb_incoming = findViewById(R.id.rb_incoming);
            rb_outgoing = findViewById(R.id.rb_outgoing);
            rb_repeat_call_yes = findViewById(R.id.rb_repeat_call_yes);
            rb_repeat_call_no = findViewById(R.id.rb_repeat_call_no);
            ll_repeat_date_time = findViewById(R.id.ll_repeat_date_time);
            txt_lead_updation_submit = findViewById(R.id.txt_lead_updation_submit);

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
                    //Toast.makeText(LeadUpdateFormActivity.this, AppController.getSpUserInfo().getString(SPUtils.notification_count, "0"), Toast.LENGTH_SHORT).show();
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

            txt_lead_id.setText("" + getIntent().getStringExtra("Leadid"));
            edtxt_lead_title.setText("" + getIntent().getStringExtra("lead_title"));

            /*myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd MMM yyyy");
            txt_repeat_date_and_time.setText(sdf.format(myCalendar.getTime()));

            txt_repeat_date_and_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    whichdate = "txt_repeat_date_and_time";
                    showdatePicker();
                }
            });*/
            Date date = new Date();
            String strDateFormat = "hh:mm";
            //String strDateFormat = "hh:mm:ss a";
            //  String strDateFormat = "kk:mm:ss";
            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
            String formattedDate = dateFormat.format(date);

            String am_pm = "";
            myCalendar = Calendar.getInstance();
            if (myCalendar.get(Calendar.AM_PM) == Calendar.AM)
                am_pm = "AM";
            else if (myCalendar.get(Calendar.AM_PM) == Calendar.PM)
                am_pm = "PM";

            sdf = new SimpleDateFormat("dd MMM yyyy");
            txt_repeat_date_and_time.setText(sdf.format(myCalendar.getTime()) + "  " + formattedDate + " " + am_pm);

            txt_repeat_date_and_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whichdate = "txt_repeat_date_and_time";
                    showdatePicker();
                }
            });

            rg_repeat_call.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // checkedId is the RadioButton selected
                    RadioButton rb = (RadioButton) findViewById(checkedId);
                    // textViewChoice.setText("You Selected " + rb.getText());
                    if (rb.getText().toString().equalsIgnoreCase("Yes")) {
                        ll_repeat_date_time.setVisibility(View.VISIBLE);
                    } else {
                        ll_repeat_date_time.setVisibility(View.GONE);
                    }
                }
            });

            spinner_selectlead_status = (Spinner) findViewById(R.id.spinner_selectlead_status);

            /*jsonarray_stafflist = new JSONArray("[{\"DID\":1,\"DName\":\"Sales\"},{\"DID\":2,\"DName\":\"Services\"}]");
            spinStaff();*/
            jsonarray_stafflist = new JSONArray();
            spinner_selectlead_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                    // ((TextView) arg0.getChildAt(0)).setTextSize(12);
                    /*((TextView) view).setTextColor(Color.BLACK);
                    ((TextView) view).setTextSize(10);
                    ((TextView) view).setSingleLine(true);*/
                    if (jsonarray_stafflist.length() > 0) {
                        s_lead_status_name = ((StackHelperStaffList) spinner_selectlead_status.getSelectedItem()).getStateName();
                        s_lead_status_id = ((StackHelperStaffList) spinner_selectlead_status.getSelectedItem()).getCode();

                        if (s_lead_status_id.equalsIgnoreCase("")) {
                            s_lead_status_name = "";
                        }
                    }
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            txt_lead_updation_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    ValidateData();
                }
            });

            if (AppUtils.isNetworkAvailable(act)) {
                executeLoadStaffList();
            } else {
                AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void spinStaff() {
        try {
            stackHelperStaffLists.clear();

            s_lead_status_id = "";
            s_lead_status_name = "--Select Status--";

            StackHelperStaffList stackHelper = new StackHelperStaffList();
            stackHelper.setStateName(s_lead_status_name);
            stackHelper.setCode(s_lead_status_id);
            stackHelperStaffLists.add(stackHelper);

            for (int i = 0; i < jsonarray_stafflist.length(); i++) {
                JSONObject jsonobject = jsonarray_stafflist.getJSONObject(i);
                s_lead_status_id = jsonobject.getString("StatusID");
                s_lead_status_name = jsonobject.getString("StatusName");

                StackHelperStaffList stackHelper1 = new StackHelperStaffList();
                stackHelper1.setStateName(s_lead_status_name);
                stackHelper1.setCode(s_lead_status_id);
                stackHelperStaffLists.add(stackHelper1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<StackHelperStaffList> dataAdapter = new ArrayAdapter<StackHelperStaffList>(this, R.layout.sppiner_item, stackHelperStaffLists);
        dataAdapter.setDropDownViewResource(R.layout.sppiner_item);
        spinner_selectlead_status.setAdapter(dataAdapter);
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
            AppUtils.showExceptionDialog(LeadUpdateFormActivity.this);
        }
        System.gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void ValidateData() {

        s_lead_id = txt_lead_id.getText().toString().trim();
        s_lead_title = edtxt_lead_title.getText().toString().trim();
        // s_lead_status = spinner_selectlead_status.getSelectedItem().toString().trim();

        int selectedId = rg_call_type.getCheckedRadioButtonId();
        rb_call_type = (RadioButton) findViewById(selectedId);
        s_calltype = "" + rb_call_type.getText().toString();

        int selectedId2 = rg_repeat_call.getCheckedRadioButtonId();
        rb_repeat_call_yes = (RadioButton) findViewById(selectedId2);
        s_repeatcallsts = "" + rb_repeat_call_yes.getText().toString();
        s_repeatcall_datetime = txt_repeat_date_and_time.getText().toString().trim();
        s_remarks = edtxt_remarks.getText().toString().trim();

        if (TextUtils.isEmpty(s_lead_id)) {
            Toasty.error(act, "Please Enter Valid Lead Id", Toast.LENGTH_SHORT, true).show();
        } else if (TextUtils.isEmpty(s_lead_title)) {
            Toasty.error(act, "Please Enter Lead Title", Toast.LENGTH_SHORT, true).show();
        } else if (TextUtils.isEmpty(s_lead_status_id)) {
            Toasty.error(act, "Please Select Lead Status", Toast.LENGTH_SHORT, true).show();
        } else if (TextUtils.isEmpty(s_calltype)) {
            Toasty.error(act, "Please Select Call Type", Toast.LENGTH_SHORT, true).show();
        } else if (s_repeatcallsts.equalsIgnoreCase("Yes") && TextUtils.isEmpty(s_repeatcall_datetime)) {
            Toasty.error(act, "Please Enter Repeat call Date/Time", Toast.LENGTH_SHORT, true).show();
        } else if (TextUtils.isEmpty(s_remarks)) {
            Toasty.error(act, "Please Enter Remarks", Toast.LENGTH_SHORT, true).show();
        } else {
            if (AppUtils.isNetworkAvailable(act)) {
                executeLeadUpdateFormRequest();
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void executeLeadUpdateFormRequest() {
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
                            /*LeadID:	LeadStatus:	Title:	CallType:	Repeat:	RepeatDateAndTime:	Remarks:	CallRecording:Extension:	*/
                            postParameters.add(new BasicNameValuePair("LeadID", s_lead_id));
                            postParameters.add(new BasicNameValuePair("LeadStatus", s_lead_status_id));
                            postParameters.add(new BasicNameValuePair("Title", s_lead_title));
                            postParameters.add(new BasicNameValuePair("CallType", s_calltype));
                            postParameters.add(new BasicNameValuePair("Repeat", s_repeatcallsts));
                            postParameters.add(new BasicNameValuePair("RepeatDateAndTime", s_repeatcall_datetime));
                            postParameters.add(new BasicNameValuePair("Remarks", s_remarks));
                            postParameters.add(new BasicNameValuePair("CallRecording", ""));
                            postParameters.add(new BasicNameValuePair("Extension", ""));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToLead_FillForm, TAG);
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
                                    // AppUtils.alertDialogWithFinishHome(act, jsonObject.getString("Message"));
                                    Intent intent = new Intent(act, LeadUpdateFormSuccessActivity.class);
                                    intent.putExtra("Lead_id", "" + s_lead_id);
                                    intent.putExtra("Msg", "Your Lead Status is successfully Updated !!");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    AppUtils.alertDialog(act, jsonObject.getString("Message"));
                                }
                            } else {
                                AppUtils.alertDialog(act, jsonObject.getString("Message"));
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
                    response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToLoadStatus, TAG);
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
                            spinStaff();
                        } else {
                            jsonarray_stafflist = new JSONArray("[{\"StatusID\":0,\"StatusName\":\"No Status Found\"}]");
                            spinStaff();
                        }
                    } else {
                        jsonarray_stafflist = new JSONArray("[{\"StatusID\":0,\"StatusName\":\"-- No Status Found --\"}]");
                        spinStaff();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}