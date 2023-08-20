#!/bin/bash

# Export utf-8
export LC_ALL=C.UTF-8

# Get environment variables to show up in SSH session
eval $(printenv | sed -n "s/^\([^=]\+\)=\(.*\)$/export \1=\2/p" | sed 's/"/\\\"/g' | sed '/=/s//="/' | sed 's/$/"/' >> /etc/profile)

# starting sshd process
echo "start sshd"
/usr/sbin/sshd

echo "start java"
java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
    -Duser.timezone=${USER_TIMEZONE} \
    -Dfile.encoding=UTF-8 \
    -Djava.library.path=/app/opencv \
    -jar /app/ROOT.jar