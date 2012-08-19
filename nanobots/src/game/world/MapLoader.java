package game.world;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
			return load(text, teams);
		}
	}

	public static World load(String mapString, ImmutableList<Team> teams) {

		// initialize map
		World map;
		{
			Dimension size = findMapStringDimensions(mapString);

			map = new World(size.width, size.height);
		}

		// fill map with specified entities
		loadMapEntities(mapString, map, teams);
		map.tick(); //flush add queue

		return map;
	}

	static void loadMapEntities(String mapString, World world, ImmutableList<Team> teams) {
			int y = 0;
			Iterable<String> allRows = Splitter.on("\n").trimResults().split(mapString);
			for (String row : allRows) {

				int x = 0;
				Iterable<String> splitRow = Splitter.fixedLength(1).trimResults().split(row);
				for (String cellSymbol : splitRow) {
					AbsPos cellPos = AbsPos.of(x, y);

					Entity fromCell = getEntityForSymbol(cellSymbol, teams);

					world.addNewEntity(cellPos, fromCell);
					x++;
				}

				y++;
			}
	}

	static Entity getEntityForSymbol(String cellSymbol, ImmutableList<Team> teams) {
		switch (cellSymbol) {
		case ".":
			return Entity.getNewEmpty();
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
	static Dimension findMapStringDimensions(String mapString) {
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
