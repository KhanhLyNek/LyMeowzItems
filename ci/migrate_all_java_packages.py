#!/usr/bin/env python3
"""One-shot package relocation for LyMeowzItem and LyMeowzLib imports; can be dry-run locally.

Only Java namespace/path strings and build/test references are rewritten.
Never alters YAML gameplay keys, NBT identifiers, command aliases or permissions.
"""
from __future__ import annotations
import argparse
import json
import re
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PREFIXES = (
    ("core.praya.myitems", "com.lymeowz.item.core"),
    ("api.praya.myitems", "com.lymeowz.item.api"),
    ("com.praya.myitems", "com.lymeowz.item"),
    ("core.praya.agarthalib", "com.lymeowz.lib.core"),
    ("api.praya.agarthalib", "com.lymeowz.lib.api"),
    ("com.praya.agarthalib", "com.lymeowz.lib"),
)
REPLACEMENTS = [(a, b) for a, b in PREFIXES]
REPLACEMENTS += [(a.replace(".", "/"), b.replace(".", "/")) for a, b in PREFIXES]
TEXT_EXT = {".java", ".kt", ".kts", ".gradle", ".py", ".yml", ".yaml", ".json", ".xml",
            ".properties", ".txt", ".md", ".sh", ".toml"}
SKIP = {".git", ".gradle", "build", ".idea", ".venv", "node_modules", "agarthalib"}

def transformed(text: str) -> str:
    for old, new in REPLACEMENTS:
        text = text.replace(old, new)
    # Publish Java artifacts under the new group, matching MyItems' next cutover.
    text = re.sub(r'(?m)^group\s*=\s*"com\.praya"\s*$', 'group = "com.lymeowz"', text)
    text = text.replace("com.praya:LyMeowzLib", "com.lymeowz:LyMeowzLib")
    return text

def new_path(path: Path) -> Path:
    rel = path.relative_to(ROOT).as_posix()
    if path.suffix == ".java":
        for old, new in REPLACEMENTS[len(PREFIXES):]:
            rel = rel.replace("/" + old + "/", "/" + new + "/")
    return ROOT / rel

def git(*args: str) -> str:
    return subprocess.check_output(["git", *args], cwd=ROOT, text=True).strip()

def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--apply", action="store_true")
    args = parser.parse_args()
    files = [p for p in ROOT.rglob("*") if p.is_file()
             and not any(x in SKIP for x in p.relative_to(ROOT).parts)
             and p.suffix.lower() in TEXT_EXT]
    changes: list[tuple[Path, Path, str]] = []
    occupied = set(files)
    for src in files:
        if src == Path(__file__).resolve():
            continue  # Keep migration mapping immutable for reruns.
        dest = new_path(src)
        if dest != src and dest.exists() and dest not in {a for a, _, _ in changes}:
            raise SystemExit(f"PACKAGE COLLISION: {src} -> {dest}")
        old = src.read_text("utf-8")
        changed = transformed(old)
        if src != dest or changed != old:
            changes.append((src, dest, changed))
    all_dest = [dst for _, dst, _ in changes]
    if len(set(all_dest)) != len(all_dest):
        raise SystemExit("PACKAGE COLLISION: duplicate destination paths")
    for src, dst, _ in changes:
        if dst != src and dst in occupied and dst not in {a for a, _, _ in changes}:
            raise SystemExit(f"PACKAGE COLLISION: existing {dst}")
    if not args.apply:
        print(f"DRY RUN: {len(changes)} text/path changes")
        return
    moved = [(s, d, c) for s, d, c in changes if s != d]
    for src, dst, _ in moved:
        src.unlink()
    for src, dst, text in changes:
        dst.parent.mkdir(parents=True, exist_ok=True)
        dst.write_text(text, "utf-8")
    # Rebaseline hash-only source guard after exactly mechanical namespace moves.
    baseline = ROOT / ".github/legacy-logic-baseline.json"
    if baseline.exists():
        data = json.loads(baseline.read_text("utf-8"))
        remap = {s.relative_to(ROOT).as_posix(): d.relative_to(ROOT).as_posix()
                 for s, d, _ in moved}
        new_files = {}
        for old_path, sha in data["files"].items():
            new_name = remap.get(old_path, transformed(old_path))
            item = ROOT / new_name
            if not item.is_file():
                raise SystemExit(f"Missing gameplay baseline file: {new_name}")
            new_files[new_name] = git("hash-object", str(item))
        data["files"] = new_files
        baseline.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", "utf-8")
    bad = []
    for file in ROOT.rglob("*.java"):
        if ".git" in file.parts or "build" in file.parts:
            continue
        s = file.read_text("utf-8")
        if any(old in s for old, _ in PREFIXES):
            bad.append(str(file.relative_to(ROOT)))
        if any(seg in file.parts for seg in ("praya",)):
            bad.append(str(file.relative_to(ROOT)))
    if bad:
        raise SystemExit("Old Java packages remain: " + ", ".join(bad[:20]))
    # A broad gitignore pattern such as "lib" can silently discard all moved
    # sources. Stage and validate the entire tracked Java inventory first.
    subprocess.run(["git", "add", "-A"], cwd=ROOT, check=True)
    tracked = git("ls-files").splitlines()
    tracked_java = [name for name in tracked if name.endswith(".java")]
    expected_java = [p for p in ROOT.rglob("*.java")
                     if not any(x in SKIP for x in p.relative_to(ROOT).parts)]
    if len(tracked_java) != len(expected_java) or len(tracked_java) < 345:
        raise SystemExit(f"Missing staged Java files: tracked={len(tracked_java)}, "
                         f"filesystem={len(expected_java)}")
    if any("/praya/" in p for p in tracked_java):
        raise SystemExit("Legacy Java paths remain in Git index")
    print(f"OK: migrated {len(moved)} Java paths, "
          f"{len(tracked_java)} tracked Java files and {len(changes)} references")

if __name__ == "__main__":
    main()
