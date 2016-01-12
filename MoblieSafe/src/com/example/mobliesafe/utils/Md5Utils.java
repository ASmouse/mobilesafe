package com.example.mobliesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author jacksonCao
 * @data 2016-7-12
 * @desc MD5加密

 * @version  $Rev: 7 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-07-12 20:02:45 +0800 (周二, 12 七月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/utils/Md5Utils.java $
 */
public class Md5Utils {
	
	

	/**加密的方法
	 * @param str 需要加密的字符串
	 * @return 加密后的MD5值
	 */
	public static String encode(String str){
		//javase API
		String res = "";
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			//MD5加密后的字节数组
			byte[] digest = md.digest(str.getBytes());
			//字节数组转换成字符串!!!! 
			//1byte == 8bit =2个16进制数
			for(byte b:digest){ //数组 Iterale 是所有容器的根
				//把一个字节转换成16进制数Integer.toHexString().想到打印对象
				//过滤掉int类型的前三个字节
				int d= b & 0x000000ff; 
				String s = Integer.toHexString(d);
				//把所有的十六进制数不齐成2位数 
				if(s.length() == 1){
					s = "0" +s;
				}
				
				res +=s;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return res;
		
	}
}
