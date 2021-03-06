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

package l2server.gameserver.stats.conditions;

import l2server.gameserver.model.L2ItemInstance;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.stats.Env;

import java.util.ArrayList;

/**
 * The Class ConditionPlayerHasPet.
 */
public class ConditionPlayerHasPet extends Condition {
	private final ArrayList<Integer> controlItemIds;

	/**
	 * Instantiates a new condition player has pet.
	 *
	 * @param itemIds the item ids
	 */
	public ConditionPlayerHasPet(ArrayList<Integer> itemIds) {
		if (itemIds.size() == 1 && itemIds.get(0) == 0) {
			controlItemIds = null;
		} else {
			controlItemIds = itemIds;
		}
	}

	/* (non-Javadoc)
	 * @see l2server.gameserver.stats.conditions.Condition#testImpl(l2server.gameserver.stats.Env)
	 */
	@Override
	public boolean testImpl(Env env) {
		if (!(env.player instanceof L2PcInstance)) {
			return false;
		}

		if (((L2PcInstance) env.player).getPet() == null) {
			return false;
		}

		if (controlItemIds == null) {
			return true;
		}

		final L2ItemInstance controlItem = ((L2PcInstance) env.player).getPet().getControlItem();
		if (controlItem == null) {
			return false;
		}

		return controlItemIds.contains(controlItem.getItemId());
	}
}
