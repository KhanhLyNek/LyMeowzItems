#!/usr/bin/env python3
"""Compare both repositories against their last pre-rename source revisions."""
import json
import re
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
REPOS = [
    ("item", ROOT, "a0586071b2a8ef32e53d6ea80ec82053f4ce5f44", "com.lymeowz.item"),
    ("lib", ROOT / "agarthalib", "3e1c94d5cbf1f5b013a89a009dbf88ee46aa0686", "com.lymeowz.lib"),
]
# Explicitly reviewed departures from the original rename-only snapshot.
# This is NOT an automatic baseline refresh: production Java changes must
# match the exact reviewed Git blob. Legacy gameplay/YAML hash guards run
# separately in each repository's Paper 26.3 CI.
REVIEWED_NONMECHANICAL = {
    "item": {
        "src/com/lymeowz/item/api/builder/player/PlayerPowerCooldown.java": "ad815862aeb3d286789bd957abd079f25a3fb03a",
        "src/com/lymeowz/item/config/plugin/MainConfig.java": "2b941692b0202c1476f568c7a14897d230230a74",
        "src/com/lymeowz/item/listener/main/ListenerEntityDamageByEntity.java": "affc3e4ce0b3515e6397103e240388fe7ff57a25",
        "src/com/lymeowz/item/manager/game/AbilityWeaponManager.java": "8f0d63dfb784a6b86df80cae6bdd148236f4bcbe",
        "src/com/lymeowz/item/builder/ability/weapon/AbilityWeaponVenomBlast.java": "08c6b619882e2031519da8a469bf81a8021bf331",
    },
    "lib": {
        "src/com/lymeowz/lib/modern/Paper26ItemTextColors.java": "dd6df8e6a7ede11a45759cdb752b698d936b45c2",
        "src/com/lymeowz/lib/utility/CombatUtil.java": "98ab50930e811a362d209c6a98b871d5e55324f8",
        "src/com/lymeowz/lib/core/builder/stats/Stats.java": "7ec8dd72d47fb0b09b5be99e3c82d51458a1e827",
    },
}
REVIEWED_NEW_SOURCE = {
    "item": {
        "src/com/lymeowz/item/ability/SkillProcChain.java": "4624d329ada3a0ca3361b8297f3a338c772dc969",
    },
    "lib": {},
}

REMAP = [
    ("core.praya.agarthalib", "com.lymeowz.lib.core"),
    ("api.praya.agarthalib", "com.lymeowz.lib.api"),
    ("com.praya.agarthalib", "com.lymeowz.lib"),
    ("core.praya.myitems", "com.lymeowz.item.core"),
    ("api.praya.myitems", "com.lymeowz.item.api"),
    ("com.praya.myitems", "com.lymeowz.item"),
]

def git(root, *args):
    return subprocess.check_output(["git", "-C", str(root), *args], text=True)

def convert(s):
    for old, new in REMAP:
        s = s.replace(old, new)
    for old, new in REMAP:
        s = s.replace(old.replace(".", "/"), new.replace(".", "/"))
    return s

def scan(repo, root, base, namespace):
    git(root, "cat-file", "-e", base + "^{commit}")
    previous = git(root, "ls-tree", "-r", "--name-only", base).splitlines()
    current = git(root, "ls-files").splitlines()
    past = [p for p in previous if p.endswith(".java") and p.startswith(("src/", "ci/"))]
    now = {p for p in current if p.endswith(".java") and p.startswith(("src/", "ci/"))}
    dest = {p: convert(p) for p in past}
    missing = [p + " -> " + q for p, q in dest.items() if q not in now]
    extras, reviewed_extras = [], []
    for extra in sorted(now - set(dest.values())):
        # New isolated test fixtures can evolve with new Paper integrations;
        # none is part of the production plugin's source set.
        if extra.startswith("ci/"):
            reviewed_extras.append(extra + " (CI only)")
        elif git(root, "hash-object", "--", extra).strip() == REVIEWED_NEW_SOURCE[repo].get(extra):
            reviewed_extras.append(extra + " (approved new source blob)")
        else:
            extras.append(extra)
    modified, reviewed_nonmechanical = [], []
    for before, after in dest.items():
        source = root / after
        if source.exists():
            expected = convert(git(root, "show", base + ":" + before))
            if expected != source.read_text("utf-8"):
                change = before + " -> " + after
                if after.startswith("ci/"):
                    reviewed_nonmechanical.append(change + " (CI fixture)")
                elif git(root, "hash-object", "--", after).strip() == REVIEWED_NONMECHANICAL[repo].get(after):
                    reviewed_nonmechanical.append(change + " (approved exact source blob)")
                else:
                    modified.append(change)
    stale, paths = [], []
    for f in sorted(now):
        text = (root / f).read_text("utf-8")
        if any(old in text for old, _ in REMAP):
            stale.append(f)
        if f.startswith("src/"):
            match = re.search(r"(?m)^package\s+([\w.]+)\s*;", text)
            expected = f[4:].rsplit("/", 1)[0].replace("/", ".")
            if not f.startswith("src/" + namespace.replace(".", "/") + "/") or match is None or match.group(1) != expected:
                paths.append(f)
    resource_delta = []
    stale_resource_refs = []
    for p in previous:
        if p.startswith("resources/") and p.endswith((".yml", ".yaml", ".json", ".properties")):
            source = root / p
            old = git(root, "show", base + ":" + p)
            if not source.exists():
                resource_delta.append([p, "MISSING"])
            else:
                current = source.read_text("utf-8")
                for legacy, _ in REMAP:
                    if legacy in current:
                        stale_resource_refs.append(p + ": " + legacy)
                if current != old:
                    resource_delta.append([p, "NAMESPACE_ONLY" if convert(old) == current else "NONMECHANICAL"])
    gradle = (root / "build.gradle.kts").read_text("utf-8")
    plugin = (root / "resources/plugin.yml").read_text("utf-8")
    metadata = []
    if 'group = "com.lymeowz"' not in gradle:
        metadata.append("Maven group")
    if "main: " + namespace + (".LyMeowzItem" if repo == "item" else ".LyMeowzLib") not in plugin:
        metadata.append("plugin main")
    if repo == "item" and 'compileOnly("com.lymeowz:LyMeowzLib:' not in gradle:
        metadata.append("cross-library Maven dependency")
    return dict(repo=repo, before=len(past), after=len(now), missing=missing,
                extra=extras, reviewed_extra=reviewed_extras,
                nonmechanical_java=modified, reviewed_nonmechanical_java=reviewed_nonmechanical,
                stale_refs=stale,
                package_mismatch=paths, resources_changed=resource_delta,
                stale_resource_class_refs=stale_resource_refs, metadata=metadata)

reports = [scan(*r) for r in REPOS]
for r in reports:
    print("\n===", r["repo"].upper(), "===")
    for key, value in r.items():
        if isinstance(value, list):
            print(key, len(value))
            for item in value[:20]:
                print("   ", item)
        else:
            print(key, value)
output = ROOT / "build/reports/deep-postmigration-audit.json"
output.parent.mkdir(parents=True, exist_ok=True)
output.write_text(json.dumps(reports, indent=2, ensure_ascii=False) + "\n", "utf-8")
critical = ["missing", "extra", "nonmechanical_java", "stale_refs",
            "stale_resource_class_refs", "package_mismatch", "metadata"]
failure = any(r[k] for r in reports for k in critical)
# Reviewed migrations are reported, not concealed. Unexpected modifications
# still fail until their exact production blobs are independently approved.
print("DEEP MIGRATION AUDIT:", "FAIL" if failure else "PASS")
raise SystemExit(1 if failure else 0)
