package com.stock.app.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


@SuppressWarnings(value = { "unused", "deprecation", "unchecked" })
@Controller
@Scope("prototype")
public class ToolsUtils {

	private static Random rd = new Random();
	private static HashMap<String, String> ApachePhysicalURL;
	
	public static String WEBHEADURLPORT="11";//本地显示端口
//	public static String WEBHEADURLPORT="01";//服务器不显示端口
	 /////////////////////////////////////////////////////////////////////////////
	/**
	 * 获得字符串的MD5值
	 * 
	 * @param info
	 * @return
	 */
	public static String getStringForMD5(String info) {
		return Encode(info, "MD5");
	}
	
	public static String Encode(String Str, String Type) {
		if (Str != null && Type != null) {
			try {
				Str = byteArrayToHexString(MessageDigest.getInstance(Type).digest(Str.getBytes())).toUpperCase();
			} catch (Exception ex) {
				System.err.println("编码错误！");
				ex.printStackTrace();
				Str = null;
			}
		}
		return Str;
	}

	//byte转String
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer RSB = new StringBuffer();
		for (byte c : b) {
			int v = 0xFF & c;
//			System.out.println("c=" + c + "v=" + v);
			if (Integer.toHexString(v).length() == 1)
				RSB.append("0").append(Integer.toHexString(v));
			else
				RSB.append(Integer.toHexString(v));
		}
		return RSB.toString();
	}
	//String hex转byte
	public static byte[] hex2byte(String hex,int length) {
		byte[] ret = new byte[length];
		byte[] tmp = hex.getBytes();
		for (int i = 0; i < length; i++) {
			byte src0 = tmp[i * 2];
			byte src1 = tmp[i * 2 + 1];
			byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
			_b0 = (byte) (_b0 << 4);
			byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
			ret[i] = (byte) (_b0 ^ _b1);
		}
		return ret;
	}
	/**
	 * 字节数组转N进制字符串
	 * 
	 * @param bs
	 * @param n
	 * @param tf
	 * @return
	 */
	public static String byteToAryString(byte[] bs, int n, boolean tf) {
		StringBuffer sb = new StringBuffer();
		for (byte b : bs)
			sb.append(Integer.toString(b, n));
		return sb.toString();
	}

	/*
	 * 将Map对象转化为JSON文本
	 * 
	 * @param map
	 * @param type
	 * @return
	 */
	public static String getJSON(Map map, boolean... type) {
		if (map.isEmpty())
			return "{}";
		StringBuilder sb = new StringBuilder(map.size() << 4);
		sb.append('{');
		Set<String> keys = map.keySet();
		for (String key : keys) {
			Object value = map.get(key);
			sb.append('\"');
			sb.append(key);
			sb.append('\"');
			sb.append(':');
			sb.append(toJson(value));
			sb.append(',');
		}
		// 将最后的 ',' 变为 '}':
		sb.setCharAt(sb.length() - 1, '}');
		return sb.toString();
	}

	public static String toJson(Object o) {
		if (o == null)
			return "null";
		if (o instanceof String)
			return string2Json((String) o);
		if (o instanceof Boolean)
			return boolean2Json((Boolean) o);
		if (o instanceof Number)
			return number2Json((Number) o);
		if (o instanceof Map)
			return map2Json((Map<String, Object>) o);
		if (o instanceof Object[])
			return array2Json((Object[]) o);
		throw new RuntimeException("Unsupported type: " + o.getClass().getName());
	}

	public static String string2Json(String s) {
		StringBuilder sb = new StringBuilder(s.length() + 20);
		sb.append('\"');
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '/':
				sb.append("\\/");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				sb.append(c);
			}
		}
		sb.append('\"');
		return sb.toString();
	}

	public static String map2Json(Map<String, Object> map) {
		if (map.isEmpty())
			return "{}";
		StringBuilder sb = new StringBuilder(map.size() << 4);
		sb.append('{');
		Set<String> keys = map.keySet();
		for (String key : keys) {
			Object value = map.get(key);
			sb.append('\"');
			sb.append(key);
			sb.append('\"');
			sb.append(':');
			sb.append(toJson(value));
			sb.append(',');
		}
		// 将最后的 ',' 变为 '}':
		sb.setCharAt(sb.length() - 1, '}');
		return sb.toString();
	}

	public static String number2Json(Number number) {
		return number.toString();
	}

	public static String boolean2Json(Boolean bool) {
		return bool.toString();
	}

	public static String array2Json(Object[] array) {
		if (array.length == 0)
			return "[]";
		StringBuilder sb = new StringBuilder(array.length << 4);
		sb.append('[');
		for (Object o : array) {
			sb.append(toJson(o));
			sb.append(',');
		}
		// 将最后添加的 ',' 变为 ']':
		sb.setCharAt(sb.length() - 1, ']');
		return sb.toString();
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * 字符串边缘修剪
	 * @param str 修剪字符串
	 * @param ts 修剪替换字符串
	 * @return 
	 */
	public static String strim(String str, String ts) {
		return str.replaceAll("^\\" + ts + "*|\\" + ts + "*$", "");
	}

	public static HashMap<String, String> getApachePhysicalURL(){
		return ApachePhysicalURL;
	}

	/**
	 * 从Map中提取HttpServletRequest
	 * 
	 * @param map
	 * @return
	 */
	private static HttpServletRequest getRequest(Map map) {
		HttpServletRequest request = null;
		if (map != null && map.size() > 0) {
			Object obj = map.get("request");
			if (obj instanceof HttpServletRequest) {
				request = (HttpServletRequest) obj;
			}
		}
		return request;
	}

	/**
	 * 获得时间格式化对象
	 * 
	 * @param pattern
	 * @return
	 */
	private static SimpleDateFormat sdf = null;

	public static DateFormat getDf(String pattern) {
		if (sdf == null)
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (pattern == null)
			return (DateFormat) sdf;
		sdf.applyPattern(pattern);
		return (DateFormat) sdf;
	}
	/**
	 * 获得每天的开始时间
	 * @param c
	 * @return Long
	 */
	public static Long getTimeStart(Calendar c){
		c.clear(Calendar.SECOND);	//去秒
		c.clear(Calendar.MINUTE);	//去分
		c.clear(Calendar.MILLISECOND);//去毫秒
		c.set(Calendar.HOUR_OF_DAY,0);
		return c.getTimeInMillis();
	}
	/**
	 * 获得每天的结束时间
	 * @param c
	 * @return Long
	 */
	public static Long getTimeEnd(Calendar c){
		c.set(Calendar.MILLISECOND,999);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.HOUR_OF_DAY,23);
		return c.getTimeInMillis();
	}

	// /////////////////////////////////////////////////////////////////////////////

	// 检查是否有空值
	// 存在空值返回emptyObjList.size()>0
	// /////////////////////////////////////////////////////////////////////////////
	public static List<Object> searchEmpty(Object... objs) {
		List emptyObjList = new ArrayList<Object>();
		for (int i = 0; i < objs.length; i++) {
			if (objs[i] == null) {
				emptyObjList.add(objs[i]);
				continue;
			}
			if (objs[i] instanceof String && objs[i].toString().trim().equals("")) {
				emptyObjList.add(objs[i]);
				continue;
			}
			if (objs[i] instanceof Object[] && Array.getLength(objs[i]) <= 0) {
				emptyObjList.add(objs[i]);
				continue;
			}
			if (objs[i] instanceof Collection && ((Collection) objs[i]).size() <= 0) {
				emptyObjList.add(objs[i]);
				continue;
			}
			if (objs[i] instanceof Map && ((Map) objs[i]).size() <= 0) {
				emptyObjList.add(objs[i]);
				continue;
			}
		}
		return emptyObjList;
	}

	// /////////////////////////////////////////////////////////////////////////////

	// 将Bean转换为XML
	// /////////////////////////////////////////////////////////////////////////////
	public static String beanToXml(Object bean) {
		return new XStream(new DomDriver()).toXML(bean);
	}

	// /////////////////////////////////////////////////////////////////////////////

	// 获得文件大小
	// /////////////////////////////////////////////////////////////////////////////
	public static String getFileAutoSize(File file) {
		long size = file.length();
		DecimalFormat Df = new DecimalFormat("0.00"); // 设定四舍五入格式--->保留小数点后2位
		return size < 1024 ? Df.format(size) + "Byte" : (size > 1024 && size < 1048576 ? Df.format(size / 1024.0) + "KB" : (size > 1073741824 ? Df.format(size / 1073741824.0) + "GB" : Df.format(size / 1048576.0) + "MB"));
	}

	public static long getFileByteSize(File file) {
		if (file.exists()) {
			return file.length();
		} else {
			return 0;
		}
	}

	// /////////////////////////////////////////////////////////////////////////////

	// 磁盘容量百分比
	// /////////////////////////////////////////////////////////////////////////////
	public static BigDecimal getDiskPercent(String disk) {
		File rootFiles[] = File.listRoots();
		List list = Arrays.asList(rootFiles);
		for (ListIterator<File> ite = list.listIterator(); ite.hasNext();) {
			File file = (File) ite.next();
			if (file.getPath().equals(disk)) {
				return new BigDecimal(file.getFreeSpace()).divide(new BigDecimal(file.getTotalSpace()), 2, BigDecimal.ROUND_HALF_UP);
			}
		}
		return new BigDecimal(-1);
	}

	// /////////////////////////////////////////////////////////////////////////////

	// 获得图片的长宽
	/**
	 * @param file
	 *            图片文件
	 * @param woh
	 *            true：获得Width false：获得Height
	 * @return -1 文件不存在或者不是图片文件
	 */
	public static int getPicWH(File file, boolean woh) {
		try {
			BufferedImage img = ImageIO.read(file);
			return img == null ? -1 : woh ? img.getWidth() : img.getHeight();
		} catch (IOException e) {
			return -1;
		}
	}

	// /////////////////////////////////////////////////////////////////////////////

	// 文件拷贝
	// /////////////////////////////////////////////////////////////////////////////
	public static boolean CopyFile(File srcFile, File targetFile) {
		try {
			if (!targetFile.getParentFile().exists())
				targetFile.getParentFile().mkdirs();
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile));
			byte[] buf = new byte[1024];
			int n = -1;
			while ((n = bis.read(buf)) > -1) {
				bos.write(buf, 0, n);
			}
			bos.flush();
			bos.close();
			bis.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 文件拷贝
	// /////////////////////////////////////////////////////////////////////////////
	public static boolean CopyImage(BufferedImage BufImage, File targetFile) {
		try {
			if (!targetFile.getParentFile().exists())
				targetFile.getParentFile().mkdirs();
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile));
			ImageIO.write(BufImage, "jpeg", bos);
			bos.flush();
			bos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// /////////////////////////////////////////////////////////////////////////////

	// 执行DOS命令
	// /////////////////////////////////////////////////////////////////////////////
	public static String cmdExe(String cmd) {
		StringBuffer sb = new StringBuffer();
		Runtime rt = Runtime.getRuntime();
		try {
			Process pr = rt.exec(cmd);
			BufferedInputStream bis = new BufferedInputStream(pr.getInputStream());
			byte[] buf = new byte[1024];
			int n = -1;
			while ((n = bis.read(buf)) != -1)
				sb.append(new String(buf, 0, n, "gbk"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	// 获得UUID
	// /////////////////////////////////////////////////////////////////////////////
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		return s.replaceAll("-", "");
	}

	/**
	 * HTML编码
	 * @param str
	 * @return
	 */
	public static String htmlEnCode(String str) {
		String  rs = "";
		if(str==null)return str;
		if (str.length() == 0) return "";
		rs = str.replaceAll("&", "&amp;");
		rs = rs.replaceAll("<", "&lt;");
		rs = rs.replaceAll(">", "&gt;");
		rs = rs.replaceAll("	", "&nbsp;");
		rs = rs.replaceAll("\'", "&#39;");
		rs = rs.replaceAll("\"", "&quot;");
		rs = rs.replaceAll("\n", "<br>");
		return rs;
	}
	

//获得随机类
///////////////////////////////////////////////////////////////////////////////		
	public static Random getRandom(){
		return rd;
	}
	
//获得随机类
	public static boolean ISDEBUG=false;
///////////////////////////////////////////////////////////////////////////////		
	public static void debug(Object obj){
		if(ISDEBUG)System.out.println(obj);
	}
	
	public static Map debugMode(String order){
		Map infoMap=new LinkedHashMap();
		if(!true)return infoMap;
		if(order!=null){
			if(order.equals("u-debug-print")){
				ISDEBUG=!ISDEBUG;infoMap.put("result", "DEBUGPRINT:"+ISDEBUG);
			}
		}
		if(!infoMap.containsKey("result"))infoMap.put("result", "ERROR!");
		return infoMap;
	}
	/**
	 * 获得当日的日期时间 不包含时分秒
	 * @return
	 */
	public static long getDateNoHour(){
		Calendar rightNow = Calendar.getInstance();
		rightNow.clear(Calendar.SECOND);
		rightNow.clear(Calendar.MINUTE);
		rightNow.clear(Calendar.MILLISECOND);
		rightNow.set(Calendar.HOUR_OF_DAY, 0);
		return rightNow.getTimeInMillis();
	}
	/**
	 * 验证是数字
	 * @return Integer
	 */
	public static Integer isVerifyInteger(Object obj){
		String regex="^[0-9]+$";
		if(obj.toString().matches(regex)){
			return Integer.valueOf(obj.toString());
		}
		return 0;
	}
	/**
	 * 隐藏性还不够好，效率还不够高，以后优化的时候只需要修改下面两个方法即可！
	 * url编码
	 * @return 密文
	 */
	public static String codingForUT(String Ori){
		//算法：
		//1:字符串顺序翻转
		//2:翻转后的字符串打散成字节数组
		//3:将每一个字节*10
		//4:自动转为整数后再转为17进制
		//5:16进制链接为字符串
		StringBuffer info=new StringBuffer(Ori);
		byte[] buf=info.reverse().toString().getBytes();
		info=new StringBuffer();
		for (byte b : buf)info.append(Integer.toString(b*10, 17));
		return info.toString();
	}
	
	/**
	 * url解码
	 * @return 明文
	 */
	public static String decodingForUT(String Ori){
		byte buf[]=new byte[Ori.length()/3];
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < Ori.length(); i++) {
			sb.append(Ori.charAt(i));
			if(sb.length()==3){
				try {
					buf[(i+1)/3-1]=Byte.valueOf(String.valueOf(Integer.valueOf(sb.toString(), 17)/10));
				} catch (NumberFormatException e) {
					System.out.println(i);
					e.printStackTrace();
				}
				sb.delete(0, 3);
			}
		}
		return sb.append(new String(buf)).reverse().toString();
	}
	
	public static String xmlCDATA(Object str){
		return "<![CDATA["+(str==null?"":str)+"]]>";
	}
	
	public static byte[] readFileImage(String filename) throws IOException {
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(filename));
		int len = bufferedInputStream.available();
		byte[] bytes = new byte[len];
		int r = bufferedInputStream.read(bytes);
		if (len != r) {
			bytes = null;
			throw new IOException("读取文件不正确");
		}
		bufferedInputStream.close();
		return bytes;
	}
	
	public static int getQuarter(int month){
		int quarter=0;
		if (month >= 1 && month <= 3) 
			quarter=1; 
        else if (month >= 4 && month <= 6) 
        	quarter=2; 
        else if (month >= 7 && month <= 9) 
        	quarter=3; 
        else if (month >= 10 && month <= 12) 
        	quarter=4; 
		return quarter;
	}
	
	//**end**//
}
