package com.main;

import java.sql.*;

import com.protocol.MovieServer;

import java.net.*;
import java.io.*;

public class MovieServerSystem
{
	private static final int SERVER_PORT = 5000;
	
	MovieServerSystem() throws ClassNotFoundException, SQLException, IOException
	{
		
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
	{
		new MovieServerSystem().start();
	}
	
	// 메인문과 같은 원리 대부분의 시스템 동작흐름
	public void start()
	{
		ServerSocket sSocket;
		Socket socket;
		
		try
		{
			sSocket = new ServerSocket();
			String localHostAddress = InetAddress.getLocalHost().getHostAddress();
			sSocket.bind(new InetSocketAddress(localHostAddress, SERVER_PORT));
			System.out.println("[server] binding! \n[server] address:" + localHostAddress + ", port:" + SERVER_PORT);
			System.out.println("클라이언트 접속 대기중...");
			while (true)
			{
				socket = sSocket.accept();
				System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "에서 접속 하였습니다.");
				MovieServer serverThread = new MovieServer(socket);
				serverThread.start();
			}
			
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}
}