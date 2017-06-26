# TCP-Simulator

The aim of this project is the comparison of different implementations of _TCP Protocol_.
Here, you can see the implementation of:
* AIMD (Addictive Increase / Multiplicative Decrease)
* Tahoe
* Reno

In order to generate the number of segments to send, we used the [SSJ 
(Stochastic Simulation in Java) library](https://github.com/umontreal-simul/ssj) developed by _Pierre L'Ecuyer_ of the University of Montreal. To install it you can follow the guide on the repository page or you can add it to the project creating a new library and
linking all `.jar` files that you can find in `ssj-3.2.0-3.2.0/lib` folder, you can get it cloning this repo.

A minimal GUI has been implemented in order to observe the real time behaviour of the simulator.
