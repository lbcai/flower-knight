# flower-knight
2D sidescroller written in Java with libGDX. Currently, the goal is to produce a functioning demo level in order to internalize OOP concepts learned in courses. This readme will be polished with images (and hopefully a link to a working web demo) when the game is in a more complete state.

Based on Udacity's ["How to Make a Platformer Using libGDX"](https://classroom.udacity.com/courses/ud406) tutorial.

## 03/02/21 Update
Currently, the game is using placeholder graphical assets. Here are a few gifs of some of the more interesting features that are currently available.
* The turret-type dandelion monster is capable of shooting projectiles at the player in a 180 degree range. The monster does not shoot if the player is below it.
* The combat idle, transition from combat idle to normal idle, and normal idle are all visible in this gif.
* The flash-on-damage-taken effect is visible here. The player takes damage both when touching the monster and when hit by a projectile.
<br>
<p align="center" width="100%">
  <img src="GithubImages/03_02_21_dandelion.gif?raw=true" alt="Interaction with a dandelion turret-type monster.">
</p>

* The player demonstrates running, skidding, regular jumping, downjumping, doublejumping, and walljumping.
<br>
<p align="center" width="100%">
  <img src="GithubImages/03_02_21_movement.gif?raw=true" alt="Showcase of basic movement systems.">
</p>

## 03/23/21 Update
Here is an additional gif showcasing recent additions to the game with more placeholder graphical assets.
* The player demonstrates boosting up onto a platform.
* There is currently only one attack animation. Combos will be implemented at a later date when there are more types of attacks.
* Hit effects (such as slashes and impact sparkles) and flying damage numbers are visible here.
* The player demonstrates dodging through sources of damage to avoid taking hits. The first attempt was successful and the second was not. The dodge currently uses the same animations as the skid that happens when the player stops running.
* Item looting is demonstrated as well. The drop rate of items was turned up for this demonstration.
<br>
<p align="center" width="100%">
  <img src="GithubImages/03_23_21_battle.gif?raw=true" alt="Graphical effects during combat include flying numbers.">
</p>

## TODO
### Tutorial Progress
- [x] Implement player standing, running, and jumping
- [x] Draw player sprites according to player action
- [x] Animate player run
- [x] Implement nine-patch platforms with collisions
- [x] Allow maps to continue beyond the screen
- [x] Make camera follow player
- [x] Remove floor and add kill plane
- [x] Add debug camera controls
- [x] Implement enemies with behavior, sprites, death
- [x] Add knockback
- [x] Add projectiles
- [x] Add hit detection 
- [x] Add lootable "powerup" items
- [ ] Add portals to other maps
- [ ] Add level loading with a level editor
### Custom Features
- [ ] Create a graphics mockup for maintaining cohesive design
- [x] Create basic player idle, run, and jump animations
- [x] Animate idle loop and jump
- [x] Inbetween animations: sliding to a stop after running, boosting up when just missed a platform, exit combat idle transition animation
- [x] Add extra movement options: downjump, doublejump, wall climbing, boosting up onto a platform
- [x] Create wall hanging animations
- [ ] Create platform tileset & background images
- [ ] Create custom platform class for nicer looking platforms
- [ ] Add slanted ground/platforms
- [ ] Create basic player attack animations
- [ ] Implement player attack combos
- [ ] Create 2 basic enemy types and animations
- [x] Allow projectiles to fly at angles
- [ ] Add special effects: impact & doublejump sparkles, attack wooshes, dust clouds, damage numbers, item effects, etc.
- [ ] Add enemy aggression, random motion, etc. (add gdx-ai framework)
- [ ] Create basic player death, knockback animations
- [ ] Create UI assets
- [ ] Implement UI functionality
- [ ] Allow player to reset keybinds through UI
- [ ] Add "berserk mode"
- [ ] Add fullscreen attack "ultimate"
