import re
import os
import sys
import glob

IN_DIR = sys.argv[1]
RE = re.compile('<MEMBER name="(.+?)"')
V = set()

for filename in glob.glob(os.path.join(IN_DIR, '*.xml')):
    fin = open(filename)
    transfer = False
    verbs = set()

    for line in fin:
        m = RE.match(line.strip())
        if m: verbs.add(m.group(1))
        if 'has_possession' in line: transfer = True

    if transfer: V.update(verbs)

for verb in sorted(list(V)): print verb
print len(V)