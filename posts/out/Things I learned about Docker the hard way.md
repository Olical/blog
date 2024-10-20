---
alias: things-i-learned-about-docker-the-hard-way
tags:
- blog-post
- imported-blog-post
---


This list was just ripped from a Google Keep item I wrote late last night, just wanted to share.

* A process per container is noble but hard for existing projects.
* Use one container if you have multiple components with a startup order / dependency.
* Write a script to manage the processes.
* When you build a directory with a Dockerfile it will push the entire content of that directory into tmp.
* I tried it with a big thing, overflowed tmp and tried to fill up my root partition.
* Have a context directory which is almost empty, that’s where you put your Dockerfile and anything you need to ADD.
* Docker compose link doesn’t wire the exposed ports together on the same localhost / interface. It gives you a host in /etc/hosts that you must point to by name. I wasted days thinking two containers could somehow mesh their ports together.
* This means you have to be able to configure the addresses of every link between components, sometimes that’s awkward.
* Save time and hair, use one container with a process management script. Keep the Dockerfile in it’s own small directory.
* Use volumes through docker compose for things like your npm cache. It works wonders. I have a data directory where I mount all volumes. Including the entire root directory.
* A process inside the container may just refuse connections and give you “reset by peer” errors. This is because it’s bound to localhost, bind it to 0.0.0.0. I’ve only seen it happen with some processes for some reason though. Others are okay with localhost.
