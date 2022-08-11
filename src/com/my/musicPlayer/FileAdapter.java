package com.my.musicPlayer;

import android.app.*;
import android.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.graphics.*;

public class FileAdapter extends ArrayAdapter<String>
{
	public FileAdapter(Context context, String[] values) 
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
		//final int post=position;
		final String filename=getItem(position);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.filelistv, parent, false);
		TextView imageView = view.findViewById(R.id.filelistvImageView);
		TextView textviee = view.findViewById(R.id.filelistvTextView);
		Button bt = view.findViewById(R.id.filelistvButton1);
		final boolean is_krc=!filename.equals("")&&filename.substring(filename.lastIndexOf(".")+1).equals("krc");
		final boolean is_lrc=!filename.equals("")&&filename.substring(filename.lastIndexOf(".")+1).equals("lrc");
		bt.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					if(is_krc||(filename.indexOf(".")==-1&&!filename.equals(""))||is_lrc)
					     readKrcDialog(filename);
				    else 
						Toast.makeText(getContext(),"不是歌词，不可用!",1000).show();
				}
			});
			
		if(is_krc||(filename.indexOf(".")==-1&&!filename.equals(""))||is_lrc)
		    textviee.setText(filename.substring(filename.lastIndexOf("/")+1));	
		  else
		  {
			  textviee.setText(filename.substring(filename.lastIndexOf("/")+1));	
			  imageView.setText("X");
		  }
		 
	    return view;
	}
	
	public void readKrcDialog(String file){
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final TextView tv=new TextView(getContext());
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(12);
		tv.setGravity(Gravity.CENTER|Gravity.LEFT);
		tv.setWidth(myDate.dip2px(getContext(),250));
		tv.setHeight(myDate.dip2px(getContext(),700));
		
		if(file.indexOf(".")!=-1)
		{
		    boolean is_krc=file.substring(file.lastIndexOf(".")+1).equals("krc");
		//    boolean is_lrc=file.substring(file.lastIndexOf(".")+1).equals("krc");
			if(is_krc)
		         tv.setText(file.substring(file.lastIndexOf("/")+1)+"\n"+ReadKrc.ReadKrcFiletoStr(file));
			else
				tv.setText(file.substring(file.lastIndexOf("/")+1)+"\n"+ReadKrc.readGechiWY( myFile.readFileToStr(file)));
         }
		 else
			tv.setText(file.substring(file.lastIndexOf("/")+1)+"\n"+ReadKrc.readGechiWY( myFile.readFileToStr(file)));
			
		builder.setView(tv);
		builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
		    });
		builder.show();
	}
}
