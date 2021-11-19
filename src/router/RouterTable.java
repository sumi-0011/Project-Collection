package router;

import java.util.ArrayList;



public class RouterTable {
	public ArrayList<routeEntry> entryList;	//routeEntry들을 저장해두는 리스트 
	
	//routing table 자료구조 클래스
	public class routeEntry {
		byte[] dst;
		byte[] netMask;		//subnet mask
		byte[] gateway;
		String flag;
		byte[] routeInterface;
		int metric;

		public routeEntry() {
			this.dst = new byte[4];
			this.netMask = new byte[4];
			this.gateway = new byte[4];
			this.flag = null;
			this.routeInterface = new byte[4];
			this.metric = 0;
		}
	}
	// RouterTable Constructor (생성자)
	public RouterTable() {
		entryList = new ArrayList<>();
	}
	
	//routeEntry add
	public boolean addEntry(byte[] dst, byte[] netMask, byte[] gateway, String flag, byte[] routeInterface) {
		routeEntry newRouteEntry = new routeEntry();	//새로운 routerEntry 객체 생성
		// 생성한 routerEntry객체에 매개변수로 받아온 값을 세팅해준다. 
		newRouteEntry.dst = dst;
		newRouteEntry.netMask = netMask;
		newRouteEntry.gateway = gateway;
		newRouteEntry.flag = flag;
		newRouteEntry.routeInterface = routeInterface;
		
		entryList.add(newRouteEntry);	//생성한 routerEntry를 routerList에 추가
		
		return true;
	}
	
	// routing table의 모든 element를 제거한다. clear
	public boolean removeAll() {
		entryList.clear(); 	//entryList를 비워서 모든 routerEntry정보를 삭제한다. => routerTable을 비운다. 
		return true;
	}
	
	//해당 패킷을 전달할 네트워크 주소를 알아낸다.
	// findInterface와 같은거예요
	public byte[] findSendingNetwork(byte[] dstIP) {
		byte[] result = null;
		
		for (int i=0; i< entryList.size();i++) {	//entryList의 모든 요소를 하나씩 다 돌려가면서
			routeEntry temp = entryList.get(i);		//entryList의 i번째 요소를 temp에 저장 (routeEntry 객체)
			boolean check = masking(temp,dstIP);	//temp랑 dstIP와 subnetMask를 &한 값이 같은지 확인
			
			if(check) {	//matching되면
				if(temp.flag.contains("H")) {
					// flag가 H인지 확인 : Host와 direct connection이 되어있는지 확인되면
					result = temp.routeInterface;
				}
				
			}
			
		}
		
		return result;
	}

	public boolean masking(routeEntry entry, byte[] dstIP) {
		boolean check = true;
		for (int j = 0; j < 4; j++) {
			if (entry.dst[j] != (dstIP[j] & entry.netMask[j])) {
				check = false;
			}
		}
		return check;
	}
}
