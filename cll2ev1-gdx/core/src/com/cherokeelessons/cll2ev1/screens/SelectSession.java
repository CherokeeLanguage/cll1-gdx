package com.cherokeelessons.cll2ev1.screens;

import java.nio.charset.StandardCharsets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.cherokeelessons.cll2ev1.AbstractGame;
import com.cherokeelessons.cll2ev1.CLL2EV1;
import com.cherokeelessons.cll2ev1.StartSession;
import com.cherokeelessons.deck.DeckStats;
import com.cherokeelessons.deck.SkillLevel;
import com.cherokeelessons.util.SlotFolder;

public class SelectSession extends AbstractScreen {

	private Json json = new Json();

	protected static final String TITLE = "Please choose session:";

	public SelectSession(AbstractGame game) {
		super(game);
		setBackdrop(CLL2EV1.BACKDROP);
		setSkin(CLL2EV1.SKIN);
		json.setUsePrototypes(false);
		Gdx.app.postRunnable(init);
	}

	protected Runnable init = new Runnable() {
		@Override
		public void run() {
			Label titleLabel = new Label(TITLE, skin);
			// titleLabel.setFontScale(.75f);

			Table menu = new Table(skin);
			menu.setFillParent(true);
			menu.defaults().expand();
			menu.row();
			menu.add(titleLabel);
			for (int ix = 0; ix < 4; ix++) {
				FileHandle fh = SlotFolder.getSlotFolder(ix).child("DeckStats.json");
				DeckStats di;
				try {
					String strJson;
					strJson = fh.readString(StandardCharsets.UTF_8.name());
					di = json.fromJson(DeckStats.class, strJson);
				} catch (Exception e) {
					di = new DeckStats();
					di.level = SkillLevel.Newbie;
					json.toJson(di, fh);
				}
				String c = di.level == null ? SkillLevel.Newbie.getEnglish() : di.level.getEnglish();
				int a = di.proficiency;
				int t = di.activeCards;
				String text = c + " - Active Cards: " + t + " - Proficiency: " + a + "%";
				if (t == 0) {
					text = "--- EMPTY SLOT ---";
				}
				TextButton btnSession = new TextButton(text, skin);
				btnSession.getLabel().setFontScale(.8f);
				btnSession.getLabel().setAlignment(Align.center);
				menu.row();
				menu.add(btnSession).fillX();
				btnSession.addListener(chooseSession(ix));
			}
			TextButton btnBack = new TextButton(CLL2EV1.BACKTEXT, skin);
			btnBack.getLabel().setFontScale(.7f);
			btnBack.pack();
			btnBack.addListener(onBack);
			menu.row();
			menu.add(btnBack).left().fill(false).expand(false, false);
			stage.addActor(menu);
		}
	};

	protected ClickListener chooseSession(final int session) {
		return new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.postRunnable(new StartSession(game, session));
			}
		};
	}

	private ClickListener onBack = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			onBack();
		};
	};

	@Override
	protected boolean onBack() {
		game.previousScreen();
		return true;
	}

	@Override
	protected boolean onMenu() {
		return false;
	}

}
