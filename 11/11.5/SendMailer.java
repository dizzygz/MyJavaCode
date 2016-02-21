
import java.awt.*;
/**
  * @author Yeeku.H.Lee kongyeeku@163.com
 * @version 1.0
 */
public class SendMailer
{
	private Frame f = new Frame("test");
	private TextField tf = new TextField(40);
	private Button send = new Button("send");
	public void init()
	{
		// 使用MailerListener对象作为事件监听器
		send.addActionListener(new MailerListener(tf));
		f.add(tf);
		f.add(send , BorderLayout.SOUTH);
		f.pack();
		f.setVisible(true);
	}
	public static void main(String[] args)
	{
		new SendMailer().init();
	}
}
