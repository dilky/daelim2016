package example.expense.user.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import example.expense.user.app.common.CommNetwork;
import example.expense.user.app.common.ErrorUtils;
import example.expense.user.app.common.listener.onNetworkResponseListener;
import example.expense.user.app.expense.NewExpense;
import example.expense.user.app.expense.ViewExpense;

/**
 * Created by dilky on 2016-07-20.
 * 신청 경비 목록 화면
 */
public class ExpenseList extends AppCompatActivity {

    ArrayList<ExpenseListModel> listDatas;

    ListView listView;

    ExpenseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.enableDefaults();
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.content_main);

            addToolBar();

            listView = (ListView) findViewById(R.id.expenseListView);
            adapter = new ExpenseListAdapter(this);
            listDatas = new ArrayList<>();

            CommNetwork network = new CommNetwork(this, new onNetworkResponseListener() {
                @Override
                public void onSuccess(String api_key, JSONObject response) {

                    try {
                        JSONArray array = response.getJSONArray("REC");
                        for( int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String seq = object.getString("EXPENSE_SEQ");
                            String paymentStoreName = object.getString("PAYMENT_STORE_NM");
                            String statusNm = object.getString("ADMISSION_STATUS_NM");
                            String summary = object.getString("SUMMARY");
                            String paymentDate = object.getString("PAYMENT_DTTM");
                            String paymentAmount = object.getString("PAYMENT_AMT");
                            listDatas.add(new ExpenseListModel(seq, paymentStoreName, statusNm, summary, paymentDate, paymentAmount));
                        }

                        adapter.setList(listDatas);
                        listView.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String api_key, String error_cd, String error_msg) {
                    ErrorUtils.Alert(ExpenseList.this, error_msg);
                }
            });

            JSONObject req_data = new JSONObject();
            req_data.put("USER_ID", "test_user1");
            network.requestToServer("EXPENSE_L001", req_data);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ExpenseList.this, ViewExpense.class);
                    intent.putExtra("EXPENSE_SEQ",listDatas.get(position).expenseSeq);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            ErrorUtils.AlertException(this, getString(R.string.error_msg_default_with_activity), e);
        }
    }

    private void addToolBar() throws Exception {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expense_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 메뉴의 항목을 선택(클릭)했을 때 호출되는 콜백메서드
        int id = item.getItemId();
        switch(id) {
            case R.id.menu_add:
                Intent intent = new Intent(this, NewExpense.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    class ExpenseListModel {
        public ExpenseListModel(String _expenseSeq, String _paymentStoreName, String _status, String _summary, String _paymentDate, String _paymentAmount) {
            expenseSeq = _expenseSeq;
            paymentStoreName = _paymentStoreName;
            status = _status;
            summary = _summary;
            paymentDate = _paymentDate;
            paymentAmount = _paymentAmount;
        }
        public String expenseSeq;
        public String paymentStoreName;
        public String status;
        public String summary;
        public String paymentDate;
        public String paymentAmount;
    }

    class ExpenseListAdapter extends BaseAdapter {
        private Context context;
        private List<ExpenseListModel> lists;
        public ExpenseListAdapter(Context ctx ) {
            context = ctx;
        }
        public void setList(List list) {
            lists = list;
        }
        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int i) {
            return lists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(context, R.layout.content_main_item, null);
            }

            TextView tvSummary = (TextView)view.findViewById(R.id.tv_Summary);
            TextView tvPaymentStoreName = (TextView) view.findViewById(R.id.tv_PaymentStoreName);
            TextView tvStatus = (TextView) view.findViewById(R.id.tv_status);
            TextView tvPaymentDate = (TextView) view.findViewById(R.id.tv_PaymentDate);
            TextView tvPaymentAmount = (TextView) view.findViewById(R.id.tv_PaymentAmount);

            tvSummary.setText(lists.get(i).summary);
            tvPaymentStoreName.setText(lists.get(i).paymentStoreName);
            tvStatus.setText(lists.get(i).status);
            tvPaymentDate.setText(lists.get(i).paymentDate);
            tvPaymentAmount.setText(lists.get(i).paymentAmount);

            return view;
        }
    }


}
