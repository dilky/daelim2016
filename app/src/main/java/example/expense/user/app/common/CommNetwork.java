
package example.expense.user.app.common;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import example.expense.user.app.BuildConfig;
import example.expense.user.app.common.listener.onNetworkResponseListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dilky on 2016-08-03.
 * 공통 통신 모듈
 */
public class CommNetwork {

    /**
     * API KEY
     */
    private String apiKey;

    /**
     * 입력 파라미터
     */
    private JSONObject inputObject;

    /**
     * HttpRequest 통신 객체
     */
    private final OkHttpClient client = new OkHttpClient();

    private Activity activity;
    private onNetworkResponseListener listener;

    public CommNetwork(@NonNull Activity atvt, @NonNull onNetworkResponseListener i) {
        activity = atvt;
        listener = i;
    }


    public void requestToServer(String api_key, JSONObject requestObject) throws Exception {
        apiKey = api_key;
        inputObject = new JSONObject();
        inputObject.put("API_KEY", apiKey);
        inputObject.put("REQ_DATA", requestObject);

        if (activity != null) {
            request();
        } else {
            // TODO : 화면이 없는 경우
        }
    }

    private void request() {

        Request request = new Request.Builder()
                .url("http://61.84.24.245:8888/daelim2016/gateway.jsp?json_data=" + inputObject.toString())
                .build();

        Log.d("dilky", "http://61.84.24.245:8888/daelim2016/gateway.jsp?json_data=" + inputObject.toString());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onFailure(apiKey, "T999", e.getMessage());
                        }
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!response.isSuccessful()) {
                            if (listener != null) {
                                listener.onFailure(apiKey, String.valueOf(response.code()), response.message());
                            }
                            return;
                        }

                        if (BuildConfig.DEBUG) {
                            Headers responseHeaders = response.headers();
                            for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                            }
                        }

                        //
                        // 응답에 성공한 경우
                        //
                        if (listener != null) {
                            try {
                                JSONObject outputObject = new JSONObject(response.body().string());
                                Log.d("dilky", outputObject.toString());

                                if (outputObject.has("RSLT_CD") && !"0000".equals(outputObject.getString("RSLT_CD")) ) {
                                    String resultMsg = "오류가 발생하였습니다. 오류코드를 확인하세요.";
                                    if (outputObject.has("RSLT_MSG") && !outputObject.isNull("RSLT_MSG") || !"".equals(outputObject.getString("RSLT_MSG"))) {
                                        resultMsg = outputObject.getString("RSLT_MSG");
                                    }
                                    listener.onFailure(apiKey, outputObject.getString("RSLT_CD"), resultMsg);
                                    return;
                                }

                                if (!outputObject.has("RESP_DATA") || outputObject.isNull("RESP_DATA")) {
                                    listener.onFailure(apiKey, "1000", "응답부가 존재하지 않습니다.");
                                    return;
                                }
                                listener.onSuccess(apiKey, outputObject.getJSONObject("RESP_DATA"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
            }
        });
    }

}
