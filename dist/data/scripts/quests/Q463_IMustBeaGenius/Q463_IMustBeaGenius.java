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

package quests.Q463_IMustBeaGenius;

import l2server.gameserver.model.actor.L2Npc;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.quest.Quest;
import l2server.gameserver.model.quest.QuestState;
import l2server.gameserver.model.quest.State;
import l2server.gameserver.network.serverpackets.NpcSay;
import l2server.gameserver.util.Util;
import l2server.util.Rnd;

/**
 * * @author Gnacik
 * *
 * * 2010-08-19 Based on Freya PTS
 */
public class Q463_IMustBeaGenius extends Quest {
	private static final String qn = "463_IMustBeaGenius";
	private static final int gutenhagen = 32069;
	private static final int _corpse_log = 15510;
	private static final int collection = 15511;
	private static final int[] mobs = {22801, 22802, 22804, 22805, 22807, 22808, 22809, 22810, 22811, 22812};

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		QuestState st = player.getQuestState(qn);

		if (st == null) {
			return htmltext;
		}

		if (npc.getNpcId() == gutenhagen) {
			if (event.equalsIgnoreCase("32069-03.htm")) {
				st.playSound("ItemSound.quest_accept");
				st.setState(State.STARTED);
				st.set("cond", "1");
				// Generate random daily number for player
				int number = Rnd.get(500, 600);
				st.set("number", String.valueOf(number));
				// Set drop for mobs
				for (int mob : mobs) {
					int rand = Rnd.get(-2, 4);
					if (rand == 0) {
						rand = 5;
					}
					st.set(String.valueOf(mob), String.valueOf(rand));
				}
				// One with higher chance
				st.set(String.valueOf(mobs[Rnd.get(0, mobs.length - 1)]), String.valueOf(Rnd.get(1, 100)));
				htmltext = getHtm(st.getPlayer().getHtmlPrefix(), "32069-03.htm");
				htmltext = htmltext.replace("%num%", String.valueOf(number));
			} else if (event.equalsIgnoreCase("32069-05.htm")) {
				htmltext = getHtm(st.getPlayer().getHtmlPrefix(), "32069-05.htm");
				htmltext = htmltext.replace("%num%", st.get("number"));
			} else if (event.equalsIgnoreCase("32069-07.htm")) {
				st.addExpAndSp(317961, 25427);
				st.unset("cond");
				st.unset("number");
				for (int mob : mobs) {
					st.unset(String.valueOf(mob));
				}
				st.takeItems(collection, -1);
				st.playSound("ItemSound.quest_finish");
				st.exitQuest(false);
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		QuestState st = player.getQuestState(qn);
		if (st == null) {
			return htmltext;
		}

		if (npc.getNpcId() == gutenhagen) {
			switch (st.getState()) {
				case State.CREATED:
					if (player.getLevel() >= 70) {
						htmltext = "32069-01.htm";
					} else {
						htmltext = "32069-00.htm";
					}
					break;
				case State.STARTED:
					if (st.getInt("cond") == 1) {
						htmltext = "32069-04.htm";
					} else if (st.getInt("cond") == 2) {
						htmltext = "32069-06.htm";
					}
					break;
				case State.COMPLETED:
					htmltext = "32069-08.htm";
					break;
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet) {
		QuestState st = player.getQuestState(qn);
		if (st == null) {
			return null;
		}

		if (st.getState() == State.STARTED && st.getInt("cond") == 1 && Util.contains(mobs, npc.getNpcId())) {
			int _day_number = st.getInt("number");
			int number = st.getInt(String.valueOf(npc.getNpcId()));

			if (number > 0) {
				st.giveItems(_corpse_log, number);
				st.playSound("ItemSound.quest_itemget");
				npc.broadcastPacket(new NpcSay(npc.getObjectId(),
						0,
						npc.getNpcId(),
						"Att... attack... " + player.getName() + "... Ro... rogue... " + number + ".."));
			} else if (number < 0 && st.getQuestItemsCount(_corpse_log) + number > 0) {
				st.takeItems(_corpse_log, Math.abs(number));
				st.playSound("ItemSound.quest_itemget");
				npc.broadcastPacket(new NpcSay(npc.getObjectId(),
						0,
						npc.getNpcId(),
						"Att... attack... " + player.getName() + "... Ro... rogue... " + number + ".."));
			}

			if (st.getQuestItemsCount(_corpse_log) == _day_number) {
				st.takeItems(_corpse_log, -1);
				st.giveItems(collection, 1);
				st.set("cond", "2");
			}
		}
		return null;
	}

	public Q463_IMustBeaGenius(int questId, String name, String descr) {
		super(questId, name, descr);

		addStartNpc(gutenhagen);
		addTalkId(gutenhagen);
		for (int mob : mobs) {
			addKillId(mob);
		}
	}

	public static void main(String[] args) {
		new Q463_IMustBeaGenius(463, qn, "I Must Be a Genius");
	}
}
