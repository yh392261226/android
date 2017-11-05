package talkworker.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.gjzg.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bean.ToSelectTaskBean;
import bean.ToTalkWorkerBean;
import config.IntentConfig;
import config.NetConfig;
import persondetail.view.PersonDetailActivity;
import selecttask.view.SelectTaskActivity;
import talkworker.presenter.ITalkWorkerPresenter;
import talkworker.presenter.TalkWorkerPresenter;
import utils.DataUtils;
import utils.UserUtils;
import utils.Utils;
import bean.WorkerBean;
import view.CImageView;
import view.CProgressDialog;

public class TalkWorkerActivity extends AppCompatActivity implements ITalkWorkerActivity, View.OnClickListener {

    private View rootView;
    private RelativeLayout returnRl;
    private CProgressDialog cpd;
    private CImageView iconIv;
    private ImageView sexIv, statusIv;
    private TextView nameTv, skillTv, infoTv, addressTv;
    private MapView mapView;

    private ITalkWorkerPresenter talkWorkerPresenter;
    private WorkerBean workerBean;
    private ToTalkWorkerBean toTalkWorkerBean;

    private final int LOAD_SUCCESS = 1;
    private final int LOAD_FAILURE = 2;
    private final int CHECK_HAVE = 3;
    private final int CHECK_NONE = 4;

    private final int NO_YOURSELF = 1, TALK_TO_YOU = 2, DOING_FOR_YOU = 3, BUSY = 4, WAIT = 5;
    private RelativeLayout noYourselfRl, talkToYouRl, doingForyouRl, busyRl, waitRl;
    private int SHOW_STATE;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                switch (msg.what) {
                    case LOAD_SUCCESS:
                        notifyData();
                        break;
                    case LOAD_FAILURE:
                        break;
                    case CHECK_HAVE:
                        Intent intent = new Intent(TalkWorkerActivity.this, SelectTaskActivity.class);
                        ToSelectTaskBean toSelectTaskBean = new ToSelectTaskBean();
                        toSelectTaskBean.setSkillId(toTalkWorkerBean.getS_id());
                        toSelectTaskBean.setWorkerId(toTalkWorkerBean.getU_id());
                        intent.putExtra(IntentConfig.toSelectTask, toSelectTaskBean);
                        startActivity(intent);
                        break;
                    case CHECK_NONE:
                        Utils.toast(TalkWorkerActivity.this, "请先去发布工作");
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(TalkWorkerActivity.this).inflate(R.layout.activity_talk_worker, null);
        setContentView(rootView);
        initView();
        initData();
        setListener();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (talkWorkerPresenter != null) {
            talkWorkerPresenter.destroy();
            talkWorkerPresenter = null;
        }
        if (handler != null) {
            handler.removeMessages(LOAD_SUCCESS);
            handler.removeMessages(LOAD_FAILURE);
            handler.removeMessages(CHECK_HAVE);
            handler.removeMessages(CHECK_NONE);
            handler = null;
        }
    }

    private void initView() {
        initRootView();
    }

    private void initRootView() {
        mapView = (MapView) rootView.findViewById(R.id.mv_talk_worker);
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_talk_worker_return);
        iconIv = (CImageView) rootView.findViewById(R.id.iv_talk_worker_icon);
        sexIv = (ImageView) rootView.findViewById(R.id.iv_talk_worker_sex);
        statusIv = (ImageView) rootView.findViewById(R.id.iv_talk_worker_status);
        nameTv = (TextView) rootView.findViewById(R.id.tv_talk_worker_name);
        skillTv = (TextView) rootView.findViewById(R.id.tv_talk_worker_skill);
        infoTv = (TextView) rootView.findViewById(R.id.tv_talk_worker_info);
        addressTv = (TextView) rootView.findViewById(R.id.tv_talk_worker_address);
        noYourselfRl = (RelativeLayout) rootView.findViewById(R.id.rl_talk_worker_no_yourself);
        talkToYouRl = (RelativeLayout) rootView.findViewById(R.id.rl_talk_worker_talk_to_you);
        doingForyouRl = (RelativeLayout) rootView.findViewById(R.id.rl_talk_worker_doing_for_you);
        busyRl = (RelativeLayout) rootView.findViewById(R.id.rl_talk_worker_busy);
        waitRl = (RelativeLayout) rootView.findViewById(R.id.rl_talk_worker_wait);
        cpd = Utils.initProgressDialog(TalkWorkerActivity.this, cpd);
    }

    private void initData() {
        talkWorkerPresenter = new TalkWorkerPresenter(this);
        toTalkWorkerBean = (ToTalkWorkerBean) getIntent().getSerializableExtra(IntentConfig.toTalkWorker);
        Utils.log(TalkWorkerActivity.this, "toTalkWorkerBean\n" + toTalkWorkerBean.toString());
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        iconIv.setOnClickListener(this);
        waitRl.setOnClickListener(this);
    }

    private void loadData() {
        cpd.show();
        talkWorkerPresenter.load(NetConfig.workerUrl + "?u_id=" + toTalkWorkerBean.getU_id() + "&fu_id=" + UserUtils.readUserData(TalkWorkerActivity.this).getId() + "&o_status=0,-3");
    }

    private void notifyData() {
        Picasso.with(TalkWorkerActivity.this).load(workerBean.getU_img()).placeholder(R.mipmap.person_face_default).error(R.mipmap.person_face_default).into(iconIv);
        String sex = workerBean.getU_sex();
        if (sex.equals("0")) {
            sexIv.setImageResource(R.mipmap.female);
        } else if (sex.equals("1")) {
            sexIv.setImageResource(R.mipmap.male);
        }
        String u_id = workerBean.getU_id();
        if (u_id.equals(UserUtils.readUserData(TalkWorkerActivity.this).getId())) {
            SHOW_STATE = NO_YOURSELF;
        } else {
            int relation = workerBean.getRelation();
            if (relation == 1) {
                int relationType = workerBean.getRelation_type();
                if (relationType == 1) {
                    SHOW_STATE = DOING_FOR_YOU;
                } else if (relationType == 0) {
                    SHOW_STATE = TALK_TO_YOU;
                }
            } else {
                String status = workerBean.getU_task_status();
                if (status.equals("1")) {
                    SHOW_STATE = BUSY;
                } else if (status.equals("0")) {
                    SHOW_STATE = WAIT;
                }
            }
        }
        refreshState();
        nameTv.setText(workerBean.getU_true_name());
        skillTv.setText(toTalkWorkerBean.getS_name());
        infoTv.setText(workerBean.getUei_info());
        addressTv.setText(workerBean.getUei_address());
        if (workerBean.getU_task_status().equals("0")) {
            statusIv.setImageResource(R.mipmap.worker_leisure);
        } else if (workerBean.getU_task_status().equals("1")) {
            statusIv.setImageResource(R.mipmap.worker_mid);
        }
        loadMap();
        cpd.dismiss();
    }

    private void refreshState() {
        switch (SHOW_STATE) {
            case NO_YOURSELF:
                noYourselfRl.setVisibility(View.VISIBLE);
                talkToYouRl.setVisibility(View.GONE);
                doingForyouRl.setVisibility(View.GONE);
                busyRl.setVisibility(View.GONE);
                waitRl.setVisibility(View.GONE);
                break;
            case TALK_TO_YOU:
                noYourselfRl.setVisibility(View.GONE);
                talkToYouRl.setVisibility(View.VISIBLE);
                doingForyouRl.setVisibility(View.GONE);
                busyRl.setVisibility(View.GONE);
                waitRl.setVisibility(View.GONE);
                break;
            case DOING_FOR_YOU:
                noYourselfRl.setVisibility(View.GONE);
                talkToYouRl.setVisibility(View.GONE);
                doingForyouRl.setVisibility(View.VISIBLE);
                busyRl.setVisibility(View.GONE);
                waitRl.setVisibility(View.GONE);
                break;
            case BUSY:
                noYourselfRl.setVisibility(View.GONE);
                talkToYouRl.setVisibility(View.GONE);
                doingForyouRl.setVisibility(View.GONE);
                busyRl.setVisibility(View.VISIBLE);
                waitRl.setVisibility(View.GONE);
                break;
            case WAIT:
                noYourselfRl.setVisibility(View.GONE);
                talkToYouRl.setVisibility(View.GONE);
                doingForyouRl.setVisibility(View.GONE);
                busyRl.setVisibility(View.GONE);
                waitRl.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void loadMap() {
        if (TextUtils.isEmpty(workerBean.getUcp_posit_y()) || workerBean.getUcp_posit_y().equals("null")) {
            workerBean.setUcp_posit_y("0");
        }
        if (TextUtils.isEmpty(workerBean.getUcp_posit_x()) || workerBean.getUcp_posit_x().equals("null")) {
            workerBean.setUcp_posit_x("0");
        }
        mapView.showScaleControl(false);
        mapView.showZoomControls(false);
        BaiduMap baiduMap = mapView.getMap();
        UiSettings settings = baiduMap.getUiSettings();
        settings.setAllGesturesEnabled(false);
        settings.setOverlookingGesturesEnabled(false);
        settings.setZoomGesturesEnabled(false);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.point_blue);
        LatLng latLng = new LatLng(Double.parseDouble(workerBean.getUcp_posit_y()), Double.parseDouble(workerBean.getUcp_posit_x()));
        OverlayOptions overlayOptions = new MarkerOptions().position(latLng).icon(bitmap);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, Float.parseFloat("19"));
        baiduMap.animateMapStatus(mapStatusUpdate);
        baiduMap.addOverlay(overlayOptions);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_talk_worker_return:
                finish();
                break;
            case R.id.iv_talk_worker_icon:
                Intent intent = new Intent(TalkWorkerActivity.this, PersonDetailActivity.class);
                intent.putExtra(IntentConfig.talkToDetail, workerBean.getU_id());
                startActivity(intent);
                break;
            case R.id.rl_talk_worker_wait:
                talkWorkerPresenter.check(NetConfig.taskBaseUrl + "?t_author=" + UserUtils.readUserData(TalkWorkerActivity.this).getId() + "&t_storage=0&t_status=0,1,5&skills=" + toTalkWorkerBean.getS_id());
                break;
        }
    }

    @Override
    public void loadSuccess(String json) {
        workerBean = DataUtils.getWorkerBeanList(json).get(0);
        Utils.log(TalkWorkerActivity.this, "workerBean\n" + workerBean.toString());
        handler.sendEmptyMessage(LOAD_SUCCESS);
    }

    @Override
    public void loadFailure(String failure) {
        handler.sendEmptyMessage(LOAD_FAILURE);
    }

    @Override
    public void checkSuccess(String json) {
        Utils.log(TalkWorkerActivity.this, json);
        if (DataUtils.getTaskBeanList(json).size() == 0) {
            handler.sendEmptyMessage(CHECK_NONE);
        } else {
            handler.sendEmptyMessage(CHECK_HAVE);
        }
    }

    @Override
    public void checkFailure(String failure) {

    }
}
