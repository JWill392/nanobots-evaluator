package jwill392.nanobotsreplay.world.display.ent;

import java.util.Collections;
import java.util.EnumMap;
import org.newdawn.slick.Image;
import org.newdawn.slick.PackedSpriteSheet;
import org.newdawn.slick.geom.Vector2f;

import com.google.common.collect.ImmutableMap;
import replay.ReplayProto.Replay;
import replay.ReplayProto.Replay.Action.Type;
import replay.ReplayProto.Replay.Entity;
import teampg.grid2d.point.AbsPos;
import teampg.grid2d.point.RelPos;
import jwill392.nanobotsreplay.assets.Assets;
import jwill392.nanobotsreplay.world.EntityModel;
import jwill392.nanobotsreplay.world.display.WorldView;
import jwill392.slickutil.SlickUtil;

public class DisplayBot extends WorldDisplayEntity {
	private final Image idleImg;
	private final Image focusImg;
	private static EnumMap<Replay.Action.Type, Image> actionImg;
	private static ImmutableMap<Vector2f, Image> dirImg;
	private static boolean initialized = false;

	public DisplayBot(WorldView world, EntityModel data) {
		super(world, data);

		PackedSpriteSheet sheet = Assets.getSheet("assets/spritesheet");


		if (!initialized) {
			initialized = true;

			dirImg = new ImmutableMap.Builder<Vector2f, Image>()
			.put(new Vector2f( 0, -1), sheet.getSprite("bot_dir_mu.gif"))
			.put(new Vector2f( 1, -1), sheet.getSprite("bot_dir_ru.gif"))
			.put(new Vector2f( 1,  0), sheet.getSprite("bot_dir_rm.gif"))
			.put(new Vector2f( 1,  1), sheet.getSprite("bot_dir_rb.gif"))
			.put(new Vector2f( 0,  1), sheet.getSprite("bot_dir_mb.gif"))
			.put(new Vector2f(-1,  1), sheet.getSprite("bot_dir_lb.gif"))
			.put(new Vector2f(-1,  0), sheet.getSprite("bot_dir_lm.gif"))
			.put(new Vector2f(-1, -1), sheet.getSprite("bot_dir_lu.gif"))
			.build();

			actionImg = new EnumMap<>(Replay.Action.Type.class);
			actionImg.put(Type.ATTACK, sheet.getSprite("bot_attack.gif"));
			actionImg.put(Type.BIRTH, sheet.getSprite("bot_birth.gif"));
			actionImg.put(Type.CONCEIVE, sheet.getSprite("bot_conceive.gif"));
			actionImg.put(Type.HARVEST, sheet.getSprite("bot_harvest.gif"));
			actionImg.put(Type.MOVE, sheet.getSprite("bot_move.gif"));
			actionImg.put(Type.TRANSFER, sheet.getSprite("bot_transfer.gif"));
			actionImg.put(Type.BROADCAST, sheet.getSprite("bot_transmit.gif"));
			actionImg.put(Type.WAIT, sheet.getSprite("bot_wait.gif"));
		}

		idleImg = sheet.getSprite("bot.gif");
		idleImg.setCenterOfRotation(idleImg.getWidth()/2, idleImg.getHeight()/2);

		focusImg = sheet.getSprite("bot_focus.gif");
		focusImg.setCenterOfRotation(idleImg.getWidth()/2, idleImg.getHeight()/2);
	}

	@Override
	protected void draw(float x, float y) {
		// TODO button-ify... img hover/click etc is currently wrong
		if (isFocused() || (isMouseDown() && isMouseHover())) {
			focusImg.draw(x, y);
		}
		idleImg.draw(x, y);

		drawActionIndicator(x, y);
	}

	private void drawActionIndicator(float x, float y) {
		if (!getData().hasTurn(getTurn() + 1)) {
			return;
		}

		Entity nextTurnPlans = getData().getTurn(getTurn() + 1);
		Entity currTurnInfo = getData().getTurn(getTurn());

		if (!nextTurnPlans.hasRunningAction()) {
			return;
		}

		Replay.Action action = nextTurnPlans.getRunningAction();
		actionImg.get(action.getType()).draw(x, y);

		if (action.hasTarget()) {
			AbsPos currPos = replay.Util.of(currTurnInfo.getPos());
			AbsPos target = replay.Util.of(action.getTarget());

			Vector2f targetVector = SlickUtil.of(RelPos.offsetVector(currPos,target));

			SlickUtil.ByAngleLikeness byNearnessToTargetAngle = new SlickUtil.ByAngleLikeness(targetVector);

			Vector2f nearestImgVector = Collections.min(dirImg.keySet(), byNearnessToTargetAngle);
			Image dirImgToUse = dirImg.get(nearestImgVector);

			dirImgToUse.draw(x, y);
		}
	}


}
