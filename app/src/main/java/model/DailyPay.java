package model;

import org.xutils.DbManager;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.ex.DbException;

import java.util.ArrayList;

/**
 * Created by zpf on 2016/12/22.
 *
 */

@Table(name = "dailyPay")
public class DailyPay {

    @Column(name = "id", isId = true)
    private int id;

    /**
     * 外键表id
     */
    @Column(name = "targetId")
    private int targetId;

    @Column(name = "dailyTotal")
    private float dailyTotal;

    @Column(name = "dailyExpense")
    private float dailyExpense;

    @Column(name = "dailySurplus")
    private float dailySurplus;

    @Column(name = "dailyDate")
    private String dailyDate;

    public DailyPay() {}

    public DailyPay(int targetId, float dailyTotal, float dailyExpense) {

        this.targetId = targetId;
        this.dailyTotal = dailyTotal;
        this.dailyExpense = dailyExpense;
        this.dailySurplus = this.dailyTotal - this.dailyExpense;
    }

    public Target getTarget(DbManager db) throws DbException {
        return db.findById(Target.class, targetId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public float getDailyTotal() {
        return dailyTotal;
    }

    public void setDailyTotal(float dailyTotal) {
        this.dailyTotal = dailyTotal;
    }

    public float getDailyExpense() {
        return dailyExpense;
    }

    public void setDailyExpense(float dailyExpense) {
        this.dailyExpense = dailyExpense;
    }

    public float getDailySurplus() {
        return dailySurplus;
    }

    public void setDailySurplus(float dailySurplus) {
        this.dailySurplus = dailySurplus;
    }

    public String getDailyDate() {
        return dailyDate;
    }

    public void setDailyDate(String dailyDate) {
        this.dailyDate = dailyDate;
    }

    public String toString() {

        return "DailyPay{" +
                "id=" + id +
                ", targetId=" + targetId +
                ", dailyTotal=" + dailyTotal +
                ", dailyExpense=" + dailyExpense +
                ", dailySurplus=" + dailySurplus +
                ", dailyDate=" + dailyDate;
    }
}
