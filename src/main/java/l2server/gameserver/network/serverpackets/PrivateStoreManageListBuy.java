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

import l2server.gameserver.model.L2ItemInstance;
import l2server.gameserver.model.TradeList;
import l2server.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class ...
 *
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:40 $
 */
public class PrivateStoreManageListBuy extends L2ItemListPacket {
	private int objId;
	private long playerAdena;
	private L2ItemInstance[] itemList;
	private TradeList.TradeItem[] buyList;
	
	public PrivateStoreManageListBuy(L2PcInstance player) {
		objId = player.getObjectId();
		playerAdena = player.getAdena();
		itemList = player.getInventory().getUniqueItems(false, true);
		buyList = player.getBuyList().getItems();
	}
	
	@Override
	protected final void writeImpl() {
		//section 1
		writeD(objId);
		writeQ(playerAdena);
		
		//section2
		writeD(itemList.length); // inventory items for potential buy
		for (L2ItemInstance item : itemList) {
			writeItem(item);
			writeQ(item.getItem().getReferencePrice() * 2);
		}
		
		//section 3
		writeD(buyList.length); //count for all items already added for buy
		for (TradeList.TradeItem item : buyList) {
			writeItem(item);
			
			writeQ(item.getPrice());
			writeQ(item.getItem().getReferencePrice() * 2);
			writeQ(item.getCount());
		}
	}
}
