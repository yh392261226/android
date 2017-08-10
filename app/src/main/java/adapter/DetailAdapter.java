package adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gjzg.R;

import java.util.List;

import bean.Detail;

/**
 * 创建日期：2017/8/9 on 10:55
 * 作者:孙明明
 * 描述:明细适配器
 */

public class DetailAdapter extends BaseAdapter {

    private Context context;
    private List<Detail> list;
    private ViewHoler holer;

    public DetailAdapter(Context context, List<Detail> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_detail, null);
            holer = new ViewHoler(convertView);
            convertView.setTag(holer);
        } else {
            holer = (ViewHoler) convertView.getTag();
        }
        Detail detail = list.get(position);
        if (detail != null) {
            holer.titleTv.setText(detail.getTitle());
            holer.balanceTv.setText(detail.getBalance());
            holer.timeTv.setText(detail.getTime());
            holer.desTv.setText(detail.getDes());
        }
        return convertView;
    }

    private class ViewHoler {

        private TextView titleTv, balanceTv, timeTv, desTv;

        public ViewHoler(View itemView) {
            titleTv = (TextView) itemView.findViewById(R.id.tv_item_detail_title);
            balanceTv = (TextView) itemView.findViewById(R.id.tv_item_detail_balance);
            timeTv = (TextView) itemView.findViewById(R.id.tv_item_detail_time);
            desTv = (TextView) itemView.findViewById(R.id.tv_item_detail_des);
        }
    }
}