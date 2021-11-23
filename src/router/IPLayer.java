package router;

import java.util.ArrayList;


public class IPLayer implements BaseLayer {

	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	private RouterTable router_Table = null;
	
	private int IP_HEAD_SIZE = 20;

	private class _IP_ADDR {
		private byte[] addr = new byte[4];

		public _IP_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
		}
	}

	private class _IP_HEADER { // 20byte
		byte ip_ver; // ip version -> IPv4 : 4 (O, 1byte)
		byte ip_service; // type of service (X, 1byte)
		byte[] ip_totalLen; // total packet length (��, 2byte)
		byte[] ip_id;	// datagram id (X, 2byte)
		byte[] ip_fragoff; // fragment offset (X, 2byte)
		byte ip_ttl; // time to live in gatewaye hopes(X, 1byte)
		byte ip_proto; // IP protocol (X, 1byte)
		byte[] ip_cksum; // header checksum (X, 2byte)
		_IP_ADDR ip_src; // IP address of source (O, 4byte)
		_IP_ADDR ip_dst; // IP address of destination (O, 4byte)
		byte[] ip_data; // new (O, IP_DATA_SIZE)

		public _IP_HEADER() {
			this.ip_ver = (byte) 0x00; // 0
			this.ip_service = (byte) 0x00; // 1
			this.ip_totalLen = new byte[2]; // 2-3
			this.ip_id = new byte[2]; // 4-5
			this.ip_fragoff = new byte[2]; //6-7
			this.ip_ttl = (byte) 0x00; // 8
			this.ip_proto = (byte) 0x00; // 9
			this.ip_cksum = new byte[2]; // 10-11
			this.ip_src = new _IP_ADDR(); // 12-15
			this.ip_dst = new _IP_ADDR(); // 16-19
			this.ip_data = null;
		}
	}

	_IP_HEADER m_iHeader = new _IP_HEADER();

	int interfaceNumber;

	IPLayer otherIPLayer;

	public IPLayer(String layerName) {
		pLayerName = layerName;

	}

	void setOtherIPLayer(IPLayer other) {
		otherIPLayer = other;
	}

	void setInterfaceNumber(int number) {
		interfaceNumber = number;
	}

	void setSourceIpAddress(byte[] sourceAddress) {
		for (int i = 0; i < 4; i++)
			m_iHeader.ip_src.addr[i] = sourceAddress[i];
	}

	void setDestinationIPAddress(byte[] destinationAddress) {
		for (int i = 0; i < 4; i++)
			m_iHeader.ip_dst.addr[i] = destinationAddress[i];
	}

	public void ResetHeader() {
		for (int i = 0; i < 4; i++) {
			m_iHeader.ip_src.addr[i] = (byte) 0x00;
			m_iHeader.ip_dst.addr[i] = (byte) 0x00;
		}
		for (int i = 0; i < 2; i++) {
			m_iHeader.ip_totalLen[i] = (byte) 0x00;
			m_iHeader.ip_id[i] = (byte) 0x00;
			m_iHeader.ip_fragoff[i] = (byte) 0x00;
			m_iHeader.ip_cksum[i] = (byte) 0x00;
		}
		m_iHeader.ip_ver = (byte) 0x00;
		m_iHeader.ip_service = (byte) 0x00;
		m_iHeader.ip_ttl = (byte) 0x00;
		m_iHeader.ip_proto = (byte) 0x00;

		m_iHeader.ip_data = null;
	}

	boolean isEqualIpAddress(byte[] input, byte[] addr) {
		for (int i = 0; i < input.length; i++) {
			if (input[i] != addr[i]) {
				return false;
			}
		}
		return true;

	}

	public byte[] ObjToByte(_IP_HEADER Header, byte[] input, int length) {
		byte[] buf = new byte[length + 20];

		buf[0] = Header.ip_ver; // buf에 1byte ver 초기화
		buf[1] = Header.ip_service; // 1byte service
		System.arraycopy(Header.ip_totalLen, 0, buf, 2, 2); // 2byte totallen
		System.arraycopy(Header.ip_id, 0, buf, 4, 2); // 2byte id
		System.arraycopy(Header.ip_fragoff, 0, buf, 6, 2); // 2byte fragoff
		buf[8] = Header.ip_ttl; // 1byte ttl
		buf[9] = Header.ip_proto; // 1byte proto
		System.arraycopy(Header.ip_cksum, 0, buf, 10, 2); // 2byte cksum
		System.arraycopy(Header.ip_src.addr, 0, buf, 12, 4); // 4byte src
		System.arraycopy(Header.ip_dst.addr, 0, buf, 16, 4); // 4byte dst
		System.arraycopy(input, 0, buf, 20, length); // IP header에 TCP header 붙이기

		for (int i = 0; i < length; i++)
			buf[20 + i] = input[i];

		return buf;
	}
	
	public boolean Receive(byte[] input) {

		byte[] dstIpAddr = new byte[4];

		for (int i = 0; i < 4; i++)
			dstIpAddr[i] = input[16 + i];
		
		dstIpAddr = router_Table.findSendingNetwork(dstIpAddr);

		try {
			if (isEqualIpAddress(dstIpAddr, m_iHeader.ip_src.addr)) {
				this.Send(input, input.length);
			} else if (isEqualIpAddress(dstIpAddr, otherIPLayer.m_iHeader.ip_src.addr)) {
				otherIPLayer.Receive(input);
			}
		} catch (NullPointerException e) {
		}
		return true;
	}
	
	public boolean Send(byte[] input, int length) {
		byte[] IP_header_added_bytes = ObjToByte(m_iHeader, input, length);
		((ARPLayer) this.GetUnderLayer()).Send(IP_header_added_bytes, IP_header_added_bytes.length);
		return true;
	}
	
//	public boolean GratSend(byte[] input, int length) {
//		byte[] TCP_header_added_bytes = ObjToByte(m_iHeader, input, length);
//
//		this.GetUnderLayer().GratSend(TCP_header_added_bytes, TCP_header_added_bytes.length);
//		return false;
//	}

	
	public static int byte2Int(byte[] src) {
		int s1 = src[0] & 0xFF;
		int s2 = src[1] & 0xFF;

		return ((s1 << 8) + (s2 << 0));
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
		// nUpperLayerCount++;
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

	// Jieun

}