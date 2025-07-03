# Using Selenium's standalone Chrome image as base
FROM selenium/standalone-chrome

LABEL authors="densudas"

USER root

# Install tools
RUN apt-get -qqy update && apt-get install -qqy \
    curl \
    unzip \
    maven \
    openjdk-21-jdk \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Add the current directory contents into the container at /app
ADD . /app

# Build the project with Maven
RUN mvn clean install

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file when the container launches
CMD ["java", "-jar", "target/my-app-1.0-SNAPSHOT.jar"]
