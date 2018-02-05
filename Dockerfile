FROM openjdk:alpine

RUN apk update && apk add curl zip bash

# Install Android SDK
WORKDIR /usr/local/android-sdk
RUN curl http://dl.google.com/android/repository/sdk-tools-linux-3859397.zip > android-sdk && \
    unzip android-sdk -d /usr/local/android-sdk
ENV ANDROID_HOME="/usr/local/android-sdk"
RUN yes | $ANDROID_HOME/tools/bin/sdkmanager "extras;m2repository;com;android;support;constraint;constraint-layout;1.0.2"
RUN yes | $ANDROID_HOME/tools/bin/sdkmanager "extras;m2repository;com;android;support;constraint;constraint-layout-solver;1.0.2"

# Accept Android licences, otherwise dependencies won't download
RUN yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses

WORKDIR /usr/builder
COPY . .

RUN ./gradlew assembleDebug