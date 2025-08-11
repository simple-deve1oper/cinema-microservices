#FROM eclipse-temurin:21.0.6_7-jdk-alpine AS build
#
#ARG JAR_FILE
#WORKDIR /build
#
#ADD $JAR_FILE application.jar
#RUN java -Djarmode=layertools -jar application.jar extract --destination extracted
#
##FROM eclipse-temurin:21.0.6_7-jdk-alpine
#FROM debian:trixie-slim
#
#RUN addgroup spring-boot-group && adduser --ingroup spring-boot-group spring-boot
#USER spring-boot:spring-boot-group
#
#VOLUME /tmp
#WORKDIR /application
#
#COPY --from=build /build/extracted/jdk .
#COPY --from=build /build/extracted/dependencies .
#COPY --from=build /build/extracted/spring-boot-loader .
#COPY --from=build /build/extracted/snapshot-dependencies .
#COPY --from=build /build/extracted/application .
#
#ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher \"$@\"", "--"]

## 1-ый этап сборки с использованием JDK 21
#FROM eclipse-temurin:21.0.6_7-jdk-alpine AS build
#
##RUN which java
##RUN java -version
##
##RUN chmod +x /opt/java/openjdk/bin/java
##RUN ls -l /opt/java/openjdk/bin/java
#
## Объявляется переменная JAR_FILE
#ARG JAR_FILE
## Устанавливается рабочая директория для сборки
#WORKDIR /opt/build
#
## Копируется собранный jar файл в application.jar
#COPY $JAR_FILE application.jar
## Используется Spring Boot layertools для извлечения слоёв приложения
#RUN java -Djarmode=layertools -jar application.jar extract
#
##RUN ls -l /build/extracted/
##RUN find /build/extracted -type f
#
##RUN find /opt/java/openjdk -name java
##RUN /opt/java/openjdk/bin/java -version
#
## 2-й этап сборки с использование debian
#FROM debian:trixie-slim
##FROM debian:buster-slim
#
#RUN ls -l /opt/build
#RUN find /opt/build -name "jdk"
#
## Установка необходимых пакетов
#RUN apt-get update && apt-get install -y --no-install-recommends \
#    passwd \
#    && rm -rf /var/lib/apt/lists/*
#
## Объявляется переменная, где указан путь до jdk
#ARG BUILD_PATH=/opt/build
#ENV JAVA_HOME=/opt/jdk
#ENV PATH "${JAVA_HOME}/bin:${PATH}"
#
## Создаётся неприоритетный пользователь для запуска приложения
#RUN groupadd --gid 1000 cinema-group \
#  && useradd --uid 1000 --gid cinema-group --shell /bin/bash --create-home cinema-app
#USER cinema-app:cinema-group
#
## Устанавливается рабочую директорию внутри контейнера
#WORKDIR /application
#
##RUN ls -l $JAVA_HOME/bin/
##RUN chmod +x /opt/java/openjdk/bin/java
##RUN ls -l /opt/java/openjdk/bin/java
##RUN /opt/java/openjdk/bin/java -version
#
## Копируется JDK из 1-ого этапа сборки
#COPY --from=build $BUILD_PATH/jdk $JAVA_HOME
#
## Копируются извлечённые слои из этапа сборки
#COPY --from=build $BUILD_PATH/dependencies .
#COPY --from=build $BUILD_PATH/spring-boot-loader .
#COPY --from=build $BUILD_PATH/snapshot-dependencies .
#COPY --from=build $BUILD_PATH/application .
#
##RUN java -version
#
##RUN ls -l /build
#
## Задаётся команда контейнеру, что при запуске оболочки sh необходимо выполнить команду "exec java..."
#ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher \"$@\"", "--"]

## 1-ый этап сборки
FROM eclipse-temurin:21.0.6_7-jdk-jammy AS build

# Объявляется переменная JAR_FILE
ARG JAR_FILE
## Устанавливается рабочая директория для сборки
WORKDIR /build

# Копируется собранный jar файл в application.jar
ADD $JAR_FILE application.jar
# Используется Spring Boot layertools для извлечения слоёв приложения
RUN java -Djarmode=layertools -jar application.jar extract

# 2-ой этап сборки
FROM eclipse-temurin:21.0.6_7-jdk-jammy

# Создаётся пользователь для запуска приложения
RUN addgroup cinema-group && adduser --ingroup cinema-group cinema-app
USER cinema-app:cinema-group

# Объявляет том /tmp для сохранения временных данных и совместного доступа
VOLUME /tmp
# Устанавливается рабочая директория внутри контейнера для запуска приложения
WORKDIR /application

# Копируются извлечённые слои из этапа сборки
COPY --from=build /build/dependencies .
COPY --from=build /build/spring-boot-loader .
COPY --from=build /build/snapshot-dependencies .
COPY --from=build /build/application .

# Задаётся команда контейнеру, что при запуске оболочки sh необходимо выполнить команду "exec java..."
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher \"$@\"", "--"]