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

package l2server.gameserver.network.clientpackets;

import l2server.gameserver.instancemanager.ClanRecruitManager;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.serverpackets.ExPledgeRecruitApplyInfo;

/**
 * @author Pere
 */
public final class RequestPledgeRecruitBoardAccess extends L2GameClientPacket {
	private int action;
	private int karma;
	private String introduction;
	private String largeIntroduction;
	
	@Override
	protected void readImpl() {
		action = readD();
		karma = readD();
		introduction = readS();
		largeIntroduction = readS();
	}
	
	@Override
	protected void runImpl() {
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null || !activeChar.isClanLeader()) {
			return;
		}
		
		switch (action) {
			case 0:
				if (ClanRecruitManager.getInstance().removeClan(activeChar.getClan())) {
					sendPacket(new ExPledgeRecruitApplyInfo(0));
				}
				break;
			case 1:
				if (ClanRecruitManager.getInstance().addClan(activeChar.getClan(), karma, introduction, largeIntroduction)) {
					sendPacket(new ExPledgeRecruitApplyInfo(1));
				}
				break;
			case 2:
				ClanRecruitManager.getInstance().updateClan(activeChar.getClan(), karma, introduction, largeIntroduction);
				break;
		}
	}
}
