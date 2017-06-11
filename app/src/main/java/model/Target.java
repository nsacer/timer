package model;

import org.xutils.DbManager;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.ex.DbException;
import java.util.List;
import utils.DateUtil;

/**
 * Created by zpf on 2016/12/22.
 *
 */

@Table(name = "target")
public class Target {

    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "moneyTotal", property = "NOT NULL")
    private String moneyTotal;

    @Column(name = "moneySurplus", property = "NOT NULL")
    private String moneySurplus;

    @Column(name = "moneyDaily", property = "NOT NULL")
    private String moneyDaily;

    @Column(name = "daysTotal", property = "NOT NULL")
    private String daysTotal;

    @Column(name = "daysSurplus", property = "NOT NULL")
    private String daysSurplus;

    @Column(name = "dateStart", property = "NOT NULL")
    private String dateStart;

    @Column(name = "dateEnd", property = "NOT NULL")
    private String dateEnd;

    @Column(name = "inProgress", property = "NOT NULL")
    private boolean inProgress;

    public Target() {}

    public Target(String moneyTotal, String dateStart, String dateEnd) {

        this.moneyTotal = this.moneySurplus = moneyTotal;
        this.daysTotal  = this.daysSurplus =
                String.valueOf(DateUtil.getInstance().getCurrentDays(dateStart, dateEnd));
        this.moneyDaily = String.valueOf(Float.valueOf(this.moneyTotal) / Float.valueOf(this.daysTotal));
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.inProgress = Boolean.TRUE;
    }

    public List<DailyPay> getDailyPayList(DbManager db) throws DbException {
        return db.selector(DailyPay.class).where("targetId", "=", this.id).findAll();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMoneyTotal() {
        return moneyTotal;
    }

    public void setMoneyTotal(String moneyTotal) {
        this.moneyTotal = moneyTotal;
    }

    public String getMoneySurplus() {
        return moneySurplus;
    }

    public void setMoneySurplus(String moneySurplus) {
        this.moneySurplus = moneySurplus;
    }

    public String getMoneyDaily() {
        return moneyDaily;
    }

    public void setMoneyDaily(String moneyDaily) {
        this.moneyDaily = moneyDaily;
    }

    public String getDaysTotal() {
        return daysTotal;
    }

    public void setDaysTotal(String daysTotal) {
        this.daysTotal = daysTotal;
    }

    public String getDaysSurplus() {
        return daysSurplus;
    }

    public void setDaysSurplus(String daysSurplus) {
        this.daysSurplus = daysSurplus;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public boolean getInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public String toString() {

        return "Target{" +
                "id=" + id +
                ", moneyTotal=" + moneyTotal +
                ", moneySurplus=" + moneySurplus +
                ", moneyDaily=" + moneyDaily +
                ", daysTotal=" + daysTotal +
                ", daysSurplus=" + daysSurplus +
                ", dateStart=" + dateStart +
                ", dateEnd=" + dateEnd +
                ", inProgress=" + inProgress;
    }
}
