package com.my.musicPlayer;

import java.io.*;
import java.math.*;
import java.util.regex.*;
import java.util.zip.*;

public class ReadKrc
{
	
	public static String ReadKrcFiletoStr(String filename){
		String root="";//读取krc文件
		try	{  root= new KrcText().getKrcText(filename); }catch (IOException e){}
		return removeHuice( replayGechiTime( makeGechi( root)));
	}

	public static boolean Krc_To_Lrc(String Krcfilename,String Lrcfilename)
	   {  //krc文件转化为lrc
		   File OutputLrcFile=new File(Lrcfilename);
		   FileWriter fw;
		  try {
			   fw=new FileWriter(OutputLrcFile);
			   String LrcString = ReadKrcFiletoStr(Krcfilename);
			   fw.write(LrcString);
			   fw.close();
			   return true;
		     }
		   catch (IOException e)
		   { 
		     e.printStackTrace(); 
			 return false;
		    }  
	   }
	
   public static String readGechiWY(String gechi){
	   gechi = gechi.replace("\\","");
	   String su="",stri="";
	   for(int i=0;i<gechi.length()-1;i++)
	     {
			 stri=gechi.substring(i,i+1);
			 if(stri.equals("n") && gechi.substring(i+1,i+2).equals("["))
				su=su+"\n" ;
				 else
					 su=su+stri;
		 }
	   return su;
   }
	
   public static String addHuice(String gechu){
	   String su="";   //加入回车
	   String stri="";
	   for(int i=0;i<gechu.length()-1;i++)
	     {
			 stri=gechu.substring(i,i+1);
			 if(gechu.substring(i+1,2+i).equals("[")&&!stri.equals("\n"))
				 su=su+stri+"\n";
			 else
				 su=su+stri;
		 }     
		 return su;
   }
	
	private static  String makeGechi(String GECHI)
	{
		boolean k=true;
		String gechi="";
		for(int i=0;i<GECHI.length();i++)
		{
			String stri=GECHI.substring(i,i+1);
			if(stri.equals("<")) k=false;		   
			if( k && !stri.equals(",")) gechi=gechi+stri; 		   
			if(stri.equals(">")) k=true;   
		}		   
		return gechi;
	}

	private static String replayGechiTime(String GECHI)
	{
		//boolean k=true;
		String gechi="";
		String time="";
		long lastTime=0l;
		for(int i=0;i<GECHI.length();i++)
		{
			String stri=GECHI.substring(i,i+1);
			if(stri.equals("["))
				for(int j=i+1;;j++)
				{
					String strj=GECHI.substring(j,j+1);
					if(!strj.equals("]")&&j-i<30)
						time=time+strj;
					else
					{  
						if(checkStrIsNum(time))
						{ 
							long nowTime=String2Long(time);
							if(nowTime>lastTime)
							{
								gechi=gechi+stri+makeTime(nowTime)+strj;
							}
							i=j;
							lastTime=nowTime;
						}
						break;
					}
				}
		    else  gechi=gechi+stri;
		    time=""; 
		}		   
		return  gechi;
	}

	private static String removeHuice(String tiget){//除去不正确的回车,建议最后执行
		String gechi="";
		String nextChar="";
	    for(int i=0;i<tiget.length()-1;i++)
		{
		    String stri=tiget.substring(i,i+1);
			if(stri.equals("\n"))
			{
				nextChar=tiget.substring(i+1,i+2);
				if(nextChar.equals("["))
					gechi=gechi+stri;
			}
			else
				gechi=gechi+stri;
		}
		return gechi;
	}

	private static String makeTime(long time){
		//18.78s==18783 3554
		long time_ms=time/10000;  //毫秒
		long time_s=time_ms/1000;  //秒
		long mm=time_s/60;   //分
		long ss=time_s%60;   //秒
		long ms=(time_ms%1000)/10;
		String min,s,mms=null;
		if(mm<10)  min="0"+mm; else min=mm+"";
		if(ss<10)  s="0"+ss ;  else s=ss+"";
		if(ms<10) mms="0"+ms;  else mms=""+ms;	
		return min+":"+s+"."+mms;
	}

	private static long String2Long(String stra)
	{
		long a=0;
		if(stra.length()<10)
			a = Integer.valueOf(stra);
		else
		{
			String stra1=stra.substring(9);
			String stra2=stra.substring(0,9);
			int n=stra.length()-9;
			int a1= Integer.valueOf(stra1);
			long a2= Integer.valueOf(stra2);
			long ka2=(long) Math.pow(10,n);
			a=ka2*a2+a1;
		}
		return a;
	}
	public static boolean checkStrIsNum(String str) { 
		String bigStr; 
		try {      
			/** 先将str转成BigDecimal，然后在转成String */
			bigStr = new BigDecimal(str).toString(); 
		} catch (Exception e) { 
			/** 如果转换数字失败，说明该str并非全部为数字 */
			return false; 
		} 
	    Pattern NUMBER_PATTERN = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
		Matcher isNum = NUMBER_PATTERN.matcher(str); 
		if (!isNum.matches()) {    
			return false; 
		}  
		return true;
	}
	
}
class ZLibUtils{		
	public static byte[] compress(byte[] data) {	
		byte[] output = new byte[0];			
		Deflater compresser = new Deflater();		
		compresser.reset();		
		compresser.setInput(data);			
		compresser.finish();			
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);		
		try {			
			byte[] buf = new byte[1024];		
			while (!compresser.finished())
			{			
				int i = compresser.deflate(buf);	
				bos.write(buf, 0, i);		
			}			
			output = bos.toByteArray();			
		} catch (Exception e) {			
			output = data;			
			e.printStackTrace();			
		} finally {			
			try {			
			    bos.close();			
			} catch (IOException e) {			
				e.printStackTrace();				}	
		}			
		compresser.end();				
		return output;			}			
	public static void compress(byte[] data, OutputStream os) {		
		DeflaterOutputStream dos = new DeflaterOutputStream(os);		
		try {			
			dos.write(data, 0, data.length);		
			dos.finish();			
			dos.flush();			
		} catch (IOException e) {			
			e.printStackTrace();				}	
	}					
	public static byte[] decompress(byte[] data) {
		byte[] output = new byte[0];			
		Inflater decompresser = new Inflater();		
		decompresser.reset();			
		decompresser.setInput(data);		
		ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);		
		try {	
			byte[] buf = new byte[1024];		
			while (!decompresser.finished())
			{	
				int i = decompresser.inflate(buf);	
				o.write(buf, 0, i);			
			}	
			output = o.toByteArray();			
		} catch (Exception e) 
		{  
			output = data;			
			e.printStackTrace();			
		} finally {			
			try {			
				o.close();				
			} catch (IOException e) {			
				e.printStackTrace();				}	
		}			
		decompresser.end();			
		return output;			}		
	public static byte[] decompress(InputStream is) 
	{	
		InflaterInputStream iis = new InflaterInputStream(is);			
		ByteArrayOutputStream o = new ByteArrayOutputStream(1024);		
		try {			
			int i = 1024;			
			byte[] buf = new byte[i];			
			while ((i = iis.read(buf, 0, i)) > 0) 
			{			
				o.write(buf, 0, i);			
			}				
		} catch (IOException e) {	e.printStackTrace();	}		
		return o.toByteArray();				}		
}

class KrcText{   
	private static final char[] miarry = { '@', 'G', 'a', 'w', '^', '2', 't',       
		'G', 'Q', '6', '1', '-', 'Î', 'Ò', 'n', 'i' };   
	//public static void main(String[] args) throws IOException    {   
	 //String filenm = "";//krc文件的全路径加文件名     
	//	System.out.println(new KrcText().getKrcText(filenm));// 用例   
	//   }  
	/**     *      * @param filenm krc文件路径加文件名     * @return krc文件处理后的文本     * @throws IOException     */ 
	public String getKrcText(String filenm) throws IOException    {      
		File krcfile = new File(filenm);   
		byte[] zip_byte = new byte[(int) krcfile.length()];     
		FileInputStream fileinstrm = new FileInputStream(krcfile);     
		byte[] top = new byte[4];   
		fileinstrm.read(top);  
		fileinstrm.read(zip_byte); 
		int j = zip_byte.length;    
		for (int k = 0; k < j; k++)   
		{          
			int l = k % 16;        
			int tmp67_65 = k;      
			byte[] tmp67_64 = zip_byte;     
			tmp67_64[tmp67_65] = (byte) (tmp67_64[tmp67_65] ^ miarry[l]);      
		}     
		String krc_text = new String(ZLibUtils.decompress(zip_byte), "utf-8");    
		return krc_text;  
	}

}
