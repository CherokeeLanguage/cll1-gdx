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
import com.cherokeelessons.cll2ev1.models.CardData;
import com.cherokeelessons.cll2ev1.models.GameCard;
import com.cherokeelessons.deck.CardStats;
import com.cherokeelessons.deck.Deck;
import com.cherokeelessons.deck.DeckStats;
import com.cherokeelessons.deck.ICard;
import com.cherokeelessons.deck.SkillLevel;
import com.cherokeelessons.util.SlotFolder;

public class SelectSession extends AbstractScreen {

	private Json json = new Json();

	protected static final String TITLE = "Please choose session:";
	private static final String BACK = "[BACK]";

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
			TextButton btnBack = new TextButton(BACK, skin);
			btnBack.getLabel().setFontScale(.7f);
			btnBack.pack();
			btnBack.addListener(onBack);
			menu.row();
			menu.add(btnBack).left().fill(false).expand(false, false);

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

			stage.addActor(menu);
		}
	};

	protected ClickListener chooseSession(final int session) {
		return new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// json.setOutputType(OutputType.json);
				// json.setTypeName(null);
				FileHandle fh = SlotFolder.getSlotFolder(session).child("ActiveDeck.txt");
				String tmp;
				try {
					tmp = fh.readString(StandardCharsets.UTF_8.name());
				} catch (Exception e) {
					tmp = "";
				}
				String[] jsonCardsStats = tmp.split("(?s)\\s*\n\\s*");
				/*
				 * always create a fresh deck using cards generated from CSV as
				 * master deck. if we have a stats that don't match a card in
				 * the master deck they will be ignored and lost at next save.
				 * the match up is done by "card id" which is the minimal amount
				 * of uniqueness to match up, ignoring filenames and other data
				 * that doesn't count toward a cards uniqueness
				 */
				Deck<CardData> deck = new Deck<CardData>();
				for (GameCard card : ((CLL2EV1) game).cards) {
					deck.add(card.copy());
				}
				for (String jsonCardStats : jsonCardsStats) {
					if (jsonCardStats.trim().isEmpty()) {
						continue;
					}
					if (!jsonCardStats.contains("\t")) {
						continue;
					}
					String[] txtStats = jsonCardStats.split("\t");
					if (txtStats[0].trim().isEmpty()) {
						continue;
					}
					if (txtStats[1].trim().isEmpty()) {
						continue;
					}
					String id = txtStats[0];
					copyStatsLoop: for (ICard<CardData> card : deck.getCards()) {
						if (!id.equals(card.id())) {
							continue;
						}
						CardStats stats = json.fromJson(CardStats.class, txtStats[1]);
						card.setCardStats(stats);
						break copyStatsLoop;
					}
				}
				if (tmp.trim().isEmpty()) {
					//we didn't have a valid stats file, create a new one
					StringBuilder sb = new StringBuilder();
					for (ICard<CardData> card : deck.getCards()) {
						sb.append(card.id());
						sb.append("\t");
						sb.append(json.toJson(card.getCardStats()));
						sb.append("\n");
					}
					fh.writeString(sb.toString(), false, StandardCharsets.UTF_8.name());
				}
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
