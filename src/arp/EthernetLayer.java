package arp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;

public class EthernetLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	_ETHERNET_HEADER m_sHeader = new _ETHERNET_HEADER();
	int enetHeadSize = 14;

	private class _ETHERNET_ADDR {
		private byte[] addr = new byte[6];

		public _ETHERNET_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
			this.addr[4] = (byte) 0x00;
			this.addr[5] = (byte) 0x00;
		}
	}

	private class _ETHERNET_HEADER {
		_ETHERNET_ADDR enet_dstAddr;
		_ETHERNET_ADDR enet_srcAddr;
		byte[] enet_type;
		byte[] enet_data;

		public _ETHERNET_HEADER() {
			this.enet_dstAddr = new _ETHERNET_ADDR();
			this.enet_srcAddr = new _ETHERNET_ADDR();
			this.enet_type = new byte[2];
			this.enet_data = null;
		}
	}

	public EthernetLayer(String pName) {
		// super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
		ResetHeader();

	}

	public void ResetHeader() {
		for (int i = 0; i < 6; i++) {
			m_sHeader.enet_dstAddr.addr[i] = (byte) 0x00;
			m_sHeader.enet_srcAddr.addr[i] = (byte) 0x00;
		}
		m_sHeader.enet_type[0] = (byte) 0x00;
		m_sHeader.enet_type[1] = (byte) 0x00;
		m_sHeader.enet_data = null;
	}

	void SetDestinationAddress(byte[] dstAddr) {
		for (int i = 0; i < 6; i++)
			m_sHeader.enet_dstAddr.addr[i] = dstAddr[i];
	}

	void SetSrcAddress(byte[] srcAddr) {
		for (int i = 0; i < 6; i++)
			m_sHeader.enet_srcAddr.addr[i] = srcAddr[i];

	}

	void SetFrameType(byte[] frameType) {
		m_sHeader.enet_type[0] = frameType[0];
		m_sHeader.enet_type[1] = frameType[1];
	}

	public boolean checkBroadCast(byte[] input) throws SocketException {
		for (int i = 0; i < 6; i++) {
			if (input[i + 18] == (byte) 0x00) {
				// broadcast�� ��� true
				// Receiver�� Ethernet Address�� 0x00�ΰ��
				// ARP�� destAddress�� 18~23 �̴�.
			} else {
				return false;
			}
		}
		return true;
	}

	public boolean checkGratuitous(byte[] input) throws SocketException {

		for (int i = 0; i < 4; i++) {
			if (input[i + 14] == input[i + 24]) {
				// Gratuitous �� ���
				// Sender's IP Address �� Receiver's IP Address�� �������
			} else {
				return false;
			}
		}

		return true;
		// my_srcAddress ����
	}

	byte[] GratOriginMac = new byte[6]; //���� �� ���ּ�
	
	
	// PC�� ���ּ� ��������
	public void getMyPCAddr() throws SocketException {
		Enumeration<NetworkInterface> interfaces = null;
		interfaces = NetworkInterface.getNetworkInterfaces(); // ���� PC�� ��� NIC�� ���������� �޴´�.

		// isUP �߿� MAC�� IP �ּҸ� ���
		while (interfaces.hasMoreElements()) {
			NetworkInterface networkInterface = interfaces.nextElement();
			if (networkInterface.isUp()) {
				if (networkInterface.getHardwareAddress() != null) {// loop back Interface�� null�̹Ƿ�, �ɷ��ش�.
					GratOriginMac = networkInterface.getHardwareAddress(); // MAC�ּ� �ޱ�
					break; // ���� ������� NIC �̿ܿ��� �ʿ� ����. �ݺ��� Ż��
				}
			}
		}
	}


	boolean Send(byte[] data) throws SocketException {
		int length = data.length;
		byte[] dstAddr = new byte[6];
		byte[] srcAddr = new byte[6];

		boolean isBraod = checkBroadCast(data); // ���� ��ε��̸�
		boolean isGrat = checkGratuitous(data);

		byte[] broadcastAddr = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };

		if (isBraod) {
			System.arraycopy(broadcastAddr, 0, dstAddr, 0, 6);
		} else {
			System.arraycopy(data, 18, dstAddr, 0, 6);
			// dstAddress ����
		}

		// Gratuitous ARP�� ���, ���� �� MAC�ּ� ����

		if (isGrat) {
			this.getMyPCAddr();
			System.arraycopy(GratOriginMac, 0, srcAddr, 0, 6);
		} else {
			// �ƴ϶�� Sender's Ethernet Address ����
			System.arraycopy(data, 8, srcAddr, 0, 6);
		}
		
		// my_enetType ����
		// this.ip_src = new _IP_ADDR(); // 12-15
		// this.ip_dst = new _IP_ADDR(); // 16-19
		// System.arraycopy(enetType_ARP, 0, my_enetType, 0, 2);

		byte[] frameType = { 0x08, 0x00 };
		SetFrameType(frameType);
		
		
		m_sHeader.enet_data = new byte[data.length + enetHeadSize];

		
		SetSrcAddress(srcAddr);
		SetDestinationAddress(dstAddr);
		
		byte[] bytes = ObjToByte(m_sHeader, data, length);
		

		if (((NILayer) this.GetUnderLayer()).Send(bytes, length + 14))
			return true;
		else
			return false;
	}

	/*
	 * ARP Layer�� ������ �Լ� ���� Reply�� dst == ARP Request�� ���� MAC �ּ�, src == ���� MAC
	 * �ּ�(Request�� ���� ����-ã���� ��) �� ���� Request�̸� dst == ��ε�ĳ��Ʈ(0xffffffffff), src ==
	 * Request�� ������ MAC �ּ� (Request�� ���� ��-ó��)
	 */
	
	
	public byte[] ObjToByte(_ETHERNET_HEADER Header, byte[] input, int length) {
		byte[] buf = new byte[length + 14];

		for (int i = 0; i < 6; i++) {
			buf[i] = Header.enet_dstAddr.addr[i];
		}
		for (int i = 0; i < 6; i++) {
			buf[i + 6] = Header.enet_srcAddr.addr[i];
		}
		buf[12] = Header.enet_type[0];
		buf[13] = Header.enet_type[1];

		for (int i = 0; i < length; i++)
			buf[14 + i] = input[i];

		return buf;
	}
	

	public boolean IsMyPacket(byte[] input) {
		for (int i = 0; i < 6; i++) {
			if (m_sHeader.enet_srcAddr.addr[i] == input[i + 6])
				continue;
			else
				return false;
		}
		return true;
	}

	public boolean IsItMine(byte[] input) {
		for (int i = 0; i < 6; i++) {
			if (m_sHeader.enet_srcAddr.addr[i] == input[i])
				continue;
			else
				return false;
		}
		return true;
	}

	public boolean IsBroadcast(byte[] input) {
		for (int i = 0; i < 6; i++) {
			if (input[i] == (byte) 0xff)
				continue;
			else
				return false;
		}
		return true;
	}

	public byte[] RemoveEnetHeader(byte[] input, int length) {
		byte[] data = new byte[length - 14];
		for (int i = 0; i < length - 14; i++)
			data[i] = input[14 + i];
		return data;
	}

	public synchronized boolean Receive(byte[] input) {
		byte[] dstAddr = new byte[6];
		byte[] srcAddr = new byte[6];
		byte[] frame = new byte[2];
		
		for (int i = 0; i < 6; i++) {
			dstAddr[i] = input[i];
			srcAddr[i] = input[i + 6];
		}
		frame[0] = input[12];
		frame[1] = input[13];
		// ���� ��Ŷ�� dst �ּҰ� �ڽ��� �ּ��� ��� or dst�� Broadcast�� �ƴ� ��� or src �ּҰ� �ڽ��� �ּ��� ���
		if (!IsMyPacket(input) || IsBroadcast(input) || IsItMine(input)) {
			byte[] data = RemoveEnetHeader(input, input.length);
			// �Ϲ� ������ �� ���
			if (frame[0] == (byte) 0x08 && frame[1] == (byte) 0x00) {
				((IPLayer) this.GetUpperLayer(0)).Receive(data);
			}
			// ARP Request or ARP Reply�� ���
			else if (frame[0] == (byte) 0x08 && frame[1] == (byte) 0x06) {
				((ARPLayer) this.GetUpperLayer(1)).Receive(data);
			}
		}
		return false;
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
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
