# Spigot-MTD
## [Schematic](https://www.curseforge.com/minecraft/mc-mods/worldedit)-Like Build Saves
Stored as text documents intended for structures in block population.

## Commands
`/t1` First Position

`/t2` Second Position

`/tsave file` Save as file name

`/tpaste file` Paste as file name

## Formatting:

```Block Type, x1, x2, x3, BlockData```
Coordinate is relative to player's position at save.
This offers a lightweight, plain-text alternative to WorldEdit schematics.
MTD is built for world generation (block population at runtime)
Running a chunk preloader isn't necessary.

## Todo

 - Entities
 - Direct Custom Structures (no middleman plugin)

## Known Issues

 - The Spigot API has many methods written not to be thread-safe. This can lead to unpredictable behavior, leakage, and missing pastes.
 Thankfully, the server console will report these clearly.
 - Saves do not overwrite, instead, they mesh together. This is intentional.
 - MTDs in the latest patch now format as `Type,x1,x2,x3,Blockdata` and use blockdata provided by Spigot API - 
 meaning, entities and status effects will not save.
