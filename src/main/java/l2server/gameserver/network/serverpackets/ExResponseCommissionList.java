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
 * @author Erlandys
 */
public class ExResponseCommissionList extends L2GameServerPacket {
	
	@SuppressWarnings("unused")
	private L2PcInstance player;
	@SuppressWarnings("unused")
	private long category;
	@SuppressWarnings("unused")
	private int type;
	@SuppressWarnings("unused")
	private int grade;
	@SuppressWarnings("unused")
	private String search;
	@SuppressWarnings("unused")
	private boolean yourAuction;
	@SuppressWarnings("unused")
	private int yourAuctionsSize = 0;
	@SuppressWarnings("unused")
	private int categories[][] =
			{{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18}, {19, 20, 21, 22, 23, 24, 25, 26, 27, 28}, {29, 30, 31, 32, 33, 34},
					{35, 36, 37, 38, 39, 40}, {41, 42}, {43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58}};
	
	public ExResponseCommissionList(L2PcInstance player, long category, int type, int grade, String searchName) {
		this.player = player;
		this.category = category;
		this.type = type;
		this.grade = grade;
		search = searchName;
		yourAuction = false;
	}
	
	public ExResponseCommissionList(L2PcInstance player) {
		this.player = player;
		yourAuction = true;
	}
	
	@Override
	protected void writeImpl() {
		/*writeC(0xFE);
        writeH(0xF7);
		if (yourAuction)
		{
			writeD(yourAuctionsSize <= 0 ? -2 : 0x02);
			writeD((int)(System.currentTimeMillis()/1000));
			writeD(0x00);
			writeD(yourAuctionsSize);
			for (Auctions auction : am.getAuctions())
				if (auction.getPlayerID() == player.getObjectId())
					writeItems(auction);
		}
		else
		{
			writeD(search != null && category == 100 && am.getAuctionsSizeById(grade,  search) > 0 ? 3 : am.getAuctionsSizeById(grade,  search) <= 0 || am.getAuctionsSizeById(category, grade, search) <= 0 ? -1 : 3);
			writeD((int)(System.currentTimeMillis()/1000));
			writeD(0x00);
			if (category > 60 && category < 66 || category == 101)
			{
				writeD(am.getAuctionsSizeById(category, grade, search));
				for (Auctions auction : am.getAuctions())
				{
					int cat = category == 101 ? 0 : (int)(category%60);
					for (int ID : categories[cat])
					{
						if (grade == -1 && search.equals(""))
						{
							if (auction.getCategory() == ID)
								writeItems(auction);
						}
						else if (grade != -1)
						{
							if (search.equals(""))
								if (auction.getCategory() == ID && grade == auction.getItem().getItem().getCrystalType())
									writeItems(auction);
							if (!search.equals(""))
								if (auction.getCategory() == ID && grade == auction.getItem().getItem().getCrystalType() && auction.getItem().getName().contains(search))
									writeItems(auction);
						}
						else if (!search.equals(""))
							if (auction.getCategory() == ID && auction.getItem().getName().contains(search))
								writeItems(auction);
					}
				}
			}
			else if (category < 60)
			{
				writeD(am.getAuctionsSizeById(category, grade, search)); // Auction count, maybe items putted in auction???
				for (Auctions auction : am.getAuctions())
				{
					if (grade == -1 && search.equals(""))
					{
						if (auction.getCategory() == category)
							writeItems(auction);
					}
					else if (grade != -1)
					{
						if (search.equals(""))
							if (auction.getCategory() == category && grade == auction.getItem().getItem().getCrystalType())
								writeItems(auction);
						if (!search.equals(""))
							if (auction.getCategory() == category && grade == auction.getItem().getItem().getCrystalType() && auction.getItem().getName().contains(search))
								writeItems(auction);
					}
					else if (!search.equals(""))
						if (auction.getCategory() == category && auction.getItem().getName().contains(search))
							writeItems(auction);
				}
			}
			else
			{
				if (search != null)
				{
					writeD(am.getAuctionsSizeById(grade, search)); // Auction count, maybe items putted in auction???
					for (Auctions auction : am.getAuctions())
					{
							if (grade == -1)
								if (auction.getItem().getName().contains(search))
									writeItems(auction);
							if (grade != -1)
								if (grade == auction.getItem().getItem().getCrystalType() && auction.getItem().getName().contains(search))
									writeItems(auction);
					}
				}
			}
		}*/
	}
	
	//private void writeItems(Auctions auction)
	//{
        /*writeQ(auction.getAuctionId()); // Auction id
		writeQ(auction.getPrice()); // Price
		writeD(auction.getCategory()); // Category
		writeD(auction.getDuration()); // Duration / maybe in days???
		writeD((int)auction.getFinishTime()); // Time when this item will vanish from auction (in seconds)(example (currentTime+60=after 1 minute))
		writeS(CharNameTable.getInstance().getNameById(auction.getPlayerID())); // Name
		writeD(0x00); // Unkown
		writeD(auction.getItem().getItemId()); // Item ID
		writeQ(auction.getItem().getCount()); // Count
		writeH(auction.getItem().getItem().getType2()); // item.getItem().getType2()
		writeQ(auction.getItem().getItem().getBodyPart()); // item.getItem().getBodyPart()
		writeH(auction.getItem().getEnchantLevel()); // Enchant level
		writeH(auction.getItem().getCustomType2()); // item.getCustomType2()
		writeH(auction.getItem().getAttackElementType());
		writeH(auction.getItem().getAttackElementPower());
		for (byte d = 0; d < 6; d++)
			writeH(auction.getItem().getElementDefAttr(d));

		writeH(0x00); // Enchant Effect 1
		writeH(0x00); // Enchant Effect 2
		writeH(0x00); // Enchant Effect 3

		writeD(auction.getItem().getAppearance());*/
	//}
}
