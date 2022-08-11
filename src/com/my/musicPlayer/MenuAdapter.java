package com.my.musicPlayer;

import android.*;
import android.content.*;
import android.view.*;
import android.widget.*;

public class MenuAdapter extends ArrayAdapter<String>
{
	public MenuAdapter(Context context, String[] values) 
	{
		super(context, R.layout.menulistv, values);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		return  getmyView(position,parent);

	}

	public View getmyView(int position, ViewGroup parent)
	{
		//final int post=position;
		final String filename=getItem(position);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.menulistv, parent, false);
	    TextView icon= view.findViewById(R.id.menulistvTextView1);
		TextView item= view.findViewById(R.id.menulistvTextView2);
		if(position==0||position==1||position==2)
		  {
			  icon.setBackgroundResource(R.drawable.menu1_3);
		  }
		else
		  {
			  if(position==3)
				  icon.setBackgroundResource(R.drawable.menu4);
			  else
				 if(position==4)
					 icon.setBackgroundResource(R.drawable.menu5);
		  }
		item.setText(filename);
		return view;
      }
}
