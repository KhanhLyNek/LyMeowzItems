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
    extras = sorted(now - set(dest.values()))
    modified = []
    for before, after in dest.items():
        source = root / after
        if source.exists():
            expected = convert(git(root, "show", base + ":" + before))
            if expected != source.read_text("utf-8"):
                modified.append(before + " -> " + after)
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
    for p in previous:
        if p.startswith("resources/") and p.endswith((".yml", ".yaml", ".json", ".properties")):
            source = root / p
            old = git(root, "show", base + ":" + p)
            if not source.exists():
                resource_delta.append([p, "MISSING"])
            elif source.read_text("utf-8") != old:
                resource_delta.append([p, "NAMESPACE_ONLY" if convert(old) == source.read_text("utf-8") else "NONMECHANICAL"])
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
                extra=extras, nonmechanical_java=modified, stale_refs=stale,
                package_mismatch=paths, resources_changed=resource_delta, metadata=metadata)

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
critical = ["missing", "extra", "nonmechanical_java", "stale_refs", "package_mismatch", "metadata"]
failure = any(r[k] for r in reports for k in critical)
print("DEEP MIGRATION AUDIT:", "FAIL" if failure else "PASS")
raise SystemExit(1 if failure else 0)
