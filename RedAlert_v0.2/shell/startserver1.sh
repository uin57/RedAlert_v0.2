#!/bin/sh
#dyansty2启动程序
#startserver.sh nodeserver

HOME_DIR=`dirname $0`;
HOME_DIR=`cd $HOME_DIR/..;pwd`;

echo Using JAVA_HOME:       $JAVA_HOME


export CLASSPATH=.   
pathtmp=''  
for jarpath in `ls $HOME_DIR/lib/*.jar`
do   
   CLASSPATH=$CLASSPATH:$jarpath      
done   
export CLASSPATH=$HOME_DIR/classes:$HOME_DIR/conf:$CLASSPATH   

case $1 in 

nodeserver)
JAVA_MEM_OPTS="-Xms1024M -Xmx2048M -Xmn512M -XX:PermSize=64M -XX:MaxPermSize=128M"
;;
mainserver)
JAVA_MEM_OPTS="-Xms512M -Xmx1024M -Xmn256M -XX:PermSize=64M -XX:MaxPermSize=128M"
;;

jobserver)
JAVA_MEM_OPTS="-Xms512M -Xmx1024M -Xmn256M -XX:PermSize=64M -XX:MaxPermSize=128M"
;;

asyncdbserver)
JAVA_MEM_OPTS="-Xms512M -Xmx1024M -Xmn256M -XX:PermSize=64M -XX:MaxPermSize=128M"
;;

esac
#JAVA_GCLOG_OPTS="-Xloggc:gc.log -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC  -XX:+PrintTenuringDistribution"
JAVA_GC_OPTS="-XX:+DisableExplicitGC -Xnoclassgc -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=5 -XX:+CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=70 -XX:MaxTenuringThreshold=4 -XX:+UseNUMA"
JAVA_OPTS="-Dserver.name=$1 -server -Djava.net.preferIPv4Stack=true  $JAVA_MEM_OPTS $JAVA_GC_OPTS $JAVA_GCLOG_OPTS"
echo JAVA_OPTS:       $JAVA_OPTS
if [ -f /usr/local/sbin/cronolog ]
then
	nohup $JAVA_HOME/bin/java $JAVA_OPTS -cp "$CLASSPATH" com.youxigu.dynasty2.core.Start $1 |/usr/local/sbin/cronolog $HOME_DIR/bin/dynasty2.out.%Y%m%d%H >>/dev/null 2>&1 &
else
	$JAVA_HOME/bin/java $JAVA_OPTS -cp "$CLASSPATH" com.youxigu.dynasty2.core.Start $1 >>$HOME_DIR/bin/dynasty2.out 2>&1 &
fi



#echo CLASSPATH:       $CLASSPATH
#-XX:SurvivorRatio=65535 -XX:MaxTenuringThreshold=0 这样设置可以去掉新生代救助空间
#-XX:+CMSScavengeBeforeRemark 
#-XX:CMSFullGCsBeforeCompaction=5
#-XX:+UseNUMA
#-XX:+AlwaysPreTouch
#-XX:+PrintFlagsFinal 显示所有JVM参数
#+CMSPermGenSweepingEnabled -XX:+CMSClassUnloadingEnabled
#JAVA_GC_OPTS="-XX:+DisableExplicitGC -Xnoclassgc -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=5 -XX:+CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=70 -XX:MaxTenuringThreshold=4 -XX:+UseNUMA"
#JAVA_GC_OPTS="-XX:+DisableExplicitGC -Xnoclassgc -XX:+UseParallelGC -XX:ParallelGCThreads=4 -XX:+UseNUMA"
#JAVA_GCLOG_OPTS="-Xloggc:gc.log -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC  -XX:+PrintTenuringDistribution"
#JAVA_GCLOG_OPTS=-Xloggc:gc.log  
#-XX:+PrintClassHistogram
#
#-Dnet.spy.log.LoggerImpl=com.ibatis.sqlmap.engine.cache.memcached.Self4JLogger 
#-Djgroups.logging.log_factory_class=com.ibatis.sqlmap.engine.cache.memcached.broadcast.jgroup.Self4JLogFactory
#JAVA_OPTS="-server -Djava.net.preferIPv4Stack=true  -Xms512M -Xmx1024M -Xmn256M -XX:PermSize=64M -XX:MaxPermSize=128M -XX:SurvivorRatio=4 $JAVA_GC_OPTS $JAVA_GCLOG_OPTS"
#JAVA_OPTS="-server -Djava.net.preferIPv4Stack=true  $JAVA_MEM_OPTS $JAVA_GC_OPTS $JAVA_GCLOG_OPTS"
#echo JAVA_OPTS:       $JAVA_OPTS
#-Dxmemcached.jmx.enable=true -Dxmemcached.statistics.enable=true -Dxmemcached.rmi.port=7077 -Dxmemcached.rmi.name=xmemcachedServer
#service:jmx:rmi:///jndi/rmi://172.16.0.59:7077/xmemcachedServer
#$JAVA_HOME/bin/java $JAVA_OPTS -cp "$CLASSPATH" com.youxigu.dynasty2.core.Start $1 >>$HOME_DIR/bin/dynasty2.out 2>&1 &
#echo $[$! - 1] > $HOME_DIR/bin/$1.pid;
#echo -e "\033[32;31;1;2m start server success!! \033[m";
#tail -f $HOME_DIR/bin/dynasty2.out;
#;;
# esac	
