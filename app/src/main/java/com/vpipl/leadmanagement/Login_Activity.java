package com.vpipl.leadmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vpipl.leadmanagement.Utils.AppController;
import com.vpipl.leadmanagement.Utils.AppUtils;
import com.vpipl.leadmanagement.Utils.QueryUtils;
import com.vpipl.leadmanagement.Utils.SPUtils;
import com.vpipl.leadmanagement.Utils.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class Login_Activity extends AppCompatActivity {

    private static final String TAG = "Login_Register_Activity";
    TextView txt_login;
    TextView txt_forgot_password;
    private EditText edtxt_mobile, edtxt_password;

    String mobileno;
    String password;
    Activity act;
    TextView txt_eye_close, txt_eye_open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            act = Login_Activity.this;
            setContentView(R.layout.activity_login);
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            edtxt_mobile = findViewById(R.id.edtxt_mobile);
            edtxt_password = findViewById(R.id.edtxt_password);

            txt_login = findViewById(R.id.txt_login);

            txt_forgot_password = findViewById(R.id.txt_forgot_password);
            txt_eye_close = findViewById(R.id.txt_eye_close);
            txt_eye_open = findViewById(R.id.txt_eye_open);

            txt_login.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    ValidateData();
                    // startSplash(new Intent(act, MainActivity.class));
                }
            });

            edtxt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == 1234 || id == EditorInfo.IME_NULL) {
                        AppUtils.hideKeyboardOnClick(act, textView);
                        ValidateData();
                        return true;
                    }
                    return false;
                }
            });

            txt_forgot_password.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.hideKeyboardOnClick(act, v);
                    //   startActivity(new Intent(act, Forget_Password_Activity.class));
                }
            });

            txt_eye_open.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // hide password
                    edtxt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txt_eye_close.setVisibility(View.VISIBLE);
                    txt_eye_open.setVisibility(View.GONE);
                }
            });
            txt_eye_close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // show password
                    edtxt_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txt_eye_close.setVisibility(View.GONE);
                    txt_eye_open.setVisibility(View.VISIBLE);
                }
            });
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();

            Log.e(TAG, "sendRegistrationToServer: " + refreshedToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void ValidateData() {
        edtxt_mobile.setError(null);
        edtxt_password.setError(null);

        mobileno = edtxt_mobile.getText().toString().trim();
        password = edtxt_password.getText().toString().trim();

        if (TextUtils.isEmpty(mobileno)) {
            Toasty.error(act, getResources().getString(R.string.error_required_user_id), Toast.LENGTH_SHORT, true).show();
            edtxt_mobile.requestFocus();
        } else if (!AppUtils.isValidMobileno(mobileno)) {
            Toasty.error(act, "Please Enter Valid Mobile No", Toast.LENGTH_SHORT, true).show();
            edtxt_mobile.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            Toasty.error(act, getResources().getString(R.string.error_required_password), Toast.LENGTH_SHORT, true).show();
            edtxt_password.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(act)) {
                executeLoginRequest(mobileno, password);
            } else {
                AppUtils.alertDialog(act, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    private void executeLoginRequest(final String MobileNo, final String passwd) {
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
                            postParameters.add(new BasicNameValuePair("MobileNo", MobileNo));
                            postParameters.add(new BasicNameValuePair("Password", passwd));
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
                            AppUtils.dismissProgressDialog();
                            JSONObject jsonObject = new JSONObject(resultData);
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                if (jsonArrayData.length() != 0) {
                                    saveLoginUserInfo(jsonArrayData);
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

            startSplash(new Intent(act, MainActivity.class));

            AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(act);
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSplash(final Intent intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}