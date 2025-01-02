# 第一階段：構建應用
FROM openjdk:17-jdk-slim as build

# 安裝 Maven
RUN apt-get update && apt-get install -y maven
WORKDIR /app

# 複製 Maven 配置文件
COPY pom.xml .

# 複製源碼
COPY src ./src

# 執行 Maven 打包應用，生成 JAR 文件
RUN mvn clean package -DskipTests

# 第二階段：運行應用
FROM openjdk:17-oracle


WORKDIR /app

# 從第一階段（名為 build 的階段）複製構建出的 JAR 文件
COPY --from=build /app/target/btp-0.0.1.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
