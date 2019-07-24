**Zookeeper 配置**

-------------------------------------------------------------------
- 检测集群时间是否同步  
- 检测防火墙是否关闭  
- 检测主机 ip映射有没有配置  

-------------------------------------------------------------------

**下载安装包、解压**

tar -zxvf zookeeper-3.4.5.tar.gz  
mv zookeeper-3.4.5 zookeeper 

-------------------------------------------------------------------

**修改Zookeeper配置文件**

cd zookeeper/conf  
cp zoo_sample.cfg zoo.cfg  
vi zoo.cfg  
- 添加内容：  
dataDir=/root/apps/zookeeper/zkdata  
server.1=mini1:2888:3888     ## (心跳端口、选举端口)  
server.2=mini2:2888:3888  
server.3=mini3:2888:3888  
- 创建文件夹：  
cd /home/hadoop/zookeeper/  
mkdir zkdata  
- 在data文件夹下新建myid文件，myid的文件内容为：  
cd zkdata  
echo 1 > myid 

-------------------------------------------------------------------
**分发安装包到其他机器**   
scp -r /root/apps root@mini2:/root/  
scp -r /root/apps root@mini3:/root/

-------------------------------------------------------------------

**修改其他机器的配置文件**  
修改myid文件  
到mini2上：修改myid为：2  
到mini3上：修改myid为：3  

-------------------------------------------------------------------
**启动（每台机器）**  
zkServer.sh start  
或者编写一个脚本来批量启动所有机器：  
for host in "mini1 mini2 mini3"  
do  
ssh $host "source/etc/profile;/root/apps/zookeeper/bin/zkServer.sh start"

-------------------------------------------------------------------
**查看集群状态**  
jps（查看进程）  
zkServer.sh status（查看集群状态，主从信息）  

如果启动不成功，可以观察zookeeper.out日志，查看错误信息进行排查  

-------------------------------------------------------------
**配置文件中参数说明:**

tickTime这个时间是作为zookeeper服务器之间或客户端与服务器之间维持心跳的时间间隔,也就是说每个tickTime时间就会发送一个心跳。

initLimit这个配置项是用来配置zookeeper接受客户端（这里所说的客户端不是用户连接zookeeper服务器的客户端,而是zookeeper服务器集群中连接到leader的follower 服务器）初始化连接时最长能忍受多少个心跳时间间隔数。

当已经超过10个心跳的时间（也就是tickTime）长度后 zookeeper 服务器还没有收到客户端的返回信息,那么表明这个客户端连接失败。总的时间长度就是 10*2000=20秒。

syncLimit这个配置项标识leader与follower之间发送消息,请求和应答时间长度,最长不能超过多少个tickTime的时间长度,总的时间长度就是5*2000=10秒。

dataDir顾名思义就是zookeeper保存数据的目录,默认情况下zookeeper将写数据的日志文件也保存在这个目录里。

clientPort这个端口就是客户端连接Zookeeper服务器的端口,Zookeeper会监听这个端口接受客户端的访问请求。

server.A=B:C:D中的A是一个数字,表示这个是第几号服务器,B是这个服务器的IP地址，C第一个端口用来集群成员的信息交换,表示这个服务器与集群中的leader服务器交换信息的端口，D是在leader挂掉时专门用来进行选举leader所用的端口。

----------------------------------------------------------------
