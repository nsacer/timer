package model;

import android.text.TextUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by zpf on 2016/12/30.
 */

@Table(name = "dailyPayBills")
public class DailyPayBills {

    @Column(name = "id", isId = true)
    private long id;

    @Column(name = "dailyPayId")
    private long dailyPayId;

    @Column(name = "payDetail")
    private String payDetail;

    public DailyPayBills(){}

    public DailyPayBills(long dailyPayId, String payDetail) {

        if(dailyPayId < 0 || TextUtils.isEmpty(payDetail))
            return;

        this.dailyPayId = dailyPayId;
        this.payDetail = payDetail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDailyPayId() {
        return dailyPayId;
    }

    public void setDailyPayId(long dailyPayId) {
        this.dailyPayId = dailyPayId;
    }

    public String getPayDetail() {
        return payDetail;
    }

    public void setPayDetail(String payDetail) {
        this.payDetail = payDetail;
    }

    public String toString() {

        return "DailyPayBills {" +
                "id=" + id +
                ", dailyPayId=" + dailyPayId +
                ", payDetail=" + payDetail;
    }
}
