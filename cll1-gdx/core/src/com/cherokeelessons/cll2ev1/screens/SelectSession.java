package com.cherokeelessons.cll2ev1.screens;

import java.nio.charset.Charset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Scaling;
import com.cherokeelessons.cll2ev1.AbstractGame;
import com.cherokeelessons.cll2ev1.CLL2EV1;
import com.cherokeelessons.cll2ev1.StartSession;
import com.cherokeelessons.deck.DeckStats;
import com.cherokeelessons.deck.SkillLevel;
import com.cherokeelessons.util.SlotFolder;

public class SelectSession extends AbstractScreen {
	private static final Logger log = new Logger(SelectSession.class.getSimpleName(), Logger.INFO);
	private static final long HOUR_ms = 60l*60l*1000l;
	
	/**
     * Eight-bit UCS Transformation Format
     */
    public static final Charset UTF_8 = Charset.forName("UTF-8");

	private static final String EMPTY_SLOT = "--- EMPTY SLOT ---";

	private static final String TRASH = "images/trash.png";

	private Json json = new Json();

	protected static final String TITLE = "ᎦᏙ ᎤᏍᏗ ᏣᏚᎵᎭ?";

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
			assets.load(TRASH, Texture.class);
			assets.finishLoadingAsset(TRASH);
			Texture trash = assets.get(TRASH, Texture.class);

			Label titleLabel = new Label(TITLE, skin);
			 titleLabel.setFontScale(.85f);

			Table menu = new Table(skin);
			menu.setFillParent(true);
			menu.defaults().expand();
			menu.row();
			menu.add(titleLabel);
			for (int ix = 0; ix < 4; ix++) {
				FileHandle fh = SlotFolder.getSlotFolder(ix).child(CLL2EV1.DECKSTATS);
				DeckStats di;
				try {
					String strJson;
					strJson = fh.readString(UTF_8.name());
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
					text = EMPTY_SLOT;
				}
				int masterDeckSize = ((CLL2EV1)game).cards.size();
				int percentInUse = 100*t/masterDeckSize;
				text += "\n" + nextSessionIndicationText(di.nextrun, percentInUse);
				
				Image btnDeleteSession = new Image(trash);
				btnDeleteSession.setScaling(Scaling.fit);
				btnDeleteSession.setColor(Color.DARK_GRAY);
				TextButton btnSession = new TextButton(text, skin);
				btnSession.getLabel().setFontScale(.7f);
				btnSession.getLabel().setAlignment(Align.center);
				menu.row();
				menu.add(btnSession).fillX().expand();
				menu.add(btnDeleteSession).expand(false, false);
				btnSession.addListener(chooseSession(ix));
				if (t!=0) {
					btnDeleteSession.addListener(deleteSessionConfirm(ix, btnDeleteSession, btnSession));
				} else {
					btnDeleteSession.setColor(Color.LIGHT_GRAY);
				}
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

	protected ClickListener deleteSessionConfirm(final int session, final Image btnTrashcan,
			final TextButton btnSession) {
		return new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Dialog confirmDelete = new Dialog("ᎯᎠᏍ ᏣᏚᎵᎭ?", skin) {
					protected void result(Object object) {
						if ("YES".equals(object)) {
							SlotFolder.getSlotFolder(session).deleteDirectory();
							btnSession.setText(EMPTY_SLOT+"\n"+nextSessionIndicationText(0l, 0));
							btnTrashcan.setColor(Color.LIGHT_GRAY);
							btnTrashcan.clearListeners();
						}
					};
				};
				confirmDelete.button("ᎥᎥ - YES", "YES");
				confirmDelete.button("ᎥᏝ - NO", "NO");
				confirmDelete.text("Are you sure you want\n" + "to delete this session?\n" + "This cannot be undone!");
				confirmDelete.setModal(true);
				confirmDelete.pack();
				confirmDelete.show(stage);
			}
		};
	}

	protected ClickListener chooseSession(final int session) {
		return new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						log("SelectSession#clear");
						stage.clear();
					}
				});
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

	@Override
	protected void act(float delta) {
	}

	private static String nextSessionIndicationText(long nextRun, int percentInUse) {
		String text = percentInUse+"% cards in play.";
		nextRunNotice: if (nextRun!=0l) {
			nextRun-=System.currentTimeMillis();
			log.info("nextRun ms: "+nextRun);
			if (nextRun<0) {
				text += " Your practice is PAST DUE!";
				break nextRunNotice;
			}
			double hours = (double)nextRun/(double)HOUR_ms;
			log.info("nextRun hours: "+hours);
			if (hours<4d) {
				text += " Practice again now.";
				break nextRunNotice;
			}
			if (hours<12d) {
				text += " Practice again later today.";
				break nextRunNotice;
			}
			if (hours<36d) {
				text += " Practice again tomorrow.";
				break nextRunNotice;
			}
			int days = (int) Math.ceil(hours/24d);
			log.info("nextRun days: "+days);
			text += " Practice again in "+days+" days.";
		}
		return text;
	}
}
