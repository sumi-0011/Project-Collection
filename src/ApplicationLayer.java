
import java.awt.Color;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class ApplicationLayer extends JFrame implements BaseLayer {

	public static RoutingTable routingTable;
	public int nUpperLayerCount = 0;
	public int nUnderLayerCount = 0;
	public String pLayerName = null;
	public static ARPTable arpTable;
	// public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUnderLayerGUI = new ArrayList<BaseLayer>();
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	BaseLayer UnderLayer;
	public int progress_number;

	private static LayerManager m_LayerMgr = new LayerManager();
	public static boolean exist = false;

	String path;
	JTextArea proxyArea;

	int selected_index;
	private JTextField IPAddressWrite;

	Container contentPane;

	JTextArea TotalArea;
	static JTextArea RoutingArea;

	JButton btnAllDelete;
	JButton btnIPSend;
	JButton btnItemDelete;
	JButton btnRoutingDelete;
	JButton btnRoutingAdd;
	JButton Setting_Button;
	static JButton src_Setting_Button;

	JLabel choice;
	static JComboBox<String> NICComboBox;
	JComboBox combo1;
	JComboBox combo2;

	// router

	JTextArea input_Destination;
	JTextArea input_Netmask;
	JTextArea input_Gateway;
	JComboBox<String> selectInterface;
	JLabel label_Destination;
	JLabel label_Netmask;
	JLabel label_Gateway;
	JLabel label_Flag;
	JLabel label_Interface;

	String[] interfaceName = { "Port1", "Port2" };
	String interface0 = interfaceName[0];

	JCheckBox flagU;
	JCheckBox flagG;
	JCheckBox flagH;

	//

	int index1;
	int index2;

	FileDialog fd;
	private JTextField H_WAddressWrite;

	/**
	 * @wbp.nonvisual location=108,504
	 */
	private final JPopupMenu popupMenu = new JPopupMenu();

	public static void main(String[] args) throws IOException {

		// TODO Auto-generated method stub
		m_LayerMgr.AddLayer(new NILayer("NI"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet"));
		arpTable = null;
		m_LayerMgr.AddLayer(new ARPLayer("ARP2", arpTable));
		m_LayerMgr.AddLayer(new ARPLayer("ARP", arpTable));

		m_LayerMgr.AddLayer(new IPLayer("IP"));

		m_LayerMgr.AddLayer(new NILayer("NI2"));
		m_LayerMgr.AddLayer(new EthernetLayer("Ethernet2"));

		m_LayerMgr.AddLayer(new IPLayer("IP2"));
		m_LayerMgr.AddLayer(new ApplicationLayer("GUI"));

//            arpTable = new ARPTable((ARPLayer) m_LayerMgr.GetLayer("ARP"), (ARPLayer) m_LayerMgr.GetLayer("ARP2"));
		arpTable = new ARPTable((ARPLayer) m_LayerMgr.GetLayer("ARP"), (ARPLayer) m_LayerMgr.GetLayer("ARP2"),
				(ApplicationLayer) m_LayerMgr.GetLayer("GUI"));
		((ARPLayer) m_LayerMgr.GetLayer("ARP")).SetARPTable(arpTable, (ApplicationLayer) m_LayerMgr.GetLayer("GUI"));
		((ARPLayer) m_LayerMgr.GetLayer("ARP2")).SetARPTable(arpTable, (ApplicationLayer) m_LayerMgr.GetLayer("GUI"));

		m_LayerMgr.ConnectLayers(
				" NI ( +Ethernet ( +ARP ( +IP ( +GUI ) ) +IP ( +GUI ) ) ) ^GUI ( -IP ( -ARP ( -Ethernet ( -NI ) ) -Ethernet ( -NI ) ) )  ^NI2 ( +Ethernet2 ( +ARP2 ( +IP2 ( +GUI ) ) +IP2 ( +GUI ) ) ) ^GUI ( -IP2 ( -ARP2 ( -Ethernet2 ( -NI2 ) ) -Ethernet2 ( -NI2 ) ) )");

		routingTable = new RoutingTable();

		((IPLayer) m_LayerMgr.GetLayer("IP")).friendIPset(((IPLayer) m_LayerMgr.GetLayer("IP2")));
		((IPLayer) m_LayerMgr.GetLayer("IP2")).friendIPset(((IPLayer) m_LayerMgr.GetLayer("IP")));
		((IPLayer) m_LayerMgr.GetLayer("IP")).setRouterTable(routingTable);
		((IPLayer) m_LayerMgr.GetLayer("IP2")).setRouterTable(routingTable);

	}

	public ApplicationLayer(String pName) throws IOException {

		pLayerName = pName;

		setTitle("Computer Network");

		setBounds(250, 250, 980, 520);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = this.getContentPane();
		getContentPane().setLayout(null);

		// NIC 1, NIC 2 Setting
		{
			JPanel settingPanel = new JPanel();
			settingPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "setting",
					TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			settingPanel.setBounds(14, 420, 930, 50);
			contentPane.add(settingPanel);
			settingPanel.setLayout(null);

			JLabel choice1 = new JLabel("NIC select 1: ");
			choice1.setBounds(80, 20, 170, 20);
			settingPanel.add(choice1);

			// strCombo1 -> combo1
			combo1 = new JComboBox();
			combo1.setBounds(150, 20, 220, 20);
			combo1.setVisible(true);
			settingPanel.add(combo1);

			JLabel choice2 = new JLabel("NIC select 2: ");
			choice2.setBounds(390, 20, 170, 20);
			settingPanel.add(choice2);

			// strCombo2 -> combo2
			combo2 = new JComboBox();
			combo2.setBounds(460, 20, 220, 20);
			combo2.setVisible(true);
			settingPanel.add(combo2);

			for (int i = 0; ((NILayer) m_LayerMgr.GetLayer("NI")).m_pAdapterList.size() > i; i++) {
				String s = ((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(i).getDescription();
				s += i;
				combo1.addItem(s);
			}

			for (int i = 0; ((NILayer) m_LayerMgr.GetLayer("NI2")).m_pAdapterList.size() > i; i++) {
				String s = ((NILayer) m_LayerMgr.GetLayer("NI2")).GetAdapterObject(i).getDescription();
				s += i;
				combo2.addItem(s);

			}

			src_Setting_Button = new JButton("setting");// setting
			src_Setting_Button.setBounds(720, 20, 80, 20);
			src_Setting_Button.addActionListener(new setAddressListener());
			settingPanel.add(src_Setting_Button);// setting

		}

		JPanel Routing_Table = new JPanel();
		Routing_Table.setBounds(14, 12, 458, 402);
		Routing_Table.setBorder(
				new TitledBorder(null, "Static Routing Table", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		Routing_Table.setLayout(null);
		contentPane.add(Routing_Table);

		// table Label
		JTable Routing_Jtable;

		DefaultTableModel Routing_model = new DefaultTableModel();
		Routing_model.addColumn("Destination");
		Routing_model.addColumn("NetMask");
		Routing_model.addColumn("Gateway");
		Routing_model.addColumn("Flag");
		Routing_model.addColumn("Interface");
		Routing_model.addColumn("Metric");

		Routing_Jtable = new JTable(Routing_model);

		Routing_Jtable.getColumnModel().getColumn(0).setPreferredWidth(80);
		Routing_Jtable.getColumnModel().getColumn(1).setPreferredWidth(80);
		Routing_Jtable.getColumnModel().getColumn(2).setPreferredWidth(80);
		Routing_Jtable.getColumnModel().getColumn(3).setPreferredWidth(20);
		Routing_Jtable.getColumnModel().getColumn(4).setPreferredWidth(40);
		Routing_Jtable.getColumnModel().getColumn(5).setPreferredWidth(20);

		JScrollPane Routing_jScrollPane = new JScrollPane(Routing_Jtable);

		Routing_jScrollPane.setBounds(14, 30, 430, 20);

		Routing_Table.add(Routing_jScrollPane);

		RoutingArea = new JTextArea();
		RoutingArea.setEditable(false);
		RoutingArea.setBounds(14, 50, 430, 300);
		Routing_Table.add(RoutingArea);

		btnRoutingDelete = new JButton("Delete");

		btnRoutingDelete.setBounds(140, 355, 165, 35); // (249, 155, 165, 35);
		Routing_Table.add(btnRoutingDelete);

		btnRoutingDelete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String del_ip = JOptionPane.showInputDialog("Item's IP Address");
				StringTokenizer st = new StringTokenizer(del_ip, ".");
				byte[] ipAddress = new byte[4];
				for (int i = 0; i < 4; i++) {
					String ss = st.nextToken();
					int s = Integer.parseInt(ss);
					ipAddress[i] = (byte) (s & 0xFF);
				}

				String netmask = JOptionPane.showInputDialog("Item's netMask Address");
				st = new StringTokenizer(netmask, ".");
				byte[] netMaskByte = new byte[4];
				for (int i = 0; i < 4; i++) {
					String ss = st.nextToken();
					int s = Integer.parseInt(ss);
					netMaskByte[i] = (byte) (s & 0xFF);
				}
				Object[] removeValue = new Object[2];
				removeValue[0] = ipAddress;
				removeValue[1] = netMaskByte;
				routingTable.remove(removeValue);
				RoutingArea.setText(routingTable.updateRoutingTable());

			}
		});

		JPanel ARP_Cache = new JPanel();
		ARP_Cache.setBounds(486, 12, 458, 200);
		ARP_Cache.setBorder(
				new TitledBorder(null, "ARP Cache Table", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		ARP_Cache.setLayout(null);
		contentPane.add(ARP_Cache);

		JTable ARP_table;

		DefaultTableModel ARP_model = new DefaultTableModel();
		ARP_model.addColumn("IP Address");
		ARP_model.addColumn("Ethernet Address");
		ARP_model.addColumn("Interface");
		ARP_model.addColumn("Flag");

		ARP_table = new JTable(ARP_model);

		ARP_table.getColumnModel().getColumn(0).setPreferredWidth(100); // JTable �쓽 而щ읆 湲몄씠 議곗젅
		ARP_table.getColumnModel().getColumn(1).setPreferredWidth(100);
		ARP_table.getColumnModel().getColumn(2).setPreferredWidth(40);
		ARP_table.getColumnModel().getColumn(3).setPreferredWidth(10);

		JScrollPane ARP_jScrollPane = new JScrollPane(ARP_table);

		ARP_jScrollPane.setBounds(14, 30, 430, 20);

		ARP_Cache.add(ARP_jScrollPane);

		TotalArea = new JTextArea();
		TotalArea.setEditable(false);
		TotalArea.setBounds(14, 50, 430, 100);
		ARP_Cache.add(TotalArea);

		btnItemDelete = new JButton("Delete");

		btnItemDelete.setBounds(150, 155, 165, 35);
		ARP_Cache.add(btnItemDelete);

		btnItemDelete.addActionListener(new ActionListener() {
//********RE
			public void actionPerformed(ActionEvent arg0) {
				String del_ip = JOptionPane.showInputDialog("Item's IP Address");
				if (del_ip != null) {
					if (((ARPLayer) m_LayerMgr.GetLayer("ARP"))._Cache_Table.containsKey(del_ip)) {
						Object[] value = ((ARPLayer) m_LayerMgr.GetLayer("ARP"))._Cache_Table.get(del_ip);
						if (System.currentTimeMillis() - (long) value[3] / 1000 > 1) {
							((ARPLayer) m_LayerMgr.GetLayer("ARP"))._Cache_Table.remove(del_ip);
							arpTable.updateARPCacheTable();
						}
					} else if (((ARPLayer) m_LayerMgr.GetLayer("ARP2"))._Cache_Table.containsKey(del_ip)) {
						Object[] value = ((ARPLayer) m_LayerMgr.GetLayer("ARP2"))._Cache_Table.get(del_ip);
						if (System.currentTimeMillis() - (long) value[3] / 1000 > 1) {
							((ARPLayer) m_LayerMgr.GetLayer("ARP2"))._Cache_Table.remove(del_ip);
							arpTable.updateARPCacheTable();
						}
					}
				}
			}
		});

		JPanel Router_Input_Entry = new JPanel();
		Router_Input_Entry.setToolTipText("Add routerTable");
		Router_Input_Entry.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Add Router Table",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		Router_Input_Entry.setBounds(486, 214, 458, 200);
		getContentPane().add(Router_Input_Entry);
		Router_Input_Entry.setLayout(null);

		label_Destination = new JLabel("Destination");
		label_Destination.setBounds(50, 25, 90, 30);
		Router_Input_Entry.add(label_Destination);

		input_Destination = new JTextArea();
		input_Destination.setBounds(150, 30, 170, 20);
		input_Destination.setText("192.168.1.0");
		Router_Input_Entry.add(input_Destination);

		label_Netmask = new JLabel("Netmask");
		label_Netmask.setBounds(50, 60, 90, 30);
		Router_Input_Entry.add(label_Netmask);

		input_Netmask = new JTextArea();
		input_Netmask.setBounds(150, 65, 170, 20);
		input_Netmask.setText("255.255.255.0");
		Router_Input_Entry.add(input_Netmask);

		label_Gateway = new JLabel("Gateway");
		label_Gateway.setBounds(50, 95, 90, 30);
		Router_Input_Entry.add(label_Gateway);

		input_Gateway = new JTextArea();
		input_Gateway.setBounds(150, 100, 170, 20);
		input_Gateway.setText("0.0.0.0");
		Router_Input_Entry.add(input_Gateway);

		label_Flag = new JLabel("Flag");
		label_Flag.setBounds(50, 130, 90, 30);
		Router_Input_Entry.add(label_Flag);

		flagU = new JCheckBox("UP");
		flagU.setBounds(150, 135, 50, 20);
		Router_Input_Entry.add(flagU);

		flagG = new JCheckBox("Gateway");
		flagG.setBounds(205, 135, 80, 20);
		Router_Input_Entry.add(flagG);

		flagH = new JCheckBox("Host");
		flagH.setBounds(285, 135, 60, 20);
		Router_Input_Entry.add(flagH);

		label_Interface = new JLabel("Interface");
		label_Interface.setBounds(50, 165, 90, 30);
		Router_Input_Entry.add(label_Interface);

		selectInterface = new JComboBox<String>(interfaceName);
		selectInterface.setBounds(150, 170, 170, 20);
		selectInterface.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				interface0 = interfaceName[selectInterface.getSelectedIndex()];
			}
		});
		Router_Input_Entry.add(selectInterface);

		/////////////

		btnRoutingAdd = new JButton("Add");
		btnRoutingAdd.setBounds(340, 45, 100, 60);
		Router_Input_Entry.add(btnRoutingAdd);

		JButton btnOk = new JButton("Ok");
		btnRoutingAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!input_Destination.getText().equals("") && !input_Netmask.getText().equals("")
						&& !input_Gateway.getText().contentEquals("")) {

					StringTokenizer st = new StringTokenizer(input_Destination.getText(), ".");

					byte[] Destination = new byte[4];
					for (int i = 0; i < 4; i++) {
						String ss = st.nextToken();
						int s = Integer.parseInt(ss);
						Destination[i] = (byte) (s & 0xFF);
					}

					st = new StringTokenizer(input_Netmask.getText(), ".");

					byte[] Netmask = new byte[4];
					for (int i = 0; i < 4; i++) {
						String ss = st.nextToken();
						int s = Integer.parseInt(ss);
						Netmask[i] = (byte) (s & 0xFF);
					}

					st = new StringTokenizer(input_Gateway.getText(), ".");

					byte[] Gateway = new byte[4];
					for (int i = 0; i < 4; i++) {
						String ss = st.nextToken();
						int s = Integer.parseInt(ss);
						Gateway[i] = (byte) (s & 0xFF);
					}

					String interface_Num = interface0;

					/* routing table add */

					Object[] value = new Object[7];
					value[0] = Destination;
					value[1] = Netmask;
					value[2] = Gateway;
					value[3] = flagU.isSelected();
					value[4] = flagG.isSelected();
					value[5] = flagH.isSelected();
					value[6] = interface_Num;

					routingTable.add(input_Destination.getText(), value);
					RoutingArea.setText(routingTable.updateRoutingTable());
					// dispose();
				} // =======================

			}
		});
		/////

		btnOk.setBounds(130, 250, 80, 30);
		getContentPane().add(btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnCancel.setBounds(220, 250, 80, 30);
		getContentPane().add(btnCancel);
		setVisible(true);

		JMenu mnNewMenu = new JMenu("New menu");
		mnNewMenu.setBounds(-206, 226, 375, 183);
		Router_Input_Entry.add(mnNewMenu);

		this.exist = true;
		setVisible(true);

	}

	public boolean Receive(byte[] input) {
		byte[] data = input;
		return false;
	}

	class setAddressListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == src_Setting_Button) {

				src_Setting_Button.setEnabled(false);
				combo1.setEnabled(false);
				combo2.setEnabled(false);

				index1 = combo1.getSelectedIndex();
				index2 = combo2.getSelectedIndex();

				try {
					byte[] mac0 = ((NILayer) m_LayerMgr.GetLayer("NI")).m_pAdapterList.get(index1).getHardwareAddress();
					byte[] mac1 = ((NILayer) m_LayerMgr.GetLayer("NI2")).m_pAdapterList.get(index2)
							.getHardwareAddress();

					final StringBuilder EthernetAddrbuf1 = new StringBuilder();
					for (byte b : mac0) {
						if (EthernetAddrbuf1.length() != 0)
							EthernetAddrbuf1.append(":");
						if (b >= 0 && b < 16)
							EthernetAddrbuf1.append('0');
						EthernetAddrbuf1.append(Integer.toHexString((b < 0) ? b + 256 : b).toUpperCase());
					}

					final StringBuilder EthernetAddrbuf2 = new StringBuilder();
					for (byte b : mac1) {
						if (EthernetAddrbuf2.length() != 0)
							EthernetAddrbuf2.append(":");
						if (b >= 0 && b < 16)
							EthernetAddrbuf2.append('0');
						EthernetAddrbuf2.append(Integer.toHexString((b < 0) ? b + 256 : b).toUpperCase());
					}

					byte[] ipSrcAddress1 = ((((NILayer) m_LayerMgr.GetLayer("NI")).m_pAdapterList.get(index1)
							.getAddresses()).get(0)).getAddr().getData();
					final StringBuilder IPAddrbuf0 = new StringBuilder();
					for (byte b : ipSrcAddress1) {
						if (IPAddrbuf0.length() != 0)
							IPAddrbuf0.append(".");
						IPAddrbuf0.append(b & 0xff);
					}

					byte[] ipSrcAddress2 = ((((NILayer) m_LayerMgr.GetLayer("NI")).m_pAdapterList.get(index2)
							.getAddresses()).get(0)).getAddr().getData();
					final StringBuilder IPAddrbuf1 = new StringBuilder();
					for (byte b : ipSrcAddress2) {
						if (IPAddrbuf1.length() != 0)
							IPAddrbuf1.append(".");
						IPAddrbuf1.append(b & 0xff);
					}

					System.out.println("NIC1: " + IPAddrbuf0.toString() + " // " + EthernetAddrbuf1.toString());
					System.out.println("NIC2: " + IPAddrbuf1.toString() + " // " + EthernetAddrbuf2.toString());

					/* IP Address 설정 */
					((IPLayer) m_LayerMgr.GetLayer("IP")).setIPSrc(ipSrcAddress1);
					((IPLayer) m_LayerMgr.GetLayer("IP2")).setIPSrc(ipSrcAddress2);
					/* IP Port 설정 */
					((IPLayer) m_LayerMgr.GetLayer("IP")).setPort(mac0, "Port1");
					((IPLayer) m_LayerMgr.GetLayer("IP2")).setPort(mac1, "Port2");

					/* ARP Address 설정 */
					((ARPLayer) m_LayerMgr.GetLayer("ARP")).SetIPAddrSrcAddr(ipSrcAddress1);
					((ARPLayer) m_LayerMgr.GetLayer("ARP2")).SetIPAddrSrcAddr(ipSrcAddress2);

					/* Ethernet Mac 주소 설정 */
					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).setEnetSrc(mac0);
					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet2")).setEnetSrc(mac1);
					/* Receive 실행 */
					((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(index1);
					((NILayer) m_LayerMgr.GetLayer("NI2")).SetAdapterNumber(index2);

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		this.p_aUnderLayerGUI.add(nUnderLayerCount++, pUnderLayer);
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
		// TODO Auto-generated method stub
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
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		this.SetUnderLayer(pUULayer);
	}

	@Override
	public BaseLayer GetUnderLayer(int nindex) {
		if (nindex < 0 || nindex > nUnderLayerCount || nUnderLayerCount < 0)
			return null;
		return p_aUnderLayerGUI.get(nindex);
	}
}