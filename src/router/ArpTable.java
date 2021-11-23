package router;

import java.util.HashMap;
import java.util.Iterator;

public class ArpTable {
	private HashMap<byte[], byte[]> arpCache;
	public ApplicationLayer app;

	public ArpTable() {
		arpCache = new HashMap<>();
	}

	// HashMap<byte[], byte[]> getArpCache()
	// Sending ARP Cache hashmap
	public HashMap<byte[], byte[]> getArpCache() {
		return arpCache;
	}

	// void setAppLayer(ApplicationLayer applayer)
	// To use ApplicationLayer's reload(), we need ApplicationLayer
	public void setAppLayer(ApplicationLayer applayer) {
		this.app = applayer;
	}

	// void allDelete()
	// Deleting all item
	public void allDelete() {
		arpCache.clear();
	}

	// byte[] checkARP(HashMap<byte[], byte[]> map, byte[] input)
	// checking if IP is in the ARP cache
	public byte[] checkARP(byte[] input) {
		try {
			Iterator<byte[]> keys = arpCache.keySet().iterator();
			while (keys.hasNext()) {
				byte[] key = keys.next();
				for (int i = 0; i < 4; i++) {
					if (input[i] == key[i]) {
						if (i == 3)
							return arpCache.get(key);
					} else
						break;
				}
			}
		} catch (NullPointerException e) {
			return null;
		}
		return null;
	}

	// - put
	// puts input(IP address) and Enet(Ethernet address) into arpCache.
	public void put(byte[] input, byte[] Enet) {
		// TODO Auto-generated method stub
		if (checkARP(input) == null) {
			this.arpCache.put(input, Enet);
		}
		//app.reload();  //애플리케이션 레이어에서 구현 필요함
	}

	// - remove
	// removes input(IP) from arpCache.
	public void remove(byte[] input) {
		this.arpCache.remove(input);
		//app.reload(); //에플리케이션 레이어서에서 구현 필요
	}
}