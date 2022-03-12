package mapwriter;

public class Common {

    public static int NONE_INDEX=0;
    public static int ALL_INDEX=1;
    public static String NONE_GROUP="none";
    public static String ALL_GROUP="all";



    public enum EnumMarkerActionType {
        ADD,     //send new marker to server storage from client
        EDIT,   //send edited marker to server storage from client
        DELETE, //delete marker from server storage
        LOAD,    //load marker from server storage to client storage when player join to world
        SEND    //send markers to server then player change storage from local to server
    }
    public enum EnumGroupActionType {
        ADD_SYSTEM_GROUP,
        EDITGROUP,
        CHANGEVISIBLE,
        CHANGEORDER,
        DELETE

    }
    public enum EnumServerActionType {
        SYNC    //request to load marker from server storage to client storage when player join to world

    }
}
