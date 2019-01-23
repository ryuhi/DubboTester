package com.ryuhi.util.dubbo.test.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.charset.Charset;
 
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.apache.log4j.Logger;
 
/**
 * 
 *
 * @description telnet操作工具类，非线程安装，请在一个线程中操作
 * @author weichaofan
 * @date 2013年10月19日
 */
public class TelnetUtil implements Runnable, TelnetNotificationHandler {
	Logger logger = Logger.getLogger(getClass());
	private TelnetClient tc = null;
	private String remoteip;
	private int remoteport;
	private StringBuffer responseStr= new StringBuffer();
	
	
	public TelnetUtil (String ip){
		this(ip, 23,null);
	}
	public TelnetUtil (String ip,int port){
		this(ip, port,null);
	}
	public TelnetUtil (String ip,String spyFile){
		this(ip, 23,spyFile);
	}
	/**
	 * 最终构造方法
	 * @param ip 
	 * @param port
	 * @param spyFile 
	 */
	public TelnetUtil (String ip,int port,String spyFile){
		remoteip=  ip;
		remoteport=port;
		
		initClient(spyFile);
	}
 
	private void initClient(String spyFile) {
		
 
		tc = new TelnetClient();
 
		TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler(
				"VT220", false, false, true, false);
		EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true,
				false);
		SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true,
				true, true);
 
		try {
			tc.addOptionHandler(ttopt);
			tc.addOptionHandler(echoopt);
			tc.addOptionHandler(gaopt);
			
			if(null != spyFile && !"".equals(spyFile)){
				FileOutputStream fout = null;
				try {
					fout = new FileOutputStream("spy.log", true);
					tc.registerSpyStream(fout);
				} catch (IOException e) {
					logger.error("Exception while opening the spy file: "		+ e.getMessage());
				}
			}
		} catch (InvalidTelnetOptionException | IOException e) {
			logger.error(e);
		}
 
	}
 
	public String connect(long waitTime) throws SocketException, IOException   {
			tc.connect(remoteip, remoteport); 
			Thread reader = new Thread(this);
			tc.registerNotifHandler(this); 
			reader.start();			
			return getResponse( waitTime);
	}
 
	public void disConnect() {
		try {
			if(tc != null&& tc.isConnected())
			tc.disconnect();
		} catch (Exception e) {
		//	e.printStackTrace();
			logger.error("Exception when close connecting:" + e.getMessage());
		}
 
	}
	/**
	 * 发送命令,返回结果请调用 getResponse(long waitTime)
	 * @param command
	 * @throws Exception
	 */
	public void sendCommand(String command) throws StringIndexOutOfBoundsException, IOException   {
		
		try {
			responseStr.delete(0, responseStr.capacity());
			OutputStream outstr = tc.getOutputStream();			
			outstr.write(command.getBytes());
			outstr.write(13);
			outstr.write(10);
			outstr.flush();
		} catch (StringIndexOutOfBoundsException | IOException e) {
			logger.error(e.getMessage());
			throw e;
		}	
 
	}
	/**
	 * 发送命令
	 * @param command 命令
	 * @param waitTime 获取返回结果时等待时间，在等待的时间内若返回的结果不是想要的结果，可以调用 getResponse(long waitTime)继续获取
	 * @return 执行结果
	 * @throws Exception
	 */
	public String sendCommand(String command,int waitTime) throws StringIndexOutOfBoundsException,IOException  {		
		try {
			responseStr.delete(0, responseStr.capacity());
			OutputStream outstr = tc.getOutputStream();				
			outstr.write(command.getBytes());
			outstr.write(13);
			outstr.write(10);
			outstr.flush();
		} catch (StringIndexOutOfBoundsException | IOException e) {
			logger.error("telnet 发送命令["+command+"]失败", e);
			throw e;
		}		
		return getResponse(waitTime);
	}
 
	
	@Override
	public void receivedNegotiation(int negotiation_code, int option_code) {
		String command = null;
		if (negotiation_code == TelnetNotificationHandler.RECEIVED_DO) {
			command = "DO";
		} else if (negotiation_code == TelnetNotificationHandler.RECEIVED_DONT) {
			command = "DONT";
		} else if (negotiation_code == TelnetNotificationHandler.RECEIVED_WILL) {
			command = "WILL";
		} else if (negotiation_code == TelnetNotificationHandler.RECEIVED_WONT) {
			command = "WONT";
		}
		logger.debug("Received " + command + " for option code " + option_code);
	}
 
	/***
	 * Reader thread. Reads lines from the TelnetClient and echoes them on the
	 * screen.
	 ***/
	// @Override
	public void run() {
		InputStream instr = tc.getInputStream();
		
		
		try {
			byte[] buff = new byte[1024];
			int ret_read = 0;		
 
			do {
				ret_read = instr.read(buff);
				if (ret_read > 0) {
					responseStr.append(new String(buff, 0, ret_read));
				}
			} while (ret_read >= 0);
		} catch (Exception e) {
			
			logger.error("Exception while reading socket:"+ e.getMessage());
		}
 
	}
	/**
	 * 获取命令返回去
	 * @param waitTime 等待时间
	 * @return
	 */
	public String getResponse(long waitTime)
	{
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(),e);
		}
		return responseStr.toString();
	}
	
	public static void main(String[] args) throws Exception {
 
		TelnetUtil util = new TelnetUtil("10.22.1.12");		
		String welcome=util.connect(2000);
		if(welcome.contains("Microsoft")){
			System.out.println("widows opreation");
		}else{
			System.out.println("linux opreation");
		}
		System.out.println(welcome);
		
		
		System.out.println(util.sendCommand("administrator",2000));
		
		System.out.println(util.sendCommand("123456",2000));
		System.out.println(util.sendCommand("dir",2000));
		System.out.println(util.sendCommand("d:",2000));
		System.out.println(util.sendCommand("dir",2000));
		util.disConnect();
	}
 
	
}
