package com.vpipl.leadmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vpipl.leadmanagement.Adapter.Home_Lead_List_Adapter;
import com.vpipl.leadmanagement.Utils.AppController;
import com.vpipl.leadmanagement.Utils.AppUtils;
import com.vpipl.leadmanagement.Utils.CircularImageView;
import com.vpipl.leadmanagement.Utils.QueryUtils;
import com.vpipl.leadmanagement.Utils.SPUtils;
import com.vpipl.leadmanagement.Utils.Utility;
import com.vpipl.leadmanagement.firbase.Config;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ru.nikartm.support.ImageBadgeView;

public class MainActivity extends Activity {
    private String TAG = "MainActivity";

    Activity act;
    ImageBadgeView mukesh_begview;
    ImageView img_nav_back;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ValueAnimator animator;

    TextView home_staff_name, nav_username, nav_mobileno;
    CircularImageView nav_user_profile;

    public Home_Lead_List_Adapter adapter;
    RecyclerView recyclerView;
    LinearLayout ll_data_found, ll_no_data_found;
    public static ArrayList<HashMap<String, String>> array_list = new ArrayList<>();

    /*Menu Items*/
    DrawerLayout drawer;
    LinearLayout nav_home, nav_share, nav_logout, nav_lead_history, nav_rate_the_app, nav_privacy_policy;
    public static NavigationView navigationView;

    /*Profile Image Uplaod*/
    private static final int REQUEST_CODE = 1234;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;
    ImageView user_profile;

    EditText et_search;
    TextView speak;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_main);

            act = MainActivity.this;

            img_nav_back = findViewById(R.id.img_nav_back);
            mukesh_begview = findViewById(R.id.mukesh_begview);
            home_staff_name = findViewById(R.id.home_staff_name);
            et_search = findViewById(R.id.et_search);
            user_profile = findViewById(R.id.user_profile);

            animator = ValueAnimator.ofFloat(0f, 1f);
            if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) > 0) {
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
                    startActivity(new Intent(MainActivity.this, NotificationHistoryActivity.class));
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
            /*Menu data*/
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

            navigationView = findViewById(R.id.nav_view);
            View headerview = navigationView.getHeaderView(0);

            nav_username = headerview.findViewById(R.id.nav_username);
            nav_mobileno = headerview.findViewById(R.id.nav_mobileno);
            nav_user_profile = headerview.findViewById(R.id.nav_user_profile);
            nav_home = headerview.findViewById(R.id.nav_home);
            nav_lead_history = headerview.findViewById(R.id.nav_lead_history);
            nav_rate_the_app = headerview.findViewById(R.id.nav_rate_the_app);
            nav_privacy_policy = headerview.findViewById(R.id.nav_privacy_policy);
            nav_share = headerview.findViewById(R.id.nav_share);
            nav_logout = headerview.findViewById(R.id.nav_logout);

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(@NonNull View drawerView) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.ic_back));
                }

                @Override
                public void onDrawerClosed(@NonNull View drawerView) {
                    img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu));
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
            img_nav_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawer.isDrawerOpen(navigationView)) {
                        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu));
                        drawer.closeDrawer(navigationView);
                    } else {
                        img_nav_back.setImageDrawable(getResources().getDrawable(R.drawable.ic_back));
                        drawer.openDrawer(navigationView);
                    }
                }
            });

            if (AppController.getSpIsLogin().getBoolean(SPUtils.IS_LOGIN, false)) {
                String welcome_text = AppController.getSpUserInfo().getString(SPUtils.MemberName, "");
                home_staff_name.setText(welcome_text);
                nav_username.setText(welcome_text);
                String mobileno = AppController.getSpUserInfo().getString(SPUtils.MemberMobileNo, "");
                nav_mobileno.setText(mobileno);
            }

            nav_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            nav_lead_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    Intent intent = new Intent(act, LeadListActivity.class);
                    intent.putExtra("Keyword", "" + et_search.getText().toString());
                    startActivity(intent);
                }
            });
            nav_rate_the_app.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    Rate_UsPlayStore();
                }
            });
            nav_privacy_policy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    Toast.makeText(act, "Coming Soon !!", Toast.LENGTH_SHORT).show();
                }
            });
            nav_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    try {
                        final String appPackageName = getPackageName();
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                        String sAux = "Join our Management Team and share it with your Team Member";
                        sAux = sAux + "\n\n https://play.google.com/store/apps/details?id=" + appPackageName;
                        i.putExtra(Intent.EXTRA_TEXT, sAux);
                        startActivity(Intent.createChooser(i, "choose one"));
                    } catch (Exception e) {
                    }
                }
            });
            nav_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    AppUtils.showDialogSignOut(act);
                }
            });
            /*Home Today Leads */
            ll_data_found = findViewById(R.id.ll_data_found);
            ll_no_data_found = findViewById(R.id.ll_no_data_found);

            recyclerView = (RecyclerView) findViewById(R.id.listView);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);

            recyclerView.setItemAnimator(new DefaultItemAnimator());

            if (AppUtils.isNetworkAvailable(act)) {
                executeHomeLeadListRequest();
                executeLoginRequest();
            } else {
                AppUtils.alertDialogWithFinish(act, getResources().getString(R.string.txt_networkAlert));
            }

            et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        AppUtils.hideKeyboardOnClick(MainActivity.this, view);
                        performSearch();
                        return true;
                    }
                    return false;
                }
            });

            if (!AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, "").equalsIgnoreCase("")) {
                AppUtils.loadImage(act, AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, ""), user_profile);
                AppUtils.loadImage(act, AppController.getSpUserInfo().getString(SPUtils.USER_profile_pic_byte_code, ""), nav_user_profile);
            }
            user_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkAndRequestPermissions())
                        selectImage();
                }
            });
            nav_user_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkAndRequestPermissions())
                        selectImage();
                }
            });

            /*Searching by voice code*/
            speak = (TextView) findViewById(R.id.speak);

            PackageManager pm = getPackageManager();
            final List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

            speak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activities.size() == 0) {
                        //   speak.setEnabled(false);
                        // speak.setText("Recognizer not present");
                        Toast.makeText(act, "Voice Recognizer not present", Toast.LENGTH_SHORT).show();
                    } else {
                        startVoiceRecognitionActivity();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void Rate_UsPlayStore() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @Override
    protected void onRestart() {
        mukesh_begview.setBadgeValue(Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")));

        if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) == 0) {
            if (animator == null) {
            } else {
                if (animator.isStarted()) {
                    animator.pause();
                    animator.cancel();
                }
            }
        }
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

        if (Integer.parseInt(AppController.getSpUserInfo().getString(SPUtils.notification_count, "0")) == 0) {
            if (animator == null) {
            } else {
                if (animator.isStarted()) {
                    animator.pause();
                    animator.cancel();
                }
            }
        }
    }

    private void executeHomeLeadListRequest() {
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
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToSelect_TodayLead, TAG);
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
                map.put("lead_status", "" + jsonObject.getString("StatusName"));
                map.put("lead_statusID", "" + jsonObject.getString("LeadStatus"));

                map.put("isTransfer", "" );
                map.put("TransferredID", "" );
                map.put("TransferRemark", "");
                map.put("TransferedLeadID", "" );
                map.put("TransferedMobileNo", "" );
                map.put("TransferedStaffName", "" );
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
                adapter = new Home_Lead_List_Adapter(act, array_list);
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
            AppUtils.showExceptionDialog(MainActivity.this);
        }
        System.gc();
    }

    @Override
    public void onBackPressed() {
        try {
            if (drawer.isDrawerOpen(navigationView)) {
                drawer.closeDrawer(navigationView);
            } else {
                showExitDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showExitDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(MainActivity.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
            //  txt_DialogTitle.setText(Html.fromHtml("Are you sure!!! Do you want to Exit?"));
            txt_DialogTitle.setText(Html.fromHtml("Do you want to exit from this application!"));
            TextView txt_submit = dialog.findViewById(R.id.txt_submit);
            txt_submit.setText("Yes");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //android.os.Process.killProcess(android.os.Process.myPid());
                        finish();
                        System.exit(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = dialog.findViewById(R.id.txt_cancel);
            txt_cancel.setText(getResources().getString(R.string.txt_signout_no));
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performSearch() {
        if (et_search.getText().toString().isEmpty()) {
            AppUtils.alertDialog(MainActivity.this, "Please enter search keyword.");
            et_search.requestFocus();
        } else {
            Intent intent = new Intent(act, LeadListActivity.class);
            intent.putExtra("Keyword", "" + et_search.getText().toString());
            startActivity(intent);
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Populate the wordsList with the String values the recognition engine thought it heard
            final ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (!matches.isEmpty()) {
                et_search.setText("");
                String Query = matches.get(0);
                et_search.setText(Query);
                performSearch();
                // speak.setEnabled(false);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        String imageStoragePath = destination.getAbsolutePath();
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        executePostImageUploadRequest(bitmap);
        user_profile.setImageBitmap(bitmap);
        nav_user_profile.setImageBitmap(bitmap);

        Log.e("from camera data", imageStoragePath);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        executePostImageUploadRequest(bm);
        user_profile.setImageBitmap(bm);
        nav_user_profile.setImageBitmap(bm);
        String imagepath = bm.toString();
        Log.e("from gallery data", imagepath);
    }

    private void executePostImageUploadRequest(final Bitmap bitmap) {
        try {
            if (AppUtils.isNetworkAvailable(MainActivity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(MainActivity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("StaffID", AppController.getSpUserInfo().getString(SPUtils.Member_ID, "")));
                            postParameters.add(new BasicNameValuePair("Images", AppUtils.getBase64StringFromBitmap(bitmap)));
                            postParameters.add(new BasicNameValuePair("Extension", "PNG"));
                            response = AppUtils.callWebServiceWithMultiParam(MainActivity.this, postParameters, QueryUtils.methodToStaff_UploadProfileImage, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(MainActivity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                if (!jsonArrayData.getJSONObject(0).getString("Images").equals("")) {
                                    AppUtils.loadImage(act, AppUtils.imageURL() + jsonArrayData.getJSONObject(0).getString("Images"), user_profile);
                                    AppUtils.loadImage(act, AppUtils.imageURL() + jsonArrayData.getJSONObject(0).getString("Images"), nav_user_profile);
                                    AppController.getSpUserInfo().edit().putString(SPUtils.USER_profile_pic_byte_code, (AppUtils.imageURL() + jsonArrayData.getJSONObject(0).getString("Images"))).commit();
                                }
                            } else {
                                //   AppUtils.alertDialog(MainActivity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(MainActivity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(MainActivity.this);
        }
    }

    private void executeLoginRequest() {
        try {

            if (AppUtils.isNetworkAvailable(act)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        // AppUtils.showProgressDialog(act);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("MobileNo", "" + AppController.getSpUserInfo().getString(SPUtils.MemberMobileNo, "")));
                            postParameters.add(new BasicNameValuePair("Password", AppController.getSpUserInfo().getString(SPUtils.MemberPasswd, "")));
                            postParameters.add(new BasicNameValuePair("DeviceID", "" + AppUtils.getDeviceID(act)));
                            postParameters.add(new BasicNameValuePair("FirebaseID", "" + FirebaseInstanceId.getInstance().getToken()));
                            response = AppUtils.callWebServiceWithMultiParam(act, postParameters, QueryUtils.methodToStaff_Login, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            // AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (jsonArrayData.length() != 0) {
                                    saveLoginUserInfo(jsonArrayData);
                                } else {
                                    AppUtils.alertDialog(act, jsonObject.getString("Message"));
                                }
                            } else {
                                try {
                                    final Dialog dialog;
                                    dialog = new Dialog(act, R.style.CustomDialogTheme);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    //...set cancelable false so that it's never get hidden
                                    dialog.setCancelable(false);
                                    //...that's the layout i told you will inflate later
                                    dialog.setContentView(R.layout.custom_dialog_one);

                                    TextView txt_DialogTitle = dialog.findViewById(R.id.txt_DialogTitle);
                                    TextView txt_submit = dialog.findViewById(R.id.txt_submit);

                                    txt_DialogTitle.setText("" + jsonObject.getString("Message"));

                                    txt_submit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            AppController.getSpUserInfo().edit().clear().commit();
                                            AppController.getSpIsLogin().edit().clear().commit();

                                            Intent intent = new Intent(act, Login_Activity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            ((Activity) act).finish();
                                        }
                                    });

                                    dialog.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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

    private void saveLoginUserInfo(final JSONArray jsonArray) {
        try {

            AppController.getSpUserInfo().edit().clear().commit();

            AppController.getSpUserInfo().edit()
                    .putString(SPUtils.USER_TYPE, "Staff")
                    .putString(SPUtils.Member_ID, jsonArray.getJSONObject(0).getString("EMPID"))
                    .putString(SPUtils.MemberName, jsonArray.getJSONObject(0).getString("FirstName"))
                    .putString(SPUtils.MemberMobileNo, jsonArray.getJSONObject(0).getString("Mobile"))
                    .putString(SPUtils.MemberEMail, jsonArray.getJSONObject(0).getString("Email"))
                    .putString(SPUtils.MemberAddress, jsonArray.getJSONObject(0).getString("Address"))
                    .putString(SPUtils.MemberPasswd, jsonArray.getJSONObject(0).getString("Password"))
                    .putString(SPUtils.MemberDepartment, jsonArray.getJSONObject(0).getString("Department"))
                    .putString(SPUtils.DeviceToken, jsonArray.getJSONObject(0).getString("DeviceID"))
                    .putString(SPUtils.MemberActiveStatus, jsonArray.getJSONObject(0).getString("ActiveStatus"))
                    .putString(SPUtils.USER_profile_pic_byte_code, AppUtils.imageURL() + jsonArray.getJSONObject(0).getString("Images"))
                    //.putString(SPUtils.MemberDOJ, AppUtils.getDateFromAPIDate(jsonArray.getJSONObject(0).getString("RTS")))
                    .commit();

            AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    private boolean checkAndRequestPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int permissionWRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionREAD_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionREAD_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}