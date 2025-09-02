From openjdk:17-jdk-alpine as stage1

WORKDIR /app 

COPY gradle gradle
COPY src src
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
RUN chmod +x gradlew
RUN ./gradlew bootJar

# 두번째 스테이지 - FROM 기준으로 스테이지 열림
# 이미지 경령화를 위해 스테이지 분리
From openjdk:17-jdk-alpine as stage2
WORKDIR /app
# stage1의 jar파일을 stage2로 copy
COPY --from=stage1 /app/build/libs/*.jar app.jar

# 실행 : CMD 또는 ENTRYPOINT를 통해 컨테이너 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

# 도커이미지 빌드
# docker build -t ordersystem:v1.0 .

# 도커컨테이너 실행 -p의 내부포트는 application.yml의 내용과 일치
# docker내부에서 localhost를 찾는 설정은 루프백 문제 발생
# docker run --name my-ordersystem -d -p 8080:8080 ordersystem:v1.0
# 도커컨테이너 실행시점에 docker.host.internal을 환경변수로 주입
# docker run --name my-ordersystem -d -p 8080:8080 -e SPRING_REDIS_HOST=host.docker.internal -e SPRING_DATASOURCE_URL=jdbc:mariadb://host.docker.internal:3309/ordersystem ordersystem:v1.0
