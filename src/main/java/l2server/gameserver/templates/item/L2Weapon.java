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

package l2server.gameserver.templates.item;

import l2server.gameserver.handler.ISkillHandler;
import l2server.gameserver.handler.SkillHandler;
import l2server.gameserver.model.L2Abnormal;
import l2server.gameserver.model.L2ItemInstance;
import l2server.gameserver.model.L2Object;
import l2server.gameserver.model.L2Skill;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.L2Npc;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.model.quest.Quest;
import l2server.gameserver.stats.Env;
import l2server.gameserver.stats.Formulas;
import l2server.gameserver.stats.SkillHolder;
import l2server.gameserver.stats.conditions.Condition;
import l2server.gameserver.stats.conditions.ConditionGameChance;
import l2server.gameserver.stats.funcs.Func;
import l2server.gameserver.stats.funcs.FuncTemplate;
import l2server.gameserver.templates.StatsSet;
import l2server.gameserver.templates.skills.L2SkillType;
import l2server.log.Log;
import l2server.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class is dedicated to the management of weapons.
 *
 * @version $Revision: 1.4.2.3.2.5 $ $Date: 2005/04/02 15:57:51 $
 */
public final class L2Weapon extends L2Item {
	private final L2WeaponType type;
	private final boolean isMagicWeapon;
	private final int rndDam;
	private final int soulShotCount;
	private final int spiritShotCount;
	private final int mpConsume;
	private SkillHolder enchant4Skill = null; // skill that activates when item is enchanted +4 (for duals)
	private final int changeWeaponId;
	// private final String[] skill;

	// Attached skills for Special Abilities
	private SkillHolder skillsOnCast;
	private Condition skillsOnCastCondition = null;
	private SkillHolder skillsOnCrit;
	private Condition skillsOnCritCondition = null;

	private final int reuseDelay;

	/**
	 * Constructor for Weapon.<BR><BR>
	 * <U><I>Variables filled :</I></U><BR>
	 * <LI>soulShotCount & spiritShotCount</LI>
	 * <LI>pDam & mDam & rndDam</LI>
	 * <LI>critical</LI>
	 * <LI>hitModifier</LI>
	 * <LI>avoidModifier</LI>
	 * <LI>shieldDes & shieldDefRate</LI>
	 * <LI>atkSpeed & AtkReuse</LI>
	 * <LI>mpConsume</LI>
	 *
	 * @param set : StatsSet designating the set of couples (key,value) caracterizing the armor
	 * @see L2Item constructor
	 */
	public L2Weapon(StatsSet set) {
		super(set);
		type = L2WeaponType.valueOf(set.getString("weaponType", "none").toUpperCase());
		type1 = L2Item.TYPE1_WEAPON_RING_EARRING_NECKLACE;
		type2 = L2Item.TYPE2_WEAPON;
		isMagicWeapon = set.getBool("isMagicWeapon", false);
		soulShotCount = set.getInteger("soulshots", 0);
		spiritShotCount = set.getInteger("spiritshots", 0);
		rndDam = set.getInteger("randomDamage", 0);
		mpConsume = set.getInteger("mpConsume", 0);
		reuseDelay = set.getInteger("reuseDelay", 0);

		String skill = set.getString("enchant4Skill", null);
		if (skill != null) {
			String[] info = skill.split("-");

			if (info != null && info.length == 2) {
				int id = 0;
				int level = 0;
				try {
					id = Integer.parseInt(info[0]);
					level = Integer.parseInt(info[1]);
				} catch (Exception nfe) {
					// Incorrect syntax, dont add new skill
					Log.info(StringUtil.concat("> Couldnt parse ", skill, " in weapon enchant skills! item ", toString()));
				}
				if (id > 0 && level > 0) {
					enchant4Skill = new SkillHolder(id, level);
				}
			}
		}

		skill = set.getString("oncastSkill", null);
		if (skill != null) {
			String[] info = skill.split("-");
			String infochance = set.getString("oncastChance", null);
			if (info != null && info.length == 2) {
				int id = 0;
				int level = 0;
				int chance = 0;
				try {
					id = Integer.parseInt(info[0]);
					level = Integer.parseInt(info[1]);
					if (infochance != null) {
						chance = Integer.parseInt(infochance);
					}
				} catch (Exception nfe) {
					// Incorrect syntax, dont add new skill
					Log.info(StringUtil.concat("> Couldnt parse ", skill, " in weapon oncast skills! item ", toString()));
				}
				if (id > 0 && level > 0 && chance > 0) {
					skillsOnCast = new SkillHolder(id, level);
					if (infochance != null) {
						skillsOnCastCondition = new ConditionGameChance(chance);
					}
				}
			}
		}

		skill = set.getString("oncritSkill", null);
		if (skill != null) {
			String[] info = skill.split("-");
			String infochance = set.getString("oncritChance", null);
			if (info != null && info.length == 2) {
				int id = 0;
				int level = 0;
				int chance = 0;
				try {
					id = Integer.parseInt(info[0]);
					level = Integer.parseInt(info[1]);
					if (infochance != null) {
						chance = Integer.parseInt(infochance);
					}
				} catch (Exception nfe) {
					// Incorrect syntax, dont add new skill
					Log.info(StringUtil.concat("> Couldnt parse ", skill, " in weapon oncrit skills! item ", toString()));
				}
				if (id > 0 && level > 0 && chance > 0) {
					skillsOnCrit = new SkillHolder(id, level);
					if (infochance != null) {
						skillsOnCritCondition = new ConditionGameChance(chance);
					}
				}
			}
		}

		changeWeaponId = set.getInteger("changeWeaponId", 0);
	}

	/**
	 * Returns the type of Weapon
	 *
	 * @return L2WeaponType
	 */
	@Override
	public L2WeaponType getItemType() {
		return type;
	}

	/**
	 * Returns the ID of the Etc item after applying the mask.
	 *
	 * @return int : ID of the Weapon
	 */
	@Override
	public int getItemMask() {
		return getItemType().mask();
	}

	/**
	 * Returns true if L2Weapon is magic type.
	 *
	 * @return boolean
	 */
	public boolean isMagicWeapon() {
		return isMagicWeapon;
	}

	/**
	 * Returns the quantity of SoulShot used.
	 *
	 * @return int
	 */
	public int getSoulShotCount() {
		return soulShotCount;
	}

	/**
	 * Returns the quatity of SpiritShot used.
	 *
	 * @return int
	 */
	public int getSpiritShotCount() {
		return spiritShotCount;
	}

	/**
	 * Returns the random damage inflicted by the weapon
	 *
	 * @return int
	 */
	public int getRandomDamage() {
		return rndDam;
	}

	/**
	 * Return the Reuse Delay of the L2Weapon.<BR><BR>
	 *
	 * @return int
	 */
	public int getReuseDelay() {
		return reuseDelay;
	}

	/**
	 * Returns the MP consumption with the weapon
	 *
	 * @return int
	 */
	public int getMpConsume() {
		return mpConsume;
	}

	/**
	 * Returns skill that player get when has equiped weapon +4  or more  (for duals SA)
	 *
	 * @return
	 */
	public L2Skill getEnchant4Skill() {
		if (enchant4Skill == null) {
			return null;
		}
		return enchant4Skill.getSkill();
	}

	/**
	 * Returns the Id in wich weapon this weapon can be changed
	 *
	 * @return
	 */
	public int getChangeWeaponId() {
		return changeWeaponId;
	}

	/**
	 * Returns array of Func objects containing the list of functions used by the weapon
	 *
	 * @param instance : L2ItemInstance pointing out the weapon
	 * @return Func[] : array of functions
	 */
	@Override
	public Func[] getStatFuncs(L2ItemInstance instance) {
		if (funcTemplates == null || funcTemplates.length == 0) {
			return emptyFunctionSet;
		}

		ArrayList<Func> funcs = new ArrayList<>(funcTemplates.length);

		Func f;
		for (FuncTemplate t : funcTemplates) {
			f = t.getFunc(instance);
			if (f != null) {
				funcs.add(f);
			}
		}

		return funcs.toArray(new Func[funcs.size()]);
	}

	/**
	 * Returns effects of skills associated with the item to be triggered onHit.
	 *
	 * @param caster : L2Character pointing out the caster
	 * @param target : L2Character pointing out the target
	 * @param crit   : boolean tells whether the hit was critical
	 * @return L2Effect[] : array of effects generated by the skill
	 */
	public L2Abnormal[] getSkillEffects(L2Character caster, L2Character target, boolean crit) {
		if (skillsOnCrit == null || !crit) {
			return emptyEffectSet;
		}
		List<L2Abnormal> effects = new ArrayList<>();

		if (skillsOnCritCondition != null) {
			Env env = new Env();
			env.player = caster;
			env.target = target;
			env.skill = skillsOnCrit.getSkill();
			if (!skillsOnCritCondition.test(env)) {
				return emptyEffectSet; // Skill condition not met
			}
		}

		byte shld = Formulas.calcShldUse(caster, target, skillsOnCrit.getSkill());
		if (!Formulas.calcSkillSuccess(caster, target, skillsOnCrit.getSkill(), shld, L2ItemInstance.CHARGED_NONE)) {
			return emptyEffectSet; // These skills should not work on RaidBoss
		}
		if (target.getFirstEffect(skillsOnCrit.getSkill().getId()) != null) {
			target.getFirstEffect(skillsOnCrit.getSkill().getId()).exit();
		}
		Collections.addAll(effects, skillsOnCrit.getSkill().getEffects(caster, target, new Env(shld, L2ItemInstance.CHARGED_NONE)));
		if (effects.isEmpty()) {
			return emptyEffectSet;
		}
		return effects.toArray(new L2Abnormal[effects.size()]);
	}

	/**
	 * Returns effects of skills associated with the item to be triggered onCast.
	 *
	 * @param caster  : L2Character pointing out the caster
	 * @param target  : L2Character pointing out the target
	 * @param trigger : L2Skill pointing out the skill triggering this action
	 * @return L2Effect[] : array of effects generated by the skill
	 */
	public L2Abnormal[] getSkillEffects(L2Character caster, L2Character target, L2Skill trigger) {
		if (skillsOnCast == null) {
			return emptyEffectSet;
		}
		if (trigger.isOffensive() != skillsOnCast.getSkill().isOffensive()) {
			return emptyEffectSet; // Trigger only same type of skill
		}
		if (trigger.isToggle() && skillsOnCast.getSkill().getSkillType() == L2SkillType.BUFF) {
			return emptyEffectSet; // No buffing with toggle skills
		}
		if (!trigger.isMagic() && skillsOnCast.getSkill().getSkillType() == L2SkillType.BUFF) {
			return emptyEffectSet; // No buffing with not magic skills
		}

		if (skillsOnCastCondition != null) {
			Env env = new Env();
			env.player = caster;
			env.target = target;
			env.skill = skillsOnCast.getSkill();
			if (!skillsOnCastCondition.test(env)) {
				return emptyEffectSet;
			}
		}

		byte shld = Formulas.calcShldUse(caster, target, skillsOnCast.getSkill());
		if (skillsOnCast.getSkill().isOffensive() &&
				!Formulas.calcSkillSuccess(caster, target, skillsOnCast.getSkill(), shld, L2ItemInstance.CHARGED_NONE)) {
			return emptyEffectSet;
		}

		// Get the skill handler corresponding to the skill type
		ISkillHandler handler = SkillHandler.getInstance().getSkillHandler(skillsOnCast.getSkill().getSkillType());

		L2Character[] targets = new L2Character[1];
		targets[0] = target;

		// Launch the magic skill and calculate its effects
		if (handler != null) {
			handler.useSkill(caster, skillsOnCast.getSkill(), targets);
		} else {
			skillsOnCast.getSkill().useSkill(caster, targets);
		}

		// notify quests of a skill use
		if (caster instanceof L2PcInstance) {
			// Mobs in range 1000 see spell
			Collection<L2Object> objs = caster.getKnownList().getKnownObjects().values();
			//synchronized (caster.getKnownList().getKnownObjects())
			{
				for (L2Object spMob : objs) {
					if (spMob instanceof L2Npc) {
						L2Npc npcMob = (L2Npc) spMob;

						if (npcMob.getTemplate().getEventQuests(Quest.QuestEventType.ON_SKILL_SEE) != null) {
							for (Quest quest : npcMob.getTemplate().getEventQuests(Quest.QuestEventType.ON_SKILL_SEE)) {
								quest.notifySkillSee(npcMob, (L2PcInstance) caster, skillsOnCast.getSkill(), targets, false);
							}
						}
					}
				}
			}
		}
		return emptyEffectSet;
	}
}
