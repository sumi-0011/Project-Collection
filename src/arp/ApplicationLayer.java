package arp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import arp.ARPLayer;
import arp.ARPLayer._Cache_Table;
import arp.ARPLayer._Proxy_Table;

import javax.swing.border.*;

public class ApplicationLayer extends JFrame implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUnderLayer = new ArrayList<BaseLayer>();;

	BaseLayer UnderLayer;

	private static LayerManager m_LayerMgr = new LayerManager();

//	Container contentPane;
	private JPanel contentPane;
	private JTextField IPAddressWrite;
	private JTextField HW_AddressWrite;
	JTextField ItemText;

	JTextArea ARPCacheArea;
	JTextArea ProxyARPEntryArea;
	JTextArea srcAddress;
	JTextArea dstAddress;
	JTextArea GratuitousARPArea;

	JLabel lblsrc;
	JLabel lbldst;
	JLabel IPLabel; // ip 주소 Label
	JLabel HW_Label; // ip 주소 Label
	JLabel Item_num;

	JButton Setting_Button;
	JButton Item_Delete_Button; // ARP Cache : Item_Delete_Button
	JButton All_Delete_Button; // ARP Cache : All_Delete_Button
	JButton IP_Addr_Send_Button; // IP 주소 : IP_Addr_Send_Button
	JButton ProxyARP_Delete_Button; // ProxyARP : ProxyARP_Delete_Button
	JButton ProxyARP_Add_Button; // ProxyARP : ProxyARP_Add_Button
	JButton HW_Addr_Send_Button; // IP 주소 : IP_Addr_Send_Button
	JButton Cancle_Button;
	JButton End_Button;
	// ArrayList를 사용하여 delete를 편하게 함.
	static ArrayList<_Cache_Table> cacheTable; // ARP Basic Cache Table
	static ArrayList<_Proxy_Table> proxyTable; // Proxy Table
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
//			

	}

	public static void getTable() {
		cacheTable = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getCacheList();
		proxyTable = ((ARPLayer) m_LayerMgr.GetLayer("ARP")).getProxyList();
	}

	public ApplicationLayer(String pName) {
		pLayerName = pName;

		setTitle("ARP");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(250, 250, 800, 425);
//		contentPan : 전체 GUI 틀 Panel
		contentPane = new JPanel();
		((JComponent) contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// ARP Cache panel 생성
		JPanel ARPCachePanel = new JPanel();
		ARPCachePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "ARP Cache",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		ARPCachePanel.setBounds(10, 5, 360, 340);
		contentPane.add(ARPCachePanel);
		ARPCachePanel.setLayout(null);

		// ARPCacheEditorPanel : ARPCacheArea의 바탕이 되는 panel
		JPanel ARPCacheEditorPanel = new JPanel();// ARP Cache Panel write panel
		ARPCacheEditorPanel.setBounds(10, 15, 340, 210);
		ARPCachePanel.add(ARPCacheEditorPanel);
		ARPCacheEditorPanel.setLayout(null);
		ARPCacheEditorPanel.setBackground(Color.red);

		// ARPCacheArea앞에 선언되어 있어 생성만 하면 됨, 쓰여지는 부분 (흰부분)
		ARPCacheArea = new JTextArea();
		ARPCacheArea.setEditable(false);
		ARPCacheArea.setBounds(0, 0, 340, 210);
		ARPCacheEditorPanel.add(ARPCacheArea);// ARPCache edit

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

		// ProxyARPEntry
		JPanel ProxyARPEntryPanel = new JPanel();
		ProxyARPEntryPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Proxy ARP Entry",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		ProxyARPEntryPanel.setBounds(380, 5, 360, 270);
		contentPane.add(ProxyARPEntryPanel);
		ProxyARPEntryPanel.setLayout(null);

		// ProxyARPEntryPanel의 write panel
		JPanel ProxyARPEntryEditorPanel = new JPanel();
		ProxyARPEntryEditorPanel.setBounds(10, 15, 340, 250);
		ProxyARPEntryPanel.add(ProxyARPEntryEditorPanel);
		ProxyARPEntryEditorPanel.setLayout(null);

		// ProxyARPEntryArea앞에 선언되어 있어 생성만 하면 됨, 쓰여지는 부분 (흰부분)
		ProxyARPEntryArea = new JTextArea();
		ProxyARPEntryArea.setEditable(false);
		ProxyARPEntryArea.setBounds(0, 0, 340, 210);
		ProxyARPEntryEditorPanel.add(ProxyARPEntryArea);// ARPCache edit

		// GratuitousARP
		JPanel GratuitousARP_Panel = new JPanel();
		GratuitousARP_Panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Proxy ARP Entry",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GratuitousARP_Panel.setBounds(380, 280, 360, 65);
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
//                	테스트 용
					System.out.println(listCacheTable.get(num));
					reload();
//                	TODO : arp레이어에서 메소드를 불러와 index에 해당하는 cache를 제거
//                	DeleteARPCacheTableAddr(byte[] addr)
//                	해당하는 ip주소를 byte[] addr로 보내주기 위해 str을 byte로 변환이 필요
					int index = listCacheTable.get(num).indexOf(" ");
					String deleteArp = listCacheTable.get(num).substring(0, index);
					System.out.println("deleteArp" + deleteArp);
					ItemText.setText("");
					byte[] delete_IP_addr = strToByte(deleteArp);
					
					// Iterator를 사용하여 ArrayList의 cacheTable에서 해당 IP_Addr데 대한 정보를 삭제
					Iterator<_Cache_Table> it = cacheTable.iterator();
					while(it.hasNext()) {
						_Cache_Table cache = (_Cache_Table)it.next();
						if(Arrays.equals(cache.ipaddr, delete_IP_addr))
							it.remove();
					}
					
					//ARPLayer arplayer = (ARPLayer) m_LayerMgr.GetLayer("ARP");
					// ARPCacheAre에서 해당하는 ip삭제
					// arplayer.DeleteARPCacheTableAddr(delete_IP_addr);

//                	ARPCacheArea를 다시 보여준다.
					reload();
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
					// TODO : All_Delete_Button 구현
					cacheTable.clear();
					reload();
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
		ProxyARP_Add_Button.setBounds(15, 215, 130, 30);
		ProxyARP_Add_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == ProxyARP_Add_Button) {
					// 아직 구현 x Proxy 부분
				}

			}

		});
		ProxyARPEntryEditorPanel.add(ProxyARP_Add_Button);

		// ProxyARPEntry Delete button
		ProxyARP_Delete_Button = new JButton("Delete");
		ProxyARP_Delete_Button.setBounds(200, 215, 130, 30);
		ProxyARP_Delete_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == ProxyARP_Delete_Button) {
					// 아직 구현 x Proxy 부분
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
				}

			}

		});
		GratuitousARPInputPanel.add(HW_Addr_Send_Button);//

		End_Button = new JButton("종료");
		End_Button.setBounds(285, 350, 80, 30);
		End_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == End_Button) {
					// 종료 버튼 클릭시
				}

			}

		});
		contentPane.add(End_Button);//

		Cancle_Button = new JButton("취소");
		Cancle_Button.setBounds(380, 350, 80, 30);
		Cancle_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == Cancle_Button) {
					// 취소 버튼 클릭시
				}

			}

		});
		contentPane.add(Cancle_Button);//

		setVisible(true);
//		********************Test***************************************
//    	ARPCachelist.add("210.170.1.1 00:70:69:47:2F:31 complete");
//		ARPCachelist.add("210.170.1.2 00:70:69:47:2F:32 complete");
//    	ARPCachelist.add("210.170.1.3 00:70:69:47:2F:33 complete");
//    	ARPCachelist.add("210.170.1.4 00:70:69:47:2F:34 complete");
//		Todo : reload => 
		reload();

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


//  Local variable '변수명' defined in an enclosing scope must be final or effectively final 
//  라는 에러가 떠서 밖에 선언 -> 찾아보니 밖에 선언하는게 해결 방법이네요. bb
	String str = "";
	public void reload() {
//  	Todo : ARP레이어 가져오기
//  	ARPLayer arpLayer = (ARPLayer) m_LayerMgr.GetLayer("ARP");
//		HashMap<byte[], byte[]> arpCacheMap = arpLayer.getArpCache();
//		String str = "";
		listCacheTable.forEach(item -> str += item + "\n");
//    	Test
		System.out.println("----------- ARPCachelist-------------");
		System.out.println(str);
		
		ARPCacheArea.setText(str);
	}
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