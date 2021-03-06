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

package l2server.gameserver.model;

/**
 * @author -Nemesiss-
 */
public class L2SummonItem {
	private final int itemId;
	private final int npcId;
	private final byte type;
	private final int despawnDelay;

	public L2SummonItem(int itemId, int npcId, byte type, int despawnDelay) {
		this.itemId = itemId;
		this.npcId = npcId;
		this.type = type;
		this.despawnDelay = despawnDelay;
	}

	public int getItemId() {
		return itemId;
	}

	public int getNpcId() {
		return npcId;
	}

	public byte getType() {
		return type;
	}

	public boolean isPetSummon() {
		return type == 1 || type == 2;
	}

	public int getDespawnDelay() {
		return despawnDelay;
	}
}
