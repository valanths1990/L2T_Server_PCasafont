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

package handlers.bypasshandlers;

import l2server.gameserver.handler.IBypassHandler;
import l2server.gameserver.model.actor.L2Npc;
import l2server.gameserver.model.actor.instance.L2PcInstance;

public class ChatLink implements IBypassHandler {
	private static final String[] COMMANDS = {"Chat"};

	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Npc target) {
		if (target == null) {
			return false;
		}

		target.showChatWindow(activeChar, command.substring(5));

		return false;
	}

	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
