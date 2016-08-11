package example.expense.user.app.common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dilky on 2016-08-03.
 */
public class AccountTitleSpinnerList {

    private ArrayList<AccountTitleModel> dataList = new ArrayList<>();
    private ArrayList<String> arrayList = new ArrayList();

    public AccountTitleSpinnerList(JSONArray array) throws Exception {
        for(int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            dataList.add(new AccountTitleModel(obj.getString("ACCOUNT_TTL_NM"), obj.getString("ACCOUNT_TTL_CD")));
        }
    }
    public ArrayList<String> getArrayList() {
        for (int i = 0; i < dataList.size(); i++) {
            arrayList.add(getAccountTitleNm(i));
        }
        return arrayList;
    }

    public String getAccountTitleNm(int indx) {
        return dataList.get(indx).acctName;
    }

    public String getAccountTitleCd(int indx) {
        return dataList.get(indx).acctCode;
    }

    class AccountTitleModel {
        String acctName;
        String acctCode;
        public AccountTitleModel(String name, String code) {
            acctName = name;
            acctCode = code;
        }
    }
}
