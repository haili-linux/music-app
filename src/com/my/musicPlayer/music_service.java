package com.my.musicPlayer;

import android.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
import java.io.*;
import android.widget.*;


public class music_service extends Service
{

    private IBinder myBinder = new MyBinder();//与activity通信
	MediaPlayer mPlayer=new MediaPlayer();
	boolean hsGechi=false;
	
	@Override
	public void onCreate()
	{
	   // TODO: Implement this method
	     super.onCreate();
	 /*    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
			{   //播放完时调用
				@Override
				public void onCompletion(MediaPlayer p1)
				{
					Toast.makeText(music_service.this,"播放完毕",5000).show();
				}
		 }); */
		 ser_t();
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		// TODO: Implement this method
		super.onStart(intent, startId);
	//	Toast.makeText(music_service.this,"mmm",1000).show();
	    		
	}
    

	@Override
	public IBinder onBind(Intent p1)
	{
		// TODO: Implement this method
		return myBinder;
	}

	@Override
	public void onDestroy()
	{
		// TODO: Implement this method
		MainActivity.ISPLAYING=false;
		super.onDestroy();
	}
	
	private Handler mhandler=new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 1:
					Toast.makeText(music_service.this,"线程正在运行",1000).show();
			    break;
			}
		}
		  
	};
	
	public class MyBinder extends Binder{//和Mainactivity通信
        @Override
        protected boolean onTransact(int msg1, Parcel data, Parcel reply, int msg2) throws RemoteException {
            switch (msg1){
                case 1:
                    //播放 处理服务的函数
					mPlayer.start();
					MainActivity.ISPLAYING=true;				
					if(!sendProgress_thread_RUN_STOP)
					{
						sendProgress_thread_RUN_STOP=true;
						send_Music_Progress();
					}		
                    break;
					
                case 2:
                    //停止
					mPlayer.pause();
					MainActivity.ISPLAYING=false;
					sendProgress_thread_RUN_STOP=false;				
                    break;
					
                case 3:
                    //切歌
					 hsGechi=!getsave_music_gechi(MainActivity.musicPath[ MainActivity.ISPLAYING_MUSIC_ID]).equals("");
					 Gechi_line_n=-1;
					 if(hsGechi)
					  {    
					    if(gechi_exists(MainActivity.ISPLAYING_MUSIC_ID)) 
						      Gechi0(0);//准备歌词
						  else
						  {
							 // save_muise_gechi(MainActivity.ISPLAYING_MUSIC_ID,"");
							 remove_music_gechi(MainActivity.musicPath[MainActivity.ISPLAYING_MUSIC_ID]);
							  Toast.makeText(music_service.this,"歌词文件丢失!",2000).show();
						  }
					  }
                    try{
						mPlayer.release();//销毁原来的
						mPlayer=new MediaPlayer();
						mPlayer.setDataSource(getFilePath_Playing());
						mPlayer.prepare();	 
					   if(!sendProgress_thread_RUN_STOP)
					   {
						   sendProgress_thread_RUN_STOP=true;
						   send_Music_Progress();
					   }
					   mPlayer.start();
				   	  }catch (IOException e){     }
					  MainActivity.ISPLAYING=true;
					  int time_length=mPlayer.getDuration();
					  MainActivity.Length_MUSIC_Int = time_length; //获取时长
					  MainActivity.Length_MUSIC_String=make_time(time_length)[0];
					  ser(msg2,true);
					  icon_id=msg2;
                    break;
					
				case 4:
					sendProgress_thread_RUN_STOP=false;		
					mPlayer.release();
					//myBinder = new MyBinder();//与activity通信
				    mPlayer=new MediaPlayer();
					hsGechi=false;
					Gechi_is_ok_Service=false;	
					len=-1;
					Gechi_line_n=-1;			
					break;
						
            }
            return super.onTransact(msg1, data, reply, msg2);
        }
    }
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				  case 1:showGechi(msg.arg1);  break;
				  case 2:ser(icon_id,MainActivity.ISPLAYING);
					     lastPlaying=MainActivity.ISPLAYING;
				//     Toast.makeText(music_service.this,"hhhhbh",100).show();
				  break;
				case 3: msgForA2=false; mPlayer.seekTo(progress);
				}
		} 
	};
	
	public void sendMsg(int What,int date){
		 Message msg=new Message();
		 msg.what=What;
		 msg.arg1= date;
		 handler.sendMessage(msg);
	}
	/*public void player(String filepath){
		try{
			mPlayer.setDataSource(filepath);
			mPlayer.prepare();
			mPlayer.setLooping(true);
		}catch(Exception e){  }
	}*/
//	int total_time=-1;//播放进度
	int icon_id=-1;
	boolean lastPlaying=true;//用于更新前台服务
	static boolean sendProgress_thread_RUN_STOP=false;
	public void send_Music_Progress(){
		new Thread(new Runnable()
			{
				@Override
				public void run()
				{	
					while(sendProgress_thread_RUN_STOP)
					{  
					    int total_time=mPlayer.getCurrentPosition(); //mplayer为空时会崩溃
						//if(hsGechi)  Gechi0(total_time); 19日20时00分修改
						if(Gechi_is_ok_Service && hsGechi) sendMsg(1, total_time);
						String Now_time[]=make_time(total_time);
						MainActivity.Progress_MUSIC_Int = total_time;
					    MainActivity.Progress_MUSIC_String=Now_time[0];
						A2.Now_time_ms=Now_time[1];	
						if(sendProgress_thread_RUN_STOP)
					        try{ Thread.sleep(98);  }catch(Exception e){  }
					}
				}		  
			}).start();    //传递进度用
	}
	
	static boolean msgForA2=false;
	static int progress=0;
	public void ser_t(){
		new Thread(new Runnable()
			{
				@Override
				public void run()
				{	
				     while(true){
						 if(MainActivity.ISPLAYING!=lastPlaying&&!MainActivity.ISPLAYING_musicName.equals("")) sendMsg(2,0);
						 if(msgForA2) sendMsg(3,0);
						 try{ Thread.sleep(100);}catch(Exception e){}
					 }
			    }
			}).start();
	}
	
	
	//######歌词######//######歌词######//######歌词######//######歌词######
	static GeChi Gechi;
	static boolean Gechi_is_ok_Service=false;
	public void Gechi0(int time){
		
		if(time<500)
			if(Gechi_line_n!=0)
				if(!getsave_music_gechi(MainActivity.musicPath[MainActivity.ISPLAYING_MUSIC_ID]).equals(""))
				{
					makeGetchi(MainActivity.ISPLAYING_MUSIC_ID);
					Gechi_is_ok_Service=true;
				}
				else
				{
					Gechi_is_ok_Service=false;
				}
	}
	
	
	public  void makeGetchi(int music_id){
		Gechi=myDate.ReadGechi(getsave_music_gechi(MainActivity.musicPath[music_id]),getsave_music_dt(MainActivity.musicPath[ music_id]));
		Gechi_line_n=0;
		len = Gechi.len;
	}
	
	int len=-1;
	private int Gechi_line_n=-1;
    public void showGechi(int Now_time){
		int d=Now_time%100;
		int newTime=Now_time-d;
	//	Toast.makeText(music_service.this,""+newTime,500).show();
		if(d>50) newTime+=100;
		if(Gechi_line_n !=-1)
		{   
			if(newTime==Gechi.time[Gechi_line_n])
			{
				if(MainActivity.gechi_is_full)
				{
					A2.gechi2 = Gechi.word[Gechi_line_n];
				/*	if(Gechi_line_n>0) A2.gechi1 =A2.gechi1+"\n"+Gechi.word[Gechi_line_n-1];
					//if(Gechi_line_n<len-2)
					//	A2.gechi3 = Gechi.word[Gechi_line_n+1]+"\n"+A2.gechi3;
					if(Gechi_line_n<=last6)
						A2.gechi3 = Gechi.word[Gechi_line_n+1]+"\n"+Gechi.word[Gechi_line_n+2]+"\n"+Gechi.word[Gechi_line_n+3]+"\n"+Gechi.word[Gechi_line_n+4]+"\n"
							+Gechi.word[Gechi_line_n+5];
					   else if(Gechi_line_n==last5)
						       A2.gechi3 = Gechi.word[Gechi_line_n+1]+"\n"+Gechi.word[Gechi_line_n+2]+"\n"+Gechi.word[Gechi_line_n+3]
						                 +"\n"+Gechi.word[Gechi_line_n+4];
					          else if(Gechi_line_n==last4)
						                A2.gechi3 = Gechi.word[Gechi_line_n+1]+"\n"+Gechi.word[Gechi_line_n+2]+"\n"+Gechi.word[Gechi_line_n+3];
								     else if(Gechi_line_n==last3)
						                        A2.gechi3 = Gechi.word[Gechi_line_n+1]+"\n"+Gechi.word[Gechi_line_n+2];
											else if(Gechi_line_n==last2)
						                            A2.gechi3 = Gechi.word[Gechi_line_n+1];
													else A2.gechi3=""; */
					int next=Gechi_line_n+1;
					if(Gechi_line_n>0) A2.gechi1=Gechi.word[Gechi_line_n-1]; 
					if(next<len) A2.gechi3=Gechi.word[next]; else A2.gechi3=""; 								
				   for(int i=Gechi_line_n-2;i>=Gechi_line_n-8&&i>=0;i--)
				        {   
						  //  if(i<0) break;
							A2.gechi1= Gechi.word[i]+"\n"+A2.gechi1;
						}
						
					for(int i=Gechi_line_n+2;i<=Gechi_line_n+7&&i<len;i++)
					{
						//if(i>=len) break;
						if(Gechi.word[i]!=null)
						    A2.gechi3=A2.gechi3+"\n"+ Gechi.word[i];
						else break;
					}
						
			        if(Gechi_line_n<len-1)
						   Gechi_line_n++; 
						else
						{
						   Gechi_is_ok_Service=false;
						   A2.gechi3="";
						 }
				}
				else
				{
				   //Toast.makeText(music_service.this,"",500).show();
			       A2.gechi2 = Gechi.word[Gechi_line_n];
				   if(Gechi_line_n>0) A2.gechi1 = Gechi.word[Gechi_line_n-1];
				   if(Gechi_line_n<len-2)
					       A2.gechi3 = Gechi.word[Gechi_line_n+1];
					  else
					       A2.gechi3="";
			        if(Gechi_line_n<len-1)
				        {  Gechi_line_n++;  }
			     	else
						{
					     Gechi_is_ok_Service=false; 
					     A2.gechi1="";
					     A2.gechi2="";
					     A2.gechi3="";
				       } 
				  }
			}
			else 
			{
				 if(newTime>Gechi.time[Gechi_line_n])
				   {//如果现在时间已经超过歌词，重新定位歌词
					   for(int i=Gechi_line_n+1; i<Gechi.len; i++)
						   if(newTime==Gechi.time[i])
						   {
							   A2.gechi2 = Gechi.word[i];
							   Gechi_line_n=i+1;
							   //  tip();
							   break;
					       }		
				   }
			}
		}
		else
		{ 
			for(int i=0; i<Gechi.len; i++)
				if(newTime==Gechi.time[i])
				  {
				    A2.gechi2 = Gechi.word[i];
					Gechi_line_n=i+1;
					//  tip();
					break;
				  }		
		}  
	}
	
    public boolean gechi_exists(int id){
		 return new File(getsave_music_gechi(MainActivity.musicPath[id])).exists();
	}
	
	/*public String getsave_music_gechi(String i){//获取歌词文件路径
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		return shared.getString(i+"2323247730_getchi"+MainActivity.Music_item_Name,"");
	}*/
	
	public int getsave_music_dt(String i){//获取歌词文件路径
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		return shared.getInt(i+"2323247730_getchi_dt"+MainActivity.Music_item_Name,0);
	}
	
	public String getsave_music_gechi(String id){//获取歌词文件路径
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		return shared.getString(id+"2323247730_getchi"+MainActivity.Music_item_Name,"");
	}
	public void save_muise_gechi(int id,String path){//保存歌曲歌词文件
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putString(String.valueOf(id+"2323247730_getchi"+MainActivity.Music_item_Name), path);
		editor.commit();
	}
	
	public void remove_music_gechi(String name){  //移除歌词
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		SharedPreferences.Editor editor=shared.edit();
		File file=new File(shared.getString(name+"2323247730_getchi"+MainActivity.Music_item_Name,"6866d8386fkdkskdkd"));
		//Toast.makeText(A2.this,file.toString(),1000).show();
		if(file.exists()) file.delete();
		editor.remove(name+"2323247730_getchi"+MainActivity.Music_item_Name);
		editor.commit();
	}
	
	public boolean get_gechi_is_full(){//获取歌词偏移量
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		return shared.getBoolean("gechi_is_full",false);
	}
	//END######歌词######//######歌词######//######歌词######//######歌词######
	
	
	public void ser(int icon_id,boolean isPlaying)
	{//前台服务
		String text = getFilePath_Playing();
		String singer="",musicName="";
		int a1=text.indexOf("-");
		int a2=text.lastIndexOf(".");
		int a3=text.lastIndexOf("/");
		if(a3!=-1 && a1!=-1)
		{
			singer=text.substring(a3+1,a1);
		}
		else
		{
		    singer="灵魂歌手";
		}
		if(a1!=-1 && a2!=-1)
		{
			musicName=text.substring(a1+1,a2);
		}
		else
		{
		   if(a2!=-1 && a3!=-1)
			  musicName=text.substring(a3+1,a2);
		}
		//image_music_icon
		Intent intent=new Intent(this,MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);	
		
		Intent intentPlay = new Intent(PlayerReceiver.ACTION_PLAY_PAUSE);// 指定操作意图--设置对应的行为ACTION
		PendingIntent pIntentPlay = PendingIntent.getBroadcast(this.getApplicationContext(),
															   1, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);// 取的一个PendingIntent，*/
		
		Notification.Builder notificationb= new Notification.Builder(this);
		RemoteViews remoteViews = new RemoteViews(this.getPackageName(),R.layout.music_service_layout);// 获取remoteViews（参数一：包名；参数二：布局资源）
		notificationb.setContent(remoteViews);
		remoteViews.setOnClickPendingIntent(R.id.musicservicelayoutImageViewButton2,pIntentPlay);//播放，暂停
		remoteViews.setTextViewText(R.id.musicservicelayoutTextView1,musicName);
		remoteViews.setTextViewText(R.id.musicservicelayoutTextView2,singer);
		if(icon_id>=0)
		 {
		    InputStream is = getResources().openRawResource(icon_id);  
		    Bitmap iconBitmap = MapTool.toRoundCorner( BitmapFactory.decodeStream(is),13);
		    remoteViews.setImageViewBitmap(R.id.musicservicelayoutImageViewIcon,iconBitmap);
		  }	
		if(isPlaying)
			remoteViews.setImageViewResource(R.id.musicservicelayoutImageViewButton2,R.drawable.starting);
		  else
			remoteViews.setImageViewResource(R.id.musicservicelayoutImageViewButton2,R.drawable.stoping);
			
	    notificationb.setSmallIcon(R.drawable.image_music_icon);
		notificationb.setTicker("");
		notificationb.setContentIntent(pendingIntent);
		notificationb.setNumber(1);
		Notification mynotif=notificationb.build();
        startForeground(1, mynotif);	
		
    }
	
	public void sendMsg(int what){
		Message msg=new Message();
		msg.what=what;
		mhandler.sendMessage(msg);
	}
	
	
	public static String[] make_time(int time){
		String result[]={"",""};
		int time_s, time_ms;
		time_s=time/1000;
		time_ms=(time%1000)/100;
		if(time%100>=50) time_ms++;
		int mm=time_s/60;
		int ss=time_s%60;
		if(ss<10)
			result[0]=mm+":0"+ss;
		else
			result[0]=mm+":"+ss;
	    if(mm<10)
			result[1]="0"+result[0]+"."+time_ms;
		else
			result[1]=result[0]+"."+time_ms;
		return result;
	}
	
	
	public String getFilePath_Playing(){//获取activity发送的文件路径
		SharedPreferences shared = getSharedPreferences("playing_file",0);
		return shared.getString("isPlaying"+MainActivity.Music_item_Name,null);
	}
	
	
	public int getFilePath_id_Playing(){//获取activity发送的文件路径
		SharedPreferences shared = getSharedPreferences("playing_file",0);
		return shared.getInt("isPlayings"+MainActivity.Music_item_Name,0);
	}
	
	
	
	public void sendRunningDate(String key,String value){
		SharedPreferences shared = getSharedPreferences("music_running_date",0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putString(key+MainActivity.Music_item_Name, value);
		editor.commit();
	}
	
	public boolean getSettingDate(String dateName){
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		return shared.getBoolean(dateName+MainActivity.Music_item_Name,true);
	}
}

