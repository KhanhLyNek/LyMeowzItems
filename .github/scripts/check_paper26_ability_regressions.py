#!/usr/bin/env python3
"""Ability contract guards: 25 registered skills, safe lore parsing and Venom Spread duration.

The original 43-file baseline is deliberately not rewritten; the one
reviewed VenomSpread fix is pinned to an exact git blob in check_legacy_logic.
"""
from pathlib import Path
import re

root = Path(__file__).resolve().parents[2]
skills = root / "src/com/lymeowz/item/builder/ability/weapon"
register = (root / "src/com/lymeowz/item/manager/register/RegisterAbilityWeaponManager.java").read_text("utf-8")
manager = (root / "src/com/lymeowz/item/manager/game/AbilityWeaponManager.java").read_text("utf-8")
venom = (skills / "AbilityWeaponVenomSpread.java").read_text("utf-8")
files = sorted(skills.glob("AbilityWeapon*.java"))
assert len(files) == 25, f"Expected 25 skill classes, got {len(files)}"
for path in files:
    name = path.stem
    body = path.read_text("utf-8")
    assert f"class {name} " in body, name
    assert "void cast(final Entity caster, final Entity target" in body, name
    assert f"{name}.getInstance()" in register, name

assert register.count("this.register(abilityWeapon") == 25, "Some skill not registered"
assert 'if (grade > 0 && abilityWeapon != null)' in manager, "Unknown lore skill NPE"
# createPotion(PotionEffectType, duration, grade): real AgarthaLib signature.
correct = "PotionUtil.createPotion(potionType, duration, grade)"
wrong = "PotionUtil.createPotion(potionType, grade, duration)"
assert correct in venom, "Secondary Venom Spread target has wrong potion argument order"
assert wrong not in venom, "Wrong duration/grade potion order reintroduced"
assert "this.getEffectDuration(grade)" in venom, "Effect duration no longer from grade config"
# These 3 effects also apply to mobs; only send chat to actual players.
# The original unguarded CommandSender casts threw ClassCastException for mobs.
for name in ("Freeze", "Roots", "Curse"):
    body = (skills / ("AbilityWeapon" + name + ".java")).read_text("utf-8")
    assert "if (attacker instanceof Player playerAttacker)" in body, name
    assert "if (victims instanceof Player playerVictims)" in body, name
    assert "messageAttacker.sendMessage((CommandSender)attacker" not in body, name
    assert "messageVictims.sendMessage((CommandSender)victims" not in body, name
# Venom Blast tracks the SAME living target, but samples its current
# position separately at every 5-tick ring and the final explosion.
blast = (skills / "AbilityWeaponVenomBlast.java").read_text("utf-8")
tick_section = blast.split("public void run() {", 1)[1]
assert "victims.getLocation().add(0.0, 0.5, 0.0)" in tick_section
assert "victims.isDead()" in tick_section and "!victims.isValid()" in tick_section
assert "victims.getWorld() != castWorld" in tick_section
assert "CombatUtil.getNearbyUnits(location, 3.0)" in tick_section, "Explosion target range not dynamic"
assert "CombatUtil.getNearbyUnits(location, this.radius + 1.0)" in tick_section
assert "final Location flowerPosition = location.clone().add(this.x, 0.0, this.z);" in tick_section
assert "final Location locationFlower = flowerPosition.clone();" in tick_section
assert "final Location location = victims.getLocation()" not in blast.split("public void cast(", 1)[1].split("public void run() {", 1)[0], "Location must not be fixed at cast"
# Verify that secondary ability procs are scaled centrally and capped
# without altering the 25 protected, original skill implementations.
listener = (root / "src/com/lymeowz/item/listener/main/ListenerEntityDamageByEntity.java").read_text("utf-8")
chain = (root / "src/com/lymeowz/item/ability/SkillProcChain.java").read_text("utf-8")
assert "MAX_CHAIN_DEPTH = 4" in chain, "Missing bounded secondary skill-proc depth"
assert "0.2, 0.04, 0.008, 0.0016" in chain, "Missing 80% per generation chance falloff"
assert "SkillProcChain.depthForHit(attacker, isSkillDamage || isAreaDamage)" in listener
assert "abilityWeaponManager.getMapAbilityWeapon(attacker, true, skillChainDepth)" in listener
assert "SkillProcChain.castAtDepth(attacker, skillChainDepth" in listener
assert "SkillProcChain.roll(" in manager, "Secondary lore skill chance is not attenuated"
print("PASS: 25 skill classes, Venom Spread, mob casts and bounded secondary weapon proc wiring")
