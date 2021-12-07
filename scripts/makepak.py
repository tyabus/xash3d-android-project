#!/usr/bin/env python3

import sys
import struct
import os

# Dummy class for stuffing the file headers into
class FileEntry:
    pass

# Arguments are source directory, then target filename e.g. "pak0.pak"
if len(sys.argv) == 3: # Check if arguments are even specified
    rootdir = sys.argv[1]
    pakfilename = sys.argv[2]
else:
    print(sys.argv[0]+": <root dir> <pakfilename>")
    sys.exit(0)

try:
    pakfile = open(pakfilename,"wb")
except IOError:
    print(pakfilename+": IO Error")
    sys.exit(1)

# Write a dummy header to start with
pakfile.write(struct.Struct("<4s2l").pack(b"PACK",0,0))

# Walk the directory recursively, add the files and record the file entries
offset = 12
fileentries = []
for root, subFolders, files in os.walk(rootdir):
    for file in files:
        entry = FileEntry()
        impfilename = os.path.join(root,file)
        entry.filename = os.path.relpath(impfilename,rootdir).replace("\\","/")
        if(entry.filename.startswith(".git")): continue
        print("pak: "+entry.filename)
        with open(impfilename, "rb") as importfile:
            pakfile.write(importfile.read())
            entry.offset = offset
            entry.length = importfile.tell()
            offset = offset + entry.length
        fileentries.append(entry)
tablesize = 0

# After all the file data, write the list of entries
for entry in fileentries:
    pakfile.write(struct.Struct("<56s").pack(entry.filename.encode("ascii")))
    pakfile.write(struct.Struct("<l").pack(entry.offset))
    pakfile.write(struct.Struct("<l").pack(entry.length))
    tablesize = tablesize + 64

# Return to the header and write the values correctly
pakfile.seek(0)
pakfile.write(struct.Struct("<4s2l").pack(b"PACK",offset,tablesize))
