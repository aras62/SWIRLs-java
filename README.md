# Distributed Path Planning via SWIRLs
A java implementation of the SWIRLs algorithm presented in ["Distributed Path Planning for Mobile Robots using a Swarm of Interacting Reinforcement Learners"](http://www-anw.cs.umass.edu/pubs/2007/vigorito_AAMAS07.pdf) by M. Vigorito. 

This approach uses a physical path planning approach using distributed network of sensors placed in the environment. The sensors exploit a reinforcement learning technique in which through interaction with mobile agents and sharing information among themselve, they learn the closest paths to any given destination.

The code in this repository is written in Java. The implementation includes various levels of concurrency (e.g. sequential and multi-threding) among sensors for information sharing and uses a graphical interface to visualize the performance of the overall system. 
