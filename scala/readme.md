## Hi there!

Welcome to the Scala solution for JavaBin's [Language Shootout 2018](https://www.meetup.com/javaBin/events/247499235/).

This code solves two popular board games using the Monte Carlo Tree Search algorithm.

It tries to balance between idiomatic scala, simple code (algorithm allowing), and somewhat good performance: 

The code is 99% Scala standard library, with the addition of a [very simple library](https://github.com/lihaoyi/fansi) 
for doing fancy terminal output.

The code is written in the functional programming paradigm. 
For performance reasons you won't see many functions, but all interfaces are kept immutable. 
You will see mutable optimizations within methods, that's quite a nice thing to do!
 
### Building/Running

You will need sbt. 
```bash

$ wget https://raw.githubusercontent.com/paulp/sbt-extras/master/sbt -O ~/bin/sbt
$ chmod +x ~/bin/sbt

# in ~/.profile if you want
export PATH=~/bin:$PATH

$ sbt

# Wait while sbt downloads the internet, then type

sbt> coreJVM/run
```

### Scala.js
We added a javascript debugger for showing how the algorithm works.

Run this:
```
sbt> coreJS/fullOptJS
```

Then open `debugger.html` in a browser
