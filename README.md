NanoBots
========

A 1v1 swarm AI programming game.


**This document is a work in progress.**


### About ###

Two players verus each other by writing code to programming their team's bots
AI. The goal is to out survive the other team - the last team surviving wins.

Players submit Brains. The first team's bots say what actions they want to take, 
then the game executes those actions.
The next team then does the same. This repeats until there is a winner.


### License ###

Copyright (C) JWill and BDC, 2012
Licensed under the GNU GPL version 2 or up.


### Grid ###

The game takes place within a 2D finite bounded grid. A cell in the Grid may
only contain a Bot, a Food, a Wall, or be empty. A cell never contains more
than one thing at a time. The outer bounds of the Grid are marked with Walls.


### Bot ###

Bots exists within the Grid and move in empty cells. Bots have an integer
health value. Once a Bot's health value is zero, they die and no longer exists
in the Grid. Bots belong to a team.


### Brain ###

Players write the brain each bot on their team will use to decide what action to 
take.  Each turn, the brain will be given some info about a bot, and be asked 
what the bot should do.


### Bot Actions ###

Each turn, every bot on a team picks the action they want to perform that turn.
Only one action can be chosen per turn.  The team's Brain will decide for each 
bot what action they take.

Bot Actions:  
* Move  
* Attack  
* Eat  
* Broadcast information  
* Reproduce  
* Transfer energy to a teammate


### Brain Input ###
For each bot a brain is asked to decide for, it is given some information.
Bot Info:
* Energy
* Vision -- what entities are near the bot
* Position -- Absolute position in the world
* Memory -- a small number of bits of information that can be retained by that 
bot between turns.  Brain can set this to whatever it wants.  Useful because 
Brain cannot remember anything either between turns or between bots in one turn.
* Received Messages -- Packets of bits (similar to memory) that were Broadcast last turn by allied bots within transmission range

### Food ###

Food is a mineable resource found in the Grid. Food has an integer energy
value. Bots mine energy from Food to gain health. Once Food's energy level is
zero, it no longer exists in the Grid.


### Wall ###

Walls are stationary and can not be occupied by Bot or Food nor can they be
destoryed. Walls are not empty. Walls mark the outer bounds of the Grid and can
also be placed as obstacles anywhere else within the Grid.

