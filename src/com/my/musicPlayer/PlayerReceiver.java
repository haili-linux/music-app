package com.my.musicPlayer;

import android.content.*;
import android.util.*;
import android.widget.*;

public class PlayerReceiver extends BroadcastReceiver
 {
	static String ACTION_PLAY_PAUSE="h8eH0_k";
	static String ACTION_PAUSE="Sm5jX53";
	static String ACTION_LAST="MadeBy2323247730";
	static String ACTION_NEXT="v5rP6yElh";
    private static final String TAG = PlayerReceiver.class.getSimpleName();
	
	public PlayerReceiver() {  }
	
      @Override
	 public void onReceive(Context context, Intent intent) {
		   // TODO: This method is called when the BroadcastReceiver is receiving
		     String action = intent.getAction();// 获取对应Action
		    //Log.d(TAG,"action:"+action);
		
		   if(action.equals(ACTION_PLAY_PAUSE)){
			   // 进行对应操作
			      MainActivity.Msg_Form_A2=1;
		       } 
	
	}
}
