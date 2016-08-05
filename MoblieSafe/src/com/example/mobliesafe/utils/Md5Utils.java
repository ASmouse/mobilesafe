package com.example.mobliesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author jacksonCao
 * @data 2016-7-12
 * @desc MD5加密

 * @version  $Rev: 16 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-08-05 17:55:44 +0800 (周五, 05 八月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/utils/Md5Utils.java $
 */
public class Md5Utils {
	
	
	public static String encodeFile(String path){
		StringBuilder sb = new StringBuilder();
		File file = new File(path);
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(file);
			int len = -1;
			byte[] bys= new byte[10*1024];
			while((len = fis.read(bys))!=-1){
				md.update(bys, 0, len);
			}
			fis.close();
			byte[] digest = md.digest();
			for (byte b : digest) {
				int n = b & 0x000000ff;
				String s = Integer.toHexString(n);
				
				if(s.length() == 1){
					s ='0'+s;
				}
				sb.append(s);
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return sb+"";
	}
	
	

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
