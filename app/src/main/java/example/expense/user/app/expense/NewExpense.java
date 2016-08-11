package example.expense.user.app.expense;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import example.expense.user.app.R;
import example.expense.user.app.common.AccountTitleSpinnerList;
import example.expense.user.app.common.CommNetwork;
import example.expense.user.app.common.ErrorUtils;
import example.expense.user.app.common.listener.onNetworkResponseListener;

/**
 * Created by dilky on 2016-07-20.
 * 신청하기 화면
 */
public class NewExpense extends AppCompatActivity implements onNetworkResponseListener {
    public static EditText etPaymentDate;
    private Spinner spnAccountTitle;
    AccountTitleSpinnerList spinnerList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            setContentView(R.layout.content_new_expense);

            addToolBar();

            etPaymentDate = (EditText) findViewById(R.id.et_PaymentDate);

            // 어제 날짜로 초기화
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, -1);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            etPaymentDate.setTag(R.id.tag_year, year);
            etPaymentDate.setTag(R.id.tag_month, month);
            etPaymentDate.setTag(R.id.tag_day, day);
            etPaymentDate.setText(String.format("%04d년 %2d월 %2d일", year, month + 1, day));

            // 클릭시 데이트피커 보여주기
            etPaymentDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog(v);
                }
            });

            // 계정코드 조회
            spnAccountTitle = (Spinner)findViewById(R.id.spn_AccountTitle);
            getAccounttitleCodes();
        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    private void getAccounttitleCodes() throws Exception {

        CommNetwork network = new CommNetwork(this, this);

        JSONObject requestObject = new JSONObject();
        network.requestToServer("ACCOUNT_L001", requestObject);
    }

    private void addToolBar() throws Exception {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.text_new_expense);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private boolean emptyCheck(EditText editText) {
        if (TextUtils.isEmpty(editText.getText())) {
            Toast.makeText(this, getString(R.string.error_msg_required_values_are_missing), Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    // 등록 버튼 클릭
    public void toolbarRightButtonClick(View v) {
        try {
            EditText paymentStoreNm = (EditText) findViewById(R.id.et_PaymentStoreName);
            EditText paymentAmount = (EditText) findViewById(R.id.et_PaymentAmount);
            EditText summary = (EditText) findViewById(R.id.et_Summary);

            if ( emptyCheck(paymentStoreNm)
                    || emptyCheck(paymentAmount)
                    || emptyCheck(summary)
                    || emptyCheck(etPaymentDate) ) {
                return;
            }

            // 서버에 저장하기
            JSONObject requestObject = new JSONObject();
            requestObject.put("PAYMENT_STORE_NM", paymentStoreNm.getText().toString());
            requestObject.put("PAYMENT_AMT", paymentAmount.getText().toString());
            requestObject.put("PAYMENT_DTTM", String.format("%04d%02d%02d", etPaymentDate.getTag(R.id.tag_year), ((Integer)etPaymentDate.getTag(R.id.tag_month) + 1), etPaymentDate.getTag(R.id.tag_day)));
            requestObject.put("SUMMARY", summary.getText().toString());
            requestObject.put("ACCOUNT_TTL_CD", spinnerList.getAccountTitleCd(spnAccountTitle.getSelectedItemPosition()));
            requestObject.put("USER_ID", "test_user1");

            Log.d("dilky", requestObject.toString());

            CommNetwork network = new CommNetwork(this, this);
            network.requestToServer("EXPENSE_I001", requestObject);
        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    // 사용일자 클릭
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onSuccess(String api_key, JSONObject response) {
        try {
            if ("ACCOUNT_L001".equals(api_key)) {
                //
                // 계정코드 조회
                //
                JSONArray array = response.getJSONArray("REC");
                spinnerList = new AccountTitleSpinnerList(array);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(NewExpense.this, android.R.layout.simple_spinner_item, spinnerList.getArrayList());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnAccountTitle.setAdapter(adapter);
            } else if ("EXPENSE_I001".equals(api_key)) {
                //
                // 경비신청
                //
                if ( !TextUtils.isEmpty(response.getString("EXPENSE_SEQ") ) ) {
                    Intent intent = new Intent(NewExpense.this, ViewExpense.class);
                    intent.putExtra("EXPENSE_SEQ", response.getString("EXPENSE_SEQ"));
                    startActivity(intent);
                    finish();
                }
            }
        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    @Override
    public void onFailure(String api_key, String error_cd, String error_msg) {
        try {
            if ("ACCOUNT_L001".equals(api_key)) {
                Log.d("dilky", String.format("onFailure (error_cd : %s, error_msg: %s)", error_cd, error_msg) );
            } else if ("EXPENSE_I001".equals(api_key)) {

            }
        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year, month, day;
            if (etPaymentDate.getTag(R.id.tag_year) == null) {
                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } else {
                year = (Integer) etPaymentDate.getTag(R.id.tag_year);
                month = (Integer) etPaymentDate.getTag(R.id.tag_month);
                day = (Integer) etPaymentDate.getTag(R.id.tag_day);
            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String date = String.format("%04d년 %2d월 %2d일", year, month + 1, day);
            etPaymentDate.setText(date);
            etPaymentDate.setTag(R.id.tag_year, year);
            etPaymentDate.setTag(R.id.tag_month, month);
            etPaymentDate.setTag(R.id.tag_day, day);
        }
    }
}
