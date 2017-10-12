package invite.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gjzg.R;

public class InviteActivity extends AppCompatActivity implements View.OnClickListener {

    private View rootView;
    private RelativeLayout returnRl;
    private TextView inviteTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_invite, null);
        setContentView(rootView);
        initView();
        setListener();
    }

    private void initView() {
        initRootView();
    }

    private void initRootView() {
        returnRl = (RelativeLayout) rootView.findViewById(R.id.rl_invite_return);
        inviteTv = (TextView) rootView.findViewById(R.id.tv_invite);
    }

    private void setListener() {
        returnRl.setOnClickListener(this);
        inviteTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_invite_return:
                finish();
                break;
            case R.id.tv_invite:
                invite();
                break;
        }
    }

    private void invite() {
        Log.e("Invite", "invite");
//        String text = "123";
//        WXTextObject textObj = new WXTextObject();
//        textObj.text = text;
//
//        WXMediaMessage msg = new WXMediaMessage();
//        msg.mediaObject = textObj;
//        msg.description = text;
//
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = String.valueOf(System.currentTimeMillis());
//        req.message = msg;
//        req.scene = SendMessageToWX.Req.WXSceneSession;
//
//        IWXAPI api = WXAPIFactory.createWXAPI(this, "wx88a7414f850651c8", true);
//        api.sendReq(req);
    }
}