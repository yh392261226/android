package fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gjzg.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.EvaluateAdapter;
import bean.EvaluateBean;
import config.NetConfig;
import config.StateConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import refreshload.PullToRefreshLayout;
import refreshload.PullableListView;
import utils.Utils;
import view.CProgressDialog;

/**
 * 创建日期：2017/8/8 on 10:48
 * 作者:孙明明
 * 描述:收到的评价
 */

public class EvaluateGetFragment extends CommonFragment implements PullToRefreshLayout.OnRefreshListener {

    private View rootView;
    private FrameLayout fl;
    private View emptyDataView, emptyNetView;
    private TextView emptyNetTv;
    private PullToRefreshLayout ptrl;
    private PullableListView plv;
    private CProgressDialog cpd;

    private List<EvaluateBean> list;
    private EvaluateAdapter adapter;

    private OkHttpClient okHttpClient;

    private int state = StateConfig.LOAD_DONE;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case StateConfig.LOAD_NO_NET:
                        notifyNoNet();
                        break;
                    case StateConfig.LOAD_DONE:
                        notifyData();
                        break;
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(StateConfig.LOAD_NO_NET);
        handler.removeMessages(StateConfig.LOAD_DONE);
    }

    @Override
    protected View getRootView() {
        return rootView = LayoutInflater.from(getActivity()).inflate(R.layout.common_listview, null);
    }

    @Override
    protected void initView() {
        initRootView();
        initDialogView();
        initEmptyView();
    }

    private void initRootView() {
        fl = (FrameLayout) rootView.findViewById(R.id.fl);
        ptrl = (PullToRefreshLayout) rootView.findViewById(R.id.ptrl);
        plv = (PullableListView) rootView.findViewById(R.id.plv);
    }

    private void initDialogView() {
        cpd = new CProgressDialog(getActivity(), R.style.dialog_cprogress);
    }

    private void initEmptyView() {
        fl = (FrameLayout) rootView.findViewById(R.id.fl);
        emptyDataView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_data, null);
        emptyDataView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fl.addView(emptyDataView);
        emptyDataView.setVisibility(View.GONE);
        emptyNetView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_net, null);
        emptyNetTv = (TextView) emptyNetView.findViewById(R.id.tv_no_net_refresh);
        emptyNetView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        fl.addView(emptyNetView);
        emptyNetView.setVisibility(View.GONE);
        emptyNetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyNetView.setVisibility(View.GONE);
                loadNetData();
            }
        });
    }

    @Override
    protected void initData() {
        list = new ArrayList<>();
        adapter = new EvaluateAdapter(getActivity(), list);
        okHttpClient = new OkHttpClient();
    }

    @Override
    protected void setData() {
        plv.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        ptrl.setOnRefreshListener(this);
    }

    @Override
    protected void loadData() {
        cpd.show();
        loadNetData();
    }

    private void loadNetData() {
        Request evaluateGetRequest = new Request.Builder().url(NetConfig.testUrl).get().build();
        okHttpClient.newCall(evaluateGetRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(StateConfig.LOAD_NO_NET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (state == StateConfig.LOAD_REFRESH) {
                        list.clear();
                    }
                    String result = response.body().string();
                    parseJson(result);
                }
            }
        });
    }

    private void parseJson(String json) {
        try {
            JSONObject objBean = new JSONObject(json);
            if (objBean.optInt("code") == 200) {
                for (int i = 0; i < 10; i++) {
                    EvaluateBean e = new EvaluateBean();
                    e.setGet(true);
                    e.setNumCount(10);
                    e.setContent("小伙子干活特麻利");
                    e.setPraiseCount(3);
                    e.setTime("2017年5月4日 17:25");
                    list.add(e);
                }
                handler.sendEmptyMessage(StateConfig.LOAD_DONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void notifyNoNet() {
        switch (state) {
            case StateConfig.LOAD_DONE:
                cpd.dismiss();
                ptrl.setVisibility(View.GONE);
                emptyDataView.setVisibility(View.GONE);
                emptyNetView.setVisibility(View.VISIBLE);
                break;
            case StateConfig.LOAD_REFRESH:
                ptrl.hideHeadView();
                Utils.toast(getActivity(), StateConfig.loadNonet);
                break;
            case StateConfig.LOAD_LOAD:
                ptrl.hideFootView();
                Utils.toast(getActivity(), StateConfig.loadNonet);
                break;
        }
    }

    private void notifyData() {
        switch (state) {
            case StateConfig.LOAD_DONE:
                cpd.dismiss();
                if (list.size() == 0) {
                    ptrl.setVisibility(View.GONE);
                    emptyDataView.setVisibility(View.VISIBLE);
                    emptyNetView.setVisibility(View.GONE);
                } else {
                    ptrl.setVisibility(View.VISIBLE);
                    emptyDataView.setVisibility(View.GONE);
                    emptyNetView.setVisibility(View.GONE);
                }
                break;
            case StateConfig.LOAD_REFRESH:
                ptrl.hideHeadView();
                break;
            case StateConfig.LOAD_LOAD:
                ptrl.hideFootView();
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        state = StateConfig.LOAD_REFRESH;
        loadNetData();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        state = StateConfig.LOAD_LOAD;
        loadNetData();
    }
}
