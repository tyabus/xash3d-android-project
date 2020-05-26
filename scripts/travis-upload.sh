#!/bin/bash

# Upload travis generated APKs to transfer.sh

function getDaysSinceRelease
{
	printf %04d $(( ( $(date +'%s') - $(date -ud '150401' +'%s') )/60/60/24 ))
}

DAYSSINCERELEASE=`getDaysSinceRelease`
COMMITHASH=$(git rev-parse --short HEAD)
CURRENTBRANCH=$(git rev-parse --abbrev-ref HEAD)

function generateFileName
{
	echo "xash3d-$DAYSSINCERELEASE-$(date +"%H-%M")-$1-$COMMITHASH.apk"
}

# transfer.sh
TRANSFERSH_ARMV5=`curl --upload-file xashdroid-armv5.apk https://transfer.sh/$(generateFileName armv5)`
TRANSFERSH_ARMV6=`curl --upload-file xashdroid-armv6.apk https://transfer.sh/$(generateFileName armv6)`
TRANSFERSH_ARMV7=`curl --upload-file xashdroid-armv7.apk https://transfer.sh/$(generateFileName armv7)`
TRANSFERSH_ARMV7TEGRA2=`curl --upload-file xashdroid-armv7-tegra2.apk https://transfer.sh/$(generateFileName armv7-tegra2)`
TRANSFERSH_X86=`curl --upload-file xashdroid-x86.apk https://transfer.sh/$(generateFileName x86)`

echo "Transfer.sh links:"
echo "armv5:              ${TRANSFERSH_ARMV5}"
echo "armv6:              ${TRANSFERSH_ARMV6}"
echo "armv7:              ${TRANSFERSH_ARMV7}"
echo "tegra2:             ${TRANSFERSH_ARMV7TEGRA2}"
echo "x86:                ${TRANSFERSH_X86}"

exit 0
