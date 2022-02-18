package arp;

import java.util.ArrayList;


public class TCPLayer implements BaseLayer {
	
	public int nUpperLayerCount = 0;
	public BaseLayer p_UnderLayer = null;
	   public int nUnderLayerCount = 0;
	   public String pLayerName = null;
	   public ArrayList<BaseLayer> p_aUnderLayer = new ArrayList<BaseLayer>();
	   public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	   private class _TCPLayer_HEADER {
	      byte[] tcp_srcport;
	      byte[] tcp_dstport;
	      byte[] tcp_seq;
	      byte[] tcp_ack;
	      byte[] tcp_offset;
	      byte[] tcp_flag;

	      byte[] tcp_window;
	      byte[] tcp_cksum;
	      byte[] tcp_urgptr;
	      byte[] padding;
	      byte[] tcp_data;

	      public _TCPLayer_HEADER() {
	         this.tcp_srcport = new byte[2];
	         this.tcp_dstport = new byte[2];
	         this.tcp_seq = new byte[4];
	         this.tcp_ack = new byte[4];
	         this.tcp_offset = new byte[1];
			 this.tcp_flag = new byte[1];
	         this.tcp_window = new byte[2];
	         this.tcp_cksum = new byte[2];
	         this.tcp_urgptr = new byte[2];
	         this.padding = new byte[4];
	         this.tcp_data = null;
	      }
	   }

	   _TCPLayer_HEADER TCPHeader = new _TCPLayer_HEADER();

	public TCPLayer(String pName) {
		pLayerName = pName;
		ResetHeader();
		// TODO Auto-generated constructor stub
	}
	

	public void ResetHeader() {
		for (int i = 0; i < 4; i++) {
			TCPHeader.tcp_seq[i] = (byte) 0x00;
			TCPHeader.tcp_ack[i] = (byte) 0x00;
			TCPHeader.padding[i] = (byte) 0x00;
		}
		for (int i = 0; i < 2; i++) {
			TCPHeader.tcp_srcport[i] = (byte) 0x00;
			TCPHeader.tcp_dstport[i] = (byte) 0x00;
			TCPHeader.tcp_window[i] = (byte) 0x00;
			TCPHeader.tcp_cksum[i] = (byte) 0x00;
			TCPHeader.tcp_urgptr[i] = (byte) 0x00;
		}
		TCPHeader.tcp_data = null;
	}
	
	public byte[] ObjToByte(_TCPLayer_HEADER Header, byte[] input, int length) {
		byte[] buf = new byte[length + 24];

		System.arraycopy(Header.tcp_srcport, 0, buf, 0, 2); // buf�� 2byte sport �ʱ�ȭ
		System.arraycopy(Header.tcp_dstport, 0, buf, 2, 2); // 2byte dport
		System.arraycopy(Header.tcp_seq, 0, buf, 4, 4); // 4byte sqe
		System.arraycopy(Header.tcp_ack, 0, buf, 8, 4); // 4byte ack
		System.arraycopy(Header.tcp_offset, 0, buf, 12, 1);
		System.arraycopy(Header.tcp_flag, 0, buf, 13, 1);
		System.arraycopy(Header.tcp_window, 0, buf, 14, 2); // 2byte window
		System.arraycopy(Header.tcp_cksum, 0, buf, 16, 2); // 2byte cksum
		System.arraycopy(Header.tcp_urgptr, 0, buf, 18, 2); // 2byte urgptr
		System.arraycopy(Header.padding, 0, buf, 20, 4); // 4byte padding

		for (int i = 0; i < length; i++)
			buf[24 + i] = input[i];

		return buf;
	}
	
	public boolean Send(byte[] input, int length) {
		byte[] TCP_header_added_bytes = ObjToByte(TCPHeader, input, length);

		this.GetUnderLayer().Send(TCP_header_added_bytes, TCP_header_added_bytes.length);
		return false;
	}
	
	public boolean GratSend(byte[] input, int length) {
		byte[] TCP_header_added_bytes = ObjToByte(TCPHeader, input, length);
		for(byte b : TCP_header_added_bytes) {
			System.out.print(b);
		}
		this.GetUnderLayer().GratSend(TCP_header_added_bytes, TCP_header_added_bytes.length);
		return false;
	}

//	public void arpSend(byte[] input, int length) {
//		// TODO : arp�� ����?
//		((IPLayer) this.GetUnderLayer(0)).arpSend(input, length);
////		Test(sumi)
////		for(int i=0;i<IP.length;i++) {
////			System.out.println(IP[i]);
////		}
//	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
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
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		// TODO Auto-generated method stub
		this.SetUpperLayer(pUULayer);
	      pUULayer.SetUnderLayer(this);
	}

}
