#!/usr/bin/env bash
# devnull@libcrack.so
# Mon Oct  3 06:35:01 CEST 2016

scriptpath="$(realpath "$0")"
workdir="$(dirname "${scriptpath}")"
jarfile="${workdir}/target/ReportCompiler.jar"
javaopt="-Xms128M -Xmx512M -Dawt.useSystemAAFontSettings=on"

test -f "${jarfile}" ||  {
    printf ">> \e[31mERROR:\e[0m Cannot find ${jarfile}\n"
    printf ">> Moving into ${workdir}\n"
    cd "${workdir}"
    printf ">> Compiling ${jarfile}\n"
    mvn package || exit ${?}
    cd "${OLDPWD}"
}

printf ">> Launching \e[1m${jarfile}\e[0m\n"
exec java -jar "${jarfile}" ${javaopt}

exit ${?}
