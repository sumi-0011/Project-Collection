import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.swing.JOptionPane;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class ARPLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public int nUnderLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_aUnderLayerARP = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	// arpTable -> _ARP_Table
	public ARPTable _ARP_Table;

	// cacheTable -> _Cache_Table
	// proxyTable -> _Proxy_Table
	HashMap<String, Object[]> _Cache_Table = new HashMap<String, Object[]>();
	HashMap<String, Object[]> _Proxy_Table = new HashMap<String, Object[]>();
	// waitfor -> _TimeToLive
	HashMap<String, ArrayList<Object[]>> _TimeToLive = new HashMap<String, ArrayList<Object[]>>();

	_Receive_Thread receiveThread = null;
	Thread sendStartThread = null;
	Thread receiveStartThread = null;
	ApplicationLayer _APP;	// app -> _APP

	private byte[] ipAddr_port;	// ipAddress_port -> ipAddr_port
	_Cache_TimeToLive _TTL_Thread = null;	// thread -> _TTL_Thread
	String portName;

	public final static int ARPHEADER = 28;

	// inner class for dealing with Mac address
	private class _ARP_MAC_ADDR {
		private byte[] addr = new byte[6];

		public _ARP_MAC_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
			this.addr[4] = (byte) 0x00;
			this.addr[5] = (byte) 0x00;
		}
	}

	// _ARP_PROTOCOL_ADDR -> _ARP_IP_ADDR
	// inner class for dealing with Protocol address
	private class _ARP_IP_ADDR {
		private byte[] addr = new byte[4];

		public _ARP_IP_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
		}
	}

	private class _ARP_HEADER {
		byte[] arp_macType;	// arp_hwType -> arp_macType
		byte[] arp_ipType;	// arp_protoAddrType -> arp_ipType
		byte[] arp_macLength;	// arp_hwAddrLength -> arp_macLength
		byte[] arp_ipLength;	// arp_protoAddrLength -> arp_ipLength
		byte[] arp_opcode;
		_ARP_MAC_ADDR arp_macSrc;	// _arp_mac_src -> arp_macSrc
		_ARP_IP_ADDR arp_ipSrc;	// _arp_protocol_srcaddr -> arp_ipSrc
		_ARP_MAC_ADDR arp_macDst;	// _arp_mac_dstaddr -> arp_macDst
		// _arp_protocol_dstaddr -> arp_ipDst
		_ARP_IP_ADDR arp_ipDst; // first sending, it's empty.

		public _ARP_HEADER() {
			arp_macType = new byte[2];
			arp_ipType = new byte[2];
			arp_macLength = new byte[1];
			arp_ipLength = new byte[1];
			arp_opcode = new byte[2];
			arp_macSrc = new _ARP_MAC_ADDR();	// _arp_mac_src -> arp_macSrc
			arp_ipSrc = new _ARP_IP_ADDR();	// _arp_protocol_srcaddr -> arp_ipSrc
			arp_macDst = new _ARP_MAC_ADDR();	// _arp_mac_dstaddr -> arp_macDst
			arp_ipDst = new _ARP_IP_ADDR();	// _arp_protocol_dstaddr -> arp_ipDst
		}
	}

	_ARP_HEADER m_sHeader = new _ARP_HEADER();

	public ARPLayer(String pName, ARPTable arpTable) {
		// super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
//      System.out.println(pName);
		if (pName.equals("ARP"))
			portName = "Port1";
		else
			portName = "Port2";
//      System.out.println(GetLayerName()+" "+portName);
		// m_sHeader = new _ARP_HEADER();
		if (_TTL_Thread == null) {
			_TTL_Thread = new _Cache_TimeToLive(this._Cache_Table, 30, 50);
			Thread obj = new Thread(_TTL_Thread);
			obj.start();
		}
		this._ARP_Table = arpTable;
	}

	public byte[] ObjToByte(_ARP_HEADER m_sHeader) {
		byte[] buf = new byte[ARPHEADER];

		buf[0] = m_sHeader.arp_macType[0];
		buf[1] = m_sHeader.arp_macType[1];
		buf[2] = m_sHeader.arp_ipType[0];
		buf[3] = m_sHeader.arp_ipType[1];
		buf[4] = m_sHeader.arp_macLength[0];
		buf[5] = m_sHeader.arp_ipLength[0];
		buf[6] = m_sHeader.arp_opcode[0];
		buf[7] = m_sHeader.arp_opcode[1];
		for (int i = 0; i < 6; i++) {
			buf[i + 8] = m_sHeader.arp_macSrc.addr[i];
			buf[i + 18] = m_sHeader.arp_macDst.addr[i];
		}
		for (int i = 0; i < 4; i++) {
			buf[i + 14] = m_sHeader.arp_ipSrc.addr[i];
			buf[i + 24] = m_sHeader.arp_ipDst.addr[i];
		}

		return buf;
	}

	public boolean Send(byte[] arp_protocol_srcaddr, byte[] arp_protocol_dstaddr, byte[] mac_srcaddr,
			byte[] mac_dstaddr, byte[] arp_opcode) {
		
//		System.out.println("arp send ");
//		sendThread = new Send_Thread(arp_protocol_srcaddr, arp_protocol_dstaddr, mac_srcaddr, mac_dstaddr,
//				arp_opcode, ((EthernetLayer) GetUnderLayer(0)));
//		sendStartThread = new Thread(sendThread);
//		sendStartThread.start();
//		return false;
      Object[] value = new Object[4];

      String srcIpAddressToString = (arp_protocol_srcaddr[0] & 0xFF) + "." + (arp_protocol_srcaddr[1] & 0xFF) + "."
              + (arp_protocol_srcaddr[2] & 0xFF) + "." + (arp_protocol_srcaddr[3] & 0xFF);
      String dstIpAddressToString = (arp_protocol_dstaddr[0] & 0xFF) + "." + (arp_protocol_dstaddr[1] & 0xFF) + "."
            + (arp_protocol_dstaddr[2] & 0xFF) + "." + (arp_protocol_dstaddr[3] & 0xFF);


      String macAddress1 = String.format("%X:", mac_srcaddr[0]) + String.format("%X:", mac_srcaddr[1])
		+ String.format("%X:", mac_srcaddr[2]) + String.format("%X:", mac_srcaddr[3])
		+ String.format("%X:", mac_srcaddr[4]) + String.format("%X", mac_srcaddr[5]);
	
      String macAddress2 = String.format("%X:", mac_dstaddr[0]) + String.format("%X:", mac_dstaddr[1])
		+ String.format("%X:", mac_dstaddr[2]) + String.format("%X:", mac_dstaddr[3])
		+ String.format("%X:", mac_dstaddr[4]) + String.format("%X", mac_dstaddr[5]);
      
      
      if (arp_opcode[0] == (byte) 0x00 && arp_opcode[1] == (byte) 0x01) {

         if (_Cache_Table.containsKey(dstIpAddressToString))
            return true;

         value[0] = _Cache_Table.size(); // ARP-request Send ("Incomplete")
         value[1] = mac_dstaddr;
         value[2] = "Incomplete";
         value[3] = System.currentTimeMillis();

         _Cache_Table.put(dstIpAddressToString, value);
//         arpTable.updateARPCacheTable();
         _ARP_Table.addEntry(dstIpAddressToString,value,portName);
         
//         if (arp_mac_dstaddr != null)
//            m_sHeader._arp_mac_dstaddr.addr = arp_mac_dstaddr;
//         else
            m_sHeader.arp_macDst.addr = mac_dstaddr;

         m_sHeader.arp_ipSrc.addr = arp_protocol_srcaddr;
         m_sHeader.arp_ipDst.addr = arp_protocol_dstaddr;
         m_sHeader.arp_macSrc.addr = mac_srcaddr;

      } else {
//    	  System.out.println("ARP Send Reply");
//    	  System.out.println("Mac src address : "+ macAddress1);
//    	  System.out.println("Mac dst address : "+ macAddress2);
//////         if (arp_mac_dstaddr != null)
//            m_sHeader._arp_mac_dstaddr.addr = arp_mac_dstaddr;
//         else
    	 m_sHeader.arp_macDst.addr = mac_dstaddr;

         m_sHeader.arp_ipSrc.addr = arp_protocol_srcaddr;
         m_sHeader.arp_ipDst.addr = arp_protocol_dstaddr;
         m_sHeader.arp_macSrc.addr = mac_srcaddr;

      }

      m_sHeader.arp_macType[0] = (byte) 0x00; // Ethernet
      m_sHeader.arp_macType[1] = (byte) 0x01;

      m_sHeader.arp_ipType[0] = (byte) 0x08; // IP
      m_sHeader.arp_ipType[1] = (byte) 0x00;

      m_sHeader.arp_macLength[0] = 6;
      m_sHeader.arp_ipLength[0] = 4;

      m_sHeader.arp_opcode = arp_opcode;

      byte[] bytes = ObjToByte(m_sHeader);

//      arpTable.updateARPCacheTable();
     
      ((EthernetLayer)this.GetUnderLayer(0)).Send(bytes, bytes.length,null);
      
//      arp_mac_dstaddr = null;

      return true;
	}

	public boolean Receive(byte[] input, byte[] mac_srcaddress) {
//		System.out.println("arp receive "+Thread.currentThread().getName());
		receiveThread = new _Receive_Thread(input, mac_srcaddress, ((EthernetLayer) GetUnderLayer(0)),this,_ARP_Table);
		receiveStartThread = new Thread(receiveThread);
		receiveStartThread.start();
//		byte[] message = input;
//
//		Object[] value = new Object[4];
//		byte[] dstIP = new byte[4];
//		byte[] dstMac = new byte[6];
//		byte[] targetIP = new byte[4];
//
//		System.arraycopy(message, 14, dstIP, 0, 4);
//		System.arraycopy(message, 8, dstMac, 0, 6);
//		System.arraycopy(message, 24, targetIP, 0, 4);
//
//		String ipAddressToString = (dstIP[0] & 0xFF) + "." + (dstIP[1] & 0xFF) + "." + (dstIP[2] & 0xFF) + "."
//				+ (dstIP[3] & 0xFF);
//		String targetIpAddressToString = (targetIP[0] & 0xFF) + "." + (targetIP[1] & 0xFF) + "." + (targetIP[2] & 0xFF)
//				+ "." + (targetIP[3] & 0xFF);
//
//		if (message[6] == (byte) 0x00 && message[7] == (byte) 0x01) { // ARP-request Receive ("Complete")
//
//			byte[] newOp = new byte[2];
//			newOp[0] = (byte) 0x00;
//			newOp[1] = (byte) 0x02;
//
////         SetMacAddrDstAddr(dstMac);
//			if (proxyTable.containsKey(targetIpAddressToString)) {
//				byte[] getDstMac = (byte[]) (proxyTable.get(targetIpAddressToString)[1]);
//				String portName_proxy = (String) proxyTable.get(targetIpAddressToString)[0];
//				Send(targetIP, dstIP, mac_srcaddress, dstMac, newOp);
//				return true;
//			}
//			if (!cacheTable.containsKey(ipAddressToString)) {
//
//				value[0] = cacheTable.size();
//				value[1] = dstMac;
//				value[2] = "Complete";
//				value[3] = System.currentTimeMillis();
//
//				cacheTable.put(ipAddressToString, value);
////            arpTable.updateARPCacheTable();
//				arpTable.addEntry(ipAddressToString, value, portName);
////				System.out.println("지원");
//			} else {
//
//				value[0] = cacheTable.get(ipAddressToString)[0];
//				value[1] = dstMac; // mac address
//				value[2] = cacheTable.get(ipAddressToString)[2];
//				value[3] = System.currentTimeMillis();
//
//				cacheTable.put(ipAddressToString, value);
////            arpTable.addEntry(ipAddressToString,value,portName);
////            arpTable.updateARPCacheTable();
////            return true;
//			}
//			if (proxyTable.containsKey(targetIpAddressToString))
//				return true;
//
//			newOp = new byte[2];
//			newOp[0] = (byte) 0x00;
//			newOp[1] = (byte) 0x02;
//
////         SetMacAddrDstAddr(dstMac);
//
//			boolean checkPort = false;
//
//			if (!proxyTable.containsKey(targetIpAddressToString)) {
////        	 System.out.println();
////        	 System.out.println("ip 검사 : "+targetIpAddressToString);
//				for (int i = 0; i < 4; i++) {
//					if (message[i + 24] != ipAddress_port[i])
//						checkPort = true;
//
//				}
////            System.out.println("ip 검사 결과 "+(checkPort)+" portName : "+portName+" layerName : "+GetLayerName());
//				if (checkPort)
//					return false;
//
//				Send(ipAddress_port, dstIP, mac_srcaddress, dstMac, newOp);
////            if(checkPort1) Send(ipAddress_port2, dstIP,mac_srcaddress , arp_mac_dstaddr, newOp, "Port2");
////            else Send(ipAddress_port1, dstIP, mac_srcaddress, arp_mac_dstaddr, newOp,"Port1");
//				return true;
//			}
//
//		} else if (message[6] == (byte) 0x00 && message[7] == (byte) 0x02) { // ARP-reply Receive ("Incomplete" ->
//
//			/*****************************************************/
//    	  System.out.println("REPLY RECEIVE : "+ipAddressToString+" "+waitfor.containsKey(ipAddressToString));
//			if (waitfor.containsKey(ipAddressToString)) {
////        	 System.out.println("reply 도착");
//				ArrayList<Object[]> list = waitfor.get(ipAddressToString);
//				if (!list.isEmpty()) {
//					Object[] toSendObj = (Object[]) list.get(0);
//
////                 if(((String)toSendObj[1]).equals("Port1")) {
//					((EthernetLayer) this.GetUnderLayer(0)).Send((byte[]) toSendObj[0], ((byte[]) toSendObj[0]).length,
//							dstMac);
////                 }else {
////                    ((EthernetLayer)this.GetUnderLayer(1)).Send((byte[])toSendObj[0], ((byte[])toSendObj[0]).length,dstMac);
////                 }
//					if (list.size() == 1)
//						waitfor.remove(ipAddressToString);
//					else
//						list.remove(0);
//				}
//
//			}
//			/****************************************************/
//
//			if (cacheTable.containsKey(ipAddressToString)) {
////            System.out.println("cache Table already has it "+cacheTable.get(ipAddressToString)[2]);
//
//				value[0] = cacheTable.get(ipAddressToString)[0];
//				value[1] = dstMac;
//				value[2] = "Complete";
//				value[3] = System.currentTimeMillis();
//				cacheTable.replace(ipAddressToString, value);
//
//				arpTable.updateARPCacheTable();
////            arpTable.addEntry(ipAddressToString,value,portName);
//			} else {
//				value[0] = cacheTable.size();
//				value[1] = dstMac;
//				value[2] = "Complete";
//				value[3] = System.currentTimeMillis();
//				cacheTable.put(ipAddressToString, value);
//
//				arpTable.addEntry(ipAddressToString, value, portName);
//				
////				System.out.println("유나");
////             arpTable.updateARPCacheTable();
//			}
//
//		}
		return false;
	}

	public void updateCacheTable() {
		if (_APP.exist) {

			Set keyS = _Cache_Table.keySet();

			for (Iterator iterator = keyS.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				Object[] value = (Object[]) _Cache_Table.get(key);

				if (value[2].equals("Incomplete")) {
					_APP.TotalArea
							.append("       " + key + "\t" + "??????????????\t incomplete" + portName + "\n");
				} else {
					byte[] maxAddr = (byte[]) value[1];
					String macAddress = String.format("%X:", maxAddr[0]) + String.format("%X:", maxAddr[1])
							+ String.format("%X:", maxAddr[2]) + String.format("%X:", maxAddr[3])
							+ String.format("%X:", maxAddr[4]) + String.format("%X", maxAddr[5]);

					_APP.TotalArea
							.append("       " + key + "\t" + macAddress + "\t complete" + portName + "\n");
				}
			}
		}
	}

	/* ------------------------------------------------------------------------------------------- */
	/*
	 *  arp_protocol_srcaddr -> arp_ipSrcAddr
	 *  arp_protocol_dstaddr -> arp_ipDstAddr
	 *  mac_srcaddr -> arp_macSrcAddr
	 *  mac_dstaddr -> arp_macDstAddr
	 */
	public boolean SendforARP(byte[] message, String port, byte[] arp_ipSrcAddr, byte[] arp_ipDstAddr,
			byte[] arp_macSrcAddr, byte[] arp_macDstAddr, byte[] arp_opcode) {

		String ipAddressToString = (arp_ipDstAddr[0] & 0xFF) + "." + (arp_ipDstAddr[1] & 0xFF) + "."
				+ (arp_ipDstAddr[2] & 0xFF) + "." + (arp_ipDstAddr[3] & 0xFF);
//      System.out.println();
//      System.out.println("Send for ARP : "+port);
//      System.out.println("is in cacheTable ? : "+cacheTable.containsKey(ipAddressToString));
//      

		if (_Cache_Table.containsKey(ipAddressToString) && (_Cache_Table.get(ipAddressToString)[2].equals("Complete"))) {

			byte[] realAddress = (byte[]) (_Cache_Table.get(ipAddressToString)[1]);
//         if(port.equals("Port1")) {
//            ((EthernetLayer)this.GetUnderLayer(0)).Send(message, message.length,realAddress);
//         }else {
			((EthernetLayer) this.GetUnderLayer(0)).Send(message, message.length, realAddress);
//         }
			return true;
		}
		Object[] value = new Object[2];
		value[0] = message;
		value[1] = port;

		if (_TimeToLive.containsKey(ipAddressToString))
			_TimeToLive.get(ipAddressToString).add(value);
		else {
			ArrayList<Object[]> objList = new ArrayList<Object[]>();
			objList.add(value);
			_TimeToLive.put(ipAddressToString, objList);
		}
//      System.out.println("_TimeToLive size : "+ _TimeToLive.size());

		Send(arp_ipSrcAddr, arp_ipDstAddr, arp_macSrcAddr, arp_macDstAddr, arp_opcode);

		return true;
	}

	/* ------------------------------------------------------------------------------------------- */

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null) {
			return;
		}
		this.p_aUnderLayerARP.add(nUnderLayerCount++, pUnderLayer);
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		this.SetUnderLayer(pUULayer);
	}

//   public void SetMacAddrDstAddr(byte[] dstaddr) {
//      this.arp_mac_dstaddr = dstaddr;
//   }

	public void SetIPAddrSrcAddr(byte[] srcaddr) {
		this.ipAddr_port = srcaddr;
	}
//   public void SetPortName(String portName) {
//	   this.portName = portName;
//   }

	public void SetARPTable(ARPTable arpTable, ApplicationLayer app) {
		this._ARP_Table = arpTable;
		this._APP = app;
	}

	@Override
	public BaseLayer GetUnderLayer(int nindex) {
		if (nindex < 0 || nindex > nUnderLayerCount || nUnderLayerCount < 0)
			return null;
		return p_aUnderLayerARP.get(nindex);
	}

	// Cache_Timeout -> _Cache_TimeToLive
	class _Cache_TimeToLive implements Runnable {
		HashMap<String, Object[]> _Cache_Table;	// cacheTable -> _Cache_Table
		int incomplete_TTL;	// incomplete_TTL
		int complete_TTL;	//complete_TTL

		public _Cache_TimeToLive(HashMap<String, Object[]> _Cache_Table, int incomplete_TTL, int complete_TTL) {
			this._Cache_Table = _Cache_Table;
			this.incomplete_TTL = incomplete_TTL;
			this.complete_TTL = complete_TTL;
		}

		@Override
		public void run() {
			while (true) {
				if (_ARP_Table != null) {
					Set keyS = this._Cache_Table.keySet();
					ArrayList<String> deleteKey = new ArrayList<String>();
					// iterator -> it
					for (Iterator it = keyS.iterator(); it.hasNext();) {
						String key = null;
						if ((key = (String) it.next()) != null) {
							Object[] value = (Object[]) this._Cache_Table.get(key);
							if (value[2].equals("Incomplete")) {
								if ((System.currentTimeMillis() - (long) value[3]) / 60000 >= incomplete_TTL) {
									deleteKey.add(key);
								}
							} else {
								if ((System.currentTimeMillis() - (long) value[3]) / 60000 >= complete_TTL) {
									deleteKey.add(key);
								}
							}
						}
					}
					for (int i = 0; i < deleteKey.size(); i++)
						this._Cache_Table.remove(deleteKey.get(i));
					if (deleteKey.size() != 0)
						_ARP_Table.updateARPCacheTable();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
	}

//	class Send_Thread implements Runnable {
//		byte[] arp_protocol_srcaddr;
//		byte[] arp_protocol_dstaddr;
//		byte[] mac_srcaddr;
//		byte[] mac_dstaddr;
//		byte[] arp_opcode;
//		EthernetLayer ethernet;
//
//		public Send_Thread(byte[] arp_protocol_srcaddr, byte[] arp_protocol_dstaddr, byte[] mac_srcaddr,
//				byte[] mac_dstaddr, byte[] arp_opcode, EthernetLayer ethernet) {
//			this.arp_protocol_srcaddr = arp_protocol_srcaddr;
//			this.arp_protocol_dstaddr = arp_protocol_dstaddr;
//			this.mac_dstaddr = mac_dstaddr;
//			this.mac_srcaddr = mac_srcaddr;
//			this.ethernet = ethernet;
//			this.arp_opcode = arp_opcode;
//		}
//
//		@Override
//		public void run() {
//			Object[] value = new Object[4];
//
//			String srcIpAddressToString = (arp_protocol_srcaddr[0] & 0xFF) + "." + (arp_protocol_srcaddr[1] & 0xFF)
//					+ "." + (arp_protocol_srcaddr[2] & 0xFF) + "." + (arp_protocol_srcaddr[3] & 0xFF);
//			String dstIpAddressToString = (arp_protocol_dstaddr[0] & 0xFF) + "." + (arp_protocol_dstaddr[1] & 0xFF)
//					+ "." + (arp_protocol_dstaddr[2] & 0xFF) + "." + (arp_protocol_dstaddr[3] & 0xFF);
//
//			if (arp_opcode[0] == (byte) 0x00 && arp_opcode[1] == (byte) 0x01) {
//
//				if (cacheTable.containsKey(dstIpAddressToString))
//					return;
//
//				value[0] = cacheTable.size(); // ARP-request Send ("Incomplete")
//				value[1] = mac_dstaddr;
//				value[2] = "Incomplete";
//				value[3] = System.currentTimeMillis();
//
//				cacheTable.put(dstIpAddressToString, value);
//				arpTable.addEntry(dstIpAddressToString, value, portName);
//
//				m_sHeader._arp_mac_dstaddr.addr = mac_dstaddr;
//
//				m_sHeader._arp_protocol_srcaddr.addr = arp_protocol_srcaddr;
//				m_sHeader._arp_protocol_dstaddr.addr = arp_protocol_dstaddr;
//				m_sHeader.arp_mac_src.addr = mac_srcaddr;
//
//			} else {
//
//				m_sHeader._arp_mac_dstaddr.addr = mac_dstaddr;
//
//				m_sHeader._arp_protocol_srcaddr.addr = arp_protocol_srcaddr;
//				m_sHeader._arp_protocol_dstaddr.addr = arp_protocol_dstaddr;
//				m_sHeader.arp_mac_src.addr = mac_srcaddr;
//
//			}
//
//			m_sHeader.arp_hwType[0] = (byte) 0x00; // Ethernet
//			m_sHeader.arp_hwType[1] = (byte) 0x01;
//
//			m_sHeader.arp_protoAddrType[0] = (byte) 0x08; // IP
//			m_sHeader.arp_protoAddrType[1] = (byte) 0x00;
//
//			m_sHeader.arp_hwAddrLength[0] = 6;
//			m_sHeader.arp_protoAddrLength[0] = 4;
//
//			m_sHeader.arp_opcode = arp_opcode;
//
//			byte[] bytes = ObjToByte(m_sHeader);
//
//			((EthernetLayer) ethernet).Send(bytes, bytes.length, null);
//			return;
//
//		}
//	}
	// Receive_Thread -> _Receive_Thread
	class _Receive_Thread implements Runnable {
		byte[] input;
		byte[] macSrcAddr;	// mac_srcaddress -> macSrcAddr
		EthernetLayer ethernet;
		ARPLayer _ARP;	// arp -> _ARP
		ARPTable _ARP_Table_Thread;	// arpTableInThread -> _ARP_Table_Thread

		public _Receive_Thread(byte[] input, byte[] macSrcAddr, EthernetLayer ethernet,ARPLayer _ARP, ARPTable _ARP_Table_Thread) {
			this.input = input;
			this.macSrcAddr=macSrcAddr;
			this.ethernet =ethernet;
			this._ARP = _ARP;
			this._ARP_Table_Thread = _ARP_Table_Thread;
		}

		@Override
		public void run() {
			byte[] message = input;

			Object[] value = new Object[4];
			byte[] ipDst = new byte[4];	// dstIP -> ipDst
			byte[] macDst = new byte[6];	// dstMac -> macDst
			byte[] targetIP = new byte[4];

			System.arraycopy(message, 14, ipDst, 0, 4);
			System.arraycopy(message, 8, macDst, 0, 6);
			System.arraycopy(message, 24, targetIP, 0, 4);

			String ipAddressToString = (ipDst[0] & 0xFF) + "." + (ipDst[1] & 0xFF) + "." + (ipDst[2] & 0xFF) + "."
					+ (ipDst[3] & 0xFF);
			String targetIpAddressToString = (targetIP[0] & 0xFF) + "." + (targetIP[1] & 0xFF) + "."
					+ (targetIP[2] & 0xFF) + "." + (targetIP[3] & 0xFF);

			if (message[6] == (byte) 0x00 && message[7] == (byte) 0x01) { // ARP-request Receive ("Complete")

				byte[] newOp = new byte[2];
				newOp[0] = (byte) 0x00;
				newOp[1] = (byte) 0x02;

//				if (proxyTable.containsKey(targetIpAddressToString)) {
//					byte[] getDstMac = (byte[]) (proxyTable.get(targetIpAddressToString)[1]);
//					String portName_proxy = (String) proxyTable.get(targetIpAddressToString)[0];
//					((ARPLayer)arp).Send(targetIP, dstIP, mac_srcaddress, dstMac, newOp);
//					return;
//				}
				if (!_Cache_Table.containsKey(ipAddressToString)) {

					value[0] = _Cache_Table.size();
					value[1] = macDst;
					value[2] = "Complete";
					value[3] = System.currentTimeMillis();

					_Cache_Table.put(ipAddressToString, value);
					_ARP_Table_Thread.addEntry(ipAddressToString, value, portName);
//					System.out.println("추가");
				} else {

					value[0] = _Cache_Table.get(ipAddressToString)[0];
					value[1] = macDst; // mac address
					value[2] = _Cache_Table.get(ipAddressToString)[2];
					value[3] = System.currentTimeMillis();

					_Cache_Table.put(ipAddressToString, value);
				}
				if (_Proxy_Table.containsKey(targetIpAddressToString))
					return;

				newOp = new byte[2];
				newOp[0] = (byte) 0x00;
				newOp[1] = (byte) 0x02;

				boolean checkPort = false;

				if (!_Proxy_Table.containsKey(targetIpAddressToString)) {
					for (int i = 0; i < 4; i++) {
						if (message[i + 24] != ipAddr_port[i])
							return;
					}
//					System.out.println("arp reply");
					((ARPLayer)_ARP).Send(ipAddr_port, ipDst, macSrcAddr, macDst, newOp);
					return;
				}

			} else if (message[6] == (byte) 0x00 && message[7] == (byte) 0x02) { // ARP-reply Receive ("Incomplete" ->

				/*****************************************************/
				if (_TimeToLive.containsKey(ipAddressToString)) {
					ArrayList<Object[]> list = _TimeToLive.get(ipAddressToString);
					if (!list.isEmpty()) {
						Object[] toSendObj = (Object[]) list.get(0);

						((EthernetLayer) ethernet).Send((byte[]) toSendObj[0],
								((byte[]) toSendObj[0]).length, macDst);
						if (list.size() == 1)
							_TimeToLive.remove(ipAddressToString);
						else
							list.remove(0);
					}

				}
				/****************************************************/

				if (_Cache_Table.containsKey(ipAddressToString)) {

					value[0] = _Cache_Table.get(ipAddressToString)[0];
					value[1] = macDst;
					value[2] = "Complete";
					value[3] = System.currentTimeMillis();
					_Cache_Table.replace(ipAddressToString, value);

					_ARP_Table.updateARPCacheTable();
				} else {
					value[0] = _Cache_Table.size();
					value[1] = macDst;
					value[2] = "Complete";
					value[3] = System.currentTimeMillis();
					_Cache_Table.put(ipAddressToString, value);

					_ARP_Table.addEntry(ipAddressToString, value, portName);
				}

			}
		}
	}
}
