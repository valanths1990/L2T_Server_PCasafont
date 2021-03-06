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

import l2server.Config;
import l2server.gameserver.model.L2Clan;
import l2server.gameserver.model.L2Clan.SubPledge;
import l2server.gameserver.model.L2ClanMember;
import l2server.gameserver.model.actor.instance.L2PcInstance;

//import java.util.logging.Logger;

/**
 * sample from gracia final:
 * <p>
 * 5A // packet id
 * <p>
 * 00 00 00 00 // pledge = 1 subpledge = 0
 * D0 2D 00 00 // clan ID
 * 00 00 00 00 // pledge Id
 * 54 00 68 00 65 00 4B 00 6E 00 69 00 67 00 68 00 74 00 73 00 4F 00 66 00 47 00 6F 00 64 00 00 00 // clan name
 * 54 00 68 00 65 00 47 00 72 00 65 00 65 00 6E 00 44 00 72 00 61 00 67 00 30 00 6E 00 00 00 // clan leader
 * <p>
 * 9D 4F 01 00 // crest ID
 * 03 00 00 00 // level
 * 00 00 00 00 // castle id
 * 00 00 00 00 // hideout id
 * 00 00 00 00 // fort id
 * 00 00 00 00 // rank
 * 00 00 00 00 // reputation
 * 00 00 00 00 // ?
 * 00 00 00 00 // ?
 * 00 00 00 00 // ally id
 * 00 00	   // ally name
 * 00 00 00 00 // ally crest id
 * 00 00 00 00 // is at war
 * 00 00 00 00 // territory castle ID
 * <p>
 * 01 00 00 00 // member count
 * <p>
 * 51 00 75 00 65 00 65 00 70 00 68 00 00 00 // member name
 * 22 00 00 00 // member level
 * 07 00 00 00 // member class id
 * 01 00 00 00 // member sex
 * 00 00 00 00 // member race
 * 00 00 00 00 // member object id (if online)
 * 00 00 00 00 // member sponsor
 * <p>
 * <p>
 * format   dddSS ddddddddddSddd d (Sdddddd)
 *
 * @version $Revision: 1.6.2.2.2.3 $ $Date: 2005/03/27 15:29:57 $
 */
public class PledgeShowMemberListAll extends L2GameServerPacket {
	private L2Clan clan;
	private L2PcInstance activeChar;
	private L2ClanMember[] members;
	private int pledgeType;
	
	//
	
	public PledgeShowMemberListAll(L2Clan clan, L2PcInstance activeChar) {
		this.clan = clan;
		this.activeChar = activeChar;
		members = clan.getMembers();
	}
	
	@Override
	protected final void writeImpl() {
		pledgeType = 0;
		writePledge(0);
		
		for (SubPledge subPledge : clan.getAllSubPledges()) {
			activeChar.sendPacket(new PledgeReceiveSubPledgeCreated(subPledge, clan));
		}
		
		for (L2ClanMember m : members) {
			if (m.getPledgeType() == 0) {
				continue;
			}
			activeChar.sendPacket(new PledgeShowMemberListAdd(m));
		}
		
		// unless this is sent sometimes, the client doesn't recognize the player as the leader
		//activeChar.sendPacket(new ExUserInfo(activeChar));
		//activeChar.sendPacket(new ExBrExtraUserInfo(activeChar));
		
	}
	
	void writePledge(int mainOrSubpledge) {
		writeD(mainOrSubpledge);
		writeD(clan.getClanId());
		writeD(Config.SERVER_ID); // server id?
		writeD(pledgeType);
		writeS(clan.getName());
		writeS(clan.getLeaderName());
		
		writeD(clan.getCrestId()); // crest id .. is used again
		writeD(clan.getLevel());
		writeD(0); // GoD ???
		writeD(clan.getHasCastle());
		writeD(clan.getHasHideout());
		writeD(clan.getHasFort());
		writeD(clan.getRank());
		writeD(clan.getReputationScore());
		writeD(0); //0
		writeD(0); //0
		writeD(clan.getAllyId());
		writeS(clan.getAllyName());
		writeD(clan.getAllyCrestId());
		writeD(clan.isAtWar() ? 1 : 0);// new c3
		writeD(0); // Territory castle ID
		//writeD(0); // GoD ???
		writeD(clan.getSubPledgeMembersCount(pledgeType));
		
		for (L2ClanMember m : members) {
			if (m.getPledgeType() != pledgeType) {
				continue;
			}
			writeS(m.getName());
			writeD(m.getLevel());
			writeD(m.getCurrentClass());
			L2PcInstance player;
			if ((player = m.getPlayerInstance()) != null) {
				writeD(player.getAppearance().getSex() ? 1 : 0); // no visible effect
				writeD(player.getRace().ordinal());//writeD(1);
			} else {
				writeD(1); // no visible effect
				writeD(1); //writeD(1);
			}
			writeD(m.isOnline() ? m.getObjectId() : 0); // objectId=online 0=offline
			writeC(0x00); // ??? Activity?
			writeD(m.getSponsor() != 0 ? 1 : 0);
		}
	}
}
