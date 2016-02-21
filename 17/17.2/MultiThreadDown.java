

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
public class MultiThreadDown
{
	public static void main(String[] args) throws Exception
	{
		// 初始化DownUtil对象
		//final DownUtil downUtil = new DownUtil("http://www.crazyit.org/"
		//	+ "attachments/month_1403/1403202355ff6cc9a4fbf6f14a.png"
		//	, "ios.png", 4);
		
		final DownUtil downUtil = new DownUtil("http://download.virtualbox.org/virtualbox/5.0.14/VirtualBox-5.0.14-105127-Win.exe"
			, "temp.zip", 10);

		

		// 开始下载
		downUtil.download();
		new Thread(() -> {
				while(downUtil.getCompleteRate() < 1)
				{
					// 每隔0.1秒查询一次任务的完成进度，
					// GUI程序中可根据该进度来绘制进度条
					System.out.println("已完成："
						+ downUtil.getCompleteRate());
					try
					{
						System.out.println("active thread:"+downUtil.getActiveThreads());
					}
					catch(Exception ex){}
					
					try
					{
						Thread.sleep(4000);
						/*new Thread(()->{
							try{
							 	downUtil.checkThreads();
							}
							catch(Exception ex){}
							
						}).start();*/
					}
					catch (Exception ex){}
				}
		}).start();

		new Thread(()->{
			while(downUtil.getCompleteRate() < 1){
				try
				{

					downUtil.checkThreads();
					Thread.sleep(1000);
				}
				catch(Exception ex){}
				
			}
		}).start();

		
	}
}
