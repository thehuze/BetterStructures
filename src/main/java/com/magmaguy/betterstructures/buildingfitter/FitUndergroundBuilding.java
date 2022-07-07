package com.magmaguy.betterstructures.buildingfitter;

import com.magmaguy.betterstructures.buildingfitter.util.TerrainAdequacy;
import com.magmaguy.betterstructures.config.generators.GeneratorConfigFields;
import com.magmaguy.betterstructures.schematics.SchematicContainer;
import com.magmaguy.betterstructures.util.WorldEditUtils;
import org.bukkit.*;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class FitUndergroundBuilding extends FitAnything {

    private int lowestY;
    private int highestY;
    private GeneratorConfigFields.StructureType structureType;

    //For commands
    public FitUndergroundBuilding(Chunk chunk, SchematicContainer schematicContainer, int lowestY, int highestY) {
        this.lowestY = lowestY;
        this.highestY = highestY;
        this.schematicContainer = schematicContainer;
        this.schematicClipboard = schematicContainer.getClipboard();
        scan(chunk);
    }

    public FitUndergroundBuilding(Chunk chunk, int lowestY, int highestY, GeneratorConfigFields.StructureType structureType) {
        this.lowestY = lowestY;
        this.highestY = highestY;
        this.structureType = structureType;
        scan(chunk);
    }

    private void scan(Chunk chunk) {
        //Note about the adjustments:
        //The 8 offset on x and y is to center the anchor on the chunk
        Location originalLocation = new Location(chunk.getWorld(), chunk.getX() * 16D, 0, chunk.getZ() * 16D).add(new Vector(8, 0, 8));
        switch (chunk.getWorld().getEnvironment()) {
            case NORMAL:
            case CUSTOM:
                originalLocation.setY(ThreadLocalRandom.current().nextInt(lowestY, highestY));
                break;
            case NETHER:
                if (structureType == GeneratorConfigFields.StructureType.UNDERGROUND_SHALLOW) {
                    boolean streak = false;
                    int lowPoint = 0;
                    int highPoint = 0;
                    int tolerance = 3;
                    for (int y = lowestY; y < highestY; y++) {
                        Location currentLocation = originalLocation.clone();
                        currentLocation.setY(y);
                        if (currentLocation.getBlock().getType().isSolid()) {
                            if (streak) {
                                highPoint = y;
                            } else {
                                lowPoint = y;
                                streak = true;
                            }
                        } else {
                            if (currentLocation.getBlock().getType() == Material.VOID_AIR ||
                                    currentLocation.getBlock().getType() == Material.BEDROCK ||
                                    tolerance == 0) {
                                if (streak) {
                                    streak = false;
                                    if (highPoint - lowPoint >= 20)
                                        break;
                                    if (currentLocation.getBlock().getType() == Material.VOID_AIR ||
                                            currentLocation.getBlock().getType() == Material.BEDROCK)
                                        return;
                                    tolerance = 3;
                                }
                            } else {
                                if (streak) {
                                    tolerance--;
                                    highPoint = y;
                                }
                            }
                        }
                    }
                    if (highPoint - lowPoint < 20) {
                        //Case in which no ground was found which could be used as a valid underground surface
                        return;
                    }
                    if (highPoint - lowPoint > 30) {
                        originalLocation.setY(ThreadLocalRandom.current().nextInt(lowPoint + 1, highPoint - 20));
                    } else {
                        originalLocation.setY(lowPoint + 1D);
                    }
                } else {
                    boolean streak = false;
                    int lowPoint = 0;
                    int highPoint = 0;
                    int tolerance = 3;
                    for (int y = highestY; y > lowestY; y--) {
                        Location currentLocation = originalLocation.clone();
                        currentLocation.setY(y);
                        if (currentLocation.getBlock().getType().isSolid()) {
                            if (streak) {
                                lowPoint = y;
                            } else {
                                highPoint = y;
                                streak = true;
                            }
                        } else {
                            if (currentLocation.getBlock().getType() == Material.VOID_AIR ||
                                    currentLocation.getBlock().getType() == Material.BEDROCK ||
                                    tolerance == 0) {
                                if (streak) {
                                    streak = false;
                                    if (highPoint - lowPoint >= 20)
                                        break;
                                    if (currentLocation.getBlock().getType() == Material.VOID_AIR ||
                                            currentLocation.getBlock().getType() == Material.BEDROCK)
                                        return;
                                    tolerance = 3;
                                }
                            } else {
                                if (streak) {
                                    tolerance--;
                                    lowPoint = y;
                                }
                            }
                        }
                    }
                    if (highPoint - lowPoint < 20) {
                        //Case in which no ground was found which could be used as a valid underground surface
                        return;
                    }
                    if (highPoint - lowPoint > 30) {
                        originalLocation.setY(ThreadLocalRandom.current().nextInt(lowPoint, highPoint - 20));
                    } else {
                        originalLocation.setY(lowPoint + 1D);
                    }
                }
                break;
            case THE_END:
                if (structureType == GeneratorConfigFields.StructureType.UNDERGROUND_SHALLOW) {
                    boolean streak = false;
                    int lowPoint = 0;
                    int highPoint = 0;
                    int tolerance = 3;
                    for (int y = lowestY; y < highestY; y++) {
                        Location currentLocation = originalLocation.clone();
                        currentLocation.setY(y);
                        if (currentLocation.getBlock().getType().isSolid()) {
                            if (streak) {
                                highPoint = y;
                            } else {
                                lowPoint = y;
                                streak = true;
                            }
                        } else {
                            if (currentLocation.getBlock().getType() == Material.VOID_AIR ||
                                    currentLocation.getBlock().getType() == Material.BEDROCK ||
                                    tolerance == 0) {
                                if (streak) {
                                    streak = false;
                                    if (highPoint - lowPoint >= 20)
                                        break;
                                    if (currentLocation.getBlock().getType() == Material.VOID_AIR ||
                                            currentLocation.getBlock().getType() == Material.BEDROCK)
                                        return;
                                    tolerance = 3;
                                }
                            } else {
                                if (streak) {
                                    tolerance--;
                                    highPoint = y;
                                }
                            }
                        }
                    }
                    if (highPoint - lowPoint < 20) {
                        //Case in which no ground was found which could be used as a valid underground surface
                        return;
                    }
                    if (highPoint - lowPoint > 30) {
                        originalLocation.setY(ThreadLocalRandom.current().nextInt(lowPoint + 1, highPoint - 20));
                    } else {
                        originalLocation.setY(lowPoint + 1D);
                    }
                }
                break;
        }

        setSchematicFilename(originalLocation, structureType);
        if (schematicClipboard == null) {
            //Bukkit.getLogger().info("Did not spawn structure in biome " + originalLocation.getBlock().getBiome() + " because no valid schematics exist for it.");
            return;
        }

        schematicOffset = WorldEditUtils.getSchematicOffset(schematicClipboard);
        for (int chunkX = -searchRadius; chunkX < searchRadius + 1; chunkX += 4) {
            for (int chunkZ = -searchRadius; chunkZ < searchRadius + 1; chunkZ += 4) {
                chunkScan(originalLocation, chunkX, chunkZ);
                if (highestScore > 90) break;
            }
            if (highestScore > 90) break;
        }

        if (location == null)
            return;

        paste(location);
    }

    private void chunkScan(Location originalLocation, int chunkX, int chunkZ) {
        Location iteratedLocation = originalLocation.clone().add(new Vector(chunkX * 16, 0, chunkZ * 16));
        double score = TerrainAdequacy.scan(scanStep, schematicClipboard, iteratedLocation, schematicOffset, TerrainAdequacy.ScanType.UNDERGROUND);
        if (!originalLocation.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            if (score < 70)
                return;
        } else if (score < 50)
            return;

        if (score > highestScore) {
            highestScore = score;
            location = iteratedLocation;
        }
    }
}