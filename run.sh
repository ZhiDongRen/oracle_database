#!/bin/sh

echo -n "Oracle database username: "
read ORACLE_USER

echo -n "Oracle database password: "
read -s ORACLE_PASS

# run target does not use a console, so jLine etc. does not work, see https://github.com/jline/jline3/issues/77
. ./gradlew "-Dlogin=${ORACLE_USER}" "-Dpassword=${ORACLE_PASS}" run $@
