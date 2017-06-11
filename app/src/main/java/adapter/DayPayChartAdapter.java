package adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import java.util.ArrayList;
import java.util.List;
import model.DailyPay;
import model.Target;
import utils.DateUtil;
import zhao.pary.timer.R;

/**
 * Created by zpf on 2017/1/3.
 */

public class DayPayChartAdapter extends RecyclerView.Adapter<DayPayChartAdapter.DayPayViewHolder> {

    private DbManager dbManager;
    private Target target;
    private List<DailyPay> dailyPays = new ArrayList<>();

    public DayPayChartAdapter(DbManager dbManager, Target target) {
        this.dbManager = dbManager;
        this.target = target;
        List<DailyPay> dailyPayList = selectDailyPaysFromDB(dbManager, target.getId());
        addDailyDate(target, dailyPayList);
        if (!dailyPayList.isEmpty()) {
            dailyPays = dailyPayList;
        }
    }

    @Override
    public DayPayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_pay_chart, parent, false);
        return new DayPayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DayPayViewHolder holder, int position) {

        DailyPay dailyPay = dailyPays.get(position);

        if(!TextUtils.isEmpty(dailyPay.getDailyDate()))
            holder.tvDate.setText(dailyPay.getDailyDate());

        holder.tvExpense.setText(String.valueOf(dailyPay.getDailyExpense()));

        if(target != null) {

            float dailyTotal = Float.valueOf(target.getMoneyTotal()) /
                    Integer.valueOf(target.getDaysTotal());
            int progress = (int) (dailyTotal / 50 * dailyPay.getDailyExpense());
            holder.progressBar.setProgress(progress);
        }

    }

    @Override
    public int getItemCount() {
        return dailyPays.size();
    }

    class DayPayViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate, tvExpense;
        private ProgressBar progressBar;

        DayPayViewHolder(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvExpense = (TextView) itemView.findViewById(R.id.tv_expense);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

    /**
     * 查找Target 的id对应的DailyPay集合
     */
    private List<DailyPay> selectDailyPaysFromDB(DbManager dbManager, int targetId) {

        List<DailyPay> dailyPays = null;
        try {
            dailyPays = dbManager.selector(DailyPay.class)
                    .where("targetId", "=", targetId).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dailyPays;
    }

    /**
     * DailyPay添加dailyDate字段信息
     */
    private void addDailyDate(Target target, List<DailyPay> dailyPays) {

        if (dailyPays.isEmpty())
            return;

        long till = DateUtil.getInstance().getTimeInMillisFromString(target.getDateStart());
        for (int i = 0; i < dailyPays.size(); i++) {

            DailyPay dailyPay = dailyPays.get(i);
            till += 1000 * 60 * 60 * 24 * i;
            if (dailyPay.getDailyDate() != null)
                return;
            dailyPay.setDailyDate(DateUtil.getInstance().getDateFormatShort(till));
            dailyPays.set(i, dailyPay);
            try {
                dbManager.update(dailyPay);
            } catch (DbException e) {
                e.printStackTrace();
            }

        }
    }
}
