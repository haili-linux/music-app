package com.my.musicPlayer;

import android.*;
import android.app.*;
import android.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;


class MyAdapter extends ArrayAdapter<String>
{
	public static int ISPLAYING_ID=-1;
	public MyAdapter(Context context, String[] values) 
	{
		super(context, R.layout.entry, values);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		return  getmyView(position,parent);
		
	}
	
	
	public View getmyView(int position, ViewGroup parent)
	{
		final int post=position;
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.entry, parent, false);
		TextView numberTextv = view.findViewById(R.id.entryTextView1);
		TextView textviee=(TextView)view.findViewById(R.id.entryTextView_musiceName);
		TextView singerTextview=(TextView)view.findViewById(R.id.entryTextView_singer);
		TextView setting_textview=(TextView)view.findViewById(R.id.entryTextView_setting);
		setting_textview.setOnClickListener(new OnClickListener()
		{
			@Override
		    public void onClick(View p1)
		    {
		         doialist(getItem(post));
				 MainActivity.Ontouch_list_value=post;
		    }
		});
		
		String text = getItem(position);
		String singer="",musicName="";
		int a1=text.indexOf("-");
		int a2=text.lastIndexOf(".");
		int a3=text.lastIndexOf("/");
		if(a3!=-1 && a1!=-1)
		{
			singer=text.substring(a3+1,a1);
			singerTextview.setText(singer);
		}
		else
		{
		    singerTextview.setText("灵魂歌手");
		}
		if(a1!=-1 && a2!=-1)
		 {
			musicName=text.substring(a1+1,a2);
		 }
		 else
		 {
			musicName=text.substring(a3+1,a2);
		 }
	
		textviee.setText(musicName);
		
		numberTextv.setText(position+"");
		if(ISPLAYING_ID==position) 
		{
			LinearLayout mainlayout=(LinearLayout)view.findViewById(R.id.entryMainLinearLayout);
			mainlayout.setBackgroundResource(R.drawable.image_blue_30touming);
		//	Toast.makeText(getContext(),"一会会",1000).show();
		}
		return view;
	}
	
	
	
	
	public void doialist(String title){
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 点击事件

				}
			});
		/*builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});*/
		builder.setTitle(title);
		builder.setView(R.layout.doial_voulem_setting);
		builder.show();
	}
	
	public void send_Ontouch_ListId(int id){//向服务传递文件路径
		
	
	 }
  }  
