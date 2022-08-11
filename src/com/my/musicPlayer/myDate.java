package com.my.musicPlayer;

import android.content.*;
import java.io.*;

public class myDate
{
		
	
	static int getIntOn(int a范围){
		int result=2;
		double a=Math.random();
		double b=1.0/a范围;
		double c=b;
		for(int i=1;i<=a范围; i++)
		{
			if(a<c)
			{
				result=i;
				break;
			}else{
				c=b*i;
			}
		}		
	 	return result;
	 }//在0到指定范围内随机生成一个数,不包括0，包括a
	 
	public static int radom(){
		int ruselt=0;
		double a=Math.random();
		double b=Math.random(); 
		if(b>0.5)
			ruselt=(int)(a*(-10));
		else
			ruselt=(int)(a*10);
		return ruselt;
	}
	 
	public static boolean cookFile(String FilePath)
    { //检验文件是否是歌词文件
	    boolean rusult=false;
		File file = new File(FilePath);    //打开文件
		if (!file.isDirectory())  
		{    //如果path是传递过来的参数，可以做一个非目录的判断	
			try {
				InputStream instream = new FileInputStream(file); 
				if (instream != null) 
				{
					InputStreamReader inputreader = new InputStreamReader(instream);
         			BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
					while ( (line= buffreader.readLine())!= null ) {
					   int a= line.indexOf("[");
					   int b= line.indexOf("]");
					   int c= line.indexOf(":");
					   int d= line.indexOf(".");
					   if(a==0 && (b==9||b==10) && c==3 && d==6)
						 {
						     rusult=true;
							 break;
					     }				   
					}                
					instream.close();
				}
			}catch(Exception e){}
		}
		return rusult;
    }
	 
	public static String[][] ReadTxtFile2(String FilePath)
    { //读取歌词文件内容
		int line_number=getTxtFileLine(FilePath);
		String[][] rusult=rusult=new String[line_number][2];    //文件内容字符串
		File file = new File(FilePath);    //打开文件
		if (!file.isDirectory())  
		{    //如果path是传递过来的参数，可以做一个非目录的判断	
			try {
				InputStream instream = new FileInputStream(file); 
				if (instream != null) 
				{
					InputStreamReader inputreader = new InputStreamReader(instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line=null;//每行内容
					int i=0;
					while((line=buffreader.readLine())!=null){
						int a= line.indexOf("[");
						int b= line.indexOf("]");
						int c= line.indexOf(":");
						int d= line.indexOf(".");
						if(a==0 && b==9 && c==3 && d==6)
						   {			   
						     rusult[i][0]=line.substring(1,8);
						     rusult[i][1]=line.substring(10);
							 i++;
						   }
						
					}
				    instream.close();
				}
			}catch(Exception e){}
		}
		return rusult;
    }

	
	public static GeChi ReadGechi(String FilePath,int dt/*歌词时间偏移量，单位是0.1s*/)
    { //读取歌词文件内容
	    int length=getTxtFileLine(FilePath);
		GeChi rusult= new GeChi();   //文件内容字符串
		rusult.len=length;
		rusult.time=new int[length];
		rusult.word=new String[length];
		File file = new File(FilePath);    //打开文件
		if (!file.isDirectory())  
		{    //如果path是传递过来的参数，可以做一个非目录的判断	
			try {
				InputStream instream = new FileInputStream(file); 
				if (instream != null) 
				{
					InputStreamReader inputreader = new InputStreamReader(instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line=null;//每行内容
					int i=0;
					while((line=buffreader.readLine())!=null){
						int a= line.indexOf("[");
						int b= line.indexOf("]");
						int c= line.indexOf(":");
						int d= line.indexOf(".");
						if(a==0 && (b==9||b==10) && c==3 && d==6)
						{	
						    int p=Integer.valueOf(line.substring(7,8))*100;
							int q=Integer.valueOf(line.substring(8,9));
							if(q>=5) p+=100;
							int time =Integer.valueOf(line.substring(1,3))*60*1000
							              +Integer.valueOf(line.substring(4,6))*1000
										  +p + dt;
							if(time>=100)//如果时间为负，忽略
								{
							      rusult.time[i]=time;
							      rusult.word[i]=line.substring(10);
							      i++;
							    }
						}

					}
				    instream.close();
				}
			}catch(Exception e){}
		}
		return rusult;
    }
	
	
	public static int getTxtFileLine(String strFilePath)
    {
		int i=0;
		File file = new File(strFilePath);    //打开文件
		if (!file.isDirectory())  
		{    //如果path是传递过来的参数，可以做一个非目录的判断	
			try {
				InputStream instream = new FileInputStream(file); 
				if (instream != null) 
				{
					InputStreamReader inputreader = new InputStreamReader(instream);
         			BufferedReader buffreader = new BufferedReader(inputreader);
                    String line=null;
					while ((line= buffreader.readLine()) != null) {
						if(line.indexOf(":")!=-1)
					       i++;
					}                
					instream.close();
				}
			}catch(Exception e){}
		}
		return i;
    }
	 
	public static int dip2px(Context context, float dpValue) { //dp转化dx
		final float scale = context.getResources().getDisplayMetrics().density ; 
		return (int) (dpValue * scale + 0.5f) ;
	}
	
}
class GeChi{
	int time[]=null;
	String word[]=null;
	int len=0;
}
