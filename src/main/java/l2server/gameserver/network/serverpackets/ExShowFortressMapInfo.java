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

import l2server.gameserver.model.L2Spawn;
import l2server.gameserver.model.entity.Fort;

import java.util.List;

/**
 * @author KenM
 */
public class ExShowFortressMapInfo extends L2GameServerPacket {
	private final Fort fortress;
	
	public ExShowFortressMapInfo(Fort fortress) {
		this.fortress = fortress;
	}

    /*
	  @see l2server.gameserver.network.serverpackets.L2GameServerPacket#getType()
     */
	
	/**
	 * @see l2server.gameserver.network.serverpackets.L2GameServerPacket#writeImpl()
	 */
	@Override
	protected final void writeImpl() {
		writeD(fortress.getFortId());
		writeD(fortress.getSiege().getIsInProgress() ? 1 : 0); // fortress siege status
		writeD(fortress.getFortSize()); // barracks count
		
		List<L2Spawn> commanders = fortress.getCommanderSpawns();
		if (commanders != null && commanders.size() != 0 && fortress.getSiege().getIsInProgress()) {
			switch (commanders.size()) {
				case 3: {
					for (L2Spawn spawn : commanders) {
						if (isSpawned(spawn.getNpcId())) {
							writeD(0);
						} else {
							writeD(1);
						}
					}
					break;
				}
				case 4: // TODO: change 4 to 5 once control room supported
				{
					int count = 0;
					for (L2Spawn spawn : commanders) {
						count++;
						if (count == 4) {
							writeD(1); // TODO: control room emulated
						}
						if (isSpawned(spawn.getNpcId())) {
							writeD(0);
						} else {
							writeD(1);
						}
					}
					break;
				}
			}
		} else {
			for (int i = 0; i < fortress.getFortSize(); i++) {
				writeD(0);
			}
		}
	}
	
	/**
	 * @param npcId
	 * @return
	 */
	private boolean isSpawned(int npcId) {
		boolean ret = false;
		for (L2Spawn spawn : fortress.getCommanderSpawns()) {
			if (spawn.getNpcId() == npcId) {
				ret = true;
			}
		}
		return ret;
	}
}
