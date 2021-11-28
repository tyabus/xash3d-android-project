#!/bin/bash

GIT_REV_XML=res/values/git-rev.xml

mkdir -p $(dirname $GIT_REV_XML)

echo '<?xml version="1.0" encoding="utf-8"?>' > $GIT_REV_XML
echo '<resources>' >> $GIT_REV_XML
echo -n '<string name="git_revisions">' >> $GIT_REV_XML
echo -n '<b>Version information:</b> ' $*_endl| sed -e s/_endl/'\\n'/ >> $GIT_REV_XML
git submodule --quiet foreach --recursive 'echo -n \<b\>`basename $name`:\</b\>\ `git log --abbrev-commit --pretty=oneline -1` _endl' | sed -e 's/_endl/\\n/g' >> $GIT_REV_XML
echo -n $USER@$(hostname) $(date +%H:%M:%S-%d-%m-%y) >> $GIT_REV_XML
sed -i "s/notset/$(printf %04d $(( ( $(date +'%s') - $(date -ud '2015-04-01' +'%s') )/60/60/24 )))/g" AndroidManifest.xml
echo '</string>' >> $GIT_REV_XML
echo '</resources>' >> $GIT_REV_XML
cat $GIT_REV_XML
