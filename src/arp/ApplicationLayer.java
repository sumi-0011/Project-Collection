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

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import arp.ARPLayer._Cache_Table;
import arp.ARPLayer._Proxy_Table;
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

//	Container contentPane;
	private JPanel contentPane;
	private JTextField IPAddressWrite;
	private JTextField HW_AddressWrite;
	JTextField ItemText;

//	JTextArea ARPCacheArea;
//	JTextArea ProxyARPEntryArea;
	JTextArea srcAddress;
	JTextArea dstAddress;
	JTextArea GratuitousARPArea;

	JLabel lblsrc;
	JLabel lbldst;
	JLabel IPLabel; // ip 주소 Label
	JLabel HW_Label; // 
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
	
	static ArrayList<_Cache_Table> cacheTable; // ARP Basic Cache Table
	static ArrayList<_Proxy_Table> proxyTable; // Proxy Table
	// ArrayList를 사용하여 delete를 편하게 함.
	//static _Proxy_Entry proxyEntry;
	//static Map<String, _Cache_Entry> cache_Table;
	//static Set<String> cache_Itr;
	//static Map<String, _Proxy_Entry> proxy_Table;
	static ArrayList<byte[]> byteArray = new ArrayList<byte[]>();
	// 출력하는 ArrayList
	ArrayList<String> listCacheTable = new ArrayList<String>();
	ArrayList<String> listProxyTable = new ArrayList<String>();

	public static void main(String[] args) throws SocketException {

		m_LayerMgr.AddLayer(new NILayer("NI"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
		m_LayerMgr.AddLayer(new ARPLayer("ARP"));
		m_LayerMgr.AddLayer(new IPLayer("IP"));
		m_LayerMgr.AddLayer(new TCPLayer("TCP"));
//		m_LayerMgr.AddLayer(new ChatAppLayer("ChatApp"));
//		m_LayerMgr.AddLayer(new FileAppLayer("FileApp"));
		m_LayerMgr.AddLayer(new ApplicationLayer("Application"));

		// Connect according to layer architecture
		//m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *IP ( *TCP ( *Application ) ) )  )");
		m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *ARP +IP ( -ARP *TCP ( *Application ) ) ) )");
		((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(0);//PC마다 다르다. 0번이 아닐 수도 있기 때문에, 탐색하는 함수를 만들면 편할듯
		
		// Connect IP and ARP
		//((ARPLayer) m_LayerMgr.GetLayer("ARP")).SetUpperLayer((IPLayer) m_LayerMgr.GetLayer("IP"));
		//((IPLayer) m_LayerMgr.GetLayer("IP")).SetUnderLayer((ARPLayer) m_LayerMgr.GetLayer("ARP"));

		// Connect ARP and Ethernet
		//((ARPLayer) m_LayerMgr.GetLayer("ARP")).SetUnderLayer((EthernetLayer) m_LayerMgr.GetLayer("Ethernet"));
		//((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetUpperLayer((ARPLayer) m_LayerMgr.GetLayer("ARP"));

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
	
	static Iterator cache_Itr;
	static Iterator proxy_Itr;
	static DefaultListModel<String> ARPModel; 
	static DefaultListModel<String> ProxyARPModel; 
	
	public static void getTable() {
		cacheTable = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getCacheList();
		proxyTable = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getProxyList();
	}

	
	public static void printCache() {
		getTable();
		// ARPLayer에서 만든 cache_Table을 가져온다. Sleep초 마다 갱신하는 셈.
		//cacheTable = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getCacheList();
		cache_Itr = cacheTable.iterator();
		proxy_Itr = proxyTable.iterator();
		ARPModel.removeAllElements(); 
		//proxyTable = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getProxyList();
		
		//ARPModel.removeAllElements(); // ARPModel의 값을 모두 지우고,
		while (cache_Itr.hasNext()) { // 캐시테이블의 모든 값을 ARPModel에 저장하기 위해서 캐시테이블을 순회.
			_Cache_Table cache = (_Cache_Table)cache_Itr.next();
			ARPModel.addElement(String.format("%20s%25s%15s", // 캐시테이블의 값 중 dstIPAddr(key), dstMACaddr, status를 아래 형식으로 ARPModel에 저장
					byteToIP(cache.ipaddr), 														// key는 String이기 때문에 그대로 저장.
					byteToMAC(cache.ethaddr),
					cache.state));
					//ethAddrToQuestionOrEth(cacheTable.get(key).cache_ethaddr), // ethAddr은 byte[]이기 때문에 ??? 혹은 xx:xx 형태의 String으로 변경해서 저장
					//cache_Table.get(key).cache_status));						// status는 String이기 때문에 그대로 저장
		}
		
		ProxyARPModel.removeAllElements(); 
		while (proxy_Itr.hasNext()) { // 캐시테이블의 모든 값을 ARPModel에 저장하기 위해서 캐시테이블을 순회.
			_Proxy_Table proxy = (_Proxy_Table)proxy_Itr.next();
			ProxyARPModel.addElement(String.format("%15s%20s%25s", 
					proxy.device,
					byteToIP(proxy.ipaddr), 														
					byteToMAC(proxy.ethaddr)
					));
		}
	}
	
//
//	public static void getTable() {
//		cacheTable = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getCacheList();
//		proxyTable = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getProxyList();
//	}

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

		
		// ARPCacheArea앞에 선언되어 있어 생성만 하면 됨, 쓰여지는 부분 (흰부분)
//		ARPCacheArea = new JTextArea();
//		ARPCacheArea.setEditable(false);
//		ARPCacheArea.setBounds(0, 0, 340, 210);
//		ARPCacheEditorPanel.add(ARPCacheArea);// ARPCache edit

		// ARPCache 부분의 IP주소 입력 부분 Panel
		JPanel ARPCacheInputPanel = new JPanel();
//		ARPCacheInputPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
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

		ProxyARPModel = new DefaultListModel<String>();
		ProxyArpArea = new JList<String>(ProxyARPModel);
		ProxyArpArea.setBounds(0, 0, 340, 120);
		ProxyARPEntryEditorPanel.add(ProxyArpArea);
		
//		// ProxyARPEntryArea앞에 선언되어 있어 생성만 하면 됨, 쓰여지는 부분 (흰부분)
//		ProxyARPEntryArea = new JTextArea();
//		ProxyARPEntryArea.setEditable(false);
//		ProxyARPEntryArea.setBounds(0, 0, 340, 120);
//		ProxyARPEntryEditorPanel.add(ProxyARPEntryArea);// ARPCache edit

//		GUI 수정
		Device = new JLabel("Device");
		Device.setBounds(30, 130, 50, 25);
		ProxyARPEntryEditorPanel.add(Device);

		DeviceText = new JTextField();
		DeviceText.setBounds(110, 130, 210, 25);// 249
		ProxyARPEntryEditorPanel.add(DeviceText);
		DeviceText.setColumns(10);

		MAC_Addr = new JLabel("MAC_Addr");
		MAC_Addr.setBounds(30, 165, 70, 25);
		ProxyARPEntryEditorPanel.add(MAC_Addr);

		MAC_AddrText = new JTextField();
		MAC_AddrText.setBounds(110, 165, 210, 25);// 249
		ProxyARPEntryEditorPanel.add(MAC_AddrText);
		MAC_AddrText.setColumns(10);

		IP_Addr = new JLabel("IP_Addr");
		IP_Addr.setBounds(30, 200, 50, 25);
		ProxyARPEntryEditorPanel.add(IP_Addr);

		IP_AddrText = new JTextField();
		IP_AddrText.setBounds(110, 200, 210, 25);// 249
		ProxyARPEntryEditorPanel.add(IP_AddrText);
		IP_AddrText.setColumns(10);
		
		
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
					// Item_Delete_Button작동
					// ARPCachelist의 num에 해당하는 item것을 제거한다.
					int num = ItemText.getText() == "" ? 0 : Integer.parseInt(ItemText.getText());
					ARPLayer arplayer = (ARPLayer) m_LayerMgr.GetLayer("ARP");
					arplayer.ARPTable_index_delete(num);
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

					ARPLayer arplayer = (ARPLayer) m_LayerMgr.GetLayer("ARP");
					arplayer.ARPTable_All_delete();
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
					// tcplayer.Send(IP, 4); //byte[]형식의 IP는 length = 4
					// abort 수정 사용한 이유가 궁금합니다!

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
					// 아직 구현 x Proxy 부분
//					TODO
					String device = DeviceText.getText();
					String ip_addr = IP_AddrText.getText();
					String mac_addr = MAC_AddrText.getText();
					
//					ProxyARPEntryArea.append(device + " " + ip_addr + " " + mac_addr + "\n");

					byte[] add_IP = strToByte(ip_addr);
					byte[] add_MAC = strToByte(mac_addr);
					
//					setProxyTable
					((ARPLayer) m_LayerMgr.GetLayer("ARP")).setProxyTable(add_IP, add_MAC, device);
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
					// TODO : proxy 테이블 정보 모두삭제
					((ARPLayer) m_LayerMgr.GetLayer("ARP")).ProxyTable_All_delete();
					
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
					// 아직 구현 x Proxy 부분
					String MAC_str = HW_AddressWrite.getText();
					byte[] MAC = strToByte(MAC_str);

					TCPLayer tcpLayer = (TCPLayer) m_LayerMgr.GetLayer("TCP");
					tcpLayer.Send(MAC, 6);
					HW_AddressWrite.setText("");
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


////  Local variable '변수명' defined in an enclosing scope must be final or effectively final 
////  라는 에러가 떠서 밖에 선언 -> 찾아보니 밖에 선언하는게 해결 방법이네요. bb
//	String str = "";
//	public void reload() {
////  	Todo : ARP레이어 가져오기
////  	ARPLayer arpLayer = (ARPLayer) m_LayerMgr.GetLayer("ARP");
////		HashMap<byte[], byte[]> arpCacheMap = arpLayer.getArpCache();
////		String str = "";
//		listCacheTable.forEach(item -> str += item + "\n");
////    	Test
//		System.out.println("----------- ARPCachelist-------------");
//		System.out.println(str);
//		
//		ARPCacheArea.setText(str);
//	}
//    baselayer override

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
		// 이건 무슨 역할을 하는거지?

	}

	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		return null;
	}

}