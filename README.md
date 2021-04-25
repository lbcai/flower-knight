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

## 04/10/21 Graphical Plan Update
Here is a draft of the visual design goal for this project meant to help keep the project on track during development. Other unique areas in the game will have different design goals.
<br>
<p align="center" width="100%">
  <img src="GithubImages/04_10_21_mockup.png?raw=true" alt="Visual design goal for the project.">
</p>
<br>
<p align="center" width="100%">
  <img src="GithubImages/04_22_21_mockup.png?raw=true" alt="Visual design goal for the project with UI elements and extra flashy effects.">
</p>

Here is an animation showcasing the element layering required to achieve the above effect (0th layer not included in animation):
* 0th layer: Foreground decorations (only for the "ground" layer, or the lowest y value platforms in the map, tracks with camera and only shows at certain y values)
* 1st layer: Actual grass/dirt platform (tiled)
* 2nd layer: Waving grass/bushes that will move when player moves over them and produce flying particles
* 3rd layer: Player, enemies, interactables (will require shaders)
* 4th layer: Background grass that the player actually stands on
* 5th layer: Background interactables such as portals to other maps (represented here by a gate)
* 6th layer: Background grass/bushes that will not move when player moves near them 
* 7th layer: Background image for the zone that tracks with camera movement (may be created in layers for mild parallax effect)

Elements associated with a platform lower in y value should be rendered above higher y value platform elements, if possible.
<br>
<p align="center" width="100%">
  <img src="GithubImages/04_10_21_layering.gif?raw=true" alt="Visual representation of element layering required to achieve design goal.">
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
- [x] Add UI/HUD

### Custom Features
- [x] Create a graphics mockup for maintaining cohesive design
- [x] Create basic player idle, run, and jump animations
- [x] Animate idle loop and jump
- [x] Inbetween animations: sliding to a stop after running, boosting up when just missed a platform, exit combat idle transition animation
- [x] Add extra movement options: downjump, doublejump, wall climbing, boosting up onto a platform
- [x] Create wall hanging animations
- [ ] Create platform tileset & background images
- [ ] Create custom platform class for nicer looking platforms
- [ ] Add slanted ground/platforms
- [x] Add breakable background objects
- [ ] Create basic player attack animations
- [ ] Implement player attack combos
- [ ] Create 2 basic enemy types and animations
- [x] Allow projectiles to fly at angles
- [ ] Add special effects: impact & doublejump sparkles, attack wooshes, dust clouds, damage numbers, item effects, etc.
- [x] Add enemy aggression, random motion, etc. 
- [x] Create basic player death, knockback animations
- [ ] Create UI/HUD assets and customize UI functionality
- [x] Add debug rendering
- [ ] Allow player to reset keybinds & resize window through UI
- [ ] Add "berserk mode"
- [ ] Add fullscreen attack "ultimate"
