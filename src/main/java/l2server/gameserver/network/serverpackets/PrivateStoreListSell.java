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

import l2server.gameserver.model.TradeList;
import l2server.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class ...
 *
 * @version $Revision: 1.2.2.3.2.6 $ $Date: 2005/03/27 15:29:57 $
 */
public class PrivateStoreListSell extends L2ItemListPacket {
	private int objId;
	private long playerAdena;
	private boolean packageSale;
	private TradeList.TradeItem[] items;
	
	// player's private shop
	public PrivateStoreListSell(L2PcInstance player, L2PcInstance storePlayer) {
		objId = storePlayer.getObjectId();
		playerAdena = player.getAdena();
		items = storePlayer.getSellList().getItems();
		packageSale = storePlayer.getSellList().isPackaged();
	}
	
	@Override
	protected final void writeImpl() {
		writeD(objId);
		writeD(packageSale ? 1 : 0);
		writeQ(playerAdena);
		writeD(0x00); // GoD ???
		writeD(items.length);
		for (TradeList.TradeItem item : items) {
			writeItem(item);
			
			writeQ(item.getPrice());
			writeQ(item.getItem().getReferencePrice() * 2);
		}
	}
}
