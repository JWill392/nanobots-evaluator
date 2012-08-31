package jwill392.nanobotsreplay;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import jwill392.nanobotsreplay.assets.Assets;
import jwill392.nanobotsreplay.ui.AbstractUIComponent;
import jwill392.nanobotsreplay.ui.UIComponent;
import jwill392.nanobotsreplay.ui.WorldInfoPanel;
import jwill392.nanobotsreplay.world.WorldModel;
import jwill392.nanobotsreplay.world.display.WorldView;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import replay.ReplayProto.Replay;

import com.google.common.eventbus.EventBus;

public class NBRV extends BasicGame {
	public static final int HEIGHT = 600;
	public static final int WIDTH = 800;
	public static final Rectangle SCREEN = new Rectangle(0, 0, WIDTH, HEIGHT);

	public static final EventBus eventBus = new EventBus();

	private WorldView worldDisplay;
	private WorldInfoPanel infoPanel;

	private WorldModel worldModel;

	public NBRV() {
		super("Nanobots Replay Viewer");
	}

	public static void main(String[] args) throws SlickException, FileNotFoundException, IOException {
		//checkArgument(args.length == 1);
		//checkArgument(new File(args[0]).exists());

		NBRV inst = new NBRV();
		AppGameContainer app = new AppGameContainer(inst);

		app.setShowFPS(false);
		app.setDisplayMode((int)SCREEN.getWidth(), (int)SCREEN.getHeight(), false);
		app.start();
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		container.getGraphics().setBackground(new Color(0x9990AA));

		Assets.loadSheet("assets/spritesheet");
		Assets.loadFont(2);

		UIComponent.setRoot(SCREEN, container);

		worldDisplay = new WorldView(new Rectangle(1, 1, 598, 598));
		eventBus.register(worldDisplay);

		infoPanel = new WorldInfoPanel(new Rectangle(601, 1, 198, 598));
		eventBus.register(infoPanel);


		//Replay rep = Replay.parseFrom(new FileInputStream(args[0]));
		Replay rep = null;
		try {
			rep = Replay.parseFrom(new FileInputStream("/home/jackson/testreplay"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		load(rep);
	}

	@Override
	public void keyPressed(int key, char c) {
		super.keyPressed(key, c);

		if (key == Input.KEY_RIGHT) {
			if (worldModel != null && worldModel.hasNextTurn()) {
				worldModel.nextTurn();
			}
		} else if (key == Input.KEY_LEFT) {
			if (worldModel != null && worldModel.hasPrevTurn()) {
				worldModel.prevTurn();
			}
		} else if (key == Input.KEY_UP) {
			if (worldModel != null && worldModel.hasTurn(worldModel.getTurn() + 10)) {
				worldModel.setTurn(worldModel.getTurn() + 10);
			}
		} else if (key == Input.KEY_DOWN) {
			if (worldModel != null && worldModel.hasTurn(worldModel.getTurn() - 10)) {
				worldModel.setTurn(worldModel.getTurn() - 10);
			}
		} else if (key == Input.KEY_SPACE) {
			if (worldModel != null) {
				System.out.println(worldModel.getEndTurn());
			}
		}
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		AbstractUIComponent.getRoot().render(container, g);

		if (container.getInput().isKeyDown(Input.KEY_UP)) {
			if (worldModel != null && worldModel.hasTurn(worldModel.getTurn() + 10)) {
				worldModel.setTurn(worldModel.getTurn() + 10);
			}
		}
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		AbstractUIComponent.getRoot().update(container, delta);

		if (worldDisplay != null) {
			worldDisplay.update(container, delta);
		}

	}

	public void load(Replay rep) {
		worldModel = new WorldModel(rep);
		worldDisplay.connectWorldModel(worldModel);
	}
}
