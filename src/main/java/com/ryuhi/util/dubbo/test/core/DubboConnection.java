package com.ryuhi.util.dubbo.test.core;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import lombok.Data;

@Data
public class DubboConnection {

	private SocketChannel socketChannel;
	
	private Selector selector;
	
	private SelectionKey selectionKey;
}
