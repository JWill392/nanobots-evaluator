package game.world;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import teampg.grid2d.GridInterface.Entry;
import teampg.grid2d.RectGrid;
import teampg.grid2d.point.AbsPos;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import entity.Entity;
import game.Settings;
import game.Team;

public class MapLoader {
	public static World load(File mapFile, ImmutableList<Team> teams) throws FileNotFoundException {
		try (Scanner loadedMapFile = new Scanner(mapFile, "UTF-8")) {
			loadedMapFile.useDelimiter("\\A");

			String text = loadedMapFile.next();
			return load(new GameMap("PLACEHOLDER_NAME", text), teams);
		}
	}

	public static World load(GameMap toLoad, ImmutableList<Team> teams) {
		// fill map with specified entities
		RectGrid<Entity> ents = readMapEntities(toLoad, teams);

		//TODO load into world
		World world = new World(ents.getSize().width, ents.getSize().height);
		for (Entry<Entity> entry : ents.getEntries()) {
			if (entry.getContents() == null) {
				continue;
			}
			world.addNewEntity(entry.getPosition(), entry.getContents());
		}
		world.tick(); //flush add queue

		return world;
	}

	public static RectGrid<Entity> readMapEntities(GameMap map, List<Team> teams) {
		String mapString = map.contents;
		Dimension mapSize = findMapStringDimensions(map);
		RectGrid<Entity> ret = new RectGrid<>(mapSize);

		int y = 0;
			Iterable<String> allRows = Splitter.on("\n").trimResults().split(mapString);
			for (String row : allRows) {

				int x = 0;
				Iterable<String> splitRow = Splitter.fixedLength(1).trimResults().split(row);
				for (String cellSymbol : splitRow) {
					AbsPos cellPos = AbsPos.of(x, y);

					Entity fromCell = getEntityForSymbol(cellSymbol, teams);

					ret.set(cellPos, fromCell);
					x++;
				}

				y++;
			}
		return ret;
	}

	static Entity getEntityForSymbol(String cellSymbol, List<Team> teams) {
		switch (cellSymbol) {
		case ".":
			return null;
		case "#":
			return Entity.getNewWall();
		case "0":
			return Entity.getNewBot(teams.get(0));
		case "1":
			return Entity.getNewBot(teams.get(1));
		case "F":
			return Entity.getNewFood(Settings.getFoodEnergy());
		default:
			// TODO make our own exception: IllegalMapFileFormatException
			throw new IllegalStateException("Illegal symbol in map file: " + cellSymbol);
		}
	}

	// returns null if invalid
	static Dimension findMapStringDimensions(GameMap map) {
		String mapString = map.contents;

		try (Scanner rowScanner = new Scanner(mapString)) {
			Iterable<String> allLines = Splitter.on("\n")
					.omitEmptyStrings()
					.split(mapString);

			int height = Iterables.size(allLines);
			int width = allLines.iterator().next().length();


			// ensure map is rectangular
			for (String line : allLines) {
				if (width != line.length()) {
					// TODO make our own exception: IllegalMapFileFormatException
					throw new IllegalStateException("Inconsistent row width; Map is not square");
				}
			}


			return new Dimension(width, height);
		}
	}
}
