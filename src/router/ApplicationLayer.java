package router;

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

import router.ARPLayer;
import router.TCPLayer;
import router.ARPLayer._Cache_Entry;
import router.ARPLayer._Proxy_Entry;

import javax.swing.border.*;

public class ApplicationLayer extends JFrame implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUnderLayer = new ArrayList<BaseLayer>();;
	static JList<String> ArpArea; // ARP 텍스트 출력란
	static JList<String> ProxyArpArea; // proxy ARP 텍스트 출력란
	static JList<String> RoutingArea;

	BaseLayer UnderLayer;

	private static LayerManager m_LayerMgr = new LayerManager();

	private JPanel contentPane;

	JButton Routing_Delete_Button; // RoutingTable : Delete_Button
	JButton Routing_Add_Button; // RoutingTable : Add_Button
	JButton Delete_Button; // ARP Cache : Delete_Button
	JButton ProxyARP_Delete_Button; // ProxyARP : ProxyARP_Delete_Button
	JButton ProxyARP_Add_Button;

	// ProxyARP Add부분
	JTextField DeviceText;
	JTextField MAC_AddrText;
	JTextField IP_AddrText;
	JLabel Device;
	JLabel MAC_Addr;
	JLabel IP_Addr;

	// RoutingTable Add부분
	JTextField DestinationText;
	JTextField NetmaskText;
	JTextField GatewayText;
	JTextField InterfaceText;
	JLabel Destination;
	JLabel Netmask;
	JLabel Gateway;
	JLabel Flag;
	JLabel Interface;
	Checkbox upCheck;
	Checkbox gateCheck;
	Checkbox hostCheck;

	String flagStr = "";

	static _Proxy_Entry proxyEntry;
	static Map<String, _Cache_Entry> cache_Table;
	static Set<String> cache_Itr;
	static Map<String, _Proxy_Entry> proxy_Table;
	static RouterTable router_Table;
	
	static ArrayList<byte[]> byteArray = new ArrayList<byte[]>();
	static DefaultListModel<String> ARPModel; // JList의 Item들을 관리하는 모델
	static DefaultListModel<String> ProxyModel;
	static DefaultListModel<String> RoutingModel;

	public static void main(String[] args) throws SocketException {

		m_LayerMgr.AddLayer(new NILayer("NI"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
		m_LayerMgr.AddLayer(new ARPLayer("ARP"));
		m_LayerMgr.AddLayer(new IPLayer("IP"));
		m_LayerMgr.AddLayer(new TCPLayer("TCP"));

		m_LayerMgr.AddLayer(new ApplicationLayer("Application"));

		m_LayerMgr.ConnectLayers(" NI ( *Ethernet ( *ARP +IP ( -ARP *TCP ( *Application ) ) ) )");
		((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(0);// PC마다 다르다. 0번이 아닐 수도 있기 때문에, 탐색하는 함수를 만들면 편할듯

		router_Table = new RouterTable();
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
		Thread cacheUpdate = new Thread(task, "cacheUpdataThread");
		cacheUpdate.start();

	}

	public static boolean isThisQuestion(byte[] input) {
		for (byte a : input) { // byte[]를 순회하는데
			if (a != 0) { // 0 이 아닌 게 있다면,
				return false; // ???가 아니다.
			}
		}
		return true;
	}

	public static String ethAddrToQuestionOrEth(byte[] input) {
		if (isThisQuestion(input)) { // byte[]가 0000..이다 -> Question mark로 출력
			return "???????????";
		} else { // byte[]가 정상적인 ethAddr 형식이다 -> String으로 바꿔서 출력.
			return byteArrayToString(input);
		}
	}

	public static String byteArrayToString(byte[] b) { // byte[0] = 1, byte[1] = 2 일 때... 01:02 String으로 변경
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; ++i) {
			sb.append(String.format("%02X", b[i])); // sb에 바이트를 str으로 바꿔서 저장한다. 이 때 1은 01, 2는 02 등 두 글자로 formatting
			sb.append(":");
		}
		sb.deleteCharAt(sb.length() - 1); // 마지막에 추가된 :를 지운다
		return sb.toString();
	}

	public static void getTable() {
		cache_Table = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getCacheList();
		cache_Itr = cache_Table.keySet();
		proxy_Table = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getProxyList();
	}

	public static void printCache() {
		getTable();

		// proxy_Table = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getProxyList();

		ARPModel.removeAllElements(); // ARPModel의 값을 모두 지우고,
		for (String key : cache_Itr) { // 캐시테이블의 모든 값을 ARPModel에 저장하기 위해서 캐시테이블을 순회.
			ARPModel.addElement(String.format("%20s%25s%15s", // 캐시테이블의 값 중 dstIPAddr(key), dstMACaddr, status를 아래 형식으로
																// ARPModel에 저장
					key, // key는 String이기 때문에 그대로 저장.
					ethAddrToQuestionOrEth(cache_Table.get(key).cache_ethaddr), // ethAddr은 byte[]이기 때문에 ??? 혹은 xx:xx
																				// 형태의 String으로 변경해서 저장
					cache_Table.get(key).cache_status)); // status는 String이기 때문에 그대로 저장
		}

	}

	public ApplicationLayer(String pName) {
		pLayerName = pName;

		setTitle("Static Routing Table");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(350, 100, 800, 650);
//		contentPane : 전체 GUI 틀 Panel
		contentPane = new JPanel();
		((JComponent) contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

// ------------ Static Routing Table ------------------------------------------------------------------------------------------
				
		JPanel RoutingPanel = new JPanel();
		RoutingPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Static Routing Table",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		RoutingPanel.setBounds(10, 5, 360, 595);
		contentPane.add(RoutingPanel);
		RoutingPanel.setLayout(null);

		// RoutingPanel의 write panel
		JPanel RoutingEditorPanel = new JPanel();
		RoutingEditorPanel.setBounds(10, 20, 340, 525);
		RoutingPanel.add(RoutingEditorPanel);
		RoutingEditorPanel.setLayout(null);

		RoutingModel = new DefaultListModel<String>();
		RoutingArea = new JList<String>(RoutingModel);
		RoutingArea.setBounds(0, 0, 340, 300);
		RoutingEditorPanel.add(RoutingArea);

//		staticRoutingTable의 요소 추가
		Destination = new JLabel("Destination");
		Destination.setBounds(30, 310, 70, 25);
		RoutingEditorPanel.add(Destination);

		DestinationText = new JTextField();
		DestinationText.setBounds(110, 310, 210, 25);
		RoutingEditorPanel.add(DestinationText);
		DestinationText.setColumns(10);
		DestinationText.setText("192.168.0.0");

		Netmask = new JLabel("Netmask");
		Netmask.setBounds(30, 345, 70, 25);
		RoutingEditorPanel.add(Netmask);

		NetmaskText = new JTextField();
		NetmaskText.setBounds(110, 345, 210, 25);
		RoutingEditorPanel.add(NetmaskText);
		NetmaskText.setColumns(10);
		NetmaskText.setText("255.255.255.0");

		Gateway = new JLabel("Gateway");
		Gateway.setBounds(30, 380, 70, 25);
		RoutingEditorPanel.add(Gateway);

		GatewayText = new JTextField();
		GatewayText.setBounds(110, 380, 210, 25);
		RoutingEditorPanel.add(GatewayText);
		GatewayText.setColumns(10);
		GatewayText.setText("192.168.2.1");

		Flag = new JLabel("Flag");
		Flag.setBounds(30, 415, 70, 25);
		upCheck = new Checkbox("UP");
		upCheck.setBounds(110, 415, 40, 30);
		upCheck.setState(true);

		gateCheck = new Checkbox("Gateway");
		gateCheck.setBounds(150, 415, 70, 30);
		gateCheck.setState(true);

		hostCheck = new Checkbox("Host");
		hostCheck.setBounds(220, 415, 70, 30);

		RoutingEditorPanel.add(Flag);
		RoutingEditorPanel.add(upCheck);
		RoutingEditorPanel.add(gateCheck);
		RoutingEditorPanel.add(hostCheck);

		Interface = new JLabel("Interface");
		Interface.setBounds(30, 450, 70, 25);
		RoutingEditorPanel.add(Interface);
		
		InterfaceText = new JTextField();
		InterfaceText.setBounds(110, 450, 210, 25);
		RoutingEditorPanel.add(InterfaceText);
		InterfaceText.setColumns(10);
		InterfaceText.setText("3C905");

		// Routing : Add button
		Routing_Add_Button = new JButton("Add");// setting
		Routing_Add_Button.setBounds(30, 480, 130, 30);
		Routing_Add_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == Routing_Add_Button) {
					String dst = DestinationText.getText();
					String net = NetmaskText.getText();
					String gate = GatewayText.getText();
					String interf = InterfaceText.getText();
					
					if(upCheck.getState())
						flagStr += "U";
					if(gateCheck.getState())
						flagStr += "G";
					if(hostCheck.getState())
						flagStr += "H";
					
					String input = dst + " / " + net + " / " + gate + " / " + flagStr + " / " + interf + "\n";
					RoutingModel.addElement(input);
					
					byte[] dstByte = new byte[4];
					byte[] netByte = new byte[4];
					byte[] gateByte = new byte[4];
					byte[] interfByte = new byte[4];
					
					DestinationText.setText("");
					NetmaskText.setText("");
					GatewayText.setText("");
					InterfaceText.setText("");
					
					dstByte = strToByte(dst);
					netByte = strToByte(net);
					gateByte = strToByte(gate);
					interfByte = strToByte(interf);
					
					router_Table.addEntry(dstByte, netByte, gateByte, flagStr, interfByte);
					
					flagStr = "";
				}
			}

		});
		RoutingEditorPanel.add(Routing_Add_Button);
		

		// RoutingEntry All Delete button
		Routing_Delete_Button = new JButton("Delete");
		Routing_Delete_Button.setBounds(190, 480, 130, 30);
		Routing_Delete_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == Routing_Delete_Button) {
						RoutingModel.removeAllElements();
				}

			}

		});
		RoutingEditorPanel.add(Routing_Delete_Button);

//---------- ARP --------------------------------------------------------------------------------------------------
		JPanel ARPCachePanel = new JPanel();
		ARPCachePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "ARP Cache",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		ARPCachePanel.setBounds(410, 10, 368, 280);
		contentPane.add(ARPCachePanel);
		ARPCachePanel.setLayout(null);

		// ARPCacheEditorPanel : ARPCacheArea의 바탕이 되는 panel
		JPanel ARPCacheEditorPanel = new JPanel();// ARP Cache Panel write panel
		ARPCacheEditorPanel.setBounds(10, 20, 350, 210);
		ARPCachePanel.add(ARPCacheEditorPanel);
		ARPCacheEditorPanel.setLayout(null);
//		ARPCacheEditorPanel.setBackground(Color.red);

		ARPModel = new DefaultListModel<String>();
		ArpArea = new JList<String>(ARPModel);
		ArpArea.setBounds(0, 0, 340, 210);
		ARPCacheEditorPanel.add(ArpArea);

		// ARPCache : Delete Button
		Delete_Button = new JButton("Delete");
		Delete_Button.setBounds(142, 240, 90, 30);
		Delete_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == Delete_Button) {
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
		ARPCachePanel.add(Delete_Button);// Item_Delete_Button button

// ------------ ProxyARPEntry ------------------------------------------------------------------------------------------
		JPanel ProxyARPEntryPanel = new JPanel();
		ProxyARPEntryPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Proxy ARP Entry",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		ProxyARPEntryPanel.setBounds(410, 290, 368, 310);
		contentPane.add(ProxyARPEntryPanel);
		ProxyARPEntryPanel.setLayout(null);

		// ProxyARPEntryPanel의 write panel
		JPanel ProxyARPEntryEditorPanel = new JPanel();
		ProxyARPEntryEditorPanel.setBounds(10, 20, 340, 270);
		ProxyARPEntryPanel.add(ProxyARPEntryEditorPanel);
		ProxyARPEntryEditorPanel.setLayout(null);

		ProxyModel = new DefaultListModel<String>();
		ProxyArpArea = new JList<String>(ProxyModel);
		ProxyArpArea.setBounds(0, 0, 340, 120);
		ProxyARPEntryEditorPanel.add(ProxyArpArea);

//		ProxyARPEntry의 요소 추가
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

					((ARPLayer) m_LayerMgr.GetLayer("ARP")).setProxyTable( // proxy_Table에 저장
							ip_addr, // argu1 : proxy_Table의 key로 지정할, IP 주소칸에 입력한 텍스트
							strToByteArray2(mac_addr), // argu2 : proxy_Table의 value로 지정할, Ethernet 주소칸에 입력한 텍스트
							device); // argu3 : proxy_Table의 value로 지정할, Combobox 에 선택된 값.

					ProxyModel.addElement(String.format("%18s%20s%23s", // ProxyModel(GUI)에 출력
							device, ip_addr, mac_addr));

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
						StringTokenizer st2 = new StringTokenizer(ProxyArpArea.getSelectedValue().toString().trim(),
								" ");

						// proxy_Table 에서 제거한 뒤 GUI에서도 제거
						while (st2.hasMoreTokens()) {
							String key = st2.nextToken();
							if (proxy_Table.containsKey(key)) {
								proxy_Table.remove(key); // proxy_Table에서 제거
								break;
							}
						}
						ProxyModel.remove(ProxyArpArea.getSelectedIndex()); // GUI에서 제거
					}

				}

			}

		});
		ProxyARPEntryEditorPanel.add(ProxyARP_Delete_Button);

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
		for (byte b : addr) {
			ip += Integer.toString(b & 0xff) + ".";
		}
		// 마지막 .를 빼기 위해서 substring 메소드 사용
		return ip.substring(0, ip.length() - 1);
	}

	// byte 형식의 addr를 MAC Address(ex. ff:ff:ff:ff:ff:ff) 형식으로 변환
	public static String byteToMAC(byte[] addr) {
		StringBuilder mac = new StringBuilder();
		for (byte b : addr) {
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
