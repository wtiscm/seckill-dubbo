**RabbitMQ安装配置**  
- 安装erlang  
下载地址：http://www.erlang.org/downloads  
yum install  ncurses-devel  
tar xf otp_src_20.1.tar.gz  
cd otp_src_20.1  
./configure --prefix=/usr/local/erlang20 --without-javac  
 make  
 make install  
 erl验证  
- 安装python  
yum install python -y
- 安装simplejson  
yum install xmlto -y  
yum install python-simplejson -y  
- 安装RabbitMQ  
  - 下载源码：http://www.rabbitmq.com/download.html  
Generic Unix -> rabbitmq-server-generic-unix-3.6.14.tar.xz  
xz -d rabbitmq-server-generic-unix-3.6.14.tar.xz  
tar xf rabbitmq-server-generic-unix-3.6.14.tar  
mv rabbitmq_server-3.6.14 /usr/local/rabbitmq  
  - 设置环境变量
export PATH=$PATH:/usr/local/ruby/bin:/usr/local/erlang20/bin:/usr/local/rabbitmq/sbin  
source /etc/profile  
./rabbitmq-server启动rabbitMQ server 5672端口监听  
rabbitmqctl stop 停止  

  - 设置guest可以远程连接  
修改rabbitmq的配置/usr/local/rabbitmq/etc/rabbitmq/rabbitmq.config  
添加：[{rabbit, [{loopback_users, []}]}].  
重新启动  
http://www.rabbitmq.com/access-control.html  

  - 启用管理控制台  
./sbin/rabbitmq-plugins enable rabbitmq_management  
重启rabbitmq  
打开浏览器输入： http://127.0.0.1:15672/， 用户名guest，密码guest  
Topic：*1个单词，#0个或者多个单词。

**pom.xml:**  
```
<!-- mq -->  
<dependency>  
    <groupId>com.rabbitmq</groupId>  
    <artifactId>amqp-client</artifactId>  
    <version>3.5.1</version>  
</dependency>  
<dependency>  
    <groupId>org.springframework.amqp</groupId>  
    <artifactId>spring-rabbit</artifactId>  
    <version>1.4.5.RELEASE</version>  
</dependency>  
```
**如何保证重启服务器消息也不丢失**
- Exchage持久化
- Queue持久化
- 发送消息的时候，设置MessageDeliveryMode为MessageDeliveryMode.PERSISTENT，默认行为
- 消息手动确认
