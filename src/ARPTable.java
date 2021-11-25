
public class ARPTable {

	ARPLayer _ARP_Layer1;
	ARPLayer _ARP_Layer2;
	ApplicationLayer _APP;

	ARPTable() {
		this._ARP_Layer1 = null;
		this._ARP_Layer2 = null;
	}

	ARPTable(ARPLayer _ARP_Layer1, ARPLayer _ARP_Layer2, ApplicationLayer _APP) {
		this._ARP_Layer1 = _ARP_Layer1;
		this._ARP_Layer2 = _ARP_Layer2;
		this._APP = _APP;

	}

	public void setARPLayer(ARPLayer _ARP_Layer1, ARPLayer _ARP_Layer2) {
		this._ARP_Layer1 = _ARP_Layer1;
		this._ARP_Layer2 = _ARP_Layer2;
	}

	public  void updateARPCacheTable() {
		_APP.TotalArea.setText("");
		if (_ARP_Layer1 != null && _ARP_Layer2 != null) {

			_ARP_Layer1.updateCacheTable();
			_ARP_Layer2.updateCacheTable();
		}

	}

	public void addCache(String ipAddr, Object[] value, String portName) {
		if (value[2].equals("Incomplete")) {
            _APP.TotalArea.append("       " + ipAddr + "\t" + "??????????????\t incomplete"+portName+"\n");
         } else {
            byte[] maxAddr = (byte[]) value[1];
            String macAddress = String.format("%X:", maxAddr[0]) + String.format("%X:", maxAddr[1])
                  + String.format("%X:", maxAddr[2]) + String.format("%X:", maxAddr[3])
                  + String.format("%X:", maxAddr[4]) + String.format("%X", maxAddr[5]);
            
            System.out.println(ipAddr +" : "+macAddress);
            _APP.TotalArea.append("       " + ipAddr + "\t" + macAddress + "\t complete"+portName+"\n");
            System.out.println("완료!!!");
         }
		return;
	}

}
