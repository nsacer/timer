package utils;

import android.text.TextUtils;

import java.math.BigDecimal;

public class MathUtil {
	/**
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2)
				+ Math.abs(y1 - y2) * Math.abs(y1 - y2));
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static double pointTotoDegrees(double x, double y) {
		return Math.toDegrees(Math.atan2(x, y));
	}
	
	public static boolean checkInRound(float sx, float sy, float r, float x,
			float y) {
		return Math.sqrt((sx - x) * (sx - x) + (sy - y) * (sy - y)) < r;
	}

	/**
	 * 设置显示对应小数点位数之后的数值
	 * @return 23.02
	 */
	public static String getStrByPrecisionUp(String sNumber, int precision) {

		if (!TextUtils.isEmpty(sNumber)) {

			BigDecimal temp = new BigDecimal(sNumber);

			return temp.setScale(precision, BigDecimal.ROUND_HALF_UP).toString();
		}

		return "";
	}

	public static String getStrByPrecisionUp(float fNumber, int precision) {

        return getStrByPrecisionUp(String.valueOf(fNumber), precision);
	}
}