/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package l2server.gameserver.network.serverpackets;

import l2server.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author kerberos
 * JIV update 27.8.10
 */
public class ExStopMoveInAirShip extends L2GameServerPacket {
	private L2PcInstance activeChar;
	private int shipObjId;
	private int x, y, z, h;
	
	public ExStopMoveInAirShip(L2PcInstance player, int shipObjId) {
		activeChar = player;
		this.shipObjId = shipObjId;
		x = player.getInVehiclePosition().getX();
		y = player.getInVehiclePosition().getY();
		z = player.getInVehiclePosition().getZ();
		h = player.getHeading();
	}
	
	@Override
	protected final void writeImpl() {
		writeD(activeChar.getObjectId());
		writeD(shipObjId);
		writeD(x);
		writeD(y);
		writeD(z);
		writeD(h);
	}
}
