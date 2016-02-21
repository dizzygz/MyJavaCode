
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.*;
/**
 * Description:
 * <br/>网站: <a href="http://www.crazyit.org">疯狂Java联盟</a>
 * <br/>Copyright (C), 2001-2016, Yeeku.H.Lee
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:
 * <br/>Date:
 * @author Yeeku.H.Lee kongyeeku@163.com
 * @version 1.0
 */
public class DownUtil
{
	// 定义下载资源的路径
	private String path;
	// 指定所下载的文件的保存位置
	private String targetFile;
	// 定义需要使用多少线程下载资源
	private int threadNum;
	// 定义下载的线程对象
	private DownThread[] threads;
	// 定义下载的文件的总大小
	private int fileSize;
	//public static int active_thread;

	public DownUtil(String path, String targetFile, int threadNum)
	{
		this.path = path;
		this.threadNum = threadNum;
		// 初始化threads数组
		threads = new DownThread[threadNum];
		this.targetFile = targetFile;
	}

	public void download() throws Exception
	{
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		//conn.setRequestProperty(
		//	"Accept",
		//	"image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
		//	+ "application/x-shockwave-flash, application/xaml+xml, "
		//	+ "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
		//	+ "application/x-ms-application, application/vnd.ms-excel, "
		//	+ "application/vnd.ms-powerpoint, application/msword, */*");
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.connect();

		// 得到文件大小
		fileSize = conn.getContentLength();
		System.out.println("file size:"+fileSize);
		conn.disconnect();
		int currentPartSize = fileSize / threadNum + 1;
		RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
		// 设置本地文件的大小
		file.setLength(fileSize);
		file.close();
		
		for (int i = 0; i < threadNum; i++)
		{
			// 计算每条线程的下载的开始位置
			int startPos = i * currentPartSize;
			// 每个线程使用一个RandomAccessFile进行下载
			RandomAccessFile currentPart = new RandomAccessFile(targetFile,
					"rw");
			// 定位该线程的下载位置
			currentPart.seek(startPos);

			if (i != 0 ){Thread.sleep(1000);}
			// 创建下载线程
				
			threads[i] = new DownThread(startPos, currentPartSize,
				currentPart);

			System.out.println("Begin thread: " + threads[i]);
			threads[i].start();	
			// 启动下载线程	
				
				
			//Thread.sleep(5000);
		}
		

		//System.out.println("FFFF: " + getCompleteRate());

		
	}

	public int getActiveThreads() throws Exception{
		int iThread=0;
		for (int i = 0; i < threadNum; i++){
			if ( (threads[i] != null) && 
				!(threads[i].getDone() == true && threads[i].getSuccess() == true)) {
				iThread++;
			}
		}

		return iThread;
	}

	public  void checkThreads() throws Exception {

		if (getActiveThreads()  <=  (int)0 ) {
			return;
		}

		int currentPartSize = fileSize / threadNum + 1;
		for (int i = 0; i < threadNum; i++)
		{
			// 计算每条线程的下载的开始位置
			int startPos = i * currentPartSize;
			// 每个线程使用一个RandomAccessFile进行下载
			RandomAccessFile currentPart = new RandomAccessFile(targetFile,
				"rw");
			// 定位该线程的下载位置
			currentPart.seek(startPos);

			// 创建下载线程
			if (threads[i] == null) {
				threads[i] = new DownThread(startPos, currentPartSize,
				currentPart);

				System.out.println("Begin thread: " + threads[i]);
				threads[i].start();	
			}
			
			// 启动下载线程	
			if ( (threads[i].getDone ()== true && threads[i].getSuccess() == false)  ) {
				System.out.println("re-start thread: " + threads[i]);
				threads[i].run();	
				}		
			

			/*if ( (threads[i].getLength < currentPartSize) && (threads[i].getRunned() == true) ) {
				System.out.println("re-start thread: " + threads[i]);
				threads[i].run();	
			}*/
			//System.out.println("length: "+threads[i].getLength()+"  currentPart: "+ currentPartSize+"  "+threads[i].getRunned());
			/*if ( (threads[i].getLength() < currentPartSize) && (threads[i].getRunned() == true) ) {
				System.out.println("re-start thread: " + threads[i]);
				threads[i].run();	
			}*/


			System.out.println("Status :  " + threads[i]+" " + "done: "+threads[i].getDone()+"  success:"+threads[i].getSuccess()
				+" " + threads[i].getLength());

			//Thread.sleep(1000);

		}

		//checkThreads();

		//System.out.println("Finished current threads check ");
	}

	// 获取下载的完成百分比
	public double getCompleteRate()
	{

		// 统计多条线程已经下载的总大小
		int sumSize = 0;
		for (int i = 0; i < threadNum; i++)
		{
			if (threads[i] == null) return sumSize * 1.0 / fileSize;
			sumSize += threads[i].length;
		}
		// 返回已经完成的百分比
		return sumSize * 1.0 / fileSize;
	}

	private class DownThread extends Thread
	{
		// 当前线程的下载位置
		private int startPos;
		// 定义当前线程负责下载的文件大小
		private int currentPartSize;
		// 当前线程需要下载的文件块
		private RandomAccessFile currentPart;
		// 定义已经该线程已下载的字节数
		public int length;

		private boolean success; // this thread has download the part successly.
		private boolean done; // this thread has finished but may not success
		private boolean runned;// this thread has executed at least once.
		

		public DownThread(int startPos, int currentPartSize,
			RandomAccessFile currentPart)
		{
			this.startPos = startPos;
			this.currentPartSize = currentPartSize;
			this.currentPart = currentPart;
			this.success = false;
			this.done = false;
		}

		public boolean getSuccess(){
			return this.success;
		}

		public boolean getDone(){
			return this.done;
		}

		public int getLength(){
			return this.length;
		}

		public boolean getRunned(){
			return this.runned;
		}

		@Override
		public void run()
		{
			try
			{
				System.out.println(" entered in ... "+this);
				this.done = false;				
				length=0;
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection)url
					.openConnection();
				conn.setConnectTimeout(5 * 1000);
				conn.setRequestMethod("GET");
				
				conn.setRequestProperty("Accept", "*/*");
				conn.setRequestProperty("Accept-Language", "zh-CN");
				conn.setRequestProperty("Charset", "UTF-8");
				//conn.disconnect(); // reset the connection if this is the second re-try in case of 
				                                      //previous download failure
				//Thread.sleep(1000);
				conn.connect();

				InputStream inStream = conn.getInputStream();
				//DownUtil.active_thread++;
				// 跳过startPos个字节，表明该线程只下载自己负责哪部分文件。
				inStream.skip(this.startPos);
				byte[] buffer = new byte[1024];
				int hasRead = 0;
				// 读取网络数据，并写入本地文件
				int i=0;
				

				while (  (length < currentPartSize)    &&  ((hasRead = inStream.read(buffer) ) != -1) ) 	
				{
					//System.out.println("Loop..."+this);

					/*if( ( (hasRead = inStream.read(buffer) ) == -1 ) ){
						inStream.close();
						conn.disconnect();

						this.done = false;
						conn = (HttpURLConnection)url
							.openConnection();
						conn.setConnectTimeout(5 * 1000);
						conn.setRequestMethod("GET");
						
						//conn.setRequestProperty("Accept", "*//*");
						conn.setRequestProperty("Accept-Language", "zh-CN");
						conn.setRequestProperty("Charset", "UTF-8");
						conn.connect();

						inStream = conn.getInputStream();
						//DownUtil.active_thread++;
						// 跳过startPos个字节，表明该线程只下载自己负责哪部分文件。
						inStream.skip(this.startPos + length);
						System.out.println("cannot read out, re-connet..." + length+"---"+currentPartSize);
						continue;
					}*/

					//System.out.println("Added bytes: " + hasRead);
					if(hasRead <= 0 ) continue;
					//System.out.println("try to write..."+this);
					currentPart.write(buffer, 0, hasRead);
					// 累计该线程下载的总大小
					length += hasRead;
					if (i++ > 10000) {
						System.out.println("..."+this);
						i=0;
					}
					
					
					
				}
				this.runned = true;
				System.out.println(" continue... "+this);
				if ((length < currentPartSize) && hasRead==-1) {
					System.out.println("cannot read from network, current thread exit!");
					System.out.println(length+ "  " + currentPartSize + " " + i);
					this.success = false;
					this.done = true;
					
				}
				if(length >= currentPartSize){
					System.out.println("finished current part"+" " +this);
					this.success = true;
					this.done = true;
					currentPart.close();
					//DownUtil.active_thread--;
				}
				else{
					Thread.sleep(19000);
				}

				this.done = true;				
				inStream.close();
				
				//this.started = false;
			}
			catch (Exception e)
			{
				this.success = false;				
				this.done = true;
				this.runned = true;
				//this.started = false;
				e.printStackTrace();
				//System.out.println("ERROR..."+" " +this);
				try
				{
					Thread.sleep(19000);
				}
				catch(Exception ex){}
				
				//DownUtil.active_thread--;				
				
			}
			
		}
	}
}
