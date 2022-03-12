package mapwriter.map;

import java.util.*;

import mapwriter.Mw;
import mapwriter.MwUtil;
import mapwriter.forge.MwConfig;
import mapwriter.map.mapmode.MapMode;
import mapwriter.Common;
import mapwriter.server.networkPackets.ClientToServer.ClientGroupsPacket;
import mapwriter.server.networkPackets.ClientToServer.ClientMarkersPacket;
import org.apache.commons.lang3.StringUtils;


public class MarkerManager {


	Mw mw;

	//private List<Marker> markerList = new ArrayList<Marker>();

	//Key in Hashmap markerLis is a uniq hash(murmurhashV3 algo ), for search and sync client<->server
	private HashMap<Integer, Marker> markerList=new HashMap<Integer, Marker>();
	private List<String> orderedGroupList = new ArrayList<String>();
	private List<Integer> groupOrder =new ArrayList<Integer>();

	// groupName as Key, groupIndex as Value
	private HashMap<String,Integer> groupList = new HashMap<String,Integer>();



	public HashMap<String, HashMap<Integer,UserPresetMarker> > userPresetMarker=new HashMap<String, HashMap<Integer,UserPresetMarker>>();
	public HashMap<Integer, String> userPresetGroup=new HashMap<Integer, String>();

	private List<Marker> visibleMarkerList = new ArrayList<Marker>();
	
	private int  visibleGroupIndex =0;
	
	public Marker selectedMarker = null;

	public int selectedColor;
	
	public MarkerManager(Mw mw) {
		this.mw=mw;
		//add system group "all" & "none" to group list
		this.groupList.put(Common.NONE_GROUP,Common.NONE_INDEX);
		this.groupList.put(Common.ALL_GROUP,Common.ALL_INDEX);

		//add system group "all" & "none" to group order list
		this.groupOrder.add(Common.NONE_INDEX);
		this.groupOrder.add(Common.ALL_INDEX);

		if(this.mw.isMwOnServerWorks && this.mw.saveMarkersOnServer==1){
			new ClientGroupsPacket(Common.EnumGroupActionType.ADD_SYSTEM_GROUP).sendToServer();
		}

		//this.updateOrderedGroupList();
	}


	public void load(MwConfig config, String category) {
		this.markerList.clear();
		//load info
		if (config.hasCategory("groups")){
			//new versions markers and group info in file
			this.mw.newVersionMarkers=true;

			int groupsCount = config.get("groups", "groupsCount", 0).getInt();
			this.visibleGroupIndex = config.get("groups", "visibleGroup", "").getInt();
			this.groupOrder.clear();

			String orderGroupValue=config.get("groups","orderGroups","").getString();
			String[] orderGroupList = orderGroupValue.split(":");

			for(int i=0;i<orderGroupList.length; i++){
				this.groupOrder.add(Integer.parseInt(orderGroupList[i]));
			}


			if(groupsCount>0){
				this.groupList.clear();


				for (int i = 0; i < groupsCount; i++) {
					String value=config.get("groups","group"+i,"").getString();
					String[] split = value.split(":");
					if(split.length==2){
						this.groupList.put(split[0],Integer.parseInt(split[1]));
					}

				}
			}else {
				this.groupList.put(Common.NONE_GROUP,Common.NONE_INDEX);
				this.groupList.put(Common.ALL_GROUP,Common.ALL_INDEX);
			}





		}

		if (config.hasCategory(category)) {
			int markerCount = config.get(category, "markerCount", 0).getInt();

			
			if (markerCount > 0) {
				for (int i = 0; i < markerCount; i++) {
					String key = "marker" + i;
					String value = config.get(category, key, "").getString();
					Marker marker = this.stringToMarker(value);
					if (marker != null) {
						this.addMarker(marker);
					} else {
						MwUtil.log("error: could not load " + key + " from config file");
					}
				}
			}
		}

		this.update();

	}


	public void save(MwConfig config, String category) {
		config.getCategory(category).clear();
		config.get(category, "markerCount", 0).set(this.markerList.size());

		//save markers
		int i = 0;
		for(Map.Entry<Integer, Marker> entry : this.markerList.entrySet() ){
			String key = "marker" + i;
			String value = this.markerToString(entry.getValue());
			config.get(category, key, "").set(value);
			i++;
		}

		//save groups
		i=0;
		config.get("groups","groupsCount",0).set(this.groupList.size());

		for(Map.Entry<String,Integer> groupEntry: this.groupList.entrySet()){
			config.get("groups","group"+i,"").set(groupEntry.getKey()+":"+groupEntry.getValue());
			i++;
		}
		config.get("groups", "visibleGroup", 0).set(this.visibleGroupIndex);
		config.get("groups","orderGroups","0:1").set(StringUtils.join(this.groupOrder,":"));





	}

	public void loadPresetGroup(MwConfig config, String category){

		this.userPresetGroup.clear();

		if (config.hasCategory(category)) {

			int presetGroupCount = config.get(category, "presetGroupCount", 0).getInt();

			//load preset group from config to HashMap
			for (int i = 0; i < presetGroupCount; i++) {
				String key = "presetGroup" + i;
				if (config.get(category, key, "").getString().length() != 0) {
					String value = config.get(category, key, "").getString();
					String[] split = value.split(":");
					try {
						this.userPresetGroup.put(Integer.parseInt(split[1]), split[0]);
					} catch (NumberFormatException e) {
					}
				}
			}
		}

	}

	/*
	public void loadPresetMarkers(MwConfig config, String category){

		this.userPresetMarker.clear();
		List<UserPresetMarker> presetMarkerList=new ArrayList<UserPresetMarker>();
		List< List <UserPresetMarker> > tempList= new ArrayList<List<UserPresetMarker>>();

		for(int i=0; i<10; i++){
			tempList.add(new ArrayList<UserPresetMarker>());
		}



		//load preset marker from config to temp ArrayList
		if (config.hasCategory(category)) {

			int presetMarkerCount = config.get(category, "presetMarkerCount", 0).getInt();
			if (presetMarkerCount > 0) {
				for (int i = 0; i < presetMarkerCount; i++) {
					String key = "presetMarker" + i;
					String value = config.get(category, key, "").getString();
					UserPresetMarker presetMarker = this.stringToPresetMarker(value);
					if (presetMarker != null) {
						String presetGroupName=presetMarker.getPresetGroup();
						int groupIndex=presetMarker.getGroupNumber();
						int markerIndex=presetMarker.getMarkerNumber();

						if(this.userPresetMarker.containsKey(presetGroupName)){
							this.userPresetMarker.get(presetGroupName).put(markerIndex,presetMarker);

						}else {
							this.userPresetMarker.put(presetGroupName,new HashMap<Integer, UserPresetMarker>());
							this.userPresetMarker.get(presetGroupName).put(markerIndex,presetMarker);
						}
					} else {
						MwUtil.log("error: could not load " + key + " from config file");
					}
				}
			}


		}

	}*/

	public void savePresetGroups(MwConfig config, String category){

		config.getCategory(category).clear();
		config.get(category, "presetGroupCount", 0).set(userPresetGroup.size());
		int j=0;
		for(Map.Entry<Integer, String> groupMapEntry : this.userPresetGroup.entrySet()){
			String key="presetGroup"+j;
			String value=groupMapEntry.getValue()+":"+String.valueOf(groupMapEntry.getKey());
			config.get(category, key, "").set(value);
			j++;
		}

	}
	/*
	public void savePresetMarkers(MwConfig config, String category){

		List<UserPresetMarker> presetMarkerListForSave=new ArrayList<UserPresetMarker>();
		for(Map.Entry<String, HashMap<Integer,UserPresetMarker> > mapEntry : this.userPresetMarker.entrySet()){
			for(Map.Entry<Integer, UserPresetMarker>  markersMapEntry : mapEntry.getValue().entrySet()){
				presetMarkerListForSave.add(markersMapEntry.getValue());
			}
		}
		config.getCategory(category).clear();
		config.get(category, "presetMarkerCount", 0).set(presetMarkerListForSave.size());
		int i = 0;
		for (UserPresetMarker presetMarker : presetMarkerListForSave) {
			String key = "presetMarker" + i;
			String value = this.presetMarkerToString(presetMarker);
			config.get(category, key, "").set(value);
			i++;
		}

	}

	 */

	/*
	public void saveMarkersToFile(){
		this.save(this.mw.worldConfig, this.mw.getCatMarkers());
		this.mw.saveWorldConfig();
	}*/
	
	public void setVisibleGroupIndex(int groupIndex) {
		this.visibleGroupIndex=groupIndex;
		//add new visible group index to server storage
		if(this.mw.isMwOnServerWorks && this.mw.saveMarkersOnServer==1){
			new ClientGroupsPacket(Common.EnumGroupActionType.CHANGEVISIBLE,this.visibleGroupIndex ).sendToServer();
		}


	}

	public int getVisibleGroupIndex(){ return this.visibleGroupIndex; }
	public String getVisibleGroupName() {
		//if(this.visibleGroupIndex==NONE_INDEX){
		//	return this.getGroupNameFromIndex(ALL_INDEX);
		//}
		return this.getGroupNameFromIndex(this.visibleGroupIndex);
	}

	public List<Marker> getVisibleMarkerList(){ return visibleMarkerList; }
	
	public void clear() {
		/*
		this.markerList.clear();
		this.groupList.clear();
		this.visibleMarkerList.clear();
		this.visibleGroupName = "none";

		 */
	}

	public String presetMarkerToString(UserPresetMarker marker) {
		return String.format("%s:%s:%06x:%d:%d",
				marker.getPresetGroup(),
				marker.getPresetMarkerName(),
				marker.getColor() & 0xffffff,
				marker.getGroupNumber(),
				marker.getMarkerNumber()

		);
	}

/*
	public UserPresetMarker stringToPresetMarker(String s) {

		String[] split = s.split(":");

		UserPresetMarker marker = null;
		if (split.length == 5) {
			try {
				int color = 0xff000000 | Integer.parseInt(split[2], 16);

				marker = new UserPresetMarker(split[0],split[1], color,Integer.parseInt(split[3]),Integer.parseInt(split[4]));

			} catch (NumberFormatException e) {
				marker = null;
			}
		} else {
			MwUtil.log("Marker.stringToMarker: invalid preset marker '%s'", s);
		}
		return marker;


	}
*/

	public String markerToString(Marker marker) {

		return String.format("%s:%d:%d:%d:%d:%06x:%s",
			marker.getMarkerName(),
			marker.getPosX(),
			marker.getPosY(),
			marker.getPosZ(),
			marker.getDimension(),
			marker.getColour() & 0xffffff,
			marker.getGroupIndex()
		);
	}

	public Marker stringToMarker(String s) {
		// new style delimited with colons
		String[] split = s.split(":");
		if (split.length != 7) {
			// old style was space delimited
			split = s.split(" ");
		}
		Marker marker = null;
		if (split.length == 7) {
			try {
				int x = Integer.parseInt(split[1]);
				int y = Integer.parseInt(split[2]);
				int z = Integer.parseInt(split[3]);
				int dimension = Integer.parseInt(split[4]);
				int colour = 0xff000000 | Integer.parseInt(split[5], 16);
				int markerIndex=0;
				//if load markers from previous versions, convert markers and groups
				if(!this.mw.newVersionMarkers){
					//convert markers and group info from previous version
					markerIndex=addGroupToList(split[6]);

				}else {
					markerIndex=Integer.parseInt(split[6]);

				}
				
				marker = new Marker(split[0], markerIndex, x, y, z, dimension, colour);
				
			} catch (NumberFormatException e) {
				marker = null;
			}
		} else {
			MwUtil.log("Marker.stringToMarker: invalid marker '%s'", s);
		}
		return marker;
	}

	public HashMap<Integer,Marker> getMarkerList() { return markerList; }

	public List<Integer> getGroupOrder() { return groupOrder; }

	public void setGroupOrder(List<Integer> groupOrderList){
		groupOrder.clear();
		groupOrder.addAll(groupOrderList);
	}


	public HashMap<String,Integer> getGroupList(){ return groupList; }

	public void setGroupList(HashMap<String,Integer> groupListMap){
		groupList.clear();
		groupList.putAll(groupListMap);
	}


	public int getGroupIndex(String groupName){
		return this.groupList.containsKey(groupName) ? this.groupList.get(groupName): -1;
	}

	public String getGroupNameFromIndex(Integer index){
		for(String getKey: this.groupList.keySet()) {
			if (this.groupList.get(getKey).equals(index)) {
				return getKey;
			}
		}
		return "";
	}

	public boolean groupNameExists(String groupName){
		return this.groupList.containsKey(groupName);
			}

	public int addGroupToList(String groupName){
		if(!this.groupList.containsKey(groupName)){
			//add new group to group list on client storage
			this.groupList.put(groupName,this.groupList.size());
			this.groupOrder.add(groupList.get(groupName));

			//add new group and order group to server storage
			if(this.mw.isMwOnServerWorks && this.mw.saveMarkersOnServer==1){
				new ClientGroupsPacket(Common.EnumGroupActionType.EDITGROUP, this.groupList,this.groupOrder).sendToServer();
			}


			this.updateOrderedGroupList();
		}
		return  this.groupList.get(groupName);
	}

	public void addMarkerToVisibleGroup(Marker marker){
		if (!this.visibleMarkerList.contains(marker) && this.visibleGroupIndex==marker.getGroupIndex()){
			this.visibleMarkerList.add(marker);

		}
	}

	public void addMarker(Marker marker){
		//add marker to client`s storage
		this.markerList.put(MwUtil.getHashFromMarker(marker),marker);
		//add marker to server`s storage(if works on server side
		if(this.mw.isMwOnServerWorks && this.mw.saveMarkersOnServer==1){
			new ClientMarkersPacket(Common.EnumMarkerActionType.ADD,
					MwUtil.getHashFromMarker(marker),
					marker.convertMarkerToArray(marker) ).sendToServer();
			}

	}

	public void editMarker(int oldHash,Marker changedMarker){
		this.markerList.remove(oldHash);
		this.markerList.put(MwUtil.getHashFromMarker(changedMarker),changedMarker);
		//send edited marker to server`s storage(if works on server side)
		if(this.mw.isMwOnServerWorks && this.mw.saveMarkersOnServer==1){
			new ClientMarkersPacket(Common.EnumMarkerActionType.EDIT,
					oldHash,
					MwUtil.getHashFromMarker(changedMarker),
					changedMarker.convertMarkerToArray(changedMarker) ).sendToServer();
		}
	}
	public void sendAllMarkersToServer(){
		HashMap<Integer, List<String>> allMarkersData = new HashMap<Integer, List<String>>();
		for(Map.Entry<Integer, Marker> markerEntry: this.markerList.entrySet()){
			allMarkersData.put(markerEntry.getKey(),markerEntry.getValue().convertMarkerToArray(markerEntry.getValue()));
		}


		new ClientGroupsPacket(Common.EnumGroupActionType.DELETE, groupList, groupOrder,visibleGroupIndex).sendToServer();

		new ClientMarkersPacket(Common.EnumMarkerActionType.SEND,allMarkersData).sendToServer();


	}
	public void addMarker(String name, int groupIndex, int x, int y, int z, int dimension, int colour) {
		this.addMarker(new Marker(name, groupIndex, x, y, z, dimension, colour));
	}
	
	// returns true if the marker exists in the arraylist.
	// safe to pass null.
	public void delMarker(Marker markerToDelete) {
		//delete marker in local storage
		this.markerList.remove(MwUtil.getHashFromMarker(markerToDelete));

		//delete marker in server storage(if works on server side)
		if(this.mw.isMwOnServerWorks && this.mw.saveMarkersOnServer==1){
			new ClientMarkersPacket(Common.EnumMarkerActionType.DELETE,
									MwUtil.getHashFromMarker(markerToDelete)).sendToServer();
		}

		update();
		if(isGroupEmpty(markerToDelete.getGroupIndex())){
			//group is empty
			delEmptyGroup(markerToDelete.getGroupIndex());
		}
	}
	
	// deletes the first marker with matching name and group.
	// if null is passed as either name or group it means "any".
	public void delMarker(String name, String group) {

		Marker markerToDelete = null;
		for (Map.Entry<Integer,Marker> markerEntry : markerList.entrySet() ) {
			if (((name == null) || markerEntry.getValue().getMarkerName().equals(name)) &&
				((group == null) || markerEntry.getValue().getGroupIndex()== this.getGroupIndex(group)) ) {
				markerToDelete = markerEntry.getValue();
				break;
			}
		}
		if(markerToDelete!=null){
			this.delMarker(markerToDelete);
		}

	}

	public boolean isGroupEmpty(int groupIndex){

		return countMarkersInGroup(groupIndex) == 0 &&
				(groupIndex != Common.ALL_INDEX || groupIndex != Common.NONE_INDEX);
	}


	public void delEmptyGroup(int groupIndex) {

		//delete empty group in local storage
		groupList.remove(getGroupNameFromIndex(groupIndex));
		groupOrder.remove(groupIndex);

		//delete empty group in server storage
		if(this.mw.isMwOnServerWorks && this.mw.saveMarkersOnServer==1){
			new ClientGroupsPacket(Common.EnumGroupActionType.DELETE, groupList, groupOrder).sendToServer();
			updateOrderedGroupList();
			if(groupIndex==visibleGroupIndex){

				new ClientGroupsPacket(Common.EnumGroupActionType.CHANGEVISIBLE,Common.ALL_INDEX);
				visibleGroupIndex = Common.ALL_INDEX;
			}
		}

	}
	
	public void renameGroup(String oldGroupName, String newGroupName){

		if(!this.groupList.containsKey(newGroupName)){
			this.groupList.put(newGroupName,this.groupList.get(oldGroupName));
		}
		this.updateMarkersGroup(getGroupIndex(oldGroupName),getGroupIndex(newGroupName));
		this.groupList.remove(oldGroupName);
		this.delEmptyGroupFromOrderGroup();
		//send  group and order group to server storage
		if(this.mw.isMwOnServerWorks && this.mw.saveMarkersOnServer==1){
			new ClientGroupsPacket(Common.EnumGroupActionType.EDITGROUP, this.groupList).sendToServer();
		}
		this.updateOrderedGroupList();

	}
	public void update() {

		this.visibleMarkerList.clear();

		for(Map.Entry<Integer,Marker> markerEntry:markerList.entrySet()){
			if(this.visibleGroupIndex!=Common.NONE_INDEX){
				if (markerEntry.getValue().getGroupIndex()==this.visibleGroupIndex || this.visibleGroupIndex==Common.ALL_INDEX) {
					this.visibleMarkerList.add(markerEntry.getValue());
				}

			}

		}


	}

	public void delEmptyGroupFromOrderGroup(){
		int index = -1;
		for(int i=0;i< groupOrder.size();i++){
			if(!groupList.containsValue(groupOrder.get(i))){
				index=i;
				break;
			}
		}
		if(index!=-1) {
			groupOrder.remove(index);
		}
	}

	public void updateMarkersGroup(int oldGroupIndex, int newGroupIndex){

		for(Map.Entry<Integer,Marker> markerEntry:markerList.entrySet()){
			if(markerEntry.getValue().getGroupIndex()==oldGroupIndex){
				markerEntry.getValue().setGroupIndex(newGroupIndex);
			}
		}

	}

	public void updateOrderedGroupList(){
		orderedGroupList.clear();
		for(int element:groupOrder){
			orderedGroupList.add(getGroupNameFromIndex(element));
		}

		//add new order group to server storage
		if(this.mw.isMwOnServerWorks && this.mw.saveMarkersOnServer==1){
			new ClientGroupsPacket(Common.EnumGroupActionType.CHANGEORDER,this.groupOrder).sendToServer();
		}

	}

	//change order group, active group index - index selected group in order group list
	// index=1 - move group one level up
	// index=-1 - move group one leve down
	public void changeGroupOrder(int selectedGroupIndex,int index){

		//move the selected element one level up
		if (index == 1) {
			// one level up
			if(selectedGroupIndex>0) {
				Collections.swap(groupOrder, selectedGroupIndex, selectedGroupIndex - 1);

			}

		}else if(index==-1){
			// one level down
			if(selectedGroupIndex<groupOrder.size()-1) {
				Collections.swap(this.groupOrder, selectedGroupIndex, selectedGroupIndex + 1);

			}
		}
		updateOrderedGroupList();

	}

	public List<String> getOrderedGroupList(){
		return orderedGroupList;
	}

	public void nextGroup(int n) {

		if (this.orderedGroupList.size() > 0) {
			int i = this.orderedGroupList.indexOf(this.getGroupNameFromIndex(this.visibleGroupIndex));
			int size = this.orderedGroupList.size();
			if (i != -1) {
				i = (i + size + n) % size;
			} else {
				i = 0;
			}
			this.visibleGroupIndex = this.getGroupIndex(this.orderedGroupList.get(i));
		} //else {
		//	this.visibleGroupName = "none";
		//	this.groupList.add("none");
	   //}

	}

	public void nextGroup() {
		this.nextGroup(1);
	}

	public void prevGroup(int n) {	this.nextGroup(n);	}

	public void prevGroup() {
		this.nextGroup(-1);
	}

	public int countMarkersInGroup(int groupIndex) {

		int count = 0;
		if (groupIndex==Common.ALL_INDEX) {
			count = this.markerList.size();
		} else {
			for(Map.Entry<Integer,Marker> markerEntry:markerList.entrySet()) {
				if (markerEntry.getValue().getGroupIndex() == groupIndex) {
					count++;
				}
			}
		}
		return count;
	}
	
	public void selectNextMarker() {
		if (this.visibleMarkerList.size() > 0) {
        	int i = 0;
        	if (this.selectedMarker != null) {
        		i = this.visibleMarkerList.indexOf(this.selectedMarker);
        		if (i == -1) {
        			i = 0;
        		}
        	}
        	i = (i + 1) % this.visibleMarkerList.size();
        	this.selectedMarker = this.visibleMarkerList.get(i);
    	} else {
    		this.selectedMarker = null;
    	}
	}
	
	public Marker getNearestMarker(int x, int z, int maxDistance) {
		int nearestDistance = maxDistance * maxDistance;
		Marker nearestMarker = null;
		for (Marker marker : this.visibleMarkerList) {
			int dx = x - marker.getPosX();
			int dz = z - marker.getPosZ();
			int d = (dx * dx) + (dz * dz);
			if (d < nearestDistance) {
				nearestMarker = marker;
				nearestDistance = d;
			}
		}
		return nearestMarker;
	}
	
	public Marker getNearestMarkerInDirection(int x, int z, double desiredAngle) {
		int nearestDistance = 10000 * 10000;
		Marker nearestMarker = null;
		for (Marker marker : this.visibleMarkerList) {
			int dx = marker.getPosX() - x;
			int dz = marker.getPosZ() - z;
			int d = (dx * dx) + (dz * dz);
			double angle = Math.atan2(dz, dx);
			// use cos instead of abs as it will wrap at 2 * Pi.
			// cos will be closer to 1.0 the closer desiredAngle and angle are.
			// 0.8 is the threshold corresponding to a maximum of
			// acos(0.8) = 37 degrees difference between the two angles.
			if ((Math.cos(desiredAngle - angle) > 0.8D) && (d < nearestDistance) && (d > 4)) {
				nearestMarker = marker;
				nearestDistance = d;
			}
		}
		return nearestMarker;
	}
	
	public void drawMarkers(MapMode mapMode, MapView mapView) {
		for (Marker marker : this.visibleMarkerList) {
	    	// only draw markers that were set in the current dimension
			if (mapView.getDimension() == marker.getDimension()) {
				marker.draw(mapMode, mapView, 0xff000000);
			}
		}
		if (this.selectedMarker != null) {
			this.selectedMarker.draw(mapMode, mapView, 0xffffffff);
		}
	}
}
