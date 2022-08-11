package com.my.musicPlayer;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;

import android.view.View.OnClickListener;
import android.graphics.*;
import android.graphics.drawable.*;

public class MainActivity extends Activity
{
	ListView musicListView;
	ProgressBar progressbar_music;
	TextView musicnametextview,singernametextview,icontextview;
	TextView start_Textview,end_Teztview,titleTextv;
	Button menuButton;
	Button button_start_stop,button_PlayMod;
    final static String MUSIC_PATH="music_path";
    static String musicPath[]={"",""};
	int old_v;//记录正常音量，以便换歌时调回正常音量
	boolean old_;//上一次播放是否调音
	public static int Ontouch_list_value=-1;//服务传递信息，由myadapert传递来
	int icon_id=-1;//传递给前台服务icon的id
	public static int Length_MUSIC_Int=-1; //当前播放的音乐文件总时长，由服务传递来
	public static String Length_MUSIC_String="";
	public static int Progress_MUSIC_Int=0;//播放进度，由服务传递来
	public static String Progress_MUSIC_String="";
    public static boolean ISPLAYING=false; //是否正在播放，由服务传递来
	public static int PLAY_MODE=1; //播放模式，1为顺序播放，2为随机播放，3为单曲循环
	public static String Music_item_Name="";  //""为默认歌单一，"2"为歌单2.....，默认为歌单一
	public String itemname1="fshh4umq",itemn2="56wfhvmp",itemn3="hdbj555w";
	public static boolean gechi_is_full=false;
	MyAdapter adapter;
	
     @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
	//  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//系统通知栏
		 
        setContentView(R.layout.main);
		
		Music_item_Name=getMusicItem();
		gechi_is_full=get_gechi_is_full();
		musicListView=(ListView)findViewById(R.id.musicListView);
		progressbar_music=(ProgressBar)findViewById(R.id.mainProgressBar_music);
		musicnametextview=(TextView)findViewById(R.id.mainTextView_musicName);
		singernametextview=(TextView)findViewById(R.id.mainTextView_singerNmae);
		start_Textview=(TextView)findViewById(R.id.mainTextView_music_start);
		end_Teztview=(TextView)findViewById(R.id.mainTextView_music_end);
		icontextview=(TextView)findViewById(R.id.singer_icon_textview);
		titleTextv=findViewById(R.id.mainTextView1);
		titleTextv.setOnClickListener(new OnClickListener(){public void onClick(View p){ Toast.makeText(MainActivity.this,"长按以修改标题名称",1000).show();  }});
		titleTextv.setOnLongClickListener(new OnLongClickListener(){ public boolean onLongClick(View p1){ cdialog(Music_item_Name); return false; }});
		menuButton=findViewById(R.id.mainMenuButton);
		//menuButton.setOnClickListener(new OnClickListener(){ public void onClick(View p){ find_music_button(null); }});
		icontextview.setOnClickListener(new OnClickListener(){ public void onClick(View p1){  startA2(); } });	
		button_start_stop=(Button)findViewById(R.id.mainButton_start_stop);
		button_PlayMod=(Button)findViewById(R.id.mainButton_play_mod);
		singernametextview.setSelected(true);//跑马灯效果
		PLAY_MODE=getPLAY_MODE();
		setbutton_play_MOD_bg();
		startMusicService();//启动服务
		ISPLAYING=false;  
		 OLD_PLAY_TIME=System.currentTimeMillis();
		titleTextv.setText(getMusicItemName(getMusicItem()));
	     startList();
		 myHandler_For_A2_isRunning=true;
		 myHandler_For_A2(); 
	 }//end onCreate
	 
  public void startA2(){ 
      if(!ISPLAYING_musicName.equals(""))
	  {
          myHandler_For_A2_isRunning=true;
		//  myHandler_For_A2();
         startActivity(new Intent(this,A2.class)); 
   	  }
	}
 
	public void startList(){
		if(getMusicNumber()>0) //初始化列表
		{
			musicPath=new String[getMusicNumber()];
			for(int i=0;i<musicPath.length;i++)
			{ 
		        musicPath[i]=getMusicPath(i+1); 
			}	
			//make_list(musicPath,musicListView);
			 adapter = new MyAdapter(this, musicPath);
			 musicListView.setAdapter(adapter);
		}
		else
		{
			musicListView.setAdapter(null);
		}
		
		musicListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> list, View v, int position, long id)
				{			 
					 play(position);	
				}
			});
			
    }
	
	static int Music_Progress=0;//进度条进度
	private int bg_time=30;//刷新背景时间间隔
	private Handler handler= new Handler()
	{
		 @Override
		public void handleMessage(Message msg)
		{
			switch(msg.what){
				case 1:
					 start_Textview.setText(Progress_MUSIC_String);
					 if(Length_MUSIC_Int>0)
					 {
						  Music_Progress=(100*Progress_MUSIC_Int)/Length_MUSIC_Int;
					//	 int dt=Length_MUSIC_Int-progress;
						 if(/*Music_Progress<99*/(!(Progress_MUSIC_Int>Length_MUSIC_Int-300)))
						 {
						      progressbar_music.setProgress(Music_Progress);
							  if(isPlay_have_bg && getSettingDate("is_setbg"))
							    {   
								    if(Progress_MUSIC_Int<1000) bg_time=30;
								    if(Progress_MUSIC_Int/1000==bg_time)
									  {
										 // musicListView.setBackgroundResource(myDate.singer_zdbgj.bg_id[ISPLAYING_singer_id][myDate.getIntOn(myDate.singer_zdbgj.bg_id[ISPLAYING_singer_id].length)-1]);
										  A2.next_bg=1;
										  bg_time+=30+myDate.radom();
									  }
							    }			  
						 }
						 else
						 {   //播放结束
						     // Play5_Thread_isRunning=false;
							  music_service.sendProgress_thread_RUN_STOP=false;
							  ISPLAYING_singer_id=-1;
							  progressbar_music.setProgress(100);
							//  Toast.makeText(MainActivity.this,"",500).show();
							  new Thread(new Runnable(){public void run()
							     {  
								    int p1=getisPlayingNumber();
								    try{Thread.sleep(/*1200*/500);}catch(Exception e){}
									int p2=getisPlayingNumber();
								 if(p1==p2)//判断在线程休眠期间是否换歌，是则说明用户主动换歌，则不进行操作
									 if(PLAY_MODE==1)
									   {//顺序播放
									           if(getisPlayingNumber()+1==musicPath.length)
										        sendMsg(3);
									           else
										         sendMsg(4);
									    }
									 else
									    {
										    if(PLAY_MODE==2)//随机播放
											    sendMsg(5);
										    else//单曲循环
											    sendMsg(6);
									    }
							    }}).start();
						 }			 
					 }
			        break;
				case 2:next_Music(); break;
				case 3:play(0); break;
				case 4:play(getisPlayingNumber()+1); break;
				//case 2: musicListView.setBackgroundResource(myDate.singer_zdbgj.bg_id[msg.arg1][msg.arg2]); break; 
				case 5:play(myDate.getIntOn(musicPath.length)-1); break;
				case 6:play(getisPlayingNumber()); break;
	            case 7:start_stop_button(null); break;
				case 8:setbutton_play_MOD_bg(); break;
				case 9:startList();
				      Toast.makeText(MainActivity.this,"已添加",1000).show();  break;
				case 10:play(getMusicNumber()-1); break;
				case 11:play(getisPlayingNumber()-1); break;
				case 12:
					 EditText edit=findViewById(R.id.choosefileEditText);
					 edit.setText(msg.obj.toString());   break;
			}
		}
	
	};
	
  static int Msg_Form_A2=-1;
  static boolean myHandler_For_A2_isRunning=false;
  public void myHandler_For_A2(){
	  new Thread(new Runnable(){
		  public void run(){
			  while(myHandler_For_A2_isRunning)
				  {
					  if(Msg_Form_A2!=-1)
					  {
						  switch(Msg_Form_A2)
						  {
							  case 1:
								  sendMsg(7);
								  break;
								  
							  case 2:
								  sendMsg(8);
								  break;
							  case 3:
								new Thread(new Runnable(){
								 public void run(){
								//	Play5_Thread_isRunning=false;
									try{ Thread.sleep(600); }catch(Exception e){}
								    if(getisPlayingNumber()+1==musicPath.length)
									     sendMsg(3);
								      else
									     sendMsg(4);
								   }
							   }).start();
								  break;
							  case 4:
								  new Thread(new Runnable(){
										  public void run(){
											//  Play5_Thread_isRunning=false;
											  try{ Thread.sleep(600); }catch(Exception e){}
											  if(getisPlayingNumber()==0)
												  sendMsg(10);
											  else
												  sendMsg(11);
										  }
									  }).start();
									 break;
						  }
						 Msg_Form_A2=-1;
					  }
					  try{ Thread.sleep(5); }catch(Exception e){ }
				  }//end while
		  }
	  }).start();
  }
	
//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	//PLAY
	private long OLD_PLAY_TIME=0;
	public void play(int p)
	{
		music_service.sendProgress_thread_RUN_STOP=false;
		long Now=System.currentTimeMillis();
		if(Now-OLD_PLAY_TIME>600)
		{
			if(!new File(musicPath[p]).exists())
			  {
				  Doilag3(p,musicPath[p]);  
			  }
			  else
			  {
				  A2.gechi1="";
				  A2.gechi2="";
				  A2.gechi3="";
				  isPlay_have_bg=false;
			      play1(p);
			  }
			OLD_PLAY_TIME=System.currentTimeMillis();
		}
	}
	
	static String ISPLAYING_singerName="",ISPLAYING_musicName="";
	static int ISPLAYING_MUSIC_ID=-1;
	public void play1(int position)
	{   	    
	    MyAdapter.ISPLAYING_ID=position;
		ISPLAYING_MUSIC_ID=position;
	    start_Textview.setText("0:0");
		String text =musicPath[position];//(String) list.getItemAtPosition(position);
		sendFilePath_playing(text,position);
		int a1=text.indexOf("-");
		int a2=text.lastIndexOf(".");
		int a3=text.lastIndexOf("/");
		if(a3!=-1 && a1!=-1)
		{
			ISPLAYING_singerName=text.substring(a3+1,a1);
		}
		else
		{
			ISPLAYING_singerName="";
		}
		if(a1!=-1 && a2!=-1)
		{
			ISPLAYING_musicName=text.substring(a1+1,a2);
		}
		else
		{
			ISPLAYING_musicName=text.substring(a3+1,a2);
		}
		//play2_bg(ISPLAYING_singerName.replaceAll(" ",""));
		sendMsgToService(3,icon_id);
		play3_volum(position);
		musicnametextview.setText(ISPLAYING_musicName);
		singernametextview.setText(ISPLAYING_singerName);
		button_start_stop.setBackgroundResource(R.drawable.starting);	
	    icon_id=-1;
		end_Teztview.setText("/"+Length_MUSIC_String);
		//Toast.makeText(MainActivity.this,"widrh:"+musicListView.getWidth()+"   height:"+musicListView.getHeight(),2000).show();
		if(!Play4_Thread_isRunning) play4_thread_setProgressBar();
		
		int op=musicListView.getSelectedItemPosition();//获取列表当前位置
		adapter.notifyDataSetChanged();//刷新列表
		musicListView.setSelection(op);//回到当前位置
	}

	static int ISPLAYING_singer_id = -1; //歌手有无bg
	boolean isPlay_have_bg=false;
	/*public void play2_bg(String singer)
	{//处理背景
		int n=myDate.singer_zdbgj.Name.length;
		for(int i=0; i<n; i++)
		{
			if(singer.equals(myDate.singer_zdbgj.Name[i]))
			{
				ISPLAYING_singer_id=i;
				InputStream is = getResources().openRawResource(myDate.singer_zdbgj.icon[i]);
				Bitmap iconBitmap= BitmapFactory.decodeStream(is);
				icontextview.setBackgroundDrawable(new BitmapDrawable( MapTool.toRoundCorner(iconBitmap,15)));
				isPlay_have_bg=true;
				if(getSettingDate("is_setbg"))
				    {		  
				         musicListView.setBackgroundResource(myDate.singer_zdbgj.bg_id[i][myDate.getIntOn(myDate.singer_zdbgj.bg_id[i].length)-1]);
					 }
					 else
					 {
						 musicListView.setBackgroundColor(getResources().getColor(R.color.mlistcolor));
						 
					 }
				icon_id=myDate.singer_zdbgj.icon_bg[i];
				break;
			}
			else
			{
				if(i==n-1)
				{
					ISPLAYING_singer_id=-1;
					icon_id=R.drawable.lh;
					icontextview.setBackgroundResource(R.drawable.image_music_icon);
					musicListView.setBackgroundColor( getResources().getColor(R.color.mlistcolor));
					//setbg(musicListView,R.drawable.image_mlistview_bg);
				}
			}
		}
	}
	*/
	public void play3_volum(int position)
	{//处理音量
		int v=getsave_music_v(musicPath[position]);//获取音量
		AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		//Toast.makeText(MainActivity.this,v+"",1000).show();
		if(v==-1) 
		{
			if(old_) setMusicVolum(old_v);
			old_v=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			old_=false;
		}   
		else
		{   
		    if(!old_) old_v=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		    setMusicVolum(v);
			old_=true;
		}
	}
	
	private int play4_old_progress=0;
	private boolean Play4_Thread_isRunning=false;
	private int k1=0;
	public void play4_thread_setProgressBar()
	{
		new Thread(new Runnable(){public void run()
		 {
			 Play4_Thread_isRunning=true;
		/*     while(Play4_Thread_isRunning)
			 {
		        try{ Thread.sleep(1000);}catch(InterruptedException e){}
	   	        sendMsg(1);		
		     }*/
			 for(;;){		     
					 if(Progress_MUSIC_Int!=play4_old_progress)
						{
							sendMsg(1);
					        play4_old_progress=Progress_MUSIC_Int;
							k1=0;
				     	}
						else
						{
							if(ISPLAYING)
							  {
								  if(k1>=350)
								     {  
									    k1=0;
										sendMsg(2);   //next				
									 }
								   k1++;
							  }
						}
			 	     try{ Thread.sleep(10);}catch(InterruptedException e){}
			       }
		 }}).start();
	}
	
	
	public void next_Music(){
		music_service.sendProgress_thread_RUN_STOP=false;
		ISPLAYING_singer_id=-1;
		progressbar_music.setProgress(100);
		//  Toast.makeText(MainActivity.this,"",500).show();
		new Thread(new Runnable(){public void run()
				{  
					int p1=getisPlayingNumber();
					try{Thread.sleep(/*1200*/500);}catch(Exception e){}
					int p2=getisPlayingNumber();
					if(p1==p2)//判断在线程休眠期间是否换歌，是则说明用户主动换歌，则不进行操作
						if(PLAY_MODE==1)
						{//顺序播放
							if(getisPlayingNumber()+1==musicPath.length)
								sendMsg(3);
							else
								sendMsg(4);
						}
						else
						{
							if(PLAY_MODE==2)//随机播放
								sendMsg(5);
							else//单曲循环
								sendMsg(6);
						}
				}}).start();
	}
	/*
	private boolean Play5_Thread_isRunning=false;
	private int play5_t=0;
	public void play5_thread_setListView_bg(final int singer_id,final int isplay_id)
	{   	
	    play5_t=0;
		new Thread(new Runnable(){public void run()
				{
					Play5_Thread_isRunning=true;
					while(Play5_Thread_isRunning)
					{
						if(getisPlayingNumber()!=isplay_id) break;
					    if(Play5_Thread_isRunning)
						   try{ Thread.sleep(500); }catch(InterruptedException e){}
						play5_t++;
						if(play5_t%60==0)
						{
						    Message msg=new Message();
						    msg.what=2;
						    msg.arg1=singer_id;
						    msg.arg2=myDate.getIntOn(myDate.singer_zdbgj.bg_id[singer_id].length)-1;
						    handler.sendMessage(msg);
							A2.next_bg=1;
						 }
					}
				}}).start();
	}*/
	
	//END PLAY
//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	
	
	private music_service musicService;
    ServiceConnection mConnection;
	IBinder myBinder;

    public void startMusicService(){
		// sendFilePath_playing(music_path,i);
		 musicService = new music_service();
        //绑定服务用""
        mConnection = new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder)
		    {
                myBinder = iBinder;  //数据绑定
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName)
			{
                mConnection = null;
            }
        };
 
         Intent intent = new Intent(this,music_service.class);
         startService(intent);
         bindService(intent,mConnection, Context.BIND_AUTO_CREATE);//绑定

	   }
	
	public  void sendMsgToService(int msg1,int msg2){
		try {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            myBinder.transact(msg1,data,reply,msg2);
        }catch (RemoteException e){  e.printStackTrace();     }
	}
	
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){//选择文件返回
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
			switch(requestCode){
				case 232324773:// FileChoose.TEPY
					Uri uri=data.getData();
					 file_this =FileChoose.getInstance(this).getChooseFileResultPath(uri);
					 showInputDialog(file_this);
					break;
			}
		}
	}

	
//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
//按键方法区
	
	public void find_music_button(View e){   menudialog();  }
		/* PopupMenu menu = new PopupMenu(MainActivity.this,menuButton);
		 menu.getMenuInflater().inflate(R.menu.main_menu,menu.getMenu());
		 //3 为popupMenu设置点击监听器
		 menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				 @Override
				 public boolean onMenuItemClick(MenuItem menuItem) {
					 switch (menuItem.getItemId()){
						 case R.id.Music_item1: changeMusicItem("");   break;
						 case R.id.Music_item2: changeMusicItem("1");  break;
						 case R.id.Music_item3: changeMusicItem("2");  break;
						 case R.id.findMusic_item:
							 if(hasExternalStoragePermission(MainActivity.this))
								 showInputDialog("");
							 else
							 {  
								 checkPermission();
								 Toast.makeText(MainActivity.this,"没有文件读取权限，请前往系统权限管理处授权!",3000).show();
							 }  
							 break;
						 case R.id.about_item:   break;
					 }
					 return false;
				 }
			 });
		 menu.show();
		 
	/*	if(hasExternalStoragePermission(MainActivity.this))
	         showInputDialog("");
	      else
		  {  
		     checkPermission();
			 Toast.makeText(MainActivity.this,"没有文件读取权限，请前往系统权限管理处授权!",3000).show();
		 }  */
		   
	public void changeMusicItem(String itemId)
	   {
		   if(!Music_item_Name.equals(itemId))
		   {
			   Music_item_Name=itemId; 
			   saveMusicItem(itemId);
			   ISPLAYING=false;
			   ISPLAYING_musicName="";
			   sendMsgToService(4,0); 
			   musicListView.setBackgroundColor(getResources().getColor(R.color.mlistcolor));	   
			   icontextview.setBackgroundResource(R.drawable.image_touming);
			   button_start_stop.setBackgroundResource(R.drawable.stoping);
			   progressbar_music.setProgress(0);
			   Progress_MUSIC_Int=0;
			   musicnametextview.setText(""); 
			   singernametextview.setText("");
			   start_Textview.setText("");
			   end_Teztview.setText("");	
			   titleTextv.setText(getMusicItemName(itemId));
			   startList();
		   }
	   }
	 public void f(){
		 if(hasExternalStoragePermission(MainActivity.this))
			 showInputDialog("");
		 else
		 {  
			 checkPermission();
			 Toast.makeText(MainActivity.this,"没有文件读取权限，请前往系统权限管理处授权!",3000).show();
		 }  
	 }
	 public void menudialog(){
		 String item_str[]={getMusicItemName(itemname1),getMusicItemName(itemn2),getMusicItemName(itemn3),"扫描音乐","关于"};
		 final ListView listv=new ListView(MainActivity.this);
		 listv.setBackgroundColor(getResources().getColor(R.color.dimgray));
		 ListAdapter adapter = new MenuAdapter(this, item_str);
		 listv.setAdapter(adapter);
		 
		final Dialog builder = new Dialog(MainActivity.this);
		 builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 builder.setContentView(listv);
		 builder.show();
		 Window bw=builder.getWindow();
		// bw.getDecorView().setPadding(0, 0, 0, 0);
		 bw.setBackgroundDrawableResource(android.R.color.white);//解决左右边距
		 
		 WindowManager.LayoutParams layoutParams = bw.getAttributes();
		 bw.setGravity(Gravity.TOP|Gravity.RIGHT);
		 layoutParams.dimAmount = 0.0f;//外部透明度
		 layoutParams.y=80;
		// layoutParams.x=-10;
		 layoutParams.width=450;
		 layoutParams.height=480;
		  bw.setAttributes(layoutParams); 
		  
		  listv.setOnItemLongClickListener(new OnItemLongClickListener(){
				 @Override
				 public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
				 {
					 switch (p3){
						 case 0: cdialog(itemname1); break;
						 case 1: cdialog(itemn2); break;
						 case 2: cdialog(itemn3); break;
						 case 3: Toast.makeText(MainActivity.this,"不可修改,made by 2323247730",1000).show();
							 break;
						 case 4: Toast.makeText(MainActivity.this,"不可修改，made by 2323247730",1000).show();
							 break;
					 }
					 builder.dismiss();
					return false;
				  }
			});
		 listv.setOnItemClickListener(new OnItemClickListener() {
				 @Override
				 public void onItemClick(AdapterView<?> list, View v, int position, long id)
				 {			 
					 //removeGechiDialog();
					 switch (position){
				       case 0: changeMusicItem(itemname1); break;
					   case 1: changeMusicItem(itemn2); break;
					   case 2: changeMusicItem(itemn3); break;
					   case 3: f();  break;
					   case 4: Toast.makeText(MainActivity.this,"made by 2323247730",1000).show();
					             break;
					   }
				     builder.dismiss();
				 }
			 });	 
	 }
	 
    public void cdialog(final String i){
		final EditText inputServer = new EditText(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		inputServer.setTextSize(16);
		inputServer.setHeight(180);
		inputServer.setHint("请输入新的名称");
		builder.setTitle("修改歌单名");
		builder.setView(inputServer);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					 String n=inputServer.getText().toString();
					 if(!n.equals(""))
					   {
						 saveMusicItemName(n,i);
					     titleTextv.setText(n);	 
						}
					}
				});
		builder.show();
	}
	 
	public void saveMusicItemName(String item,String i){//保存
		SharedPreferences shared = getSharedPreferences(MUSIC_PATH,0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putString("MusicItemName"+i, item);
		editor.commit();
	}
	public String getMusicItemName(String i){//获
		SharedPreferences shared = getSharedPreferences(MUSIC_PATH,0);
		return shared.getString("MusicItemName"+i,"歌单"+i);
	}
	
	 
	public void saveMusicItem(String item){//保存
		SharedPreferences shared = getSharedPreferences(MUSIC_PATH,0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putString("MusicItem", item);
		editor.commit();
	}
	public String getMusicItem(){//获
		SharedPreferences shared = getSharedPreferences(MUSIC_PATH,0);
		return shared.getString("MusicItem",itemname1);
	}
	
	 
	public void ChosseFileButton(View e){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/x-mpeg");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, FileChoose.TEPY);
	}
	
   private String file_this="";
	private void showInputDialog(String FilePatg){
		final EditText inputServer = new EditText(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		inputServer.setTextSize(15);
		if(FilePatg.equals(""))
		    inputServer.setHint("请输入文件夹或歌曲文件路径");
		else
			inputServer.setText(FilePatg);
		inputServer.setHeight(150);
		inputServer.setWidth(700);
		final Button button = new Button(MainActivity.this);
		final LinearLayout layout=new LinearLayout(MainActivity.this);
		layout.addView(inputServer);
		layout.addView(button);
		button.setText("浏览");
		
		builder.setTitle("添加歌曲:").setView(layout);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					final String path = inputServer.getText().toString();//inputServer.getText().toString();
				    File file=new File(path);
					if(file.exists())
					 {
						 if(file.isDirectory())
						 { 
							 final ProgressDialog prod= new ProgressDialog(MainActivity.this);
							 prod.setMessage("正在扫描.....");
							 prod.setCanceledOnTouchOutside(false);
							 prod.setCancelable(true);
							 prod.show();
						//	 Toast.makeText(MainActivity.this,"正在扫描,请不关闭应用",600).show();
							 new Thread(new Runnable(){
									 public void run(){ 
									
									    findMusic(path);
										 prod.cancel();
								        sendMsg(9);
							  }}).start();
						  }
						 else
						 {
							 String sign=path.substring(path.lastIndexOf(".")+1);
							 if(sign.equals("mp3")||sign.equals("m4a"))
							  {  
							      boolean checkSame=false;
							     for(int i=0;i<musicPath.length;i++)
								      if(path.equals(musicPath[i])){
										  checkSame=true;
										  break;
									  }
								  if(!checkSame)
								  {
									  saveMusicPath(getMusicNumber()+1,path,-1);
								      startList();//7月19日
								   }
							  }
							  else
							  {
								  Toast.makeText(MainActivity.this,"这个路径不是音乐文件，也不是一个文件夹!",1000).show();
							  }
						 }
					 }
					 else
					 {
						 Toast.makeText(MainActivity.this,"该路径不存在!",1000).show();
					 }
				}
			});
		final AlertDialog dil = builder.show();
		button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("audio/x-mpeg");
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					startActivityForResult(intent, FileChoose.TEPY); 
					dil.dismiss();//结束对话框
				}
			});
         
    }
	
	 
	public void Doilag2(){
		AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
		builder2.setTitle("扫描完成，需重启应用才能生效，是否立即重启应用?");
		builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					play(-1);//结束应用
					finish();
				}
			});	
		builder2.show();
	}
	
	
	public void Doilag3(final int id,final String name){
		AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
		builder2.setTitle(name.substring(name.indexOf("-"))+"歌曲文件已损坏或丢失，是否移除该曲?");
		builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(id>0) play(id-1);
					dialog.dismiss();
				}
			});
		builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					 detect_music(id);
					 if(id>0) play(id-1);
			     	 Toast.makeText(MainActivity.this,"已移除",500).show();
				}
			});	
		builder2.show();
	}
	
	public void start_stop_button(View e)
	 {
	  if(!ISPLAYING_musicName.equals(""))
		   if(ISPLAYING)
		    {
			   sendMsgToService(2,0);
			   button_start_stop.setBackgroundResource(R.drawable.stoping);
		    }
		   else
		    {
			   sendMsgToService(1,0);
		       button_start_stop.setBackgroundResource(R.drawable.starting);
		    }
	   else
		  if(getMusicNumber()>0)  
		      sendMsg(5);
			else
			  Toast.makeText(MainActivity.this,"没有歌曲!",800).show();
	 }
	 
	 public void button_setting_small(View e)
	 {//对话框
	 	//Toast.makeText(MainActivity.this,"add",1000).show();
		 if(getisPlayingNumber()==Ontouch_list_value)
		 {
		     AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		     int Music_value=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		     if(Music_value!=0)
		          mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,Music_value-1,0);  
		     else
		          Toast.makeText(MainActivity.this,"现在音量为0!",1000).show();
		 }
		 else
		 {
			 doialist(Ontouch_list_value);
		 }
		 
	 }

	
	 public void button_setting_big(View e)
	 {
		 if(getisPlayingNumber()==Ontouch_list_value)
		 {
	  	    AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		    int Music_value=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,Music_value+1,0);  //增加音量
			int new_Music_value=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if(new_Music_value==Music_value)
		        Toast.makeText(MainActivity.this,"已经是最大音量了!",1000).show();
		 }
		 else
		 {
			doialist(Ontouch_list_value); 
		 }
	 }
	
	 
    public void button_bofanmoshi(View v)
	{//播放模式
		if(PLAY_MODE>2)
		{
			PLAY_MODE=1;
			save_PLAY_MODE(1);
		}
		else
		{
			PLAY_MODE++;
			save_PLAY_MODE(PLAY_MODE);
		}
		switch(PLAY_MODE){
			case 1:
				Toast.makeText(MainActivity.this,"顺序播放",1000).show();
		      break;
			case 2:
				Toast.makeText(MainActivity.this,"随机播放",1000).show();
				break;
			case 3:
				Toast.makeText(MainActivity.this,"单曲循环",1000).show();
				break;
		}
		setbutton_play_MOD_bg();
	}
	
	public void setbutton_play_MOD_bg()
	{
		if(PLAY_MODE==1)
		{
			button_PlayMod.setBackgroundResource(R.drawable.image_shunxunbof);  
		}
		else
		{
		   if(PLAY_MODE==2)
		   {
			   button_PlayMod.setBackgroundResource(R.drawable.image_shuijibof);
		   }
		   else
		   {
			   button_PlayMod.setBackgroundResource(R.drawable.image_danquxunhuan);
		   }
		 }
	}
	
	 
	public void button_setting_save_volum(View e)
	{

		if(getisPlayingNumber()==Ontouch_list_value)
		{
			AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			int Music_value=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			save_muise_v(musicPath[ Ontouch_list_value],Music_value);
			Toast.makeText(MainActivity.this,"已保存,音量为:"+Music_value,1000).show();
		}
		else
		{
		   doialist(Ontouch_list_value);
		}
	}
	 
	public void setting_button_delect(View q)//删除
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					detect_music(Ontouch_list_value);
					Toast.makeText(MainActivity.this,"已删除",500).show();
				}
			});
		builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
				}
			});
		builder.setTitle("是否删除?");
		builder.show();
	}
	
	
	public void doialist(final int i){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 点击事件
                   play(i);
				}
			});
		builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
		 @Override
		 public void onClick(DialogInterface dialog, int which)
		    {
		    }
		 });
		builder.setTitle("提示");
		builder.setView(R.layout.doial_tes_mainactivity);
		builder.show();
	}
	
	
	
	
//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
//mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
	  
  public void setMusicVolum(int value){
	   AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	 // int max= mAudioManager.getStreamMaxVolume(mAudioManager.STREAM_SYSTEM);
	   if(value>=0&&value<=15)
	      mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,value,0);  
  }
	  
  int number=0;
  boolean find_is_ok=false;
  public void findMusic(String fileName){//搜索音乐
        number=getMusicNumber();
		File file=new File(fileName);
		File[] d=file.listFiles();
		for(int i=0;i<d.length;i++){
			if (d[i].isDirectory()){   
				findMusic(d[i].toString());
			}else {
				String sign=d[i].toString().substring(d[i].toString().lastIndexOf(".")+1);
				if(sign.equals("mp3")||sign.equals("m4a")){ 
				     boolean checkSame=false;
				     for(int j=0;j<musicPath.length;j++)
					    {
							if(d[i].toString().equals(musicPath[j]))
							   {
								    checkSame=true;
									break;
							   }
						}
						if(!checkSame)
						    {
					           number++;
					           saveMusicPath(number,d[i].toString(),-1);
					        //   save_muise_v(number+232324773*10,-1);
						    }
				}
			  }
		 }
	        musicPath=new String[getMusicNumber()];
	    for(int i=0;i<musicPath.length;i++)
		{   musicPath[i]=getMusicPath(i+1);     }	
		find_is_ok=true;	
	}
	
	public static boolean hasExternalStoragePermission(Context context){//判断是否有文件读写权限
		int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
		return perm == PackageManager.PERMISSION_GRANTED;
	}
	
	public void detect_music(int number)
	{//删除
		SharedPreferences shared = getSharedPreferences("music_path",0);
		SharedPreferences.Editor editor=shared.edit();
		number=number-1;//hhjj
		for(int i=number;i<getMusicNumber()-1;i++)
		{
			editor.putString(String.valueOf(i+2)+Music_item_Name,getMusicPath(i+3));
			//editor.putInt((i+1)+"2323247730"+Music_item_Name,getsave_music_v(i+2));
			//  saveMusicPath(i+2,getMusicPath(i+3),getsave_music_v(musicPath[i+3]));
		}
		editor.putInt("Music_number"+Music_item_Name,getMusicNumber()-1);
		editor.commit();
		
		if(getMusicNumber()>0)
		{
			int op=musicListView.getSelectedItemPosition();
			musicPath=new String[getMusicNumber()];
			for(int i=0;i<musicPath.length;i++)
			{   musicPath[i]=getMusicPath(i+1);     }	
			adapter=new MyAdapter(this,musicPath);
			musicListView.setAdapter(adapter);
			musicListView.setSelection(op);//
			
		}
		else
		{
			musicListView.setAdapter(null); //7月22日
		}
	}
	
	static void removeString(String list[],int listnumber){

		for(int i=listnumber;i<list.length-1;i++){
			list[i]=list[i+1];
		}
        list[list.length-1]="";
	}//删除字符串数组指定元素
	
	public void sendMsg(int what){
		  Message msg=new Message();
		  msg.what=what;
		  handler.sendMessage(msg);
	}
	
	
   	
	public void save_PLAY_MODE(int play_mod){//保存
		SharedPreferences shared = getSharedPreferences(MUSIC_PATH,0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putInt("Play_Mod"+Music_item_Name, play_mod);
		editor.commit();
	}
	public int getPLAY_MODE(){//获
		SharedPreferences shared = getSharedPreferences(MUSIC_PATH,0);
		return shared.getInt("Play_Mod"+Music_item_Name,1);
	}
	
	public void saveMusicPath(int i,String FilePath,int v){//储存文件路径
		SharedPreferences shared = getSharedPreferences("music_path",0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putString(String.valueOf(i)+Music_item_Name, FilePath);
		editor.putInt(i+"2323247730"+Music_item_Name,v);
		editor.putInt("Music_number"+Music_item_Name,i);
		editor.commit();
	}
	public String getMusicPath(int i){//获取保存的歌曲路径
		SharedPreferences shared = getSharedPreferences(MUSIC_PATH,0);
		return shared.getString(String.valueOf(i)+Music_item_Name,null);
	}
	public int getsave_music_v(String name){//获取音量
		SharedPreferences shared = getSharedPreferences(MUSIC_PATH,0);
		return shared.getInt(name+"2323247730"+Music_item_Name,-1);
	}
	public void save_muise_v(String name,int value){//保存歌曲单独配置音量
		SharedPreferences shared = getSharedPreferences(MUSIC_PATH,0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putInt(String.valueOf(name+"2323247730")+Music_item_Name, value);
		editor.commit();
	}
	
	public int getMusicNumber(){//歌曲数量
		SharedPreferences shared = getSharedPreferences(MUSIC_PATH,0);
		return shared.getInt("Music_number"+Music_item_Name,0);
	}
	
	public int getisPlayingNumber(){//获取正在播放的歌曲在数组中的序列号
		SharedPreferences shared = getSharedPreferences("playing_file",0);
		return shared.getInt("isPlayings"+Music_item_Name,0);
	}
	public void sendFilePath_playing(String FilePath,int i){//向服务传递文件路径和序列号
		SharedPreferences shared = getSharedPreferences("playing_file",0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putString("isPlaying"+Music_item_Name, FilePath);
		editor.putInt("isPlayings"+Music_item_Name,i);
		editor.commit();
	}
	public String getFilePath_Playing(){//正在播放的
		SharedPreferences shared = getSharedPreferences("playing_file",0);
		return shared.getString("isPlaying"+Music_item_Name,null);
	 }
	
	public void sendRunningDate(String key,String value){
		SharedPreferences shared = getSharedPreferences("music_running_date",0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putString(key+Music_item_Name, value);
		editor.commit();
	}
	
	
	
	public boolean getSettingDate(String dateName){
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		return shared.getBoolean(dateName+Music_item_Name,true);
	}
	
	public boolean get_gechi_is_full(){//获取歌词偏移量
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		return shared.getBoolean("gechi_is_full",false);
	}
	
	public void checkPermission() {//申请权限
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                isGranted = false;
            }
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
          //  Log.i("cbs","isGranted == "+isGranted);
            if (!isGranted) {
                this.requestPermissions(
					new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
						.ACCESS_FINE_LOCATION,
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE},
					102);
            }
        }

	}
	
	
	
}
