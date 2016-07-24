package com.example.mobliesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;

import com.example.mobliesafe.utils.SmsUtils.SmsJsonData.Sms;
import com.google.gson.Gson;

public class SmsUtils {

	/**
	 * @param str原字符串
	 * @return 特殊的字符串
	 */
	private static String convert2ts(String str) {
		String res = "";
		for (int i = 0; i < str.length(); i++) {
			res += convert2ts(str.charAt(i));
		}
		return res;
	}

	/**
	 * @param c
	 *            json格式字符
	 * @return 转义字符
	 */
	private static char convert2ts(char c) {
		// " ★ { 卍 } 卐 [ ◎ ] ¤ : ℗ , ✿

		char res = '\u0000';// 字符的默认
		switch (c) {
		case '"':
			res = '★';
			break;
		case '{':
			res = '卍';
			break;

		case '}':
			res = '卐';
			break;

		case '[':
			res = '◎';
			break;

		case ']':
			res = '¤';
			break;

		case ':':
			res = '℗';
			break;

		case ',':
			res = '✿';
			break;
		default:
			res = c;
			break;
		}

		return res;

	}

	/**
	 * @param str
	 *            特殊字符串
	 * @return 原字符串
	 */
	private static String convert2source(String str) {
		String res = "";
		for (int i = 0; i < str.length(); i++) {
			res += convert2source(str.charAt(i));
		}
		return res;
	}

	/**
	 * 
	 * @param c
	 *            json 格式字符
	 * @return 转义字符
	 */
	private static char convert2source(char c) {
		char res = '\u0000';
		// " ★ { 卍 } 卐 [ ◎ ] ¤ : ℗ , ✿
		switch (c) {
		case '★':
			res = '"';
			break;
		case '卍':
			res = '{';
			break;
		case '卐':
			res = '}';
			break;
		case '◎':
			res = '[';
			break;
		case '¤':
			res = ']';
			break;
		case '℗':
			res = ':';
			break;
		case '✿':
			res = ',';
			break;

		default:
			res = c;
			break;
		}
		return res;
	}
	
	public interface OnProgressListener{
		void show();
		void dismiss();
		void setMax(int max);
		void setProgress(int progress);
	}
	
	
	public static void smsBack(final Activity context,final OnProgressListener pb) {
		
		
		
		File file = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 挂载
			if (Environment.getExternalStorageDirectory().getFreeSpace() > 1024 * 1024 * 5) {
				file = new File(Environment.getExternalStorageDirectory()
						.getPath(), "smses.json");
			} else {
				// 空间不足
				throw new RuntimeException("剩余空间不足,请及时清理"); 
			}

		} else {
			throw new RuntimeException("sd卡未挂载 或 没有sd卡");
		}
		
		
		
		try {
			final PrintWriter out = new PrintWriter(file);
			out.print("{\"smses\":[");
			
			//
			Uri uri = Uri.parse("content://sms");
			final Cursor cursor = context.getContentResolver().query(uri,
					new String[] { "address", "date", "body", "type" }, null,
					null, null);
			
			pb.show();
			pb.setMax(cursor.getCount());
			//小技巧:避免声明为常量(不可改变值)  ,  避免静态变量(占空间)
			class PbData{
				 int progressNum;
			}
			final PbData pbData = new PbData();
			
		
			// 子线程中,可能耗时
			new Thread() {
				String sms = null;

				public void run() {

					while (cursor.moveToNext()) {
						// {"address":"132333","date":"322143432432","body":"hello","type":"1"}

						sms = "{";
						sms += "\"address\":\"" + cursor.getString(0) + "\"";
						sms += ",\"date\":\"" + cursor.getString(1) + "\"";
						sms += ",\"body\":\"" + convert2ts(cursor.getString(2))
								+ "\"";
						sms += ",\"type\":\"" + cursor.getString(3) + "\"}";

						if (cursor.isLast()) {
							sms += "]}";
						} else {
							sms += ",";
						}
						// 写到文件中
						out.println(sms);
						out.flush();
						
						pbData.progressNum++;
						
						SystemClock.sleep(500);
						
						context.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								pb.setProgress(pbData.progressNum);
							}
						});
				
						
						
					}

					out.close();
					cursor.close();
					
					context.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							pb.dismiss();
						}
					});

				};
			}.start();

		} catch (Exception e) {
			// can't reach
			throw new RuntimeException(e.getMessage());

		}

	}

	public static String stream2String(InputStream is) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String len = null;
		try {
			while ((len = br.readLine()) != null) {
				sb.append(len);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static void smsRestore(final Activity context,final OnProgressListener pb) {
		File file = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 挂载
			file = new File(
					Environment.getExternalStorageDirectory().getPath(),
					"smses.json");
			
			//文件不存在的话

		} else {
			throw new RuntimeException("sd卡未挂载 或 没有sd卡");
		}
		
		class DataProgress{
			int progress;
		}
		
		final DataProgress dataProgress = new DataProgress();
		
		
		//备份文件存在
		 try {
			//1.获取json数据
			 String smsJson = stream2String(new FileInputStream(file));
			//2.calss
			 
			 Gson gson = new Gson();
			final SmsJsonData jsonData = gson.fromJson(smsJson, SmsJsonData.class);
			
			pb.setMax(jsonData.smses.size());
			pb.show();
			
			
			//循环取短信 写短信
			
			final Uri uri = Uri.parse("content://sms");
			//耗时
			new Thread(){
				public void run() {
					for(Sms sms : jsonData.smses){
						//取一条写一条
						ContentValues values = new ContentValues();
						values.put("address", sms.address);
			System.out.println("address: "+sms.address);			
						values.put("body", convert2source(sms.body));
		System.out.println("body: "+ convert2source(sms.body));	
						//!!!!!!!数据类型!!!!
						values.put("date", Long.parseLong(sms.date));
	System.out.println("date: "+ Long.parseLong(sms.date));	
						values.put("type", Integer.parseInt(sms.type));
	System.out.println("type: "+Integer.parseInt(sms.type));	
						context.getContentResolver().insert(uri, values);
	System.out.println("success!!!!!!!!!!!!");					
						
						SystemClock.sleep(500);
						
						context.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								pb.setProgress(dataProgress.progress++);
							}
						});
						
					}
					
					context.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							pb.dismiss();
						}
					});
					
				};
			}.start();
		 
		 
		 } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public class SmsJsonData{
		public List<Sms> smses;
		
		public class Sms{
			public String address;
			public String body;
			public String date;
			public String type;
		}
	}


}
