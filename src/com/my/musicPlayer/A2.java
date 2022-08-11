package com.my.musicPlayer;
import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.lang.reflect.*;

import android.view.View.OnClickListener;

public class A2 extends Activity
{
    LinearLayout mainLinearlayout;
	TextView progressTextv,musicLeight_Textv;
	SeekBar progressbar;
	Button button_start_stop,button_playmod,button_gechi;
	TextView musicNameTextv,singerNameTextv;//iconTextv;
	TextView gechiTextv1,gechiTextv2,gechiTextv3;
	TextView gechiTextv1_full,gechiTextv2_full,gechiTextv3_full;
	public static String gechi1="",gechi2="",gechi3="";//歌词
	public boolean gechi_is_full=false;//是否显示全屏歌词
	
	
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//系统通知栏
		if(hasNotchInScreen(A2.this)||hasNotchXiaoMi(A2.this))
                setContentView(R.layout.music_a2_h);
		   else
			    setContentView(R.layout.music_a2); 
		mainLinearlayout=findViewById(R.id.music_a2_mainLearyout);
		progressTextv=findViewById(R.id.musica2_progress_textv);
		musicLeight_Textv=findViewById(R.id.musicA2_TextView_music_end);
		progressbar=findViewById(R.id.musicA2_ProgressBar);
		button_start_stop=findViewById(R.id.musicA2_start_stop);
		button_playmod=findViewById(R.id.musicA2_playmod);
		musicNameTextv = findViewById(R.id.musica2_musicName_textv);
		singerNameTextv = findViewById(R.id.musica2_SingerName_textv);
	//	iconTextv = findViewById(R.id.musica2IconTextView);
		gechiTextv1=findViewById(R.id.musica2_gechiTextv1);
		gechiTextv2=findViewById(R.id.musica2_gechiTextv2);
		gechiTextv3=findViewById(R.id.musica2_gechiTextv3);
		gechiTextv1_full=findViewById(R.id.musica2_gechiTextv1_full);
		gechiTextv2_full=findViewById(R.id.musica2_gechiTextv2_full);
		gechiTextv3_full= findViewById(R.id.musica2_gechiTextv3_full);
	    button_gechi=findViewById(R.id.musicA2_gechi_button);
		singerNameTextv.setSelected(true);
		gechiTextv1.setSelected(true);
		gechiTextv2.setSelected(true);
		gechiTextv3.setSelected(true);
	//	Gechi_line_n=-1;
	    is_setbg = getSettingDate("is_setbg");
		gechi_is_full=get_gechi_is_full();
		
		Start_ui();
		if(MainActivity.ISPLAYING)
		     {   
			     Thread_setProgressBar();
				 musicLeight_Textv.setText("/"+MainActivity.Length_MUSIC_String);
		     }
			 
		progressbar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
			int p=0;
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				//seekbar进度改变调用
				//progressTextv.setText(""+progress);
			   p=progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				//手接触seekbar的时候调用
				onsetSeek=true;
			}

	        @Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				//手离开seekbar的时候调用
				onsetSeek=false;
				progressbar.setProgress(p);
				music_service.msgForA2=true;
				music_service.progress=p*MainActivity.Length_MUSIC_Int/100;
				progressTextv.setText(music_service.make_time(music_service.progress)[0]);
			}
		});
		
		gechiTextv2.setOnClickListener(new OnClickListener(){
			public void onClick(View e){
		      if(!gechi_is_full)
				{   
				     gechiTextv1.setText("");
					 gechiTextv2.setText("");
					 gechiTextv3.setText("");
				     gechi_is_full=true;
					 MainActivity.gechi_is_full=gechi_is_full;
					 save_gechi_is_full();
			    }
			}
		});
		
		gechiTextv2_full.setOnClickListener(new OnClickListener(){
				public void onClick(View e){
					if(gechi_is_full)
					{   
						gechiTextv1_full.setText("");
						gechiTextv2_full.setText("");
						gechiTextv3_full.setText("");
						gechi_is_full=false;
						MainActivity.gechi_is_full=gechi_is_full;
						save_gechi_is_full();
					}
				}
			});
	}

    private boolean onsetSeek=false;//是否正在拉进度条
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		 {
			switch(msg.what){
				case 1: //更新播放进度条
					 progressTextv.setText(MainActivity.Progress_MUSIC_String);
					 if(!onsetSeek) progressbar.setProgress(MainActivity.Music_Progress);
					 if(music_service.Gechi_is_ok_Service)
					 {  
		                if(gechi_is_full)
						  {
						    gechiTextv1_full.setText(gechi1);
						    gechiTextv2_full.setText(gechi2);
						    gechiTextv3_full.setText(gechi3);
						  }
						  else
						  {
							  gechiTextv1.setText(gechi1);
							  gechiTextv2.setText(gechi2);
							  gechiTextv3.setText(gechi3);
						  }
					//*	 showGechi(Now_time_ms);
					 }
					break;
				
				case 2: //切歌后
				 //    testTextv.setText("next");
					/* musicNameTextv.setText(MainActivity.ISPLAYING_musicName);
					 singerNameTextv.setText(MainActivity.ISPLAYING_singerName);
				*/
					 gechiTextv1.setText(gechi1);
					 gechiTextv2.setText(gechi2);
					 gechiTextv3.setText(gechi3);
					 gechiTextv1_full.setText(gechi1);
					 gechiTextv2_full.setText(gechi2);
					 gechiTextv3_full.setText(gechi3);
					 Start_ui_next();
				    if(MainActivity.ISPLAYING_singer_id!=-1&&is_setbg)
					   {
				        // int id1=myDate.singer_zdbgj.bg_id[MainActivity.ISPLAYING_singer_id][myDate.getIntOn(myDate.singer_zdbgj.bg_id[MainActivity.ISPLAYING_singer_id].length)-1];			 
					    // setbg(mainLinearlayout,id1);
						// MainActivity.ISPLAYING_singer_id=-1;
						}
					  else
					   {
						   mainLinearlayout.setBackgroundColor(getResources().getColor(R.color.mlistcolor));
					   }
					 
					break;
					
				case 3: // 更换背景
				     if(is_setbg){
					     //int id=myDate.singer_zdbgj.bg_id[MainActivity.ISPLAYING_singer_id][myDate.getIntOn(myDate.singer_zdbgj.bg_id[MainActivity.ISPLAYING_singer_id].length)-1];			 
			 	        // setbg(mainLinearlayout,id);
					   }
				//	Toast.makeText(A2.this,"",500).show();
					break; 
					//case 5:gechiTextv1.setText(MainActivity.Progress_MUSIC_String); break;
				case 4:
					 if(MainActivity.ISPLAYING)
						 button_start_stop.setBackgroundResource(R.drawable.starting);
					 else
						 button_start_stop.setBackgroundResource(R.drawable.stoping);	
				//	 Toast.makeText(A2.this,"bbb",500).show(); 
					break;
			 }
		  }
	  };
	  
    private boolean lastPlaying=true;
	private int old_progress=0;
	public static int next_bg=0;
	private boolean Thread_setProgressBar_isRunning=false;
	private boolean Gechi_is_ok=false;
	public static String Now_time_ms="00000";
	public void Thread_setProgressBar()
	{
		new Thread(new Runnable(){public void run()
				{
					Thread_setProgressBar_isRunning=true;
					for(;;){		     
					    if(MainActivity.Progress_MUSIC_Int!=old_progress)
						 {
							sendMsg(1);//更新进度条
					        old_progress=MainActivity.Progress_MUSIC_Int;
							if(MainActivity.Progress_MUSIC_Int<1500&&MainActivity.Progress_MUSIC_Int>=500)
								if(!musicNameTextv.getText().toString().equals(MainActivity.ISPLAYING_musicName))
								      sendMsg(2);
				     	  }	 
						  
						if(MainActivity.ISPLAYING!=lastPlaying)
						{
							lastPlaying=MainActivity.ISPLAYING;
							sendMsg(4);
						}
						
					   if(next_bg==1)
						  {
							sendMsg(3);//更换背景
							next_bg=0;
						  }
					//	 sendMsg(5);
						try{ Thread.sleep(10);}catch(InterruptedException e){}
					}
				}}).start();
	}
	
	
	public void setbg(View view,int drawble_id ){
		InputStream is = getResources().openRawResource(drawble_id);  
		Bitmap mBitmap =cut_Bitmap(BitmapFactory.decodeStream(is),view);
		Drawable drawable = new BitmapDrawable(mBitmap);
		view.setBackgroundDrawable(drawable);
	}
	
	private Bitmap cut_Bitmap(Bitmap bitmap,View view){//图片剪切适配view
          int cut_x = 0,cut_y = 0,cut_width = 0,cut_height = 0;
	      int height_view = view.getHeight();
	      int width_view = view.getWidth();
		  int height_map = bitmap.getHeight();
		  int width_map = bitmap.getWidth();
		  float b_view = height_view/width_view;
		  float b_map = height_map/width_map;
		  if(b_view>b_map)
			  {
				  cut_height = height_map;
				  cut_width = height_map*width_view/height_view;
				  cut_x = (width_map-cut_width)/2;
				  cut_y = 0;
			  }
		   else
		      {
				  cut_width = width_map;
				  cut_height = (int)b_view*width_map;
				  cut_x = 0;
				  cut_y = (height_map-cut_height)/2;
			  }
		//testTextv.setText("x:"+cut_x+"   y:"+cut_y+"\nw:"+cut_width+"  h:"+cut_height);	
        return Bitmap.createBitmap(bitmap,cut_x, cut_y, cut_width, cut_height, null, false);
    }
	
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){//选择文件返回
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
			switch(requestCode){
				case 23232+47730:// FileChoose.TEPY
					Uri uri=data.getData();
					String file_this =FileChoose.getInstance(this).getChooseFileResultPath(uri);
					addGechiDialog(file_this);
					break;
			}
		}
	}
	
	
	/*
	String Gechi[][];
	public void makeGetchi(int music_id){
		 Gechi=myDate.ReadTxtFile2(getsave_music_gechi(music_id));
		 Gechi_line_n=0;
		 len = Gechi.length;
	}
	int len=-1;
	private int Gechi_line_n=-1;
    public void showGechi(String Now_time){
	   if(Gechi_line_n !=-1)
		 {
	         if(Now_time.equals(Gechi[Gechi_line_n][0]))
		      {
			    gechiTextv2.setText(Gechi[Gechi_line_n][1]);
				if(Gechi_line_n>0) gechiTextv1.setText(Gechi[Gechi_line_n-1][1]);
				if(Gechi_line_n<len-2) gechiTextv3.setText(Gechi[Gechi_line_n+1][1]);
			    if(Gechi_line_n<len-1)
				   {  Gechi_line_n++;  }
				else{
					Gechi_is_ok=false; 
					gechiTextv2.setText("");
					gechiTextv1.setText("");
					gechiTextv3.setText("");
					}
		      }
		  }
		 else
		 { 
			  for(int i=0; i<Gechi.length; i++)
				  if(Now_time.equals(Gechi[i][0]))
				  {
					  gechiTextv2.setText(Gechi[i][1]);
					  Gechi_line_n=i+1;
					//  tip();
					  break;
				  }		  
		 }
	}
	
	public void test(View w){
		
	}
	public void tip(){
		Toast.makeText(A2.this,Gechi_is_ok+"  "+Gechi_line_n,300).show();
	}
	*/
	
	//##按键方法#####按键方法#####按键方法#####按键方法###
	private boolean is_setbg=true;
	
	public void Bg_button(View e){
		if(is_setbg)
		{
			is_setbg=false;
			saveSettingDate("is_setbg",false);
			Toast.makeText(A2.this,"已关闭写真",500).show();
		/*	InputStream is = getResources().openRawResource(R.drawable.image_music2abg);  
			Bitmap mBitmap = MapTool.blurBitmap( BitmapFactory.decodeStream(is),25,A2.this);
			Drawable bg = new BitmapDrawable(mBitmap);
			mainLinearlayout.setBackgroundDrawable(bg);  */
			mainLinearlayout.setBackgroundColor(getResources().getColor(R.color.mlistcolor));		
		}
		else
		{
			is_setbg=true;
			saveSettingDate("is_setbg",true);
			if(MainActivity.ISPLAYING_singer_id>=0)
			  {
				//int id=myDate.singer_zdbgj.bg_id[MainActivity.ISPLAYING_singer_id][myDate.getIntOn(myDate.singer_zdbgj.bg_id[MainActivity.ISPLAYING_singer_id].length)-1];			 
			  //  setbg(mainLinearlayout,id);
			  }
			Toast.makeText(A2.this,"已开启写真",500).show();
			
		}
	}
	
    public void buttonSavevolue(View viee){ 
			AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			int Music_value=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			save_muise_v(MainActivity.musicPath[ getisPlayingNumber()],Music_value);
			Toast.makeText(A2.this,"已保存,音量为:"+Music_value,600).show();
        }//保存音量
	
    long last_st_stop=0;
	public void start_stop_button_A2(View e)
	{//播放暂停键
	    long now = System.currentTimeMillis();
		if(now-last_st_stop>500)
		  {
		      if(MainActivity.ISPLAYING)
			       button_start_stop.setBackgroundResource(R.drawable.stoping);
		        else
		 	       button_start_stop.setBackgroundResource(R.drawable.starting);
              sendMsgToMainActivity(1);
		  }
		last_st_stop=now;
	}
	
	long oldtime_nextmusic=0;
	public void nextMusic(View view){
		long Now=System.currentTimeMillis();
		if(Now-oldtime_nextmusic>800)
		{  
		    sendMsgToMainActivity(3);
	        Gechi_is_ok=false;
	        gechiTextv1.setText("");
	        gechiTextv2.setText("");
	        gechiTextv3.setText("");
		}
		oldtime_nextmusic=Now;
	}  
	public void lastMusic(View view){
		long Now=System.currentTimeMillis();
		if(Now-oldtime_nextmusic>800)
		  { 
		     sendMsgToMainActivity(4);
		     Gechi_is_ok=false;
		     gechiTextv1.setText("");
		     gechiTextv2.setText("");
		     gechiTextv3.setText("");
		   }
		oldtime_nextmusic=Now;
	}
	
	public void A2_playmod(View wgh){//播放模式
		if(MainActivity.PLAY_MODE>2)
		{
			MainActivity.PLAY_MODE=1;
			save_PLAY_MODE(1);
		}
		else
		{
			MainActivity.PLAY_MODE++;
			save_PLAY_MODE(MainActivity.PLAY_MODE);
		}
		setbutton_playmod_bg();
	    sendMsgToMainActivity(2);
	}
	
	public void setbutton_playmod_bg(){
		if(MainActivity.PLAY_MODE==1)//设置按键背景
		{
			button_playmod.setBackgroundResource(R.drawable.image_shunxubof2);  
		}
		else
		{
			if(MainActivity.PLAY_MODE==2)
			{
				button_playmod.setBackgroundResource(R.drawable.image_shuijibof2);
			}
			else
			{
				button_playmod.setBackgroundResource(R.drawable.image_danquxunhuan2);
			}
		}
	}
	
	public void A2_Gechi_button(View e){
		if(getsave_music_gechi(MainActivity.musicPath[MainActivity.ISPLAYING_MUSIC_ID]).equals(""))
			{
				//listDialog();
			  if(hasExternalStoragePermission(A2.this))
			         addGechiDialog("");
				else
				{   
				    checkPermission();
					Toast.makeText(A2.this,"没有文件读写权限，请前往系统设置授权!",1000).show();
		         }
			}
		  else
		    {
				Gechi_Setting_dialog();
		    }
	}
	
	public void removeGechi_button(View e){
		if(getsave_music_gechi(MainActivity.musicPath[ MainActivity.ISPLAYING_MUSIC_ID]).equals(""))
			Toast.makeText(A2.this,"已移除!",500);
	     else
			removeGechiDialog();
	}
	
	/*public void addGechiProgress_buttom(View e){
		 save_muise_gechi_dt(MainActivity.ISPLAYING_MUSIC_ID,getsave_music_gechi_dt(MainActivity.ISPLAYING_MUSIC_ID)+100);
		 Toast.makeText(A2.this,"当前调整时间为:"+(getsave_music_gechi_dt(MainActivity.ISPLAYING_MUSIC_ID)*-1)+"ms",500);
	}
    
	public void jianGechiProgress_buttom(View e){
		save_muise_gechi_dt(MainActivity.ISPLAYING_MUSIC_ID,getsave_music_gechi_dt(MainActivity.ISPLAYING_MUSIC_ID)-100);
		Toast.makeText(A2.this,"当前调整时间为:"+(getsave_music_gechi_dt(MainActivity.ISPLAYING_MUSIC_ID)*-1)+"ms",500);
	}
	*/

	//##按键方法end#####按键方法end#####按键方法#####按键方法###
	
	//######对话框############对话框############对话框############对话框######
	
	private void addGechiDialog(String FilePatg){
        final EditText inputServer = new EditText(A2.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(A2.this);
        builder.setTitle("歌词路径:");
		inputServer.setTextSize(15);
		if(FilePatg.equals(""))
		    inputServer.setHint("请输歌词文件路径");
		else
			inputServer.setText(FilePatg);
		inputServer.setHeight(150);
		inputServer.setWidth(700);
		final Button button = new Button(A2.this);
		final LinearLayout layout_child1=new LinearLayout(A2.this);
		layout_child1.addView(inputServer);
		layout_child1.addView(button);
		button.setText("浏览");
	    
		
		final TextView textVeiw = new TextView(A2.this);
		textVeiw.setText("窃取歌词:");
		textVeiw.setTextSize(17);
		textVeiw.setHeight(myDate.dip2px(A2.this,35));
		textVeiw.setWidth(myDate.dip2px(A2.this,79));
		
		final Button b1=new Button(A2.this);
		b1.setHeight(30);
		b1.setWidth(300);
		b1.setText("从酷狗音乐");
		
        
		final Button b2=new Button(A2.this);
		b2.setHeight(30);
		b2.setWidth(300);
		b2.setText("从网易音乐");
		
		
		final TextView t1=new TextView(A2.this);
		t1.setWidth(70);

		final LinearLayout layout_child2=new LinearLayout(A2.this);
		layout_child2.addView(textVeiw);
		layout_child2.addView(b1);
		layout_child2.addView(t1);
		layout_child2.addView(b2);
		
        final LinearLayout mainlayout = new LinearLayout(A2.this);
		mainlayout.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mainlayout.addView(layout_child1);
		mainlayout.addView(layout_child2);
		builder.setView(mainlayout);
		
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					final String path = inputServer.getText().toString();
					if( (!path.equals("")) && (!(path==null)) )
					{ 
						File file=new File(path);
						if(file.exists())
						{
							if(file.isFile())
							{
								if(path.substring(path.lastIndexOf(".")+1).equals("krc"))     
									{
										String outputGechi=myFile.appDatePath2+path.substring(path.lastIndexOf("/"),path.lastIndexOf("."))+".mpc";
										ReadKrc.Krc_To_Lrc(path,outputGechi);
										save_muise_gechi(MainActivity.musicPath[MainActivity.ISPLAYING_MUSIC_ID],outputGechi);				
										Toast.makeText(A2.this,"已保存,下次播放生效",800).show();
										button_gechi.setBackgroundResource(R.drawable.image_gechi_ok3);
									}
						     	else
								        if(myDate.cookFile(path))
								             {
									          save_muise_gechi(MainActivity.musicPath[MainActivity.ISPLAYING_MUSIC_ID],path);
									          Toast.makeText(A2.this,"已保存,下次播放生效",800).show();
									          button_gechi.setBackgroundResource(R.drawable.image_gechi_ok3);
								              }
								          else
								             { 
									            Toast.makeText(A2.this,"坏的歌词文件! Bad File!",600).show();
								              }
							}
							else
							{
								Toast.makeText(A2.this,"该路径是一个文件夹!",600).show();
							}
						}
						else
						{
							Toast.makeText(A2.this,"该路径不存在!",600).show();
						}
					}
					else
					{
						Toast.makeText(A2.this,"输入为空!",600).show();
					}
				}
			});
		final AlertDialog dil= builder.show();
		button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					   dil.dismiss();
					   Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					   intent.setType("text/plain");
					   intent.addCategory(Intent.CATEGORY_OPENABLE);
					   startActivityForResult(intent,23232+47730); 	   
				}
			});
		b1.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{    
				    File f=new File("/storage/emulated/0/kugou/lyrics");
					if(f.exists())
					{
						getGechi_listDialog_kuogou();
						dil.dismiss();
					}
					else
						Toast.makeText(A2.this,"错误!",2000).show();
				}
			});
		b2.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					File f=new File("/storage/emulated/0/netease/cloudmusic/Download/Lyric");
					if(f.exists()&&f.isDirectory())
					{
						getGechi_listDialog_WYi();
						dil.dismiss();
					}
                    else
						Toast.makeText(A2.this,"错误!",2000).show();
				}
			});
    }
	
	public void getGechi_listDialog_kuogou(){
		File Kugou=new File("/storage/emulated/0/kugou/lyrics");
		File Gechi_file[]=Kugou.listFiles();
	    final String strGechi[]=new String[Gechi_file.length];
		for(int i=0;i<Gechi_file.length;i++)
		    {
				if(Gechi_file[i].isFile())
					strGechi[i]=Gechi_file[i].toString();
				else
					strGechi[i]="";
			}
		final LinearLayout mainlayout=new LinearLayout(A2.this);
		final ListView listv=new ListView(A2.this);
		ListAdapter adapter = new FileAdapter(this, strGechi);
		listv.setAdapter(adapter);
		listv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> list, View v, int position, long id)
				{			 
					//removeGechiDialog();
					 Toast.makeText(A2.this,strGechi[position]+"长按以选择",1000).show();
				}
			});
		
		final TextView textv = new TextView(A2.this);
		textv.setWidth(myDate.dip2px(A2.this,270));
		textv.setHeight(60);
		textv.setText("选择歌词:");
		textv.setTextSize(16);
		textv.setTextColor(Color.BLACK);
		mainlayout.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mainlayout.setBackgroundColor(getResources().getColor(R.color.darkgray));
		mainlayout.addView(textv);
		mainlayout.addView(listv);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(A2.this);
		builder.setView(mainlayout);
	    final  AlertDialog aler= builder.show();
		listv.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					String gechi = strGechi[p3];
					myFile.createMPath();
					String outputGechi=myFile.appDatePath2+gechi.substring(gechi.lastIndexOf("/"),gechi.lastIndexOf("."))+".mpc";
					boolean is_lrc=gechi.substring(gechi.lastIndexOf(".")+1).equals("lrc");
					if(is_lrc)
						myFile.writeToFile( ReadKrc.addHuice(ReadKrc.readGechiWY(myFile.readFileToStr(gechi))),outputGechi);
					else
					    ReadKrc.Krc_To_Lrc(gechi,outputGechi);
					save_muise_gechi(MainActivity.musicPath[MainActivity.ISPLAYING_MUSIC_ID],outputGechi);
                    aler.dismiss();
					Toast.makeText(A2.this,"已添加，下次播放生效",1000).show();
					return false;
				}
			});
	}
	
    public void getGechi_listDialog_WYi(){
		File Kugou=new File("/storage/emulated/0/netease/cloudmusic/Download/Lyric");
		File Gechi_file[]=Kugou.listFiles();
	    final String strGechi[]=new String[Gechi_file.length];
		for(int i=0;i<Gechi_file.length;i++)
		{
			if(Gechi_file[i].isFile())
				strGechi[i]=Gechi_file[i].toString();
			else
				strGechi[i]="";
		}
		final LinearLayout mainlayout=new LinearLayout(A2.this);
		final ListView listv=new ListView(A2.this);
		ListAdapter adapter = new FileAdapter(this, strGechi);
		listv.setAdapter(adapter);
		listv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> list, View v, int position, long id)
				{			 
					//removeGechiDialog();
					Toast.makeText(A2.this,strGechi[position]+"长按以选择",1000).show();
				}
			});

		final TextView textv = new TextView(A2.this);
		textv.setWidth(myDate.dip2px(A2.this,270));
		textv.setHeight(60);
		textv.setText("选择歌词:");
		textv.setTextSize(16);
		textv.setTextColor(Color.BLACK);
		mainlayout.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mainlayout.setBackgroundColor(getResources().getColor(R.color.darkgray));
		mainlayout.addView(textv);
		mainlayout.addView(listv);

		AlertDialog.Builder builder = new AlertDialog.Builder(A2.this);
		builder.setView(mainlayout);
	    final  AlertDialog aler= builder.show();
		listv.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					String gechi = strGechi[p3];
					myFile.createMPath();
					String outputGechi=myFile.appDatePath2+gechi.substring(gechi.lastIndexOf("/"))+".mpc";
					myFile.writeToFile(ReadKrc.readGechiWY(myFile.readFileToStr(gechi)),outputGechi);
					save_muise_gechi(MainActivity.musicPath[MainActivity.ISPLAYING_MUSIC_ID],outputGechi);
                    aler.dismiss();
					Toast.makeText(A2.this,"已添加，下次播放生效",1000).show();
					return false;
				}
			});
	}
	
	

	private void removeGechiDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(A2.this);
        builder.setTitle("确定移除歌词?");
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					remove_music_gechi(MainActivity.musicPath[MainActivity.ISPLAYING_MUSIC_ID]);
					Toast.makeText(A2.this,"已移除",600).show();
					button_gechi.setBackgroundResource(R.drawable.image_gechi_null);
				}
		    });
		builder.show();
	}

	public void Gechi_Setting_dialog(){ //歌词设置
	    final int IsPlaymusic_id=MainActivity.ISPLAYING_MUSIC_ID; //判断在修改期间是否切歌
		final int last_dt=getsave_music_gechi_dt(MainActivity.musicPath[MainActivity.ISPLAYING_MUSIC_ID]);//用于判断是否修改
		final ToastMy toast=new ToastMy(A2.this);
		AlertDialog.Builder builder = new AlertDialog.Builder(A2.this);
		builder.setCancelable(false);//点击外部不取消
		
		final TextView title=new TextView(A2.this);
		title.setWidth(myDate.dip2px(A2.this,240));
		title.setHeight(myDate.dip2px(A2.this,35));
		title.setBackgroundColor(getResources().getColor(R.color.bluemy));
		title.setTextSize(17);
		title.setTextColor(Color.WHITE);
		title.setGravity(Gravity.LEFT|Gravity.CENTER);
		title.setText(MainActivity.ISPLAYING_singerName+ " - "+MainActivity.ISPLAYING_musicName);
		
		final TextView textVeiw = new TextView(A2.this);
		textVeiw.setText("进度调整:");
		textVeiw.setTextSize(17);
		textVeiw.setHeight(myDate.dip2px(A2.this,35));
		textVeiw.setWidth(myDate.dip2px(A2.this,79));

		final Button addProgress_button = new Button(A2.this);
		addProgress_button.setWidth(myDate.dip2px(A2.this,50));
		addProgress_button.setHeight(30);
		addProgress_button.setText("+0.1秒");
		addProgress_button.setTextSize(14);
		addProgress_button.setGravity(Gravity.CENTER);
		addProgress_button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{ 
					   int dt=getsave_music_gechi_dt(MainActivity.musicPath[ IsPlaymusic_id]);
					   save_muise_gechi_dt(MainActivity.musicPath[ IsPlaymusic_id],dt-100);
					   dt-=100;
					if(IsPlaymusic_id==MainActivity.ISPLAYING_MUSIC_ID)
						toast.Show("当前调整时间为: "+(-1*dt)+"ms");
					else
						toast.Show("当前调整时间为: "+(-1*dt)+"ms,下次播放生效");
				}
			});

		final Button reProgress_button = new Button(A2.this);
		reProgress_button.setWidth(myDate.dip2px(A2.this,50));
		reProgress_button.setHeight(30);
		reProgress_button.setText("重置");
		reProgress_button.setTextSize(14);
		reProgress_button.setGravity(Gravity.CENTER);
		reProgress_button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{  
					   save_muise_gechi_dt(MainActivity.musicPath[IsPlaymusic_id],0);
					if(IsPlaymusic_id==MainActivity.ISPLAYING_MUSIC_ID)
						toast.Show("已重置");
					else
						toast.Show("已重置,下次播放生效");
					   
				}
			});

		final Button jianProgress_button = new Button(A2.this);
		jianProgress_button.setWidth(myDate.dip2px(A2.this,50));
		jianProgress_button.setHeight(30);
		jianProgress_button.setText("-0.1秒");
		jianProgress_button.setTextSize(14);
		jianProgress_button.setGravity(Gravity.CENTER);
		jianProgress_button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{  
				      int dt=getsave_music_gechi_dt(MainActivity.musicPath[ IsPlaymusic_id]);
					  save_muise_gechi_dt(MainActivity.musicPath[ IsPlaymusic_id],dt+100);
					  dt+=100;
					  if(IsPlaymusic_id==MainActivity.ISPLAYING_MUSIC_ID)
						   toast.Show("当前调整时间为: "+(-1*dt)+"ms");
						else
					       toast.Show("当前调整时间为: "+(-1*dt)+"ms,下次播放生效");
				}
			});

		final LinearLayout layout_child1 = new LinearLayout(A2.this);
		layout_child1.addView(textVeiw);
		layout_child1.addView(jianProgress_button);
		layout_child1.addView(reProgress_button);
		layout_child1.addView(addProgress_button);

		final TextView dtextVeiw = new TextView(A2.this);
		dtextVeiw.setText("删除歌词:");
		dtextVeiw.setTextSize(17);
		dtextVeiw.setHeight(myDate.dip2px(A2.this,35));
		dtextVeiw.setWidth(myDate.dip2px(A2.this,79));

		final Button removebutton = new Button(A2.this);
		removebutton.setWidth(myDate.dip2px(A2.this,60));
		removebutton.setHeight(30);
	    removebutton.setText("删除");
		removebutton.setTextSize(14);
		removebutton.setTextColor(Color.RED);
		removebutton.setGravity(Gravity.CENTER);
		removebutton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1){  removeGechiDialog();  }
			});

		final Button changebutton = new Button(A2.this);
		changebutton.setWidth(myDate.dip2px(A2.this,60));
		changebutton.setHeight(30);
	    changebutton.setText("修改歌词");
		changebutton.setTextSize(14);
		changebutton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1){ 
			//	removeGechiDialog();
			      changgeDialog(getsave_music_gechi(MainActivity.musicPath[IsPlaymusic_id]));
				}
			});
		
		final LinearLayout layout_child2 = new LinearLayout(A2.this);
		layout_child2.addView(dtextVeiw);
		layout_child2.addView(removebutton);
		layout_child2.addView(changebutton);

		final LinearLayout mainlayout = new LinearLayout(A2.this);
		mainlayout.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mainlayout.addView(title);
		mainlayout.addView(layout_child1);
		mainlayout.addView(layout_child2);
		builder.setView(mainlayout);
        builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					int newdt=getsave_music_gechi_dt(MainActivity.musicPath[ IsPlaymusic_id]);
					if( (newdt!=last_dt&&IsPlaymusic_id==MainActivity.ISPLAYING_MUSIC_ID)||haschang )
						music_service.Gechi=myDate.ReadGechi(getsave_music_gechi(MainActivity.musicPath[IsPlaymusic_id]),getsave_music_gechi_dt(MainActivity.musicPath[ IsPlaymusic_id]));
					dialog.dismiss();
				}
		    });
	    builder.show();
	}
	
	
	private boolean haschang=false;//是否修改歌词
	public void changgeDialog(final String file){//修改歌词对话框
	    haschang = false;
		AlertDialog.Builder builder = new AlertDialog.Builder(A2.this);
        final EditText tv=new EditText(A2.this);
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(12);
		tv.setGravity(Gravity.CENTER|Gravity.LEFT);
		tv.setWidth(myDate.dip2px(A2.this,250));
		tv.setHeight(myDate.dip2px(A2.this,700));
		tv.setText(myFile.readFileToStr(file));
		builder.setView(tv);
		builder.setPositiveButton("保存并退出", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					myFile.writeToFile(ReadKrc.addHuice( tv.getText().toString()),file);
					Toast.makeText(A2.this,"已保存",500).show();
				    haschang=true;
					dialog.dismiss();
				}
		    });
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					haschang=false;
					dialog.dismiss();
				}
			});
		builder.show();
	}
	
	//End######对话框######End######对话框######End######对话框######End######对话框######
	public void Start_ui(){//初始化ui,在onCreate执行
	    setbutton_playmod_bg();
		if(!MainActivity.ISPLAYING) button_start_stop.setBackground(getResources().getDrawable(R.drawable.stoping));
		if(!MainActivity.ISPLAYING_musicName.equals(""))
		  {
			  musicNameTextv.setText(MainActivity.ISPLAYING_musicName);
			  singerNameTextv.setText(MainActivity.ISPLAYING_singerName);
			  musicLeight_Textv.setText("/"+MainActivity.Length_MUSIC_String);
			  progressbar.setProgress(MainActivity.Music_Progress);
			  progressTextv.setText(MainActivity.Progress_MUSIC_String);
			if(MainActivity.ISPLAYING_singer_id!=-1) 
				new Thread(new Runnable(){
				  public void run(){
					  try{ Thread.sleep(180);}catch(Exception e){}
					  sendMsg(3);
				  }
			  }).start();
		  }
		if( Gechi_is_ok = !(getsave_music_gechi(MainActivity.musicPath[MainActivity.ISPLAYING_MUSIC_ID]).equals("")) )
		   {
		
			   button_gechi.setBackgroundResource(R.drawable.image_gechi_ok3);
			//   Gechi = myDate.ReadTxtFile2(getsave_music_gechi(MainActivity.ISPLAYING_MUSIC_ID));//初始化歌词
		 //  len=Gechi.length;
		    }
		  else
		   {
			   button_gechi.setBackgroundResource(R.drawable.image_gechi_null);
		   }
	}
	
	public void Start_ui_next(){//初始化ui,在切歌后执行
		if(!MainActivity.ISPLAYING) button_start_stop.setBackground(getResources().getDrawable(R.drawable.stoping));
		if(!MainActivity.ISPLAYING_musicName.equals(""))
		{
			musicNameTextv.setText(MainActivity.ISPLAYING_musicName);
			singerNameTextv.setText(MainActivity.ISPLAYING_singerName);
			musicLeight_Textv.setText("/"+MainActivity.Length_MUSIC_String);
			progressbar.setProgress(MainActivity.Music_Progress);
			progressTextv.setText(MainActivity.Progress_MUSIC_String);
			
		}
		if(getsave_music_gechi(MainActivity.musicPath[ MainActivity.ISPLAYING_MUSIC_ID]).equals(""))
		{
			button_gechi.setBackgroundResource(R.drawable.image_gechi_null);
		}
		else
		{
			button_gechi.setBackgroundResource(R.drawable.image_gechi_ok3);
		}
	}
	
	public static void set_W_H(View view,int width,int height){//改变view的大小
		RelativeLayout.LayoutParams linearParams =(RelativeLayout.LayoutParams) view.getLayoutParams(); 
		linearParams.width = width;
		linearParams.height = height;
		view.setLayoutParams(linearParams);    }
	
	public void save_muise_v(String i,int value){//保存歌曲单独配置音量
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putInt(String.valueOf(i+"2323247730")+MainActivity.Music_item_Name, value);
		editor.commit();
	}
	
	public void save_muise_gechi(String name,String path){//保存歌曲歌词文件
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putString(String.valueOf(name+"2323247730_getchi")+MainActivity.Music_item_Name, path);
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
	public void save_muise_gechi_dt(String id,int dt){//保存歌曲歌词偏移量
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putInt(id+"2323247730_getchi_dt"+MainActivity.Music_item_Name, dt);
		editor.commit();
	}
	
	public String getsave_music_gechi(String id){//获取歌词文件路径
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		return shared.getString(id+"2323247730_getchi"+MainActivity.Music_item_Name,"");
	}
	public int getsave_music_gechi_dt(String id){//获取歌词偏移量
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		return shared.getInt(id+"2323247730_getchi_dt"+MainActivity.Music_item_Name,0);
	}
	
	public void save_gechi_is_full(){//保存歌曲歌词偏移量
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putBoolean("gechi_is_full", gechi_is_full);
		editor.commit();
	}
	
	public boolean get_gechi_is_full(){//获取歌词偏移量
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		return shared.getBoolean("gechi_is_full",false);
	}
					
	public void sendMsgToMainActivity(int msg){
		  MainActivity.Msg_Form_A2=msg;
	}
	
	public void sendMsg(int what){
		Message msg=new Message();
		msg.what=what;
		handler.sendMessage(msg);
	}

	public void save_PLAY_MODE(int play_mod){//保存
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putInt("Play_Mod"+MainActivity.Music_item_Name, play_mod);
		editor.commit();
	}
	
	public int getisPlayingNumber(){//获取正在播放的歌曲在数组中的序列号
		SharedPreferences shared = getSharedPreferences("playing_file",0);
		return shared.getInt("isPlayings"+MainActivity.Music_item_Name,0);
	}
	
	public void saveSettingDate(String dateName,boolean date){//保存
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		SharedPreferences.Editor editor=shared.edit();
		editor.putBoolean(dateName+MainActivity.Music_item_Name , date);
		editor.commit();
	}
	
	public boolean getSettingDate(String dateName){
		SharedPreferences shared = getSharedPreferences(MainActivity.MUSIC_PATH,0);
		return shared.getBoolean(dateName+MainActivity.Music_item_Name,true);
	}
	
	public static boolean hasExternalStoragePermission(Context context){//判断是否有文件读写权限
		int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
		return perm == PackageManager.PERMISSION_GRANTED;
	}
	
	public static boolean hasNotchInScreen(Context context) {
		boolean ret = false;
		try {//。.判断是否是刘海屏
			 ClassLoader cl = context.getClassLoader();

			 Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");

			 Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");

			 ret = (boolean) get.invoke(HwNotchSizeUtil);

		} catch (ClassNotFoundException e) {

			//​ Log.e("test", "hasNotchInScreen ClassNotFoundException");

		} catch (NoSuchMethodException e) {

			//​ Log.e("test", "hasNotchInScreen NoSuchMethodException");

		} catch (Exception e) {

			//​ Log.e("test", "hasNotchInScreen Exception");

		} finally {

			 return ret;

		}
	}
	
	

	
	/**
     * 判断xiaomi是否有刘海屏
     * https://dev.mi.com/console/doc/detail?pId=1293
     *
     * @param activity
     * @return
     */
    private static boolean hasNotchXiaoMi(Activity activity) {
        return getInt("ro.miui.notch", activity) == 1;
    }

    /**
     * 小米刘海屏判断.
     *
     * @return 0 if it is not notch ; return 1 means notch
     * @throws if the key exceeds 32 characters
     */
    public static int getInt(String key, Activity activity) {
        int result = 0;
        try {
            ClassLoader classLoader = activity.getClassLoader();
            @SuppressWarnings("rawtypes")
				Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            //参数类型
            @SuppressWarnings("rawtypes")
				Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = int.class;
            Method getInt = SystemProperties.getMethod("getInt", paramTypes);
            //参数
            Object[] params = new Object[2];
            params[0] = new String(key);
            params[1] = new Integer(0);
            result = (Integer) getInt.invoke(SystemProperties, params);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
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
