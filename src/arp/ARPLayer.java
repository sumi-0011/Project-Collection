package arp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import java.net.NetworkInterface;
import java.net.SocketException;

public class ARPLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	public ArrayList<_Cache_Table> cacheTable = new ArrayList<_Cache_Table>();
	public ArrayList<_Proxy_Table> proxyTable = new ArrayList<_Proxy_Table>();

	private final byte[] OP_ARP_REQUEST = byte4To2(intToByte(1));
	private final byte[] OP_ARP_REPLY = byte4To2(intToByte(2));
	public final _IP_ADDR MY_IP_ADDRESS = new _IP_ADDR();
	public final _MAC_ADDR MY_MAC_ADDRESS = new _MAC_ADDR();

	_ARP_HEADER m_aHeader = new _ARP_HEADER();

	// Basic ARP Cache Table
	public class _Cache_Table {
		// basic cache
		byte[] ipaddr;
		byte[] ethaddr;
		String state;
		int time; // time to live

		public _Cache_Table(byte[] ipaddr, byte[] ethaddr, String state, int time) {
			this.ipaddr = ipaddr;
			this.ethaddr = ethaddr;
			this.state = state;
			this.time = time;
		}

		// ipAddr에 대한 MACAddr, state, time를 갱신한다.
		public void update(byte[] ethaddr, String state, int time) {
			this.ethaddr = ethaddr;
			this.state = state;
			this.time = time;
		}
	}

	// ARP Porxy Table
	public class _Proxy_Table {
		byte[] ipaddr;
		byte[] ethaddr;
		String device;

		public _Proxy_Table(byte[] ipaddr, byte[] ethaddr, String device) {
			this.ipaddr = ipaddr;
			this.ethaddr = ethaddr;
			this.device = device;
		}
	}

	// IP Address class (4 bytes)
	private class _IP_ADDR {
		private byte[] addr = new byte[4];

		public _IP_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
		}
	}

	// MAC(Ethernet) Address class (6 bytes)
	private class _MAC_ADDR {
		private byte[] addr = new byte[6];

		public _MAC_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
			this.addr[4] = (byte) 0x00;
			this.addr[5] = (byte) 0x00;
		}
	}

	// ARP Header (28 bytes)
	private class _ARP_HEADER {
		byte[] arp_hdType; // Ethernet = 1
		byte[] arp_protType; // IP = 0x0800
		byte arp_hdSize; // MAC(Ethernet) address = 6 bytes
		byte arp_protSize; // IP address = 4 bytes
		byte[] arp_op; // request = 1, reply = 2
		_MAC_ADDR arp_senderMACAddr; // Sender's MAC(Ethernet) Address
		_IP_ADDR arp_senderIPAddr;// Sender's IP Address
		_MAC_ADDR arp_targetMACAddr; // Target's MAC(Ethernet) Address
		_IP_ADDR arp_targetIPAddr; // Target's IP Address

		public _ARP_HEADER() { // 28byte
			this.arp_hdType = new byte[2]; // 0 - 1
			this.arp_protType = new byte[2]; // 2 - 3
			this.arp_hdSize = (byte) 0x00;// 4
			this.arp_protSize = (byte) 0x00;// 5
			this.arp_op = new byte[2];// 6 - 7
			this.arp_senderMACAddr = new _MAC_ADDR(); // 8 - 13
			this.arp_senderIPAddr = new _IP_ADDR(); // 14 - 17
			this.arp_targetMACAddr = new _MAC_ADDR();// 18 - 23
			this.arp_targetIPAddr = new _IP_ADDR(); // 24 - 27
		}
	}

	// 생성자
	public ARPLayer(String pName) {
		this.pLayerName = pName;
		getLocalPCAddr();
		ResetARP();
		//run_Clean_Thread(cache_Table, cache_Itr);
	}

	// ARP Header를 초기화한다.
	public void ResetARP() {
		// Hard type, Prot type, Op code 초기화
		for (int i = 0; i < 2; i++) {
			m_aHeader.arp_hdType[i] = (byte) 0x00;
			m_aHeader.arp_protType[i] = (byte) 0x00;
			m_aHeader.arp_op[i] = (byte) 0x00;
		}
		// Hard size, Prot size 초기화
		m_aHeader.arp_hdSize = (byte) 0x00;
		m_aHeader.arp_protSize = (byte) 0x00;
		// Sender와 Target의 MAC Address 초기화
		for (int i = 0; i < 6; i++) {
			m_aHeader.arp_senderMACAddr.addr[i] = (byte) 0x00;
			m_aHeader.arp_targetMACAddr.addr[i] = (byte) 0x00;
		}
		// Sender와 Target의 IP Address 초기화
		for (int i = 0; i < 4; i++) {
			m_aHeader.arp_senderIPAddr.addr[i] = (byte) 0x00;
			m_aHeader.arp_targetIPAddr.addr[i] = (byte) 0x00;
		}
	}

	// 다음 Layer로 보내기 위해서 Header와 input를 합쳐주는 메소드
	public byte[] ObjToByte(_ARP_HEADER Header, byte[] input, int length) {
		byte[] data = new byte[length + 28];

		data[0] = Header.arp_hdType[0];
		data[1] = Header.arp_hdType[1];
		data[2] = Header.arp_protType[0];
		data[3] = Header.arp_protType[1];
		data[4] = Header.arp_hdSize;
		data[5] = Header.arp_protSize;
		data[6] = Header.arp_op[0];
		data[7] = Header.arp_op[1];

		data[8] = Header.arp_senderMACAddr.addr[0];
		data[9] = Header.arp_senderMACAddr.addr[1];
		data[10] = Header.arp_senderMACAddr.addr[2];
		data[11] = Header.arp_senderMACAddr.addr[3];
		data[12] = Header.arp_senderMACAddr.addr[4];
		data[13] = Header.arp_senderMACAddr.addr[5];

		data[14] = Header.arp_senderIPAddr.addr[0];
		data[15] = Header.arp_senderIPAddr.addr[1];
		data[16] = Header.arp_senderIPAddr.addr[2];
		data[17] = Header.arp_senderIPAddr.addr[3];

		data[18] = Header.arp_targetMACAddr.addr[0];
		data[19] = Header.arp_targetMACAddr.addr[1];
		data[20] = Header.arp_targetMACAddr.addr[2];
		data[21] = Header.arp_targetMACAddr.addr[3];
		data[22] = Header.arp_targetMACAddr.addr[4];
		data[23] = Header.arp_targetMACAddr.addr[5];

		data[24] = Header.arp_targetIPAddr.addr[0];
		data[25] = Header.arp_targetIPAddr.addr[1];
		data[26] = Header.arp_targetIPAddr.addr[2];
		data[27] = Header.arp_targetIPAddr.addr[3];

		for (int i = 0; i < length; i++)
			data[28 + i] = input[i];

		return data;
	}

	// ARP Header를 초기 상태로 세팅하는 메소드
	public void setARPHeader() {
		this.m_aHeader.arp_hdType = byte4To2(intToByte(1));
		this.m_aHeader.arp_protType[0] = (byte) 0x08;
		this.m_aHeader.arp_protType[1] = (byte) 0x00;
		this.m_aHeader.arp_hdSize = 6;
		this.m_aHeader.arp_protSize = 4;
		this.m_aHeader.arp_op[1] = 1;
	}

	// Gratuitous ARP Send
	public boolean GratSend(byte[] input, int length) {
		// ARP헤더 초기 세팅
		setARPHeader();
		// Sender's hardware address를 세팅,
		// 바꿀 MAC주소를 가져와서 넣기(Dlg)
		// Sender's protocol address를 세팅
		setSrcIPAddr(MY_IP_ADDRESS.addr); // 자기 IP주소 가져와서 넣기
		// Target's protocol address를 세팅
		setDstIPAddr(MY_IP_ADDRESS.addr); // 자기 IP주소 가져와서 넣기

		// Gratuitous ARP Message 생성
		byte[] grat_message = ObjToByte(m_aHeader, input, length); // ARPMessage

		// Gratuitous ARP Message 내려보내기
		this.GetUnderLayer().Send(grat_message, grat_message.length);
		return true;
	}

	// Proxy Request를 받는 메소드
	public boolean proxyRequestReceive(byte[] input, int length) {
		// 1. arp cache table 업데이트
		_MAC_ADDR myMAC = new _MAC_ADDR();
		_IP_ADDR myIP = new _IP_ADDR();
		for (int i = 0; i < 6; i++) {
			myMAC.addr[i] = input[8 + i];
		}
		for (int i = 0; i < 4; i++) {
			myIP.addr[i] = input[14 + i];
		}
		cacheTable.add(new _Cache_Table(myIP.addr, myMAC.addr, "Complete", 12));
		// 2. target protocol address가 proxy table에 있는지 확인
		// 헤더의 target protocol address를 가져온다.
		_IP_ADDR target = new _IP_ADDR();
		for (int i = 0; i < 4; i++) {
			target.addr[i] = input[i + 24];
		}
		// proxy table에 target proto addr이 존재하면 proxy reply send
		if (findProxyTable(target.addr) != -1) {
			return true;
		}
		// cache_Table에 target proto addr이 존재하지 않으면 message drop
		return false;
	}

	// Porxy Reply를 보내는 메소드
	public boolean proxyReplySend(byte[] input, int length) {
		// 1. arp message의 target protocol address가 proxy entry에 있는지 이미 proxyRQReceive에서
		// 확인함.
		// 2. arp message 작성
		// setARPHeaderBeforeSend();
		// _IP_ADDR target = new _IP_ADDR();
		// for (int i = 0; i < 4; i++) {
		// target.addr[i] = input[i + 24];
		// }
		// cache table에서 찾기 위해 target protocol address를 string으로 변환
		// String tIpAddr = target.addr.toString();
		// 찾은 ip주소로 target hardware address를 가져온다.
		// mac에는 target hardware address가 들어있다.
		// byte[] mac = proxy_Table.get(tIpAddr).proxy_ethaddr;
		// header의 target hardware address에 자기의 맥주소를 넣어야 한다.(10/27)
		for (int i = 0; i < 6; i++) {
			input[i + 18] = MY_MAC_ADDRESS.addr[i];
		}
		// ※ 주소 swap
		input = swapAddr(input);
		// opcode를 reply(2)로 변경
		input[6] = (byte) 0x00;
		input[7] = (byte) 0x02;
		// byte[] bytes = ObjToByte(m_aHeader, input, length);
		this.GetUnderLayer().Send(input, input.length);
		return true;
	}

	public boolean proxyRPReceive(byte[] input) {
		// 헤더에서 target ip, target mac 가져오기
		_IP_ADDR targetIP = new _IP_ADDR();
		for (int i = 0; i < 4; i++) {
			targetIP.addr[i] = input[i + 24];
		}
		_MAC_ADDR targetMAC = new _MAC_ADDR();
		for (int i = 0; i < 6; i++) {
			targetMAC.addr[i] = input[i + 18];
		}
		// 1. 내게 온 ARP RP message가 맞는지 확인
		// 나의 ip주소와 target ip가 일치하는지 확인, 나의 mac주소와 target mac주소가 일치하는지 확인
		if (Arrays.equals(MY_IP_ADDRESS.addr, targetIP.addr) && Arrays.equals(MY_MAC_ADDRESS.addr, targetMAC.addr)) {
			// 2. sender protocol address가 내 cache_Table에 있는지 확인
			_IP_ADDR senderIP = new _IP_ADDR();
			for (int i = 0; i < 4; i++) {
				senderIP.addr[i] = input[i + 14];
			}
			_MAC_ADDR senderMAC = new _MAC_ADDR();
			for (int i = 0; i < 6; i++) {
				senderMAC.addr[i] = input[i + 8];
			}
			// 내 cache_Table에 있다면 내 cache_table에 sender hd address 업데이트
			int index = findCacheTable(senderIP.addr);
			if (index != -1) {
				cacheTable.get(index).update(senderMAC.addr, "Complete", 20);
			}
			return true;
		}
		return false;
	}

	public void getLocalPCAddr() {
		try {
			Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces(); // 현재 PC의 모든 NIC를 열거형으로
																								// 받는다.

			// isUP 중에 MAC과 IP 주소를 출력
			while (networks.hasMoreElements()) {
				NetworkInterface network = networks.nextElement();
				if (network.isUp()) {
					if (network.getHardwareAddress() != null) {// loop back Interface가 null이므로, 걸러준다.
						MY_MAC_ADDRESS.addr = network.getHardwareAddress(); // MAC주소 받기
						MY_IP_ADDRESS.addr = network.getInetAddresses().nextElement().getAddress(); // IP주소 받기
						System.out.println(byteToIP(MY_IP_ADDRESS.addr));
						System.out.println(byteToMAC(MY_MAC_ADDRESS.addr));
						return; // 현재 사용중인 NIC 이외에는 필요 없다. 반복문 탈출
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public String byteToIP(byte[] addr) {
		String ip = "";
		for(byte b : addr) {
			ip += Integer.toString(b & 0xff) + ".";
		}
		// 마지막 .를 빼기 위해서 substring 메소드 사용
		return ip.substring(0, ip.length() - 1);
	}
	// byte 형식의 addr를 MAC Address(ex. ff:ff:ff:ff:ff:ff) 형식으로 변환
	public String byteToMAC(byte[] addr) {
		StringBuilder mac = new StringBuilder();
		for(byte b : addr) {
			mac.append(String.format("%02X", b));
			mac.append(":");
		}
		// 마지막 :를 빼기 위해서 deleteCharAt 메소드 사용
		mac.deleteCharAt(mac.length() - 1);
		return mac.toString();
	}
	
	public ArrayList<_Cache_Table> getCacheList() {
		return cacheTable;
	}

	public ArrayList<_Proxy_Table> getProxyList() {
		return proxyTable;
	}

	public void setProxyTable(byte[] key, byte[] ethaddr, String device) {
		if (findProxyTable(key) == -1)
			proxyTable.add(new _Proxy_Table(key, ethaddr, device));
	}

	// ProxyTable이
	public boolean isProxyTableEmpty() {
		if (proxyTable.size() == 0)
			return true;
		return false;
	}

	// CacheTable에서 해당 ipAddr이 있는 index를 반환
	public int findCacheTable(byte[] ipAddr) {
		for (int i = 0; i < cacheTable.size(); i++) {
			if (cacheTable.get(i).ipaddr == ipAddr)
				return i;
		}
		return -1;
	}

	// ProxyTable에서 해당 ipAddr이 있는 index를 반환
	public int findProxyTable(byte[] ipAddr) {
		for (int i = 0; i < proxyTable.size(); i++) {
			if (proxyTable.get(i).ipaddr == ipAddr)
				return i;
		}
		return -1;
	}

	public boolean Send(byte[] input, int length) {
		if (fromDlg(input)) { // 비어 있는 byte[]다 -> Dlg에서 왔다 -> Send Request Message.
			setARPHeader(); // opCode를 포함한 hdtype,prototype,hdLen,protoLen 초기화. opCode의 default는 1이다.
			setSrcMAC(MY_MAC_ADDRESS.addr);
			setSrcIPAddr(MY_IP_ADDRESS.addr);
			// setDst는 GUI에서 이루어지고 있다.
			byte[] ARP_header_added_bytes = ObjToByte(m_aHeader, input, length);

			byte[] target_IP = getDstAddrFromHeader(ARP_header_added_bytes);
			_Cache_Table newCache = new _Cache_Table(target_IP, new byte[6], "Incomplete", 3);
			// dst_Addr를 KEY로 갖고 cache_Entry를 VALUE로 갖는 hashMap 생성
			if (findCacheTable(target_IP) == -1) {
				cacheTable.add(newCache);
			}
			this.GetUnderLayer().Send(ARP_header_added_bytes, ARP_header_added_bytes.length); // Send Request Message
		} else { // byte[]가 비어있지 않다 -> Send Reply Message
			for (int i = 0; i < 6; i++)
				input[i + 18] = MY_MAC_ADDRESS.addr[i]; // 패킷의 ???(target MAC)를 내 PC의 MAC 주소로 갱신
			input = swapAddr(input); // src 주소 <-> target 주소 swapping
			input[6] = (byte) 0x00; // setOpCode(2)
			input[7] = (byte) 0x02;
			this.GetUnderLayer().Send(input, input.length); // Send Reply Message
		}
		return false;
	}

	// input이 Dlg에서 온건지 아닌지 확인
	public boolean fromDlg(byte[] input) {
		for (int i = 0; i < input.length; i++) {
			if (input[i] == 0)
				continue;
			else
				return false;
		}
		return true;
	}

	public synchronized boolean receive(byte[] input) {
		boolean Mine = isItMine(input);
		if (isRequest(input)) {// ARP request 인 경우
			if (isProxyARP(input)) {// proxy ARP request 인 경우
				boolean proxyTrue = proxyRequestReceive(input, input.length);
				if (proxyTrue == true) {
					proxyReplySend(input, input.length);
					return true;
				}
				return false;
			} else if (isGratuitousARP(input)) {// Gratuitous ARP request 인 경우
				updateCache(input);
				return true;
			} else {// basic ARP request 인 경우
				if (Mine) {
					updateCache(input);
					Send(input, input.length);// 내 ip 주소로 온 ARP일 때만 reply
					return true;
				} else
					return false;

			}
		} else if (isReply(input)) {// ARP reply 인 경우
			if (isProxyARP(input)) {// proxy ARP reply 인 경우
				if (Mine) {// then proxy send
					proxyRPReceive(input);
					return true;
				} else
					return false;
			} // else if (isGratuitousARP(input)) {
				// if (Mine) {Gratuitous ARP reply 인 경우: 추가 구현 사항이므로 구현하지 않음
				// then Gratuitous send
				// return true;
				// } else
				// return false;
				// }

			else {// basic ARP reply 인 경우
				if (Mine) {
					updateCache(input);
					return true;
				} else
					return false;
			}
		} else
			return false;
	}

	public byte[] intToByte(int value) {
		byte[] byteArray = new byte[4];
		byteArray[0] = (byte) (value >> 24);
		byteArray[1] = (byte) (value >> 16);
		byteArray[2] = (byte) (value >> 8);
		byteArray[3] = (byte) (value);
		return byteArray;
	}

	public byte[] byte4To2(byte[] data) {
		byte[] byteArray = new byte[2];
		byteArray[0] = data[2];
		byteArray[1] = data[3];
		return byteArray;
	}

	public void updateCache(byte[] input) {
		// ARP Reply수신 시 swap된 패킷이 전송되기 때문에, 기존 24를 참조하던 코드를 14로 변경.
		int index = findCacheTable(getDstAddrFromSwappedHeader(input));
		byte[] newMAC = new byte[6];
		System.arraycopy(input, 8, newMAC, 0, 6);
		if (index != -1) {// ip주소가 테이블에 존재하는 경우 == ARP reply 수신 시
			cacheTable.get(index).update(newMAC, "Complete", 80);
			// Request Message를 수신한 PC에서는 dst가 아닌 패킷 Src(14)의 주소를 테이블에 put해야 한다
		} else if (findCacheTable(getDstAddrFromHeader(input)) == -1) { // ip주소가 테이블에 존재하지 않는 경우 == ARP request 수신 시
			byte[] request_ip = getDstAddrFromSwappedHeader(input); // = 20
			cacheTable.add(new _Cache_Table(request_ip, newMAC, "Complete", 80));
		}
	}

	private boolean isProxyARP(byte[] input) {
		// 나의 IP를 가져온다
		_IP_ADDR myIp = new _IP_ADDR();
		for (int i = 0; i < 4; i++) {
			myIp.addr[i] = MY_IP_ADDRESS.addr[i];
		}

		_IP_ADDR target = new _IP_ADDR();
		for (int i = 0; i < 4; i++) {
			target.addr[i] = input[i + 24];
		} // ※ target.addr -> target.addr.toString()

		if (!Arrays.equals(myIp.addr, target.addr) && findProxyTable(target.addr) != -1) {
			return true;
		} else
			return false;
	}

	// Gratuitous ARP가 맞는지 확인
	private boolean isGratuitousARP(byte[] input) {
		for (int i = 0; i < 6; i++) {
			if (input[i + 14] == input[i + 24])
				continue;
			else {
				return false;
			}
		}
		return true;
	}

	// ARP Request가 맞는지 확인
	private boolean isRequest(byte[] input) {
		for (int i = 0; i < 2; i++) {
			if (OP_ARP_REQUEST[i] == input[i + 6])
				continue;
			else {
				return false;
			}
		}
		return true;
	}

	// ARP Reply가 맞는지 확인
	private boolean isReply(byte[] input) {
		for (int i = 0; i < 2; i++) {
			if (OP_ARP_REPLY[i] == input[i + 6])
				continue;
			else {
				return false;
			}
		}
		return true;
	}

	// 자신의 IP Address가 dst Address인지 확인
	private boolean isItMine(byte[] input) {
		for (int i = 0; i < 4; i++) {
			if (MY_IP_ADDRESS.addr[i] == input[i + 24])
				continue;// 내 IP 주소 = destProtoAddr인지 탐색
			else {
				return false;
			}
		}
		return true;
	}

	// Sender와 Target의 위치를 swap
	public byte[] swapAddr(byte[] input) {
		byte[] tempMACAddr = new byte[6];
		byte[] tempProtoAddr = new byte[4];
		int macIndex, ipIndex;

		// temp에 src를 저장
		for (macIndex = 0; macIndex < 6; macIndex++)
			tempMACAddr[macIndex] = input[8 + macIndex];
		for (ipIndex = 0; ipIndex < 4; ipIndex++)
			tempProtoAddr[ipIndex] = input[14 + ipIndex];
		// src에 dst를 저장
		for (macIndex = 0; macIndex < 6; macIndex++)
			input[8 + macIndex] = input[18 + macIndex];
		for (ipIndex = 0; ipIndex < 4; ipIndex++)
			input[14 + ipIndex] = input[24 + ipIndex];
		// dst에 src를 저장
		for (macIndex = 0; macIndex < 6; macIndex++)
			input[18 + macIndex] = tempMACAddr[macIndex];
		for (ipIndex = 0; ipIndex < 4; ipIndex++)
			input[24 + ipIndex] = tempProtoAddr[ipIndex];

		return input;
	}

	public byte[] getDstAddrFromHeader(byte[] input) {
		byte[] dstAddr = new byte[4];
		System.arraycopy(input, 24, dstAddr, 0, 4);
		return dstAddr;
	}

	public byte[] getDstAddrFromSwappedHeader(byte[] input) {
		byte[] dstAddr = new byte[4];
		System.arraycopy(input, 14, dstAddr, 0, 4);
		return dstAddr;
	}

	// SrcMAC getter & setter
	public _MAC_ADDR getSrcMAC() {
		return this.m_aHeader.arp_senderMACAddr;
	}

	public void setSrcMAC(byte[] srcMAC) {
		for (int i = 0; i < srcMAC.length; i++) {
			this.m_aHeader.arp_senderMACAddr.addr[i] = srcMAC[i];
		}
	}

	// SrcIPAddr getter & setter
	public _IP_ADDR getSrcIPAddr() {
		return this.m_aHeader.arp_senderIPAddr;
	}

	public void setSrcIPAddr(byte[] srcAddr) {
		for (int i = 0; i < srcAddr.length; i++) {
			this.m_aHeader.arp_senderIPAddr.addr[i] = srcAddr[i];
		}
	}

	// DstMAC getter & setter
	public _MAC_ADDR getDstMAC() {
		return this.m_aHeader.arp_targetMACAddr;
	}

	public void setDstMAC(byte[] dstMAC) {
		for (int i = 0; i < dstMAC.length; i++) {
			this.m_aHeader.arp_targetMACAddr.addr[i] = dstMAC[i];
		}
	}

	// DstIpAddr getter & setter
	public _IP_ADDR getDstIpAddr() {
		return this.m_aHeader.arp_targetIPAddr;
	}

	public void setDstIPAddr(byte[] dstAddr) {
		for (int i = 0; i < dstAddr.length; i++) {
			this.m_aHeader.arp_targetIPAddr.addr[i] = dstAddr[i];
		}
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
		if (p_UnderLayer == null)
			return null;
		return p_UnderLayer;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);

	}


}
