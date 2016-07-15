package com.example.mobliesafe.location;

public class CaculateRealPosition {

	public static String getRealLocation(double latitude, double longitude) {
		String res = "";
		try {

			ModifyOffset mo = ModifyOffset
					.getInstance(CaculateRealPosition.class.getClassLoader()
							.getResourceAsStream("axisoffset.dat"));

			PointDouble pt = new PointDouble(latitude, longitude);
			PointDouble newpt = mo.s2c(pt);
			res = "\n\n真实的坐标:\n" + "( " + newpt.x + " , " + newpt.y + ")";

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res="真实坐标解析失败!";
		}
		return res;
	}

}
