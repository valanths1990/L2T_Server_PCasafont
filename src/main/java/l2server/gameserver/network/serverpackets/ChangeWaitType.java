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

import l2server.gameserver.model.actor.L2Character;

/**
 * sample
 * <p>
 * 0000: 3f 2a 89 00 4c 01 00 00 00 0a 15 00 00 66 fe 00	?*..L........f..
 * 0010: 00 7c f1 ff ff									 .|...
 * <p>
 * format   dd ddd
 *
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:57 $
 */
public class ChangeWaitType extends L2GameServerPacket {
	private int charObjId;
	private int moveType;
	private int x, y, z;
	
	public static final int WT_SITTING = 0;
	public static final int WT_STANDING = 1;
	public static final int WT_START_FAKEDEATH = 2;
	public static final int WT_STOP_FAKEDEATH = 3;
	
	public ChangeWaitType(L2Character character, int newMoveType) {
		charObjId = character.getObjectId();
		moveType = newMoveType;
		
		x = character.getX();
		y = character.getY();
		z = character.getZ();
	}
	
	@Override
	protected final void writeImpl() {
		writeD(charObjId);
		writeD(moveType);
		writeD(x);
		writeD(y);
		writeD(z);
	}
}
