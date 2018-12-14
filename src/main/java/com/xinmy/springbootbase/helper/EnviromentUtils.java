/*
 * Copyright 2015-2020 reserved by jf61.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xinmy.springbootbase.helper;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * @desc
 *
 */
public class EnviromentUtils {
	public static final int    LOW_ORDER_THREE_BYTES = 0x00ffffff;
	public static final long   NODE_ID;                           // 当前JVM
	// id可通过-D系统属性指定，未指定则则取MACHINE_IDENTIFIER最后一个字节
	public static final int    MACHINE_IDENTIFIER;                // 表示服务器
	public static final String MACHINE_IP;                        // 表示服务器IP
	public static final short  PROCESS_IDENTIFIER;                // 当前JVM进程
	static {
		try {
			MACHINE_IDENTIFIER = EnviromentUtils.createMachineIdentifier();
			MACHINE_IP = EnviromentUtils.createMachineIp();
			PROCESS_IDENTIFIER = EnviromentUtils.createProcessIdentifier();
			final String sNodeId = System.getProperty("node", "126");
			if ((sNodeId == null) || sNodeId.isEmpty()) {
				throw new Exception("清指定当前节点id， -Dnode=xxx， 节点ID不允许超过127,且务必保证不重复"); // 127三个节点处理数据，应该是足够的了
			}
			NODE_ID = Long.valueOf(sNodeId) & 0x0000007f;// 取最后一个字节
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private EnviromentUtils() {
		super();
	}

	private static int createMachineIdentifier() {
		// build a 2-byte machine piece based on NICs info
		int machinePiece;
		try {
			StringBuilder sb = new StringBuilder();
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface ni = e.nextElement();
				sb.append(ni.toString());
				byte[] mac = ni.getHardwareAddress();
				if (mac != null) {
					ByteBuffer bb = ByteBuffer.wrap(mac);
					try {
						sb.append(bb.getChar());
						sb.append(bb.getChar());
						sb.append(bb.getChar());
					} catch (BufferUnderflowException shortHardwareAddressException) { // NOPMD
						// mac with less than 6 bytes. continue
					}
				}
			}
			machinePiece = sb.toString().hashCode();
		} catch (Throwable t) {
			// exception sometimes happens with IBM JVM, use random
			machinePiece = new SecureRandom().nextInt();
			IDGenerator.LOGGER.error(
			        "Failed to get machine identifier from network interface, using random number instead", t);
		}
		machinePiece = machinePiece & EnviromentUtils.LOW_ORDER_THREE_BYTES;
		return machinePiece;
	}

	/**
	 * @desc .
	 */
	private static String createMachineIp() {
		try {
			// Traversal Network interface to get the first non-loopback and non-private address
			Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
			ArrayList<String> ipv4Result = new ArrayList<String>();
			ArrayList<String> ipv6Result = new ArrayList<String>();
			while (enumeration.hasMoreElements()) {
				final NetworkInterface networkInterface = enumeration.nextElement();
				final Enumeration<InetAddress> en = networkInterface.getInetAddresses();
				while (en.hasMoreElements()) {
					final InetAddress address = en.nextElement();
					if (!address.isLoopbackAddress()) {
						if (address instanceof Inet6Address) {
							ipv6Result.add(EnviromentUtils.normalizeHostAddress(address));
						} else {
							ipv4Result.add(EnviromentUtils.normalizeHostAddress(address));
						}
					}
				}
			}

			// prefer ipv4
			if (!ipv4Result.isEmpty()) {
				for (String ip : ipv4Result) {
					if (ip.startsWith("127.0") /* || ip.startsWith("192.168") */) {
						continue;
					}

					return ip;
				}

				return ipv4Result.get(ipv4Result.size() - 1);
			} else if (!ipv6Result.isEmpty()) {
				return ipv6Result.get(0);
			}
			final InetAddress localHost = InetAddress.getLocalHost();
			return EnviromentUtils.normalizeHostAddress(localHost);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String normalizeHostAddress(final InetAddress localHost) {
		if (localHost instanceof Inet6Address) {
			return "[" + localHost.getHostAddress() + "]";
		} else {
			return localHost.getHostAddress();
		}
	}

	// Creates the process identifier. This does not have to be unique per class loader because
	// NEXT_COUNTER will provide the uniqueness.
	private static short createProcessIdentifier() {
		short processId;
		try {
			String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
			if (processName.contains("@")) {
				processId = (short) Integer.parseInt(processName.substring(0, processName.indexOf('@')));
			} else {
				processId = (short) java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode();
			}

		} catch (Throwable t) {
			processId = (short) new SecureRandom().nextInt();
			IDGenerator.LOGGER.error("Failed to get process identifier from JMX, using random number instead", t);
		}

		return processId;
	}

	/**
	 * @desc .
	 */
	public static boolean isDev() {
		return Boolean.valueOf(System.getProperty("dev", "false"));
	}
}
