# 使用 OpenJDK 17 作為基底
FROM openjdk:17

# 複製 target 資料夾內的 jar 到容器中
COPY projectA.jar app.jar

# 開放 8080 port
EXPOSE 8080

# 設定啟動指令
ENTRYPOINT ["java", "-jar", "app.jar"]