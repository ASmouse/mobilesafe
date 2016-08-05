package com.example.mobliesafe.utils;

import java.io.UnsupportedEncodingException;

/**
 * @author jacksonCao
 * @data 2016-7-25
 * @desc 字符串加密算法

 * @version  $Rev: 16 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-08-05 17:55:44 +0800 (周五, 05 八月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/utils/EncodeUtils.java $
 */
public class EncodeUtils {

	/**
	 * @param str
	 * @param seed 
	 * @return
	 */
	public static String encode(String str,byte seed){
		String res = "";
		
		try {
			byte[] bytes = str.getBytes("gbk");
			for (int i = 0 ;i<bytes.length; i++) {
				 bytes[i]= (byte) (bytes[i] ^ seed);
			}
			return new String(bytes,"gbk");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
