
import java.io.*;
/**
 * Description:
 *æ’å…¥çš„å†…å®¹
æ’å…¥çš„å†…å®¹
 <br/>ÍøÕ¾: <a href="http://www.crazyit.org">·è¿ñJavaÁªÃË</a>
 * <br/>Copyright (C), 2001-2016, Yeeku.H.Lee
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:
 * <br/>Date:
 * @author Yeeku.H.Lee kongyeeku@163.com
 * @version 1.0
 */
public class InsertContent
{
	public static void insert(String fileName , long pos
		, String insertContent) throws IOException
	{
		File tmp = File.createTempFile("tmp" , null);
		tmp.deleteOnExit();
		try(
			RandomAccessFile raf = new RandomAccessFile(fileName , "rw");
			// Ê¹ÓÃÁÙÊ±ÎÄ¼şÀ´±£´æ²åÈëµãºóµÄÊı¾İ
			FileOutputStream tmpOut = new FileOutputStream(tmp);
			FileInputStream tmpIn = new FileInputStream(tmp))
		{
			raf.seek(pos);
			// ------ÏÂÃæ´úÂë½«²åÈëµãºóµÄÄÚÈİ¶ÁÈëÁÙÊ±ÎÄ¼şÖĞ±£´æ------
			byte[] bbuf = new byte[64];
			// ÓÃÓÚ±£´æÊµ¼Ê¶ÁÈ¡µÄ×Ö½ÚÊı
			int hasRead = 0;
			// Ê¹ÓÃÑ­»··½Ê½¶ÁÈ¡²åÈëµãºóµÄÊı¾İ
			while ((hasRead = raf.read(bbuf)) > 0 )
			{
				// ½«¶ÁÈ¡µÄÊı¾İĞ´ÈëÁÙÊ±ÎÄ¼ş
				tmpOut.write(bbuf , 0 , hasRead);
			}
			// ----------ÏÂÃæ´úÂë²åÈëÄÚÈİ----------
			// °ÑÎÄ¼ş¼ÇÂ¼Ö¸ÕëÖØĞÂ¶¨Î»µ½posÎ»ÖÃ
			raf.seek(pos);
			// ×·¼ÓĞèÒª²åÈëµÄÄÚÈİ
			raf.write(insertContent.getBytes());
			// ×·¼ÓÁÙÊ±ÎÄ¼şÖĞµÄÄÚÈİ
			while ((hasRead = tmpIn.read(bbuf)) > 0 )
			{
				raf.write(bbuf , 0 , hasRead);
			}
		}
	}
	public static void main(String[] args)
		throws IOException
	{
		insert("InsertContent.java" , 45 , "²åÈëµÄÄÚÈİ\r\n");
	}
}

