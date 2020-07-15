# Dockerize-SBT-Project

### Description
A sample of developing and deploying a SBT project. <br>
<br>

### Motivation
Study for deployment knowledge and note something detail like some problems I ever faced before.
<br>

### Skills

#### Environment
OS: MacOS (Current Version: 10.14.5)

Language: Python <br>
Version: 3.7 up <br>
Framework: Requests (version: 2.4.0) <br>
<br>

Language: Scala <br>
Version: 2.12 <br>
Framework: Spark (version: 2.4.5), AKKA (version: 2.4.20) <br>
<br>

* Addition: <br>
The crawler program be adapted from [AKKA-with-Crawler](https://github.com/Chisanan232/AKKA-with-Crawler). <br>
<br>

### Pre-Process
First of all, it needs to install docker in environment. <br>
For MacOS environment, must to use tool [**Homebrew**](https://brew.sh/) to install docker. Please refer to official documentation to install. <br>
It needs to install Homebrew with only one command line: <br>

    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"

It could check what thing developers could do with option *help*. <br>

    brew help

Running-result of command line: <br>
<br>
![](https://github.com/Chisanan232/Dockerize-SBT-Project/raw/master/docs/imgs/brew-help_running_result.png)
<br>

Verify the tool works finely. <br>

    brew config

Running-result of command line: <br>
<br>
![](https://github.com/Chisanan232/Dockerize-SBT-Project/raw/master/docs/imgs/brew-config_running_result.png)
<br>


### Dockerize
It shuold clear that what thing be needed to do in Docker. <br>
It's a SBT project. ---> Run it with command line 'sbt run'. In other words, it needs a **SBT environment**. <br>
It will run python code (crawler part) in SBT project via command line. ---> It needs a **Python environment**. <br>
It also needs a **Java environment** because of SBT.
So below are all things developers need to confgiure in docker: <br>
* Java
* SBT
* Python

So select the foundation is Java. Here use **openjdk:8** because it's one of SBT requirements. <br>

    FROM openjdk:8

Set the directory where the code will be run in docker. <br>

    WORKDIR /docker_sbt_crawler

Start to configure all environment configuration it needs: <br>

    RUN \
        echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
        apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823 && \
        apt-get update && \
        apt-get install sbt && \
        apt-get install -y python3 && \
        apt-get install -y python3-pip && \
        pip3 install --no-cache-dir -r requirements.txt

(Explain keyword 'RUN')
(Explain command line)

### Running Result


