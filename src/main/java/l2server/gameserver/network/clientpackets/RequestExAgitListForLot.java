package l2server.gameserver.network.clientpackets;

import l2server.log.Log;

/**
 * @author MegaParzor!
 */
public class RequestExAgitListForLot extends L2GameClientPacket {
	@Override
	public void readImpl() {
	}
	
	@Override
	public void runImpl() {
		// TODO
		Log.info(getType() + " packet was received from " + getClient() + ".");
	}
}
