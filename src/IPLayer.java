import java.util.ArrayList;


public class IPLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public int nUnderLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUnderLayerIP = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	// IPHEADER -> IP_HEADER_SIZE
	public final static int IP_HEADER_SIZE = 20;

	byte[] chatDST_mac = new byte[6];
	byte[] arpDST_mac = new byte[6];
	byte[] chatDST_ip = new byte[4];
	byte[] arpDST_ip = new byte[4];

	// friendIPLayer -> _OtherIPLayer
	public BaseLayer _OtherIPLayer;
	// routingTable -> _Router_Table
	public RoutingTable _Router_Table;

	// Receive_Thread -> _Receive_Thread
	_Receive_Thread receiveThread = null;
	Thread receiveStartThread = null;


	String portName;
	// srcMacAddress -> macSrcAddr
	byte[] macSrcAddr;



	private class _IPLayer_HEADER {
		// ip_versionLen -> ip_version
		byte[] ip_version;   // ip version -> IPv4 : 4
		byte[] ip_serviceType;   // type of service
		// ip_packetLen -> ip_packetLength
		byte[] ip_packetLength;   // total packet length
		byte[] ip_datagramID;   // datagram id
		byte[] ip_offset;      // fragment offset
		byte[] ip_ttl;         // time to live in gateway hops
		byte[] ip_protocol;      // IP protocol
		byte[] ip_cksum;      // header checksum
		// ip_srcaddr -> ip_src
		byte[] ip_src;      // IP address of source
		// ip_dstaddr -> ip_dst
		byte[] ip_dst;      // IP address of destination
		byte[] ip_data;         // data

		public _IPLayer_HEADER(){
			this.ip_version = new byte[1];
			this.ip_serviceType = new byte[1];
			this.ip_packetLength = new byte[2];
			this.ip_datagramID = new byte[2];
			this.ip_offset = new byte[2];
			this.ip_ttl = new byte[1];
			this.ip_protocol = new byte[1];
			this.ip_cksum = new byte[2];
			this.ip_src = new byte[4];
			this.ip_dst= new byte[4];
			this.ip_data = null;      
		}
	}

	_IPLayer_HEADER m_sHeader = new _IPLayer_HEADER();

	public IPLayer(String pName) {
		// super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
		m_sHeader = new _IPLayer_HEADER(); 
	}

	// friendIPLayer -> _OtherIPLayer
	public void friendIPset( BaseLayer _OtherIPLayer ) {
		this._OtherIPLayer = _OtherIPLayer;
	}
	// friendIPget -> getOtherIP
	public BaseLayer getOtherIP() {
		return this._OtherIPLayer;
	}
	// setRouter -> setRouterTable
	// routingTable -> _Router_Table
	public void setRouterTable(RoutingTable _Router_Table) {
		this._Router_Table = _Router_Table;
	}
	// srcMacAddress -> macSrcAddr
	public void setPort(byte[] macSrcAddr, String portName) {
		this.macSrcAddr = macSrcAddr;
		this.portName = portName;
	}

	// SetIPSrcAddress -> setIPSrc
	// srcAddress -> srcAddr
	public void setIPSrc(byte[] srcAddr) {
		// TODO Auto-generated method stub
		m_sHeader.ip_src[0]= srcAddr[0];
		m_sHeader.ip_src[1]= srcAddr[1];
		m_sHeader.ip_src[2]= srcAddr[2];
		m_sHeader.ip_src[3]= srcAddr[3];

	}

	// SetIPDstAddress -> setIPDst
	// dstAddress -> dstAddr
	public void setIPDst(byte[] dstAddr) {
		// TODO Auto-generated method stub
		m_sHeader.ip_dst[0]= dstAddr[0];
		m_sHeader.ip_dst[1]= dstAddr[1];
		m_sHeader.ip_dst[2]= dstAddr[2];
		m_sHeader.ip_dst[3]= dstAddr[3];

	}

	public byte[] ObjToByte(_IPLayer_HEADER Header, byte[] input, int length) {
		byte[] buf = new byte[length + IP_HEADER_SIZE];

		buf[0] = Header.ip_version[0];
		buf[1] = Header.ip_serviceType[0];
		buf[2] = Header.ip_packetLength[0];
		buf[3] = Header.ip_packetLength[1];
		buf[4] = Header.ip_datagramID[0];
		buf[5] = Header.ip_datagramID[1];
		buf[6] = Header.ip_offset[0];
		buf[7] = Header.ip_offset[1];
		buf[8] = Header.ip_ttl[0];
		buf[9] = Header.ip_protocol[0];
		buf[10] = Header.ip_cksum[0];
		buf[11] = Header.ip_cksum[1];
		for (int i = 0; i < 4; i++) {
			buf[12 + i] = Header.ip_src[i];
			buf[16 + i] = Header.ip_dst[i];
		}
		for (int i = 0; i < length; i++) {
			buf[IP_HEADER_SIZE + i] = input[i];
		}
		return buf;
	}

	// bytes -> data
	public boolean Send(byte[] input, int length) {
		if((input[2]==(byte)0x20 && input[3]==(byte)0x80) || (input[2]==(byte)0x20 && input[3]==(byte)0x90) ) {
			m_sHeader.ip_offset[0] = 0x00;
			m_sHeader.ip_offset[1] = 0x03;

			byte[] data = ObjToByte(m_sHeader,input,length);
			((EthernetLayer)this.GetUnderLayer(1)).Send(data,length+IP_HEADER_SIZE, this);
			return true;

		}
		else {
			byte[] opcode = new byte[2];
			opcode[0] = (byte)0x00;
			opcode[1] = (byte)0x01;
			byte[] data = ObjToByte(m_sHeader,input,length);
			((ARPLayer)this.GetUnderLayer(0)).Send(m_sHeader.ip_src,m_sHeader.ip_dst,new byte[6], new byte[6],opcode);

			return true;
		}
	}

	// RemoveCappHeader -> removeHeader
	public byte[] removeHeader(byte[] input, int length) {
		// remvHeader -> removeH
		byte[] removeH = new byte[length-20];
		for(int i=0;i<length-20;i++) {
			removeH[i] = input[i+20];
		}
		return removeH;
	}

	public synchronized boolean Receive(byte[] input) {
		
//		System.out.println("IP receive "+Thread.currentThread().getName());
//		receiveThread = new Receive_Thread(input, ((IPLayer)this.friendIPLayer), (ARPLayer)this.GetUnderLayer(0));
//		receiveStartThread = new Thread(receiveThread);
//		receiveStartThread.start();
//		return false;
		//      System.out.println("IP receive input length : " + input.length);
		      byte[] data = removeHeader(input, input.length);
		
		      if (IsSrcMe(input)) {
		         return false;
		      }
		      if (IsDstMe(input)) {
		//    	  System.out.println();
		//    	 System.out.println("goto GUI Layer");
		         this.GetUpperLayer(0).Receive(data);
		         return true;
		      } else {
		         byte[] destIP = new byte[4];
		         System.arraycopy(input, 16, destIP, 0, 4);
		         Object[] value = _Router_Table.findEntry(destIP);
		         if (value != null) {
		            /* flag Ȯ�� */
		            byte[] netAdd;
		            if ((boolean) value[4]) {
		               netAdd = (byte[]) value[2];
		            } else {
		               netAdd = (byte[]) destIP;
		            }
		            /* ��Ʈ��ũ �ּ� -> arp */
		            byte[] opcode = new byte[2];
		            opcode[0] = (byte) 0x00;
		            opcode[1] = (byte) 0x01;
		
		//            System.out.println();
		//            System.out.println("portName is "+portName+", input : "+(String)value[6]);
		//            System.out.println();
		//            
		//            
		            
		//            String macAddress = String.format("%X:", srcMacAddress[0]) + String.format("%X:", srcMacAddress[1])
		//			+ String.format("%X:", srcMacAddress[2]) + String.format("%X:", srcMacAddress[3])
		//			+ String.format("%X:", srcMacAddress[4]) + String.format("%X", srcMacAddress[5]);
		//		
		//			
		//			System.out.println("IP Layer src mac : " + macAddress);
		//			
		//			String macAddress1 = String.format("%X:", ((IPLayer)this.friendIPLayer).srcMacAddress[0]) + String.format("%X:", ((IPLayer)this.friendIPLayer).srcMacAddress[1])
		//			+ String.format("%X:", ((IPLayer)this.friendIPLayer).srcMacAddress[2]) + String.format("%X:", ((IPLayer)this.friendIPLayer).srcMacAddress[3])
		//			+ String.format("%X:", ((IPLayer)this.friendIPLayer).srcMacAddress[4]) + String.format("%X", ((IPLayer)this.friendIPLayer).srcMacAddress[5]);
		//		
		//			
		//			System.out.println("Friend IP Layer src mac : " + macAddress1);
		//			System.out.println();
		            if(((String)value[6]).equals(portName)) {
		//            	System.out.println("Send");
		               ((ARPLayer) this.GetUnderLayer(0)).SendforARP(input, (String) value[6], m_sHeader.ip_src,
		                     netAdd, macSrcAddr, new byte[6], opcode);
		            }else {
		//            	System.out.println("Send to Friend");
		            	IPLayer friend = (IPLayer)this._OtherIPLayer;
		            	((ARPLayer) friend.GetUnderLayer(0)).SendforARP(input, (String) value[6], friend.m_sHeader.ip_src,
		                        netAdd, friend.macSrcAddr, new byte[6], opcode);
		            }
		            //////////////later/////////////////////////
		            
		         }
		      }
		      return false;
		
	}
	
	// dstme_Addr -> IsDstMe
	public boolean IsDstMe(byte[] add) {
		for(int i = 0; i < 4; i++) {
			if(add[i + 16] != m_sHeader.ip_src[i]) return false;
		}

		return true;
	}
	
	// srcme_Addr -> IsSrcMe
	public boolean IsSrcMe(byte[] add) {
		for(int i = 0; i < 4; i++) {
			if(add[i + 12] != m_sHeader.ip_src[i]) return false;
		}

		return true;
	}

	// _Receive_Thread
	class _Receive_Thread implements Runnable {
		
		byte[] input;

//		EthernetLayer ethernet;
		ARPLayer _ARP;	//arp -> _ARP
		IPLayer _OtherIP ;	// friend -> _OtherIP
		
		// friend -> _OtherIP
		// arp -> _ARP
		public _Receive_Thread(byte[] input, IPLayer _OtherIP, ARPLayer _ARP) {
			this.input = input;
			this._OtherIP = _OtherIP;
			this._ARP = _ARP;
		}

		@Override
		public void run() {
			//	      System.out.println("IP receive input length : " + input.length);
			byte[] data = removeHeader(input, input.length);

			if (IsSrcMe(input)) {
				return ;
			}
			if (IsDstMe(input)) {
				//		    	  System.out.println();
				//		    	 System.out.println("goto GUI Layer");
//				this.GetUpperLayer(0).Receive(data);
				return ;
			} else {
				// destIP -> ipDst
				byte[] ipDst = new byte[4];
				System.arraycopy(input, 16, ipDst, 0, 4);
				Object[] value = _Router_Table.findEntry(ipDst);
				if (value != null) {
					/* flag */
					// netAdd -> dstAddr
					byte[] dstAddr;
					if ((boolean) value[4]) {
						dstAddr = (byte[]) value[2];
					} else {
						dstAddr = (byte[]) ipDst;
					}
					/* ��Ʈ��ũ �ּ� -> arp */
					byte[] opcode = new byte[2];
					opcode[0] = (byte) 0x00;
					opcode[1] = (byte) 0x01;

					//		            System.out.println();
					//		            System.out.println("portName is "+portName+", input : "+(String)value[6]);
					//		            System.out.println();
					//		            
					//		            

					//		            String macAddress = String.format("%X:", srcMacAddress[0]) + String.format("%X:", srcMacAddress[1])
					//					+ String.format("%X:", srcMacAddress[2]) + String.format("%X:", srcMacAddress[3])
					//					+ String.format("%X:", srcMacAddress[4]) + String.format("%X", srcMacAddress[5]);
					//				
					//					
					//					System.out.println("IP Layer src mac : " + macAddress);
					//					
					//					String macAddress1 = String.format("%X:", ((IPLayer)this.friendIPLayer).srcMacAddress[0]) + String.format("%X:", ((IPLayer)this.friendIPLayer).srcMacAddress[1])
					//					+ String.format("%X:", ((IPLayer)this.friendIPLayer).srcMacAddress[2]) + String.format("%X:", ((IPLayer)this.friendIPLayer).srcMacAddress[3])
					//					+ String.format("%X:", ((IPLayer)this.friendIPLayer).srcMacAddress[4]) + String.format("%X", ((IPLayer)this.friendIPLayer).srcMacAddress[5]);
					//				
					//					
					//					System.out.println("Friend IP Layer src mac : " + macAddress1);
					//					System.out.println();
					if(((String)value[6]).equals(portName)) {
						//		            	System.out.println("Send");
						((ARPLayer) _ARP).SendforARP(input, (String) value[6], m_sHeader.ip_src,
								dstAddr, macSrcAddr, new byte[6], opcode);
					}else {
						//		            	System.out.println("Send to Friend");
//						IPLayer friend = (IPLayer)friendIPLayer;
						((ARPLayer) _OtherIP.GetUnderLayer(0)).SendforARP(input, (String) value[6], _OtherIP.m_sHeader.ip_src,
								dstAddr, _OtherIP.macSrcAddr, new byte[6], opcode);
					}
					//////////////later/////////////////////////

				}
			}
			return ;
		}
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
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
		this.p_aUnderLayerIP.add(nUnderLayerCount++, pUnderLayer);
	}

	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
		// nUpperLayerCount++;

	}
	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		//      System.out.println("ip "+pUULayer.GetLayerName());
		this.SetUpperLayer(pUULayer);
		this.SetUnderLayer(pUULayer);
	}
	@Override
	public BaseLayer GetUnderLayer(int nindex) {
		if (nindex < 0 || nindex > nUnderLayerCount || nUnderLayerCount < 0)
			return null;
		return p_aUnderLayerIP.get(nindex);
	}
}
