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

package ai.individual.GrandBosses.Valakas;

import ai.group_template.L2AttackableAIScript;
import l2server.Config;
import l2server.gameserver.GeoData;
import l2server.gameserver.ThreadPoolManager;
import l2server.gameserver.ai.CtrlIntention;
import l2server.gameserver.datatables.DoorTable;
import l2server.gameserver.datatables.SkillTable;
import l2server.gameserver.instancemanager.GrandBossManager;
import l2server.gameserver.instancemanager.InstanceManager;
import l2server.gameserver.model.L2Abnormal;
import l2server.gameserver.model.L2CharPosition;
import l2server.gameserver.model.L2Skill;
import l2server.gameserver.model.Location;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.L2Npc;
import l2server.gameserver.model.actor.L2Playable;
import l2server.gameserver.model.actor.instance.L2GrandBossInstance;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.actor.instance.L2PetInstance;
import l2server.gameserver.model.actor.instance.L2SummonInstance;
import l2server.gameserver.model.quest.QuestTimer;
import l2server.gameserver.model.zone.L2ZoneType;
import l2server.gameserver.model.zone.type.L2BossZone;
import l2server.gameserver.network.serverpackets.*;
import l2server.gameserver.stats.SkillHolder;
import l2server.gameserver.util.Broadcast;
import l2server.gameserver.util.Util;
import l2server.log.Log;
import l2server.util.Rnd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author LasTravel
 * <p>
 * Valakas AI (Based on Kerberos work)
 */

public class Valakas extends L2AttackableAIScript {
	//Quest
	private static final boolean debug = false;
	private static final String qn = "Valakas";

	//Id's
	private static final int valakasRecoveryId = 4691;
	private static final int lavasaurusAlpha = 29029;
	private static final int valakasId = 29028;
	private static final int[] gatekeepersOfFireDragons = {31384, 31686, 31687};
	private static final int klein = 31540;
	private static final int heartOfVolkano = 31385;
	private static final int teleportCubic = 31759;
	private static final L2BossZone bossZone = GrandBossManager.getInstance().getZone(212852, -114842, -1632);

	//Skills
	private static final SkillHolder VALAKAS_LAVA_SKIN = new SkillHolder(4680, 1);
	private static final SkillHolder[] VALAKAS_REGULAR_SKILLS = {new SkillHolder(4681, 1), // Valakas Trample
			new SkillHolder(4682, 1), // Valakas Trample
			new SkillHolder(4683, 1), // Valakas Dragon Breath
			new SkillHolder(4689, 1), // Valakas Fear TODO: has two levels only level one is used.
	};

	private static final SkillHolder[] VALAKAS_LOWHP_SKILLS = {new SkillHolder(4681, 1), // Valakas Trample
			new SkillHolder(4682, 1), // Valakas Trample
			new SkillHolder(4683, 1), // Valakas Dragon Breath
			new SkillHolder(4689, 1), // Valakas Fear TODO: has two levels only level one is used.
			new SkillHolder(4690, 1), // Valakas Meteor Storm
	};

	private static final SkillHolder[] VALAKAS_AOE_SKILLS = {new SkillHolder(4683, 1), // Valakas Dragon Breath
			new SkillHolder(4684, 1), // Valakas Dragon Breath
			new SkillHolder(4685, 1), // Valakas Tail Stomp
			new SkillHolder(4686, 1), // Valakas Tail Stomp
			new SkillHolder(4688, 1), // Valakas Stun
			new SkillHolder(4689, 1), // Valakas Fear TODO: has two levels only level one is used.
			new SkillHolder(4690, 1), // Valakas Meteor Storm
	};

	//Others
	private L2Playable actualVictim;
	private static L2Npc valakasBoss;
	private static long LastAction;

	private static final Location teleportCubicLocs[] =
			{new Location(214880, -116144, -1644), new Location(213696, -116592, -1644), new Location(212112, -116688, -1644),
					new Location(211184, -115472, -1664), new Location(210336, -114592, -1644), new Location(211360, -113904, -1644),
					new Location(213152, -112352, -1644), new Location(214032, -113232, -1644), new Location(214752, -114592, -1644),
					new Location(209824, -115568, -1421), new Location(210528, -112192, -1403), new Location(213120, -111136, -1408),
					new Location(215184, -111504, -1392), new Location(215456, -117328, -1392), new Location(213200, -118160, -1424)};

	public Valakas(int id, String name, String descr) {
		super(id, name, descr);

		addTalkId(teleportCubic);
		addStartNpc(teleportCubic);

		addTalkId(heartOfVolkano);
		addStartNpc(heartOfVolkano);

		addTalkId(klein);
		addStartNpc(klein);

		for (int i : gatekeepersOfFireDragons) {
			addTalkId(i);
			addStartNpc(i);
		}

		addAttackId(valakasId);
		addKillId(valakasId);

		addEnterZoneId(bossZone.getId());
		addExitZoneId(bossZone.getId());

		//Unlock
		startQuestTimer("unlock_valakas", GrandBossManager.getInstance().getUnlockTime(valakasId), null, null);
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		if (debug) {
			Log.warning(getName() + ": onTalk: " + player.getName());
		}

		if (npc.getNpcId() == heartOfVolkano) {
			int vallyStatus = GrandBossManager.getInstance().getBossStatus(valakasId);
			final List<L2PcInstance> allPlayers = new ArrayList<L2PcInstance>();

			if (bossZone.getPlayersInside().size() > 200) {
				return "31385-02.html";
			}

			if (vallyStatus == GrandBossManager.getInstance().DEAD) {
				return "31385-01.html";
			} else {
				if (!debug) {
					if (vallyStatus == GrandBossManager.getInstance().ALIVE && !InstanceManager.getInstance()
							.checkInstanceConditions(player, -1, Config.VALAKAS_MIN_PLAYERS, 200, 95, Config.MAX_LEVEL)) {
						return null;
					} else if (vallyStatus == GrandBossManager.getInstance().WAITING && !InstanceManager.getInstance()
							.checkInstanceConditions(player, -1, Config.VALAKAS_MIN_PLAYERS, 200, 95, Config.MAX_LEVEL)) {
						return null;
					} else if (vallyStatus == GrandBossManager.getInstance().FIGHTING) {
						return "31385-03.html";
					}
				}
			}

			if (vallyStatus == GrandBossManager.getInstance().ALIVE) {
				GrandBossManager.getInstance().setBossStatus(valakasId, GrandBossManager.getInstance().WAITING);

				LastAction = System.currentTimeMillis();

				startQuestTimer("valakas_spawn_task_1", debug ? 60000 : Config.VALAKAS_WAIT_TIME * 60000, null, null);
			}

			if (debug) {
				allPlayers.add(player);
			} else {
				allPlayers.addAll(Config.VALAKAS_MIN_PLAYERS > Config.MAX_MEMBERS_IN_PARTY || player.getParty().isInCommandChannel() ?
						player.getParty().getCommandChannel().getMembers() : player.getParty().getPartyMembers());
			}

			for (L2PcInstance enterPlayer : allPlayers) {
				if (enterPlayer == null) {
					continue;
				}

				bossZone.allowPlayerEntry(enterPlayer, 7200);
				enterPlayer.teleToLocation(204328 + Rnd.get(600), -111874 + Rnd.get(600), 70);
			}
		} else if (npc.getNpcId() == teleportCubic) {
			player.teleToLocation(150037 + Rnd.get(500), -57720 + Rnd.get(500), -2976);
		} else if (npc.getNpcId() == gatekeepersOfFireDragons[0]) {
			DoorTable.getInstance().getDoor(25140004).openMe();
		} else if (npc.getNpcId() == gatekeepersOfFireDragons[1]) {
			DoorTable.getInstance().getDoor(25140005).openMe();
		} else if (npc.getNpcId() == gatekeepersOfFireDragons[2]) {
			DoorTable.getInstance().getDoor(25140006).openMe();
		} else if (npc.getNpcId() == klein) {
			int playerCount = bossZone.getPlayersInside().size();
			if (playerCount < 50) {
				return "31540-01.html";
			} else if (playerCount < 100) {
				return "31540-02.html";
			} else if (playerCount < 150) {
				return "31540-03.html";
			} else if (playerCount < 200) {
				return "31540-04.html";
			} else {
				return "31540-05.html";
			}
		}
		return super.onTalk(npc, player);
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		if (debug) {
			Log.warning(getName() + ": onAdvEvent: " + event);
		}

		if (event.equalsIgnoreCase("unlock_valakas")) {
			GrandBossManager.getInstance().setBossStatus(valakasId, GrandBossManager.getInstance().ALIVE);

			Broadcast.toAllOnlinePlayers(new Earthquake(214880, -116144, -1644, 20, 10));
		} else if (event.equalsIgnoreCase("enter_hall_of_flames")) {
			player.teleToLocation(183813, -115157, -3303);
		} else if (event.equalsIgnoreCase("valakas_spawn_task_1")) {
			bossZone.stopWholeZone();

			valakasBoss = addSpawn(valakasId, 212852, -114842, -1632, 0, false, 0);
			valakasBoss.setIsInvul(true);
			valakasBoss.disableCoreAI(true);

			GrandBossManager.getInstance().addBoss((L2GrandBossInstance) valakasBoss);
			GrandBossManager.getInstance().setBossStatus(valakasId, GrandBossManager.getInstance().FIGHTING);

			LastAction = System.currentTimeMillis();

			for (L2PcInstance plyr : bossZone.getPlayersInside()) {
				plyr.sendPacket(new PlaySound(1, "B03_A", 0, 0, 0, 0, 0));
				plyr.sendPacket(new SocialAction(valakasBoss.getObjectId(), 3));
			}

			//Cameras
			bossZone.sendDelayedPacketToZone(1700, new SpecialCamera(valakasBoss.getObjectId(), 1800, 180, -1, 1500, 10000, 0, 0, 1, 0));
			bossZone.sendDelayedPacketToZone(3200, new SpecialCamera(valakasBoss.getObjectId(), 1300, 180, -5, 3000, 10000, 0, -5, 1, 0));
			bossZone.sendDelayedPacketToZone(6500, new SpecialCamera(valakasBoss.getObjectId(), 500, 180, -8, 600, 10000, 0, 60, 1, 0));
			bossZone.sendDelayedPacketToZone(9400, new SpecialCamera(valakasBoss.getObjectId(), 800, 180, -8, 2700, 10000, 0, 30, 1, 0));
			bossZone.sendDelayedPacketToZone(12100, new SpecialCamera(valakasBoss.getObjectId(), 200, 250, 70, 0, 10000, 30, 80, 1, 0));
			bossZone.sendDelayedPacketToZone(12430, new SpecialCamera(valakasBoss.getObjectId(), 1100, 250, 70, 2500, 10000, 30, 80, 1, 0));
			bossZone.sendDelayedPacketToZone(15430, new SpecialCamera(valakasBoss.getObjectId(), 700, 150, 30, 0, 10000, -10, 60, 1, 0));
			bossZone.sendDelayedPacketToZone(16830, new SpecialCamera(valakasBoss.getObjectId(), 1200, 150, 20, 2900, 10000, -10, 30, 1, 0));
			bossZone.sendDelayedPacketToZone(23530, new SpecialCamera(valakasBoss.getObjectId(), 750, 170, -10, 3400, 4000, 10, -15, 1, 0));

			startQuestTimer("valakas_last_spawn_task", 26000, null, null);
		} else if (event.equalsIgnoreCase("valakas_last_spawn_task")) {
			valakasBoss.setIsInvul(false);

			bossZone.startWholeZone();

			startQuestTimer("valakas_skill_task", 2000, null, null, true);
			startQuestTimer("check_activity_task", 60000, null, null, true);

			notifyEvent("valakas_recovery_task", null, null);

			startQuestTimer("valakas_recovery_task", 300000, null, null, true); //5min
			startQuestTimer("valakas_minion_spawns", 600000, null, null, true); //5min

			//kick dual box
			bossZone.kickDualBoxes();
		} else if (event.equalsIgnoreCase("valakas_minion_spawns")) {
			for (int i = 0; i < 20; i++) {
				addSpawn(lavasaurusAlpha,
						valakasBoss.getX() + Rnd.get(500),
						valakasBoss.getY() + Rnd.get(500),
						valakasBoss.getZ(),
						0,
						false,
						0,
						true,
						0);
			}
		} else if (event.equalsIgnoreCase("valakas_skill_task")) {
			if (!valakasBoss.isInvul() && !valakasBoss.isCastingNow()) {
				// Pickup a target if no or dead victim. 10% luck he decides to reconsiders his target.
				if (actualVictim == null || actualVictim.isDead() || !valakasBoss.getKnownList().knowsObject(actualVictim) || Rnd.get(10) == 0) {
					actualVictim = getRandomTarget();
				}

				if (debug) {
					Log.warning(getName() + ": " + (actualVictim != null ? actualVictim.getName() : " victim is null!"));
				}

				// If result is still null, Valakas will roam. Don't go deeper in skill AI.
				if (actualVictim == null) {
					if (Rnd.get(10) != 0) {
						int x = valakasBoss.getX();
						int y = valakasBoss.getY();
						int z = valakasBoss.getZ();

						int posX = x + Rnd.get(-1400, 1400);
						int posY = y + Rnd.get(-1400, 1400);

						if (GeoData.getInstance().canMoveFromToTarget(x, y, z, posX, posY, z, valakasBoss.getInstanceId())) {
							valakasBoss.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(posX, posY, z, 0));
						}
					}
				} else {
					final L2Skill skill = getRandomSkill().getSkill();

					// Cast the skill or follow the target.
					if (Util.checkIfInRange(skill.getCastRange() < 600 ? 600 : skill.getCastRange(), valakasBoss, actualVictim, true)) {
						valakasBoss.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
						valakasBoss.setIsCastingNow(true);
						valakasBoss.setTarget(actualVictim);
						valakasBoss.doCast(skill);
					} else {
						valakasBoss.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, actualVictim, null);
						valakasBoss.setIsCastingNow(false);
					}
				}
			}
		} else if (event.equalsIgnoreCase("check_activity_task")) {
			if (!GrandBossManager.getInstance().isActive(valakasId, LastAction)) {
				notifyEvent("end_valakas", null, null);
			}
		} else if (event.equalsIgnoreCase("valakas_recovery_task")) {
			if (valakasBoss != null && !valakasBoss.isDead()) {
				L2Abnormal valakasRecovery = valakasBoss.getFirstEffect(valakasRecoveryId);
				if (valakasRecovery == null) {
					valakasBoss.setTarget(valakasBoss);

					if (valakasBoss.getCurrentHp() < valakasBoss.getMaxHp() * 0.10) {
						valakasBoss.doCast(SkillTable.getInstance().getInfo(valakasRecoveryId, 5));
					} else if (valakasBoss.getCurrentHp() < valakasBoss.getMaxHp() * 0.25) {
						valakasBoss.doCast(SkillTable.getInstance().getInfo(valakasRecoveryId, 4));
					} else if (valakasBoss.getCurrentHp() < valakasBoss.getMaxHp() * 0.50) {
						valakasBoss.doCast(SkillTable.getInstance().getInfo(valakasRecoveryId, 3));
					} else if (valakasBoss.getCurrentHp() < valakasBoss.getMaxHp() * 0.75) {
						valakasBoss.doCast(SkillTable.getInstance().getInfo(valakasRecoveryId, 2));
					} else {
						valakasBoss.doCast(SkillTable.getInstance().getInfo(valakasRecoveryId, 1));
					}
				}
			}
		} else if (event.equalsIgnoreCase("cancel_timers")) {
			QuestTimer activityTimer = getQuestTimer("check_activity_task", null, null);
			if (activityTimer != null) {
				activityTimer.cancel();
			}

			QuestTimer recoveryTimer = getQuestTimer("valakas_recovery_task", null, null);
			if (recoveryTimer != null) {
				recoveryTimer.cancel();
			}

			QuestTimer skillTimer = getQuestTimer("valakas_skill_task", null, null);
			if (skillTimer != null) {
				skillTimer.cancel();
			}

			QuestTimer valakasMinions = getQuestTimer("valakas_minion_spawns", null, null);
			if (valakasMinions != null) {
				valakasMinions.cancel();
			}
		} else if (event.equalsIgnoreCase("end_valakas")) {
			notifyEvent("cancel_timers", null, null);

			bossZone.oustAllPlayers();

			if (valakasBoss != null) {
				valakasBoss.deleteMe();
			}

			for (L2Npc raidMobs : bossZone.getNpcsInside()) {
				raidMobs.getSpawn().stopRespawn();
				raidMobs.deleteMe();
			}

			if (GrandBossManager.getInstance().getBossStatus(valakasId) != GrandBossManager.getInstance().DEAD) {
				GrandBossManager.getInstance().setBossStatus(valakasId, GrandBossManager.getInstance().ALIVE);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}

	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet) {
		if (debug) {
			Log.warning(getName() + ": onAttack: " + npc.getName());
		}

		if (npc.getNpcId() == valakasId) {
			LastAction = System.currentTimeMillis();

			if (GrandBossManager.getInstance().getBossStatus(valakasId) != GrandBossManager.getInstance().FIGHTING) {
				bossZone.oustAllPlayers();
			}

			//Anti BUGGERS
			if (!bossZone.isInsideZone(attacker)) //Character attacking out of zone
			{
				attacker.doDie(null);
				if (debug) {
					Log.warning(getName() + ": Character: " + attacker.getName() + " attacked: " + npc.getName() + " out of the boss zone!");
				}
			}

			if (!bossZone.isInsideZone(npc)) //Npc moved out of the zone
			{
				int[] randPoint = bossZone.getZone().getRandomPoint();
				npc.teleToLocation(randPoint[0], randPoint[1], randPoint[2]);

				if (debug) {
					Log.warning(getName() + ": Character: " + attacker.getName() + " attacked: " + npc.getName() + " wich is out of the boss zone!");
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}

	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet) {
		if (debug) {
			Log.warning(getName() + ": onKill: " + npc.getName());
		}

		if (npc.getNpcId() == valakasId) {
			GrandBossManager.getInstance().notifyBossKilled(valakasId);

			notifyEvent("cancel_timers", null, null);

			bossZone.stopWholeZone();
			bossZone.broadcastPacket(new PlaySound(1, "B03_D", 0, 0, 0, 0, 0));

			//Cameras
			bossZone.sendDelayedPacketToZone(300, new SpecialCamera(valakasBoss.getObjectId(), 2000, 130, -1, 0, 10000, 0, 0, 1, 1));
			bossZone.sendDelayedPacketToZone(600, new SpecialCamera(valakasBoss.getObjectId(), 1100, 210, -5, 3000, 10000, -13, 0, 1, 1));
			bossZone.sendDelayedPacketToZone(3800, new SpecialCamera(valakasBoss.getObjectId(), 1300, 200, -8, 3000, 10000, 0, 15, 1, 1));
			bossZone.sendDelayedPacketToZone(8200, new SpecialCamera(valakasBoss.getObjectId(), 1000, 190, 0, 500, 10000, 0, 10, 1, 1));
			bossZone.sendDelayedPacketToZone(8700, new SpecialCamera(valakasBoss.getObjectId(), 1700, 120, 0, 2500, 10000, 12, 40, 1, 1));
			bossZone.sendDelayedPacketToZone(13300, new SpecialCamera(valakasBoss.getObjectId(), 1700, 20, 0, 700, 10000, 10, 10, 1, 1));
			bossZone.sendDelayedPacketToZone(14000, new SpecialCamera(valakasBoss.getObjectId(), 1700, 10, 0, 1000, 10000, 20, 70, 1, 1));
			bossZone.sendDelayedPacketToZone(16500, new SpecialCamera(valakasBoss.getObjectId(), 1700, 10, 0, 300, 250, 20, -20, 1, 1));

			bossZone.broadcastPacket(new ExShowScreenMessage(901900151, 0, 3000));//The evil Fire Dragon Valakas has been defeated!

			for (Location loc : teleportCubicLocs) {
				addSpawn(teleportCubic, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), false, 900000);
			}

			// Start the zone when the cameras ends
			ThreadPoolManager.getInstance().scheduleAi(new Runnable() {
				@Override
				public void run() {
					bossZone.startWholeZone();
				}
			}, 17000);

			startQuestTimer("unlock_valakas", GrandBossManager.getInstance().getUnlockTime(valakasId), null, null);
			startQuestTimer("end_valakas", 900000, null, null);
		}

		return super.onKill(npc, killer, isPet);
	}

	private SkillHolder getRandomSkill() {
		final int hpRatio = (int) (valakasBoss.getCurrentHp() / valakasBoss.getMaxHp() * 100);
		// Valakas Lava Skin has priority.
		if (hpRatio < 75 && Rnd.get(150) == 0 && valakasBoss.getFirstEffect(VALAKAS_LAVA_SKIN.getSkillId()) == null) {
			return VALAKAS_LAVA_SKIN;
		}

		// Valakas will use mass spells if he feels surrounded.
		if (Util.getPlayersCountInRadius(1200, valakasBoss, false, false) >= 20) {
			return VALAKAS_AOE_SKILLS[Rnd.get(VALAKAS_AOE_SKILLS.length)];
		}

		if (hpRatio > 50) {
			return VALAKAS_REGULAR_SKILLS[Rnd.get(VALAKAS_REGULAR_SKILLS.length)];
		}

		return VALAKAS_LOWHP_SKILLS[Rnd.get(VALAKAS_LOWHP_SKILLS.length)];
	}

	private L2Playable getRandomTarget() {
		List<L2Playable> result = new ArrayList<L2Playable>();
		for (L2Character obj : valakasBoss.getKnownList().getKnownCharacters()) {
			if (obj == null || obj instanceof L2PetInstance || obj instanceof L2SummonInstance || obj.getZ() > valakasBoss.getZ() + 200) {
				continue;
			} else if (!obj.isDead() && obj instanceof L2Playable) {
				result.add((L2Playable) obj);
			}
		}

		return result.isEmpty() ? null : result.get(Rnd.get(result.size()));
	}

	@Override
	public final String onEnterZone(L2Character character, L2ZoneType zone) {
		if (character instanceof L2PcInstance) {
			if (GrandBossManager.getInstance().getBossStatus(valakasId) == GrandBossManager.getInstance().WAITING) {
				character.sendPacket(new ExSendUIEvent(0,
						0,
						(int) TimeUnit.MILLISECONDS.toSeconds(LastAction + Config.VALAKAS_WAIT_TIME * 60000 - System.currentTimeMillis()),
						0,
						"Valakas is coming..."));
			}
		}
		return null;
	}

	@Override
	public final String onExitZone(L2Character character, L2ZoneType zone) {
		if (character instanceof L2PcInstance) {
			if (GrandBossManager.getInstance().getBossStatus(valakasId) == GrandBossManager.getInstance().WAITING) {
				character.sendPacket(new ExSendUIEventRemove());
			}
		}
		return null;
	}

	@Override
	public int getOnKillDelay(int npcId) {
		return 0;
	}

	public static void main(String[] args) {
		new Valakas(-1, qn, "ai/individual/GrandBosses");
	}
}
