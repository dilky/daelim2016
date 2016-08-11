package example.expense.user.app.common.listener;

import org.json.JSONObject;

/**
 * Created by dilky on 2016-08-03.
 */
public interface onNetworkResponseListener {
    void onSuccess(String api_key, JSONObject response);
    void onFailure(String api_key, String error_cd, String error_msg);
}
