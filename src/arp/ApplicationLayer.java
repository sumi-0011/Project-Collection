package arp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import arp.TCPLayer;
import arp.ARPLayer._Cache_Entry;
import arp.ARPLayer._Proxy_Entry;

import arp.ARPLayer;

import javax.swing.border.*;

public class ApplicationLayer extends JFrame implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUnderLayer = new ArrayList<BaseLayer>();;
	static JList<String> ArpArea; // 좌측 ARP 텍스트 출력란
	static JList<String> ProxyArpArea; // 우측 proxy ARP 텍스트 출력란
	BaseLayer UnderLayer;

	private static LayerManager m_LayerMgr = new LayerManager();

	private JPanel contentPane;
	private JTextField IPAddressWrite;
	private JTextField HW_AddressWrite;
	JTextField ItemText;

	JTextArea srcAddress;
	JTextArea dstAddress;
	JTextArea GratuitousARPArea;

	JLabel lblsrc;
	JLabel lbldst;
	JLabel IPLabel; 	// ip 주소 Label
	JLabel HW_Label; 	// mac 주소 
	JLabel Item_num;
	JLabel Device;
	JLabel MAC_Addr;
	JLabel IP_Addr;
	JTextField DeviceText;
	JTextField MAC_AddrText;
	JTextField IP_AddrText;
	
	JButton Setting_Button;
	JButton Item_Delete_Button; // ARP Cache : Item_Delete_Button
	JButton All_Delete_Button; // ARP Cache : All_Delete_Button
	JButton IP_Addr_Send_Button; // IP 주소 : IP_Addr_Send_Button
	JButton ProxyARP_Delete_Button; // ProxyARP : ProxyARP_Delete_Button
	JButton ProxyARP_Add_Button; // ProxyARP : ProxyARP_Add_Button
	JButton HW_Addr_Send_Button; // IP 주소 : IP_Addr_Send_Button
	
	JButton Cancle_Button;
	JButton End_Button;

	static _Proxy_Entry proxyEntry;
	static Map<String, _Cache_Entry> cache_Table;
	static Set<String> cache_Itr;
	static Map<String, _Proxy_Entry> proxy_Table;
	static ArrayList<byte[]> byteArray = new ArrayList<byte[]>();
	static DefaultListModel<String> ARPModel; // JList의 Item들을 관리하는 모델
	static DefaultListModel<String> ProxyModel;
	
	public static void main(String[] args) throws SocketException {

		m_LayerMgr.AddLayer(new NILayer("NI"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
		m_LayerMgr.AddLayer(new ARPLayer("ARP"));
		m_LayerMgr.AddLayer(new IPLayer("IP"));
		m_LayerMgr.AddLayer(new TCPLayer("TCP"));

		m_LayerMgr.AddLayer(new ApplicationLayer("Application"));

		m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *ARP +IP ( -ARP *TCP ( *Application ) ) ) )");
		((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(0);//PC마다 다르다. 0번이 아닐 수도 있기 때문에, 탐색하는 함수를 만들면 편할듯
		
//		// ARPLayer's applayer setting
//		((ARPLayer) m_LayerMgr.GetLayer("ARP")).setAppLayer(((ApplicationLayer) m_LayerMgr.GetLayer("Application")));
//			// 2초 마다 printCash()를 호출하여 캐시 테이블과 GUI를 갱신하는 쓰레드
		Runnable task = () -> {
			while (true) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				printCache(); // GUI에 cache_Table을 print
			}
		};
		// 위 기능을 수행하는 쓰레드 생성 및 시작.
		Thread cacheUpdate = new Thread(task,"cacheUpdataThread");
		cacheUpdate.start();

	}

	
	public static boolean isThisQuestion(byte[] input) {
		for(byte a : input ) {	//byte[]를 순회하는데
			if(a != 0) {		// 0 이 아닌 게 있다면,
				return false;	//  ???가 아니다.
			}
		}
		return true;
	}
	public static String ethAddrToQuestionOrEth(byte[] input) {
		if(isThisQuestion(input)) { // byte[]가 0000..이다 -> Question mark로 출력
			return "???????????";
		}
		else {						// byte[]가 정상적인 ethAddr 형식이다 -> String으로 바꿔서 출력.
			return byteArrayToString(input);
		}
	}
	
	public static String byteArrayToString(byte[] b) {	// byte[0] = 1, byte[1] = 2 일 때... 01:02 String으로 변경
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {	
        	sb.append(String.format("%02X", b[i] ));	// sb에 바이트를 str으로 바꿔서 저장한다. 이 때  1은 01, 2는 02 등 두 글자로 formatting
            sb.append(":");								
        }
        sb.deleteCharAt(sb.length()-1);	// 마지막에 추가된 :를 지운다
        return sb.toString();
    }
	
	public static void getTable() {
		cache_Table = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getCacheList();
		cache_Itr = cache_Table.keySet();
		proxy_Table = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getProxyList();
	}

	
	public static void printCache() {
		getTable();
		
		//proxy_Table = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getProxyList();
		
		ARPModel.removeAllElements(); // ARPModel의 값을 모두 지우고,
		for (String key : cache_Itr) { // 캐시테이블의 모든 값을 ARPModel에 저장하기 위해서 캐시테이블을 순회.
			ARPModel.addElement(String.format("%20s%25s%15s", // 캐시테이블의 값 중 dstIPAddr(key), dstMACaddr, status를 아래 형식으로 ARPModel에 저장
					key, 														// key는 String이기 때문에 그대로 저장.
					ethAddrToQuestionOrEth(cache_Table.get(key).cache_ethaddr), // ethAddr은 byte[]이기 때문에 ??? 혹은 xx:xx 형태의 String으로 변경해서 저장
					cache_Table.get(key).cache_status));						// status는 String이기 때문에 그대로 저장
		}
		
	}
	
	public ApplicationLayer(String pName) {
		pLayerName = pName;

		
		
		setTitle("ARP");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(250, 250, 800, 430);
//		contentPan : 전체 GUI 틀 Panel
		contentPane = new JPanel();
		((JComponent) contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// ARP Cache panel 생성
		JPanel ARPCachePanel = new JPanel();
		ARPCachePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "ARP Cache",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		ARPCachePanel.setBounds(10, 5, 360, 360);
		contentPane.add(ARPCachePanel);
		ARPCachePanel.setLayout(null);
		
		
		// ARPCacheEditorPanel : ARPCacheArea의 바탕이 되는 panel
		JPanel ARPCacheEditorPanel = new JPanel();// ARP Cache Panel write panel
		ARPCacheEditorPanel.setBounds(10, 15, 340, 210);
		ARPCachePanel.add(ARPCacheEditorPanel);
		ARPCacheEditorPanel.setLayout(null);
		ARPCacheEditorPanel.setBackground(Color.red);

		
		ARPModel = new DefaultListModel<String>();
		ArpArea = new JList<String>(ARPModel);
		ArpArea.setBounds(0, 0, 340, 210);
		ARPCacheEditorPanel.add(ArpArea);


		// ARPCache 부분의 IP주소 입력 부분 Panel
		JPanel ARPCacheInputPanel = new JPanel();
		ARPCacheInputPanel.setBounds(10, 280, 250, 30); // (x1,y1,x2,y2)
		ARPCachePanel.add(ARPCacheInputPanel);
		ARPCacheInputPanel.setLayout(null);

//		IPLabel : ip주소Label
		IPLabel = new JLabel("IP 주소");
		IPLabel.setBounds(8, 0, 60, 30);
		ARPCacheInputPanel.add(IPLabel);
//		IPAddressWrite : ip주소 입력 부분
		IPAddressWrite = new JTextField();
		IPAddressWrite.setBounds(55, 1, 195, 30);// 249
		ARPCacheInputPanel.add(IPAddressWrite);
		IPAddressWrite.setColumns(10); // writing area
		IPAddressWrite.setText("168.188.129.3");	//디버깅
		
		// ProxyARPEntry
		JPanel ProxyARPEntryPanel = new JPanel();
		ProxyARPEntryPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Proxy ARP Entry",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		ProxyARPEntryPanel.setBounds(380, 5, 360, 290);
		contentPane.add(ProxyARPEntryPanel);
		ProxyARPEntryPanel.setLayout(null);

		// ProxyARPEntryPanel의 write panel
		JPanel ProxyARPEntryEditorPanel = new JPanel();
		ProxyARPEntryEditorPanel.setBounds(10, 15, 340, 270);
		ProxyARPEntryPanel.add(ProxyARPEntryEditorPanel);
		ProxyARPEntryEditorPanel.setLayout(null);

		ProxyModel = new DefaultListModel<String>();
		ProxyArpArea = new JList<String>(ProxyModel);
		ProxyArpArea.setBounds(0, 0, 340, 120);
		ProxyARPEntryEditorPanel.add(ProxyArpArea);

//		GUI 수정
		Device = new JLabel("Device");
		Device.setBounds(30, 130, 50, 25);
		ProxyARPEntryEditorPanel.add(Device);

		DeviceText = new JTextField();
		DeviceText.setBounds(110, 130, 210, 25);// 249
		ProxyARPEntryEditorPanel.add(DeviceText);
		DeviceText.setColumns(10);
		DeviceText.setText("Host1");

		MAC_Addr = new JLabel("MAC_Addr");
		MAC_Addr.setBounds(30, 165, 70, 25);
		ProxyARPEntryEditorPanel.add(MAC_Addr);
		
		
		MAC_AddrText = new JTextField();
		MAC_AddrText.setBounds(110, 165, 210, 25);// 249
		ProxyARPEntryEditorPanel.add(MAC_AddrText);
		MAC_AddrText.setColumns(10);
		MAC_AddrText.setText("08:00:20:81:28:BF");
		
		IP_Addr = new JLabel("IP_Addr");
		IP_Addr.setBounds(30, 200, 50, 25);
		ProxyARPEntryEditorPanel.add(IP_Addr);

		IP_AddrText = new JTextField();
		IP_AddrText.setBounds(110, 200, 210, 25);// 249
		ProxyARPEntryEditorPanel.add(IP_AddrText);
		IP_AddrText.setColumns(10);
		IP_AddrText.setText("168.188.129.130");
		
		
		// GratuitousARP
		JPanel GratuitousARP_Panel = new JPanel();
		GratuitousARP_Panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Gratuitous ARP Entry",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GratuitousARP_Panel.setBounds(380, 300, 360, 65);
		contentPane.add(GratuitousARP_Panel);
		GratuitousARP_Panel.setLayout(null);

		// GratuitousARP의 H/W주소 input Panel
		JPanel GratuitousARPInputPanel = new JPanel();// ARP Cache Panel write panel
		GratuitousARPInputPanel.setBounds(5, 20, 350, 40);
		GratuitousARP_Panel.add(GratuitousARPInputPanel);
		GratuitousARPInputPanel.setLayout(null);
		
		HW_Label = new JLabel("H/W 주소");
		HW_Label.setBounds(8, 5, 60, 30);
		GratuitousARPInputPanel.add(HW_Label);

		HW_AddressWrite = new JTextField();
		HW_AddressWrite.setBounds(70, 5, 200, 30);// 249
		GratuitousARPInputPanel.add(HW_AddressWrite);
		HW_AddressWrite.setColumns(10);// writing area
		HW_AddressWrite.setText("06:05:04:03:02:01");
		
		// java.awt.event.ActionListener
		// addActionListener을 새로운 ActionListener를 생성하여서 작동하게 하는데 ActionListener는 안에
		// actionPerformed라는 메소드가 무조건 필요하다. actionPerformed에 원하는 작동을 구현하면 된다.
		// delete item index select textField
		Item_num = new JLabel("Item No");
		Item_num.setBounds(10, 230, 50, 30);
		ARPCachePanel.add(Item_num);

		ItemText = new JTextField();
		ItemText.setBounds(60, 230, 70, 30);// 249
		ARPCachePanel.add(ItemText);
		ItemText.setColumns(10);
		// ARPCache : itemDelete Button
		Item_Delete_Button = new JButton("Item Delete");
		Item_Delete_Button.setBounds(135, 230, 100, 30);
		Item_Delete_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == Item_Delete_Button) {
					if (ArpArea.getSelectedValue() != null) {
						// 선택한 Item의 문자열을 받아와 앞 뒤 공백을 제거한 후(trim) 가운데 공백을 구분자 삼아 토큰화 한다.
						StringTokenizer st = new StringTokenizer(ArpArea.getSelectedValue().toString().trim(), " ");
						// cache_Table 에서 제거한 뒤 GUI에서도 제거
						cache_Table.remove(st.nextToken());
						ARPModel.remove(ArpArea.getSelectedIndex());
					}
				}

			}

		});
		ARPCachePanel.add(Item_Delete_Button);// Item_Delete_Button button
		// ARPCache : All_Delete_Button
		All_Delete_Button = new JButton("All Delete");
		All_Delete_Button.setBounds(240, 230, 100, 30);
		All_Delete_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == All_Delete_Button) {

					cache_Table.clear();
					ARPModel.removeAllElements();
				}

			}

		});
		ARPCachePanel.add(All_Delete_Button);// All_Delete_Button button
		// ARPCache : IP주소 Input
		IP_Addr_Send_Button = new JButton("Send");
		IP_Addr_Send_Button.setBounds(270, 280, 70, 30);
		IP_Addr_Send_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == IP_Addr_Send_Button) {
					// TODO : IP_Addr_Send_Button 구현
					/*
					 * 1. 입력으로 IP주소를 받아오고, 빈칸으로 초기화 2. 받아온 IP주소를 byte[]로 변환 3. tcp레이어에 전송(tcp에서 arp로
					 * 전달)
					 */
					String send_IP = IPAddressWrite.getText();
					IPAddressWrite.setText("");

					byte[] IP = strToByte(send_IP);

//                	TODO:TCP 연결
					TCPLayer tcplayer = (TCPLayer) m_LayerMgr.GetLayer("TCP");
					((ARPLayer) m_LayerMgr.GetLayer("ARP")).setDstIPAddr(strToByte(send_IP)); // ARpLayer에 dstAddr Set
					((TCPLayer) m_LayerMgr.GetLayer("TCP")).Send("".getBytes(), 0);

					// abort 수정 사용한 이유가 궁금합니다! -> abort가 뭘까요?
					

				}

			}

		});
		ARPCachePanel.add(IP_Addr_Send_Button);// All_Delete_Button button

		// ProxyARPEntry Add button
		ProxyARP_Add_Button = new JButton("Add");// setting
		ProxyARP_Add_Button.setBounds(30, 230, 130, 30);
		ProxyARP_Add_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == ProxyARP_Add_Button) {
					String device = DeviceText.getText();
					String ip_addr = IP_AddrText.getText();
					String mac_addr = MAC_AddrText.getText();
					
					((ARPLayer) m_LayerMgr.GetLayer("ARP")).setProxyTable(	//proxy_Table에 저장
							ip_addr,							//argu1 : proxy_Table의 key로 지정할, IP 주소칸에 입력한 텍스트
							strToByteArray2(mac_addr), 			//argu2 : proxy_Table의 value로 지정할, Ethernet 주소칸에 입력한 텍스트
							device);	//argu3 : proxy_Table의 value로 지정할, Combobox 에 선택된 값.
					
					ProxyModel.addElement(String.format("%18s%20s%23s", 	// ProxyModel(GUI)에 출력
							device, 	
							ip_addr, 
							mac_addr));
					
				
				}

			}

		});
		ProxyARPEntryEditorPanel.add(ProxyARP_Add_Button);

		// ProxyARPEntry Delete button
		ProxyARP_Delete_Button = new JButton("Delete");
		ProxyARP_Delete_Button.setBounds(190, 230, 130, 30);
		ProxyARP_Delete_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == ProxyARP_Delete_Button) {
					// proxy 테이블 정보 모두삭제
					if (ProxyArpArea.getSelectedValue() != null) {
						StringTokenizer st2 = new StringTokenizer(ProxyArpArea.getSelectedValue().toString().trim(), " ");

						// proxy_Table 에서 제거한 뒤 GUI에서도 제거
						while(st2.hasMoreTokens()) {
							String key = st2.nextToken();
							if(proxy_Table.containsKey(key)) {
								proxy_Table.remove(key);					// proxy_Table에서 제거
								break;
							}
						}
						ProxyModel.remove(ProxyArpArea.getSelectedIndex());	//GUI에서 제거
					}
					
				}
				
				
			}

		});
		ProxyARPEntryEditorPanel.add(ProxyARP_Delete_Button);

		// GratuitousARP : h/w주소 input
		HW_Addr_Send_Button = new JButton("Send");
		HW_Addr_Send_Button.setBounds(275, 5, 65, 30);
		HW_Addr_Send_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == HW_Addr_Send_Button) {
					((ARPLayer) m_LayerMgr.GetLayer("ARP")).setSrcMAC(strToByteArray2(MAC_AddrText.getText()));
					((TCPLayer) m_LayerMgr.GetLayer("TCP")).GratSend("".getBytes(), 0); // Send 시작
					
				}

			}

		});
		
		
		GratuitousARPInputPanel.add(HW_Addr_Send_Button);//

//		End_Button = new JButton("종료");
//		End_Button.setBounds(285, 370, 80, 30);
//		End_Button.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if (e.getSource() == End_Button) {
//					// 종료 버튼 클릭시
//				}
//
//			}
//
//		});
//		contentPane.add(End_Button);//
//
//		Cancle_Button = new JButton("취소");
//		Cancle_Button.setBounds(380, 370, 80, 30);
//		Cancle_Button.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if (e.getSource() == Cancle_Button) {
//					// 취소 버튼 클릭시
//				}
//
//			}
//
//		});
//		contentPane.add(Cancle_Button);//

		setVisible(true);



	}

//   str -> byte배열로, string형식의 ip주소를 byte배열로 변환해 리턴한다. 
	public byte[] strToByte(String addr) {
		String Addr = addr.replaceAll("-", "\\.");
		String[] str = Addr.split("\\.");

		int num = str.length;
		byte[] a = new byte[num];
		if (num == 4) {
			for (int i = 0; i < num; i++) {
				a[i] = (byte) Integer.parseInt(str[i]);
			}
		} else {
			for (int i = 0; i < num; i++) {
				a[i] = (byte) Integer.parseInt(str[i], 16);
			}
		}

		return a;
	}
	
	// MAC
	public byte[] strToByteArray2(String str) {
		byte[] byteMACAddr = new byte[6];
		String[] byte_dst = str.split(":");
		
		for (int i = 0; i < 6; i++) {
			byteMACAddr[i] = (byte) Integer.parseInt(byte_dst[i], 16);
		}

		return byteMACAddr;
	}
	// byte 형식의 addr를 IP Address(ex. 1.1.1.1) 형식으로 변환
	public static String byteToIP(byte[] addr) {
		String ip = "";
		for(byte b : addr) {
			ip += Integer.toString(b & 0xff) + ".";
		}
		// 마지막 .를 빼기 위해서 substring 메소드 사용
		return ip.substring(0, ip.length() - 1);
	}
	// byte 형식의 addr를 MAC Address(ex. ff:ff:ff:ff:ff:ff) 형식으로 변환
	public static String byteToMAC(byte[] addr) {
		StringBuilder mac = new StringBuilder();
		for(byte b : addr) {
			mac.append(String.format("%02X", b));
			mac.append(":");
		}
		// 마지막 :를 빼기 위해서 deleteCharAt 메소드 사용
		mac.deleteCharAt(mac.length() - 1);
		return mac.toString();
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}


	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}

	// underLayer은 한개고 upperLayer은 여러개? 나중에 레이어들 연결할때 다시 봐야겠다.
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
		// pUpperLayer 인자가 null이 아니면 현재 객체의 p_aUpperLayer배열에 추가
		// count를 1증가 시키면서 pUpperLayer을 증가시킨다.
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		// TODO Auto-generated method stub
//		현재 레이어의 upperLayer로 pUULayer을 설정
//		pUULayer의 underLayer로 현재 레이어를 설정
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);
	}

	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		return null;
	}

}