Shadow Wing
=========

###About the project###
 - This is a project I completed for a 2nd year programming class at The University of Melborune (SWEN20003 Object Oriented Software Development).
 - This project was done with around 6 weeks of java experience (2 hours of prac + 2 hours of lectures / week).
 - The original spec is in the _specs folder.
 - We were offered the chance to extend the game, with the best extension winning a programming contest -- My entry won.

###About my extension###
 - My extension was built from the get go with coop play in mind. The game has been designed to work with more than one player.
 - The networking is pretty reliable, depending on your connection, over the uniwireless, it is playable, but can be kind of jumpy, in my network at home, it plays perfectly.
 - Added explosions, added other particle effects such as boosters on the players.
 - Added a menu system, you can change the controls and the WIDTH of the screen, due to networking, changing the height of the screen isn't feasable, you need to hit enter, then reload the game to change the screen res.
 - Added a pixel perfect collision system, units will collide pixel perfectly with the walls, however they will use bounding box collisions when checking with one another, I ran out of time here >_>
 - Added seemless splitscreen, press (default) Q to join a second player, then use (default) WADS to control the second player.
 - Check pointing is implemented to spec, however the spec is rather ambiguous, you can consider the way I implemented it a "Feature".

###Removed Features###
 - I had a lan game finder implemented, but then removed it, because it doesn't work at uni, most of the computers are on different subnets, and hence, it's impossible to do network discovery, unless I spam packets everywhere, which I don't think is a good idea, as a quick solution, you can enter an IP into the console, if you select that option from the menu screen, or you could luck out and hope the IP I put in there is the one you need to connect to :P

###Legal notice###
The graphics included with the package (tiles.png, panel.png, units/*, items/*) are primarily derived from copyrighted works from the games Chromium B.S.U. (licensed under the Artistic License), The Battle for Wesnoth (licensed under the GNU General Public License, version 2 or later), and other works. You may redistribute them, but only if you agree to the license terms.