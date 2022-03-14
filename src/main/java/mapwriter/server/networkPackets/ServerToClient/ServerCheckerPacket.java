package mapwriter.server.networkPackets.ServerToClient;

import hohserg.elegant.networking.api.ElegantPacket;
import hohserg.elegant.networking.api.ServerToClientPacket;
import mapwriter.Mw;
import net.minecraft.client.Minecraft;

@ElegantPacket
public class ServerCheckerPacket implements ServerToClientPacket {
    final boolean mwServer;


    public ServerCheckerPacket(boolean mwServerFlag) {
        this.mwServer=mwServerFlag;

    }

    @Override
    public void onReceive(Minecraft mc) {
        //sets the flag that the server is installed and running server`s MapWriter
        //(for save markers on server storage)


        if(mc.theWorld.isRemote){
            Mw.instance.isMwOnServerWorks=true;
            System.out.println("FLAG set");
        }

    }
}