package com.my.musicPlayer;

import android.app.*;
import android.os.*;
import android.view.*;

public class Player_activity extends Activity
{
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
	 }
}
