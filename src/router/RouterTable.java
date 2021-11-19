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
}
