
import java.util.ArrayList;

public class EthernetLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	public final static int HEARER_SIZE = 14;
	private static byte[] arp_macDst = null;	// arp_mac_dstaddr -> arp_macDst

	public byte[] chat_file_dstaddr;
	public byte[] ex_ethernetaddr = new byte[6];

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
		_ETHERNET_ADDR enetSrc;	// enet_srcaddr -> enetSrc
		_ETHERNET_ADDR enetDst;	// enet_dstaddr -> enetDst
		byte[] enetType;	// enet_type -> enetType
		byte[] enetData;	// enet_data -> enetData

		public _ETHERNET_HEADER() {
			this.enetSrc = new _ETHERNET_ADDR();
			this.enetDst = new _ETHERNET_ADDR();
			this.enetType = new byte[2];
			this.enetData = null;
		}
	}

	_ETHERNET_HEADER m_sHeader = new _ETHERNET_HEADER();

	public EthernetLayer(String pName) {
		// super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;

	}

	public void setEnetSrc(byte[] srcAddr) {
		// TODO Auto-generated method stub
		m_sHeader.enetSrc.addr[0] = srcAddr[0];
		m_sHeader.enetSrc.addr[1] = srcAddr[1];
		m_sHeader.enetSrc.addr[2] = srcAddr[2];
		m_sHeader.enetSrc.addr[3] = srcAddr[3];
		m_sHeader.enetSrc.addr[4] = srcAddr[4];
		m_sHeader.enetSrc.addr[5] = srcAddr[5];

	}

	public byte[] getEnetSrc() {
		// TODO Auto-generated method stub

		byte[] srcAddr = new byte[6];
		System.arraycopy(m_sHeader.enetSrc.addr, 0, srcAddr, 0, 6);
		return srcAddr;
	}


	public void setEnetDst(byte[] dstAddr) {
		// TODO Auto-generated method stub
		m_sHeader.enetDst.addr[0] = dstAddr[0];
		m_sHeader.enetDst.addr[1] = dstAddr[1];
		m_sHeader.enetDst.addr[2] = dstAddr[2];
		m_sHeader.enetDst.addr[3] = dstAddr[3];
		m_sHeader.enetDst.addr[4] = dstAddr[4];
		m_sHeader.enetDst.addr[5] = dstAddr[5];

	}


	public static void setMacIsDst(byte[] dstAddr) {
		arp_macDst = dstAddr;
	}


	public byte[] ObjToByte(_ETHERNET_HEADER Header, byte[] input, int length) {
		byte[] buf = new byte[length + HEARER_SIZE];

		buf[0] = Header.enetDst.addr[0];
		buf[1] = Header.enetDst.addr[1];
		buf[2] = Header.enetDst.addr[2];
		buf[3] = Header.enetDst.addr[3];
		buf[4] = Header.enetDst.addr[4];
		buf[5] = Header.enetDst.addr[5];
		buf[6] = Header.enetSrc.addr[0];
		buf[7] = Header.enetSrc.addr[1];
		buf[8] = Header.enetSrc.addr[2];
		buf[9] = Header.enetSrc.addr[3];
		buf[10] = Header.enetSrc.addr[4];
		buf[11] = Header.enetSrc.addr[5];
		buf[12] = Header.enetType[0];
		buf[13] = Header.enetType[1];

		for (int i = 0; i < length; i++) {
			buf[HEARER_SIZE + i] = input[i];
		}
		return buf;
	}

	public boolean Send(byte[] input, int length, byte[] dstAddr) {

		m_sHeader.enetData = input;
		if (m_sHeader.enetData.length > 1500)
			return false;
		// for IP
		if (dstAddr!=null) {
			System.out.println("IP 에서 들어옴. : EthernetLayer ");
			m_sHeader.enetData = input;
			m_sHeader.enetType[0] = (byte) 0x08;
			m_sHeader.enetType[1] = (byte) 0x00;

			m_sHeader.enetDst.addr = dstAddr;

			byte[] frame = ObjToByte(m_sHeader, input, length);

			((NILayer)GetUnderLayer()).Send(frame, length + HEARER_SIZE);

		} 
		// for ARP
		else {

			System.out.println();
			m_sHeader.enetData = input;
			m_sHeader.enetType[0] = (byte) 0x08;
			m_sHeader.enetType[1] = (byte) 0x06;

			if (input[6] == 0x00 && input[7] == 0x01) {
				System.out.println("ARP Broadcast");

				m_sHeader.enetDst.addr[0] = (byte) 0xff;
				m_sHeader.enetDst.addr[1] = (byte) 0xff;
				m_sHeader.enetDst.addr[2] = (byte) 0xff;
				m_sHeader.enetDst.addr[3] = (byte) 0xff;
				m_sHeader.enetDst.addr[4] = (byte) 0xff;
				m_sHeader.enetDst.addr[5] = (byte) 0xff;


			}
			if (input[6] == 0x00 && input[7] == 0x02) {
				
				System.out.println("ARP Reply");
				m_sHeader.enetDst.addr[0] = input[18];
				m_sHeader.enetDst.addr[1] = input[19];
				m_sHeader.enetDst.addr[2] = input[20];
				m_sHeader.enetDst.addr[3] = input[21];
				m_sHeader.enetDst.addr[4] = input[22];
				m_sHeader.enetDst.addr[5] = input[23];

				String macAddress1 = String.format("%X:", input[18]) + String.format("%X:", input[19])
						+ String.format("%X:", input[20]) + String.format("%X:", input[21])
						+ String.format("%X:", input[22]) + String.format("%X", input[23]);
				System.out.println("ARP Reply to : "+macAddress1);
				
			}

			byte[] frame = ObjToByte(m_sHeader, input, length);
			((NILayer)GetUnderLayer()).Send(frame, length + HEARER_SIZE);
			
		}
		return true;
	}

	public byte[] removeHeader(byte[] input, int length) {

		byte[] rebuf = new byte[length - HEARER_SIZE];
		m_sHeader.enetData = new byte[length - HEARER_SIZE];

		for (int i = 0; i < length - HEARER_SIZE; i++) {
			m_sHeader.enetData[i] = input[HEARER_SIZE + i];
			rebuf[i] = input[HEARER_SIZE + i];
		}
		return rebuf;
	}

	public boolean Receive(byte[] input) {
		byte[] data;
		data = removeHeader(input, input.length);

		if (!IsSrcMe(input)) {
			if (IsBroad(input) || IsDstMe(input)) {
				
				System.out.println("packet");
				if (input[12] == 0x08 && input[13] == 0x00) {
					// Sending the IP Layer
					((IPLayer)this.GetUpperLayer(1)).Receive(data);
				}
				else {
					String srcIpAddressToString = (data[14] & 0xFF) + "." + (data[15] & 0xFF) + "."
			              + (data[16] & 0xFF) + "." + (data[17] & 0xFF);
					System.out.println("ARP 도착 : "+srcIpAddressToString);
					((ARPLayer)this.GetUpperLayer(0)).Receive(data, getEnetSrc());
				}
				return true;
			}
		}
		return true;
	}

	public boolean IsDstMe(byte[] add) {
		for (int i = 0; i < 6; i++) {
			if (add[i] != m_sHeader.enetSrc.addr[i])
				return false;
		}
		return true;
	}

	public boolean IsSrcMe(byte[] add) {
		for (int i = 0; i < 6; i++) {
			if (add[i + 6] != m_sHeader.enetSrc.addr[i])
				return false;
		}
		return true;
	}

	public boolean AreDstYou(byte[] add) {
		for (int i = 0; i < 6; i++) {
			if (add[i + 6] != ((IPLayer)GetUpperLayer(1)).chatDST_mac[i]) {
				return false;
			}
		}
		return true;
	}

	public boolean IsBroad(byte[] add) {
		for (int i = 0; i < 6; i++) {
			if (add[i] != (byte) 0xff) {
				return false;
			}	
		}
		return true;
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		if (pUnderLayer == null)
			return;
		p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
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
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);
	}

	@Override
	public BaseLayer GetUnderLayer(int nindex) {
		return null;
	}
}
