package fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.gjzg.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import activity.PersonMagActivity;
import adapter.PersonPrivewAdapter;
import bean.PersonPreviewBean;
import bean.RoleBean;
import config.NetConfig;
import config.StateConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import view.CProgressDialog;

/**
 * 创建日期：2017/8/10 on 14:30
 * 作者:孙明明
 * 描述:信息预览
 */

public class PersonMagPreviewFrag extends CommonFragment {

    //根视图
    private View rootView;
    //ListView
    private ListView listView;
    //加载对话框视图
    private CProgressDialog cPd;
    //信息预览数据类对象
    private PersonPreviewBean personPreviewBean;
    //信息预览数据适配器
    private PersonPrivewAdapter personPrivewAdapter;
    //okHttpClient
    private OkHttpClient okHttpClient;
    //个人管理线程
    private Handler personPreviewHandler;

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
        //初始化根视图
        return rootView = LayoutInflater.from(getActivity()).inflate(R.layout.frag_person_mag_preview, null);
    }

    @Override
    protected void initView() {
        initRootView();
        initDialogView();
    }

    private void initRootView() {
        //初始化ListView
        listView = (ListView) rootView.findViewById(R.id.lv_person_mag_preview);
    }

    private void initDialogView() {
        //初始化加载对话框视图
        cPd = new CProgressDialog(getActivity(), R.style.dialog_cprogress);
    }

    @Override
    protected void initData() {
        //初始化个人管理线程
        personPreviewHandler = ((PersonMagActivity) getActivity()).handler;
        //初始化信息预览数据类对象
        personPreviewBean = new PersonPreviewBean();
        //初始化信息预览数据适配器
        personPrivewAdapter = new PersonPrivewAdapter(getActivity(), personPreviewBean);
        //初始化okHttpClient
        okHttpClient = new OkHttpClient();
    }

    @Override
    protected void setData() {
        //绑定信息预览适配器
        listView.setAdapter(personPrivewAdapter);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void loadData() {
        cPd.show();
        loadNetData();
    }

    private void loadNetData() {
        Request request = new Request.Builder().url(NetConfig.testUrl).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(StateConfig.LOAD_NO_NET);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
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
                personPreviewBean.setNameTitle("姓名");
                personPreviewBean.setNameContent("王小二");
                personPreviewBean.setSexTitle("性别");
                personPreviewBean.setSex(true);
                personPreviewBean.setIdNumberTitle("身份证号");
                personPreviewBean.setIdNumberContent("230***********1234");
                personPreviewBean.setAddressTitle("现居地");
                personPreviewBean.setAddressContent("哈尔滨-道里区");
                personPreviewBean.setHouseHoldTitle("户口所在地");
                personPreviewBean.setHouseHoldContent("蓬莱");
                personPreviewBean.setBriefTitle("个人简介");
                personPreviewBean.setBriefContent("专业水泥工，精通水暖，刮大白");
                personPreviewBean.setPhoneNumberTitle("手机号码（已绑定）");
                personPreviewBean.setPhoneNumberContent("152****4859");
                personPreviewBean.setRoleTitle("角色选择");
                personPreviewBean.setRole(false);
                List<RoleBean> list = new ArrayList<>();
                RoleBean roleBean1 = new RoleBean();
                roleBean1.setId("1");
                roleBean1.setContent("水泥工");
                RoleBean roleBean2 = new RoleBean();
                roleBean2.setId("2");
                roleBean2.setContent("水暖工");
                RoleBean roleBean3 = new RoleBean();
                roleBean3.setId("3");
                roleBean3.setContent("瓦工");
                list.add(roleBean1);
                list.add(roleBean2);
                list.add(roleBean3);
                personPreviewBean.setRoleBeanList(list);
                handler.sendEmptyMessage(StateConfig.LOAD_DONE);
                Bundle bundle = new Bundle();
                bundle.putSerializable("PersonPreviewBean", personPreviewBean);
                Message msg = new Message();
                msg.setData(bundle);
                personPreviewHandler.sendMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void notifyNoNet() {
        cPd.dismiss();
    }

    private void notifyData() {
        cPd.dismiss();
        personPrivewAdapter.notifyDataSetChanged();
    }
}