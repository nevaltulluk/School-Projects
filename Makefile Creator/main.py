#!/usr/bin/python

import fnmatch
import os
import re
from collections import deque
from collections import defaultdict
import sys
import makefile

workdir = sys.argv[1]

HEADERS = {}

used = []
unused = []
includes = []
files = []
qlist = deque([workdir])

sources = defaultdict(list)

while qlist:
    currentdir = qlist.popleft()
    dircontents = os.listdir(currentdir)
    for name in dircontents:
        currentitem = currentdir + "/" + name
        if os.path.isdir(currentitem):
            qlist.append(currentitem)
        else:
            if(currentitem.endswith('.h')):
                if currentitem not in HEADERS:
                    HEADERS[currentitem.rsplit('/', 1)[1]] = currentitem.replace(workdir + "/", "")

            textfile = open(currentitem, 'r')
            reg = re.compile("#include \"(.*)\"")
            for line in textfile:
                if reg.findall(line):
                    if "main" in currentitem:
                        used.append(reg.findall(line))

                    if reg.findall(line) not in includes:
                        includes.append(reg.findall(line))

                    sources[currentitem].append(reg.findall(line)[0])

            textfile.close()

for current in includes:
    if current not in used:
        unused.append(current)

if len(unused) > 0:
    raise Exception("Theres an unused header file(s)", unused)

makefile.Makefile(workdir, sources, HEADERS).generate()
