FROM 172.21.78.232:8002/yunlu/openjdk:8-jdk-alpine
#ENV 变量
ENV TZ='Asia/Shanghai'

#COPY
COPY  ./target/*.jar /root/search-micro-1.0.jar


#EXPOSE 服务端口
EXPOSE  9004

#ENTRYPOINT 启动命令
ENTRYPOINT  java -jar  /root/search-micro-1.0.jar