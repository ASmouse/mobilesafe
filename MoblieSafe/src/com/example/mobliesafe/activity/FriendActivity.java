package com.example.mobliesafe.activity;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobliesafe.R;
import com.example.mobliesafe.dao.ContactsDao;
import com.example.mobliesafe.domain.ContactBean;
import com.example.mobliesafe.utils.MyContains;

/**
 * @author jacksonCao
 * @data 2016-7-14
 * @desc 显示所有好友的信息界面,耗时加载的标准结构[1]...[6]
 * 			ListActivity自动封装了ListView组件(不用findViewByid()了)

 * @version  $Rev: 9 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-07-15 13:16:31 +0800 (周五, 15 七月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/activity/FriendActivity.java $
 */
public class FriendActivity extends ListActivity{
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private ListView lv_datas;
	
	
	//[1]. 定义一个容器 并初始化
	//默认初始化(空初始化),防止空指针异常!!!!!!!!
	private List<ContactBean> mDatas = new ArrayList<ContactBean>();	//默认数组大小10个
	private MyAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//显示所有的好友
		//1. 数据  好友信息(数据多,耗时加载的标准结构)
		//2. 定义适配
		//3. 给ListView设置适配器
		
		initView();
		
		initData();
		
		initEvent();
		
	}
	
	private void initEvent() {
		//给listview添加item点击事件
		lv_datas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 获取点击item的数据
				//会自动调用适配器的getItem()
				//这是最准确的写法,以后详细讲(复杂的时候用这个:软件管家)
				ContactBean bean = (ContactBean) lv_datas.getItemAtPosition((int)lv_datas.getItemIdAtPosition(position));
				Intent intent = new Intent(FriendActivity.this,Setup3Activity.class); 
				intent.putExtra(MyContains.SAFENUMBER, bean.getPhone());
				//保存数据
				setResult(RESULT_OK,intent);
				//关闭自己
				finish();
			}
		});
		
	}

	//[6]. handler更新数据控制界面的显示
	private Handler mHandler = new Handler(){
		private ProgressDialog pd;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				
				lv_datas.setVisibility(View.GONE);
				pd = new ProgressDialog(FriendActivity.this );
				pd.setTitle("真在玩命加载ing...");
				pd.show();
				
				break;
			case FINISH:
				//关闭对话框
			pd.dismiss();
			lv_datas.setVisibility(View.VISIBLE);
			//更新数据!!!!!!!!数据有了
			mAdapter.notifyDataSetChanged();//通知界面刷新数据
				
				break;
			default:
				break;
			}
		};
	};
	
	//一想到子线程,马上想到Handler ,他俩是好哥们
	//[5]. 子线程更新数据(耗时操作)
	private void initData() {
		new Thread(){
			public void run() {
				//1.提醒用户正在加载数据
				//最简单的写法:!!!!!!!!!!!!!相当于两句话:
				//Message msg = mHandler.obtainMessage(LOADING);
				//mHandler.sendMessage(msg);
				mHandler.obtainMessage(LOADING).sendToTarget();
				
				
				//2.加载数据,可能耗时!!!!!!!!!,需要读取数据库等等
				mDatas = ContactsDao.getContacts(getApplicationContext());
				
				//3.数据加载完毕
				//发送数据加载完成的消息
				mHandler.obtainMessage(FINISH).sendToTarget();
			};
		}.start();
	}
	
	
	//[3]. 把适配器和ListView绑定
	private void initView() {
		lv_datas = getListView();
		mAdapter = new MyAdapter();
		//设置适配器
		lv_datas.setAdapter(mAdapter);
	}
	
	
	//[2]. 定义适配器
	private class MyAdapter extends BaseAdapter{
		//系统调用getView()之前先调用此方法
		@Override
		public int getCount() {
			// 返回数据的大小
			 
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
			//获取适配器的数据!!!!!!!
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		
		// [4]. 适配器的getView()方法  缓存
			//1.自定义ViewHolder
			//2.第一次保存findViewById获取的view子组件
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			//缓存View
			if(convertView == null){
				//没有缓存
				convertView  = View.inflate(getApplicationContext(), R.layout.item_contacts_lv, null);
				holder  = new ViewHolder();
				//把子view设置给ViewHolder
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_item_contact_name);
				holder.tv_phone  = (TextView) convertView.findViewById(R.id.tv_item_contact_number);
				//避免重复findViewByID
				//设置标记给convertView
				convertView.setTag(holder);
			
			}else{
				//取出标记
				holder  = (ViewHolder) convertView.getTag();
			}
			//获取数据
			ContactBean cb = mDatas.get(position);
			holder.tv_name.setText(cb.getName());
			holder.tv_phone.setText(cb.getPhone());
			
			return convertView;
		}
		//对View进行的封装
		private class ViewHolder{
			TextView tv_name;
			TextView tv_phone;
		}
		
	}
}
