#! /bin/sh


start(){
        echo "Starting jboss.."

        # If using an SELinux system such as RHEL 4, use the command below
        # instead of the "su":
        # eval "runuser - jboss -c '/opt/jboss/current/bin/run.sh > /dev/null 2> /dev/null &'
        # if the 'su -l ...' command fails (the -l flag is not recognized by my su cmd) try:
        #   sudo -u jboss /opt/jboss/bin/run.sh > /dev/null 2> /dev/null &
        # /sbin/iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080 
        # /sbin/iptables -t nat -A PREROUTING -p tcp --dport 443 -j REDIRECT --to-port 8443 
        #sudo -u jboss /opt/jboss/bin/run.sh -Djava.awt.headless=true -c inventory -b 0.0.0.0 > /dev/null 2> /dev/null &
        su - jboss -c '/opt/jboss/bin/run.sh -Djava.awt.headless=true -c inventory -b 0.0.0.0 > /dev/null 2> /dev/null &'
}

stop(){
        echo "Stopping jboss.."

        # If using an SELinux system such as RHEL 4, use the command below
        # instead of the "su":
        # eval "runuser - jboss -c '/opt/jboss/current/bin/shutdown.sh -S &'
        # if the 'su -l ...' command fails try:
        #   sudo -u jboss /opt/jboss/bin/shutdown.sh -S &
        #sudo -u jboss /opt/jboss/bin/shutdown.sh -S &
        su - jboss -c '/opt/jboss/bin/shutdown.sh -S &'
}

restart(){
        stop
# give stuff some time to stop before we restart
        sleep 60
# protect against any services that can't stop before we restart (warning this kills all Java instances running as 'jboss' user)
        su -l jboss -c 'killall java'
# if the 'su -l ...' command fails try:
        #   sudo -u jboss killall java
        start
}




case "$1" in
  start)
        start
        ;;
  stop)
        stop
        ;;
  restart)
        restart
        ;;
  *)
        echo "Usage: jboss {start|stop|restart}"
        exit 1
esac

exit 0

