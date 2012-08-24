NanoBots
========

A 1v1 swarm AI programming game.


**This document is a work in progress.**


### About ###

Nanobots is an AI programming game.  Each player has a team consisting of many 
robots.  Each team wants to destroy the other teams, and be the sole survivor.

Players write a shared Brain for their team.  A game match is then run, with no 
input from the players.  The player's team of bots act depending on what their 
Brain decides to do.  This continues until one team of bots has killed all the
enemy bots.  This team wins.


The interesting part is lies in the limited resources:

* Information is scarce. Each bot's brain is given some input through its 
"senses" -- vision in a small 
radius, bot energy.  

* Communication is costly.  The only way teammate bots can communicate is by 
taking a turn to broadcast a small message -- it is limited to a finite string 
of bits (size depends on settings; let's say 48 bits).  

* Even memory is limited.  Each bot can store a finite string of bits between turns
(again, depends on settings; say 24 bits).  Memory is passed into the brain
each turn along with vision.  It can be modified at no cost, and the modified
bit string will be passed in next turn.

Players can view replays showing what happened throughout the match, and 
reprogram their Brains to maintain their winning streak, or beat the current
winning Brain.


### License ###

Copyright (C) JWill and BDC, 2012
Licensed under the GNU GPL version 2 or up.


### Grid ###

The game takes place within a 2D finite bounded grid. A cell in the Grid may
only contain a Bot, a Food, a Wall, or be empty. A cell never contains more
than one thing at a time. The outer bounds of the Grid are marked with Walls.


### Bot ###

Bots exists within the Grid and move in empty cells. Bots have an integer
energy value. Once a Bot's energy value is zero, they die and no longer exist
in the Grid. Bots belong to a team.


### Brain ###
Players write a Brain function.  It has one input -- information given to the 
brain so it can decide on an action -- and one output -- the chosen action.

This function is called in isolation by each bot on a Player's team, once per
turn.  Effectively, each bot has its own separate but identical brain.


### Bot Actions ###

Each turn, every bot's brain on a team picks the action they want to perform 
that turn. Only one action can be chosen per turn.  Each action has an energy
cost, and some actions take multiple turns to execute (Reproduce).


Bot Actions:  
* Move  
* Attack -- damage energy of enemy bot
* Eat  
* Broadcast information  -- sends a bit string to allied bots within a certain radius
* Reproduce  
* Transfer energy to a teammate


### Brain Input ###
Each turn when a Brain is asked to decide on an action for some bot, it's
given some information about that bot:
* Energy -- health of that bot
* Vision -- what entities are near the bot
* Position -- Absolute position in the world
* Memory -- a small number of bits of information that can be retained by that 
bot between turns.
* Received Messages -- bit strings (similar to memory) that were Broadcast last turn by allied bots within transmission range

### Food ###

Food is a mineable resource found in the Grid. Food has an integer energy
value. Bots mine energy from Food to gain energy. Once Food's energy level is
zero, it no longer exists in the Grid.


### Wall ###

Walls are stationary and can not be occupied by Bot or Food nor can they be
destoryed. Walls are not empty. Walls mark the outer bounds of the Grid and can
also be placed as obstacles anywhere else within the Grid.

