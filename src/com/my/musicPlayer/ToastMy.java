package com.my.musicPlayer;

import android.content.*;
import android.widget.*;

public class ToastMy
 {
	private Context context;
	private Toast toast = null;
	public ToastMy(Context context) {
		this.context = context;
	}
	public void Show(String text) {
		if(toast == null)
		{
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		else {
			toast.setText(text);
		}
		toast.show();
	}
}
