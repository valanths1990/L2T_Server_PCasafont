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

import l2server.gameserver.model.actor.instance.L2PcInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class ...
 *
 * @version $Revision: 1.2.4.2 $ $Date: 2005/03/27 15:29:33 $
 */
public final class L2SkillLearn {
	// these two build the primary key
	private final int id;
	private final int level;

	private final int spCost;
	private final int minLevel;
	private final int minDualLevel;

	private final boolean learnedFromPanel;
	private final boolean learnedByFs;
	private final boolean isTransfer;
	private final boolean isAutoGet;

	private final Map<Integer, Integer> costItems = new HashMap<>();
	private final List<Integer> costSkills = new ArrayList<>();

	private boolean isRemember = false;

	public L2SkillLearn(int id, int lvl, int cost, int minLvl, int minDualLvl, boolean panel, boolean fs, boolean transfer, boolean autoget) {
		this.id = id;
		level = lvl;
		minLevel = minLvl;
		minDualLevel = minDualLvl;
		spCost = cost;
		learnedFromPanel = panel;
		learnedByFs = fs;
		isTransfer = transfer;
		isAutoGet = autoget;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return Returns the level.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return Returns the minLevel.
	 */
	public int getMinLevel() {
		return minLevel;
	}

	public int getMinDualLevel() {
		return minDualLevel;
	}

	/**
	 * @return Returns the spCost.
	 */
	public int getSpCost() {
		return spCost;
	}

	/**
	 * Return true if skill can be learned by teachers
	 */
	public boolean isLearnedFromPanel() {
		return learnedFromPanel;
	}

	/**
	 * Return true if skill can be learned by forgotten scroll
	 */
	public boolean isLearnedByFS() {
		return learnedByFs;
	}

	public boolean isTransferSkill() {
		return isTransfer;
	}

	public boolean isAutoGetSkill() {
		return isAutoGet;
	}

	public void addCostItem(int itemId, int count) {
		costItems.put(itemId, count);
	}

	public Map<Integer, Integer> getCostItems() {
		return costItems;
	}

	public void addCostSkill(int skillId) {
		costSkills.add(skillId);
	}

	public List<Integer> getCostSkills() {
		return costSkills;
	}

	public Map<Integer, Integer> getCostSkills(L2PcInstance player) {
		Map<Integer, Integer> costSkills = new HashMap<>();
		for (int skillId : this.costSkills) {
			int skillLevel = player.getSkillLevelHash(skillId);
			if (skillLevel > 0) {
				costSkills.put(skillId, skillLevel);
			}
		}
		return costSkills;
	}

	public void setIsRemember(boolean remember) {
		isRemember = remember;
	}

	public boolean isRemember() {
		return isRemember;
	}
}
