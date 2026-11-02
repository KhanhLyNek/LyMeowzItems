#!/usr/bin/env python3
"""Guard full LyMeowz Java package relocation in production source."""
from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
source = root / "src"
java = sorted(source.rglob("*.java"))
assert len(java) >= 330, f"Unexpected source loss: {len(java)} Java files"
problems = []
for file in java:
    path = file.relative_to(source).as_posix()
    content = file.read_text("utf-8")
    package = re.search(r"(?m)^package\s+([\w.]+);", content)
    expected = file.parent.relative_to(source).as_posix().replace("/", ".")
    if not path.startswith("com/lymeowz/item/") or package is None or package.group(1) != expected:
        problems.append(f"{path}: package {package.group(1) if package else 'MISSING'} vs {expected}")
    if any(name in content for name in ("com.praya.myitems", "api.praya.myitems", "core.praya.myitems",
                                      "com.praya.agarthalib", "api.praya.agarthalib", "core.praya.agarthalib")):
        problems.append(f"{path}: old Java namespace reference")
if problems:
    raise SystemExit("Namespace migration incomplete:\n" + "\n".join(problems[:30]))
print(f"PASS: {len(java)} production Java files under com.lymeowz.item; no old namespaces")
