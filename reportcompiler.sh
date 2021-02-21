#!/usr/bin/env bash
# devnull@libcrack.so
# Mon Oct  3 06:35:01 CEST 2016

jarfile=ReportCompiler.jar
scriptpath="$(realpath "$0")"
workdir="$(dirname "${scriptpath}")"

test -z "$1" || workdir="$1"
test -d "$workdir" ||  {
    echo "ERROR: cannot access $workdir"
    exit 1
}

echo ">> Entering $workdir"
cd "$workdir" || exit 1

echo ">> Launching $jarfile"
java -jar "$jarfile" &

exit $?
