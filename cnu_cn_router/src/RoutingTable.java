import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RoutingTable {
	
	
   public static class CustomizedHashMap implements Comparator<Map.Entry<Integer, HashMap<String,Object[]>>> {
      @Override
      public int compare(Entry<Integer, HashMap<String,Object[]>> o1, Entry<Integer, HashMap<String,Object[]>> o2) {
         // TODO Auto-generated method stub
         return -o1.getKey().compareTo(o2.getKey());
      }
   }


   public static Map<Integer,HashMap<String,Object[]>> rounterTable;
   public HashMap<String,Object[]> head;

   public static List<Map.Entry<Integer, HashMap<String,Object[]>>> entries;

   public RoutingTable() {
	   rounterTable = new HashMap<Integer,HashMap<String,Object[]>>();
   }

   public static void add(String key, Object[] value) {

      HashMap<String,Object[]> head = null;
      byte[] netmask = (byte[]) value[1];
      int tempNetmask = NetmaskCompute(netmask);


      if(rounterTable.containsKey(tempNetmask)) {
         head = rounterTable.get(tempNetmask);

         head.put(key, value);

      }else {
    	  rounterTable.put(tempNetmask, new HashMap<String,Object[]>());
         head = rounterTable.get(tempNetmask);
         head.put(key, value);
         Sorting();

      }
   }

   public static void Sorting() {
      entries = new ArrayList<Map.Entry<Integer, HashMap<String,Object[]>>>(rounterTable.entrySet());
      Collections.sort(entries, new CustomizedHashMap());

   }

   public static int NetmaskCompute(byte[] netmask) {
      int cnt=0;

      for(int i=0;i<4;i++) {
         if((netmask[i]&0xFF) == 255) {
        	 cnt += 8;
         }
         else {
            int n= (netmask[i]&0xFF);
            while(n!=0) {
               cnt+=n%2;
               n/=2;
            }
         }
      }

      return cnt;
   }

   public Object[] findEntry(byte[] dst) {
	  long time = System.currentTimeMillis();
      if(entries == null) return null;

      for(Map.Entry<Integer, HashMap<String,Object[]>> entry : entries) {

         HashMap<String, Object[]> getMap = entry.getValue();
         HashMap.Entry<String, Object[]> nodeEntry = getMap.entrySet().iterator().next();
         byte[] netmask = (byte[])(nodeEntry.getValue()[1]);

         // Masking
         byte[] maskingResult = new byte[4];
         maskingResult[0]=(byte) (dst[0]&netmask[0]);
         maskingResult[1]=(byte) (dst[1]&netmask[1]);
         maskingResult[2]=(byte) (dst[2]&netmask[2]);
         maskingResult[3]=(byte) (dst[3]&netmask[3]);

         String maskingResult2String = (maskingResult[0]&0xFF)+"."+(maskingResult[1]&0xFF)+"."+(maskingResult[2]&0xFF)+"."+(maskingResult[3]&0xFF);
         // if Destination IP == Masking Result
         if(getMap.containsKey(maskingResult2String)) {
        	 long time2 = System.currentTimeMillis();
             System.out.println("hi : " + (time2 - time));
            return getMap.get(maskingResult2String);
         }

      }
      
      return null;
   }

   public boolean remove(Object[] value) {
      byte[] netmask = (byte[]) value[1];
      int tempNum = NetmaskCompute(netmask);

      HashMap<String,Object[]> head = rounterTable.get(tempNum);
      if(head==null) return false;


      byte[] valueDestIP = (byte[]) value[0];
      String valueDestIPString = (valueDestIP[0] & 0xFF) + "." + (valueDestIP[1] & 0xFF) + "."
            + (valueDestIP[2] & 0xFF) + "." + (valueDestIP[3] & 0xFF);

      HashMap<String, Object[]> getMap = head;
      if(getMap.containsKey(valueDestIPString)) {
         if(getMap.size()==1) {
        	 rounterTable.remove(tempNum);
            Sorting();
         }else {
            getMap.remove(valueDestIPString);
         }
         return true;
      }

      return false;
   }



   public static String updateRoutingTable() {
      String printResult = "";

      for(Entry<Integer, HashMap<String,Object[]>> entry : entries) {

         HashMap<String,Object[]> head = entry.getValue();
         HashMap<String,Object[]> map = head;

         for ( String key : head.keySet() ) {

            Object[] value = map.get(key);

            byte[] netmask = (byte[])value[1];
            byte[] gateway_Byte = (byte[])value[2];

            String destIP_String = key;
            String mask_String = "";
            String gateway_String = "";

            for (int j = 0; j < 3; j++) {

               mask_String = mask_String + (netmask[j]&0xFF)+".";
               gateway_String = gateway_String + (gateway_Byte[j]&0xFF)+".";
            }
            mask_String = mask_String + (netmask[3]&0xFF);
            gateway_String = gateway_String + (gateway_Byte[3]&0xFF);

            String flag_String = "";
            String interface_String = value[6] + "";

            if ((boolean) value[3]) {
               flag_String += "U";
            }
            if ((boolean) value[4]) {
               flag_String += "G";
            }
            if ((boolean) value[5]) {
               flag_String += "H";
            }

            printResult = printResult + "    " +destIP_String + "    " + mask_String + "      " + gateway_String + "         " + flag_String
                  + "        " + interface_String + "\n";

         }


      }
      return printResult;
   }


}
