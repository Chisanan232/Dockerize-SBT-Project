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

Update brew. <br>

    brew update
    
Install docker. <br>

    brew install docker

It also needs to install *docker-machine* and *virtualbox* because natively docker be developed for Linux environment. <br>

    brew install docker-machine
    brew cask install virtualbox

Verify the docker state. <br>

    docker info

Running-result of command line: <br>
<br>
![](https://github.com/Chisanan232/Dockerize-SBT-Project/raw/master/docs/imgs/docker-info_running_result.png)
<br>

Dockerize
===
It shuold clear that what thing be needed to do in Docker. <br>
It's a SBT project. ---> Run it with command line 'sbt run'. In other words, it needs a **SBT environment**. <br>
It will run python code (crawler part) in SBT project via command line. ---> It needs a **Python environment**. <br>
It also needs a **Java environment** because of SBT. <br>
So below are all things developers need to confgiure in docker: <br>
* Java
* SBT
* Python

FROM
---
So select the foundation is Java. Here use **openjdk:8** because it's one of [SBT requirements](https://www.scala-sbt.org/1.x/docs/Setup.html). <br>

> Install JDK (We recommend AdoptOpenJDK JDK 8 or AdoptOpenJDK JDK 11). <br>

Use keyword **FROM** to load the base image. <br>

    FROM openjdk:8

WORKDIR
---
Set the directory where the code will be run in docker. <br>

    WORKDIR /docker_sbt_crawler

COPY
---
Literally, copy pointed file(s) to destination path. It doesn't decompress archive file. <br>

    COPY requirements.txt /docker_sbt_crawler


RUN
---
Start to configure all environment configuration it needs: <br>

    RUN \
        echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
        apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823 && \
        apt-get update && \
        apt-get install sbt && \
        apt-get install -y python3 && \
        apt-get install -y python3-pip && \
        pip3 install --no-cache-dir -r requirements.txt

Keyword **RUN** will add a new layer on the current image and execute command(s) to create a new image. It often be used when it installs software packages. <br>
The above command lines which be executed are: <br>

    echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823 && \
    apt-get update && \
    apt-get install sbt && \
    apt-get install -y python3 && \
    apt-get install -y python3-pip && \
    pip3 install --no-cache-dir -r requirements.txt


It could be divided to 2 parts. One is <br>

    echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823 && \
    apt-get update && \
    apt-get install sbt

and another one is <br>

    apt-get install -y python3 && \
    apt-get install -y python3-pip && \
    pip3 install --no-cache-dir -r requirements.txt

The first part target to install SBT (Here is the [install-tutorial](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html) in documentation). <br>
The second one target to install Python 3 and package management tool 'pip'. It also install the required package program needs after all. <br>
So it needs a txt type file 'requirements.txt' which records all Python packages that will be installed in second part. <br>
The file content like below: <br>

    requests               # General used
    requests == 2.4.0      # Fixed a specific package version

Here is the [requirements file format](https://pip.pypa.io/en/stable/reference/pip_install/#requirements-file-format). <br>

ADD
---
Add all files into the target path of image file system. Source should be the path where under build context or a URL (git repository). Destination should be a absolute file path or relative file path of path where be pointed by keyword **WORKDIR**. <br>
It will decompress and copy file to the file system of docker image if sources including archive file.

    ADD . /docker_sbt_crawler
    
CMD
---
Execute the command line. It only could execute one command. If it has more than 1 command which be called by keyword **CMD**, only the last one will be executed. <br> 
    
    CMD sbt run

> The difference between **RUN** and **CMD** is: <br>
> The command(s) which be called by **RUN** is(are) executing when it building docker image (and it will create a new image). But for **CMD** part, the command be executed in docker container when it running. <br>

Dockerfile
---
Here is the Dockerfile content for the project. <br>

```docker
FROM openjdk:8

WORKDIR /docker_sbt_crawler

COPY requirements.txt ./

RUN \
    echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823 && \
    apt-get update && \
    apt-get install sbt && \
    apt-get install -y python3 && \
    apt-get install -y python3-pip && \
    pip3 install --no-cache-dir -r requirements.txt

ADD . /docker_sbt_crawler

CMD sbt run

```

Execute Docker
===
Execute the docker image to run program or activate a service, etc. <br>

Build
---
Build a Docker image. <br>

    docker build <directory path>

The directory is the path where including Dockerfile (in default setting, it will find and run the file name as *Dockerfile*). <br>
In this project, the command line is: <br>

    docker build -t stock_crawler:v13 src/main/scala/Dockerize_SBT_Project/

> Option *t* means that adding a tag for the docker image. <br>

By the way, if it be needed to customize Dockerfile name, it could use option *f* or *file*. <br>

    docker build -f src/main/scala/Dockerize_SBT_Project/Crawler-Dockerfile -t stock_crawler:v13 src/main/scala/Dockerize_SBT_Project/

Running-result of command line: <br>
First time to build: <br>

    Sending build context to Docker daemon  3.181MB
    Step 1/6 : FROM openjdk:8
     ---> b190ad78b520
    Step 2/6 : WORKDIR /test
     ---> Using cache
     ---> 40e7e45806f6
    Step 3/6 : COPY requirements.txt ./
     ---> 5f9e6e58b9a0
    Step 4/6 : RUN     echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list &&     apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823 &&     apt-get update &&     apt-get install sbt &&     apt-get install -y python3.7 &&     apt-get install -y python3-pip &&     pip3 install --no-cache-dir -r requirements.txt
     ---> Running in 68fa206edbf3
    deb https://dl.bintray.com/sbt/debian /
    Warning: apt-key output should not be parsed (stdout is not a terminal)
    Executing: /tmp/apt-key-gpghome.RlrG3w0jEl/gpg.1.sh --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
    gpg: key 99E82A75642AC823: public key "sbt build tool <scalasbt@gmail.com>" imported
    gpg: Total number processed: 1
    gpg:               imported: 1
    Get:1 http://security.debian.org/debian-security buster/updates InRelease [65.4 kB]
    Get:2 http://deb.debian.org/debian buster InRelease [121 kB]
    Get:3 http://security.debian.org/debian-security buster/updates/main 
    
    ...... (install software package log message)
    
    Installing collected packages: urllib3, chardet, certifi, idna, requests
    Successfully installed certifi-2020.6.20 chardet-3.0.4 idna-2.8 requests-2.21.0 urllib3-1.24.3
    Removing intermediate container 68fa206edbf3
     ---> 16aad3af1ab6
    Step 5/6 : ADD . /test
     ---> d6f55f519bbe
    Step 6/6 : CMD sbt run
     ---> Running in 83dc11b26058
    Removing intermediate container 83dc11b26058
     ---> 022c1a893f5c
    Successfully built 022c1a893f5c
    Successfully tagged test_sbt_python:v2


After second time to build: <br>

    Sending build context to Docker daemon  11.44MB
    Step 1/6 : FROM openjdk:8
     ---> b190ad78b520
    Step 2/6 : WORKDIR /docker_sbt_crawler
     ---> Using cache
     ---> 527a5d8f06ca
    Step 3/6 : COPY requirements.txt ./
     ---> Using cache
     ---> 3da14e74cad6
    Step 4/6 : RUN     echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list &&     apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823 &&     apt-get update &&     apt-get install sbt &&     apt-get install -y python3 &&     apt-get install -y python3-pip &&     pip3 install --no-cache-dir -r requirements.txt
     ---> Using cache
     ---> a9b4dff82bd7
    Step 5/6 : ADD . /docker_sbt_crawler
     ---> d41c556c217c
    Step 6/6 : CMD sbt run
     ---> Running in cc4c921198d1
    Removing intermediate container cc4c921198d1
     ---> 18c4530c57bb
    Successfully built 18c4530c57bb
    Successfully tagged stock_crawler:v13

Images
---
Check what image(s) could be used. <br>

    docker image ls

In this project, the target image ID is *18c4530c57bb*. <br>

Running-result of command line: <br>
<br>
![](https://github.com/Chisanan232/Dockerize-SBT-Project/raw/master/docs/imgs/dokcer-image-ls_running_result.png)
<br>

Execute
---
In first time to execute it, it must to run it via command *run*. <br>
Here is the description about command *run*: <br>

    docker --help
    
    ......
    run         Run a command in a new container
    ......
    
It never execute the image before, in other words, it also doesn't have the container absolutely. <br>

    docker run <image ID>
    
In this project, the docker command line is: <br>

    docker run -v /Dockerize_SBT_Project/src/main:/docker_sbt_crawler/Dockerize_SBT_Project/src/main -it 18c4530c57bb

> Option *v*: Bind a list with a file system and let it to be accessible storage area . <br>
> Option *it*: Instructs Docker to allocate a pseudo-TTY connected to the container's stdin and create a interactive bash shell in the container. ([docker run document](https://docs.docker.com/engine/reference/commandline/run/)) <br>

Developers also could name the container with option *name*: <br>

    docker run -name Crawler_SBT -v /Users/bryantliu/IdeaProjects/KobeDataScience/src/main/scala/Dockerize_SBT_Project/src/main:/docker_sbt_crawler/Dockerize_SBT_Project/src/main -it 18c4530c57bb

Running-result of command line: <br>
<br>
![](https://github.com/Chisanan232/Dockerize-SBT-Project/raw/master/docs/imgs/docker-run_running_result.png)
<br>

It could verify the container info after execute the image. <br>

    docker container ls -a

Running-result of command line: <br>
<br>
![](https://github.com/Chisanan232/Dockerize-SBT-Project/raw/master/docs/imgs/docker-container-ls_running_result.png)
<br>

Stop contianer: <br>

    docker stop <container ID>

If it need to run the container later, please use command *start*: <br>

    docker start <container ID>

> Command *run*: <br>
> Run command and build a new container (the container doesn't exist before). <br>
> Command *start*: <br>
> Activate to run a container which be stopped (in other words, the container(s) has existed before). <br>

Here is the description about command *start*: <br>

    docker --help
    ......
    start       Start one or more stopped containers
    ......

Pause container which is running: <br>

    docker pause <container ID>
    
Unpause container which be paused: <br>

    docker unpause <container ID>

If the container doesn't be needed anymore, could kill it: <br>

    docker kill <container ID>

