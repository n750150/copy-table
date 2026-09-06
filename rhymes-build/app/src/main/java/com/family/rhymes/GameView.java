package com.family.rhymes;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class GameView extends View implements TextToSpeech.OnInitListener {
    private static final int NONE = 0;
    private static final int SELECTED = 1;
    private static final int CORRECT = 2;
    private static final int WRONG = 3;

    private static final class Card {
        final String word;
        final String asset;
        final int pairId;
        final RectF rect = new RectF();
        Bitmap bitmap;
        boolean removed = false;
        int state = NONE;

        Card(String word, String asset, int pairId) {
            this.word = word;
            this.asset = asset;
            this.pairId = pairId;
        }
    }

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SharedPreferences prefs;
    private final float density;
    private final List<Card> cards = new ArrayList<>();
    private final Map<Integer, Bitmap> atlases = new HashMap<>();
    private final RectF restartRect = new RectF();
    private final RectF prevRect = new RectF();
    private final RectF nextRect = new RectF();
    private final RectF repeatRect = new RectF();

    private TextToSpeech tts;
    private boolean ttsReady = false;
    private int level;
    private int selected = -1;
    private boolean inputLocked = false;
    private boolean finished;
    private boolean repeatPair;

    public GameView(Context context) {
        super(context);
        setBackgroundColor(Color.WHITE);
        density = getResources().getDisplayMetrics().density;
        prefs = context.getSharedPreferences("rhymes_progress", Context.MODE_PRIVATE);
        level = Math.max(0, Math.min(GameData.LEVELS.length - 1, prefs.getInt("level", 0)));
        finished = prefs.getBoolean("finished", false);
        repeatPair = prefs.getBoolean("repeat_pair", false);
        tts = new TextToSpeech(context.getApplicationContext(), this);
        if (!finished) loadLevel(level);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(new Locale("ru", "RU"));
            tts.setSpeechRate(0.88f);
            tts.setPitch(1.02f);
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED;
        }
    }

    private void speak(String text, int queueMode) {
        if (ttsReady && tts != null) {
            tts.speak(text, queueMode, null, "rhymes_" + System.nanoTime());
        }
    }

    private void stopSpeech() {
        if (ttsReady && tts != null) tts.stop();
    }

    private void loadLevel(int newLevel) {
        recycleBitmaps();
        cards.clear();
        selected = -1;
        inputLocked = false;
        finished = false;
        level = Math.max(0, Math.min(GameData.LEVELS.length - 1, newLevel));
        GameData.Pair[] pairs = GameData.LEVELS[level];
        List<Card> raw = new ArrayList<>();
        for (int i = 0; i < pairs.length; i++) {
            GameData.Pair p = pairs[i];
            raw.add(new Card(p.left, p.leftAsset, i));
            raw.add(new Card(p.right, p.rightAsset, i));
        }
        cards.addAll(shuffleWithoutSameRow(raw, level));
        for (Card card : cards) card.bitmap = loadBitmap(card.asset);
        prefs.edit().putInt("level", level).putBoolean("finished", false).apply();
        invalidate();
    }

    private void goToLevel(int newLevel) {
        if (newLevel < 0 || newLevel >= GameData.LEVELS.length || newLevel == level) return;
        handler.removeCallbacksAndMessages(null);
        stopSpeech();
        loadLevel(newLevel);
    }

    private List<Card> shuffleWithoutSameRow(List<Card> raw, int levelNumber) {
        Random random = new Random(20260906L + levelNumber * 7919L);
        List<Card> shuffled = new ArrayList<>(raw);
        for (int attempt = 0; attempt < 500; attempt++) {
            Collections.shuffle(shuffled, random);
            boolean ok = true;
            Map<Integer, Integer> firstRow = new HashMap<>();
            for (int i = 0; i < shuffled.size(); i++) {
                int row = i / 2;
                int pair = shuffled.get(i).pairId;
                Integer seen = firstRow.get(pair);
                if (seen != null && seen == row) { ok = false; break; }
                firstRow.put(pair, row);
            }
            if (ok) return shuffled;
        }
        return shuffled;
    }

    private Bitmap loadBitmap(String asset) {
        try {
            int marker = asset.indexOf("card_");
            int dot = asset.lastIndexOf('.');
            if (marker < 0 || dot < 0) return null;
            int cardIndex = Integer.parseInt(asset.substring(marker + 5, dot));
            int atlasIndex = cardIndex / 50;
            int slot = cardIndex % 50;

            Bitmap atlas = atlases.get(atlasIndex);
            if (atlas == null || atlas.isRecycled()) {
                try (InputStream in = getContext().getAssets().open("atlases/atlas_" + atlasIndex + ".webp")) {
                    atlas = BitmapFactory.decodeStream(in);
                }
                if (atlas == null) return null;
                atlases.put(atlasIndex, atlas);
            }

            final int tileW = 192;
            final int tileH = 180;
            final int cols = 5;
            int col = slot % cols;
            int row = slot / cols;
            return Bitmap.createBitmap(atlas, col * tileW, row * tileH, tileW, tileH);
        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }

    private void recycleBitmaps() {
        for (Card card : cards) card.bitmap = null;
        for (Bitmap atlas : atlases.values()) {
            if (atlas != null && !atlas.isRecycled()) atlas.recycle();
        }
        atlases.clear();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;
        if (finished) {
            drawFinish(canvas, w, h);
            return;
        }

        float top = dp(12);
        float navSize = dp(52);
        float navTop = top;
        prevRect.set(dp(10), navTop, dp(10) + navSize, navTop + navSize);
        nextRect.set(w - dp(10) - navSize, navTop, w - dp(10), navTop + navSize);
        drawNavButton(canvas, prevRect, "‹", level > 0);
        drawNavButton(canvas, nextRect, "›", level < GameData.LEVELS.length - 1);

        textPaint.setColor(Color.rgb(13, 91, 163));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(sp(25));
        canvas.drawText("Уровень " + (level + 1) + " / " + GameData.LEVELS.length, w / 2f, top + dp(33), textPaint);

        textPaint.setColor(Color.rgb(35, 35, 35));
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT);
        textPaint.setTextSize(sp(15));
        canvas.drawText("Найди 4 пары рифм", w / 2f, top + dp(58), textPaint);

        float repeatW = Math.min(w - dp(40), dp(230));
        float repeatH = dp(36);
        repeatRect.set(w / 2f - repeatW / 2f, top + dp(67), w / 2f + repeatW / 2f, top + dp(67) + repeatH);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(repeatPair ? Color.rgb(222, 247, 231) : Color.rgb(241, 244, 247));
        canvas.drawRoundRect(repeatRect, dp(18), dp(18), paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(1.5f));
        paint.setColor(repeatPair ? Color.rgb(35, 174, 94) : Color.rgb(170, 178, 186));
        canvas.drawRoundRect(repeatRect, dp(18), dp(18), paint);
        paint.setStyle(Paint.Style.FILL);
        textPaint.setColor(repeatPair ? Color.rgb(25, 130, 70) : Color.rgb(85, 92, 100));
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(sp(14));
        canvas.drawText("Повтор пары: " + (repeatPair ? "вкл" : "выкл"), repeatRect.centerX(), repeatRect.centerY() + dp(5), textPaint);

        float gridTop = top + dp(112);
        float side = dp(10);
        float gap = dp(8);
        float bottom = dp(12);
        float cardW = (w - side * 2 - gap) / 2f;
        float cardH = (h - gridTop - bottom - gap * 3) / 4f;

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            int row = i / 2;
            int col = i % 2;
            float left = side + col * (cardW + gap);
            float y = gridTop + row * (cardH + gap);
            card.rect.set(left, y, left + cardW, y + cardH);
            if (!card.removed) drawCard(canvas, card);
        }
    }

    private void drawNavButton(Canvas canvas, RectF rect, String symbol, boolean enabled) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(enabled ? Color.rgb(231, 245, 255) : Color.rgb(247, 248, 249));
        canvas.drawRoundRect(rect, dp(14), dp(14), paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(1.8f));
        paint.setColor(enabled ? Color.rgb(86, 171, 229) : Color.rgb(210, 214, 218));
        canvas.drawRoundRect(rect, dp(14), dp(14), paint);
        paint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(sp(34));
        textPaint.setColor(enabled ? Color.rgb(13, 91, 163) : Color.rgb(190, 194, 198));
        canvas.drawText(symbol, rect.centerX(), rect.centerY() + dp(11), textPaint);
    }

    private void drawCard(Canvas canvas, Card card) {
        float radius = dp(16);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(card.rect, radius, radius, paint);

        if (card.bitmap != null) {
            RectF dst = insetToFit(card.rect, card.bitmap.getWidth(), card.bitmap.getHeight(), dp(4));
            canvas.drawBitmap(card.bitmap, null, dst, paint);
        }

        int border;
        float stroke = dp(2.5f);
        if (card.state == SELECTED) { border = Color.rgb(255, 183, 36); stroke = dp(5); }
        else if (card.state == CORRECT) { border = Color.rgb(35, 174, 94); stroke = dp(7); }
        else if (card.state == WRONG) { border = Color.rgb(230, 70, 70); stroke = dp(6); }
        else border = Color.rgb(117, 190, 239);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setColor(border);
        canvas.drawRoundRect(card.rect, radius, radius, paint);
        paint.setStyle(Paint.Style.FILL);

        if (card.state == CORRECT) {
            paint.setColor(Color.argb(62, 35, 174, 94));
            canvas.drawRoundRect(card.rect, radius, radius, paint);
            textPaint.setColor(Color.rgb(24, 135, 70));
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(sp(42));
            textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            canvas.drawText("✓", card.rect.centerX(), card.rect.centerY() + dp(16), textPaint);
        } else if (card.state == WRONG) {
            paint.setColor(Color.argb(42, 230, 70, 70));
            canvas.drawRoundRect(card.rect, radius, radius, paint);
        }
    }

    private RectF insetToFit(RectF outer, int bw, int bh, float inset) {
        RectF box = new RectF(outer.left + inset, outer.top + inset, outer.right - inset, outer.bottom - inset);
        float scale = Math.min(box.width() / bw, box.height() / bh);
        float dw = bw * scale;
        float dh = bh * scale;
        return new RectF(box.centerX() - dw / 2f, box.centerY() - dh / 2f, box.centerX() + dw / 2f, box.centerY() + dh / 2f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) return true;

        float x = event.getX();
        float y = event.getY();

        if (finished) {
            if (restartRect.contains(x, y)) restartGame();
            return true;
        }

        if (prevRect.contains(x, y)) {
            goToLevel(level - 1);
            return true;
        }
        if (nextRect.contains(x, y)) {
            goToLevel(level + 1);
            return true;
        }
        if (repeatRect.contains(x, y)) {
            repeatPair = !repeatPair;
            prefs.edit().putBoolean("repeat_pair", repeatPair).apply();
            invalidate();
            return true;
        }

        if (inputLocked) return true;
        int hit = findCard(x, y);
        if (hit < 0) return true;
        Card card = cards.get(hit);

        // Important: queue each word instead of cutting off the previous one.
        // Rapid taps now produce complete words in order rather than fragments.
        speak(card.word, TextToSpeech.QUEUE_ADD);

        if (selected < 0) {
            selected = hit;
            card.state = SELECTED;
            invalidate();
            return true;
        }
        if (selected == hit) {
            card.state = NONE;
            selected = -1;
            invalidate();
            return true;
        }

        Card first = cards.get(selected);
        if (first.pairId == card.pairId) {
            int firstIndex = selected;
            int secondIndex = hit;
            first.state = CORRECT;
            card.state = CORRECT;
            selected = -1;
            inputLocked = true;
            invalidate();

            if (repeatPair) {
                GameData.Pair pair = GameData.LEVELS[level][first.pairId];
                handler.postDelayed(() -> speak(pair.left + " — " + pair.right, TextToSpeech.QUEUE_ADD), 180);
            }

            handler.postDelayed(() -> {
                cards.get(firstIndex).removed = true;
                cards.get(secondIndex).removed = true;
                cards.get(firstIndex).state = NONE;
                cards.get(secondIndex).state = NONE;
                inputLocked = false;
                invalidate();
                if (allRemoved()) handler.postDelayed(this::advanceLevel, 450);
            }, 650);
        } else {
            int firstIndex = selected;
            int secondIndex = hit;
            first.state = WRONG;
            card.state = WRONG;
            selected = -1;
            inputLocked = true;
            invalidate();
            handler.postDelayed(() -> {
                if (!cards.get(firstIndex).removed) cards.get(firstIndex).state = NONE;
                if (!cards.get(secondIndex).removed) cards.get(secondIndex).state = NONE;
                inputLocked = false;
                invalidate();
            }, 450);
        }
        return true;
    }

    private int findCard(float x, float y) {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (!card.removed && card.rect.contains(x, y)) return i;
        }
        return -1;
    }

    private boolean allRemoved() {
        for (Card card : cards) if (!card.removed) return false;
        return true;
    }

    private void advanceLevel() {
        if (level + 1 < GameData.LEVELS.length) {
            int next = level + 1;
            // QUEUE_ADD is deliberate: the final card's word must finish first.
            speak("Молодец!", TextToSpeech.QUEUE_ADD);
            loadLevel(next);
        } else {
            recycleBitmaps();
            cards.clear();
            finished = true;
            prefs.edit().putBoolean("finished", true).apply();
            // Also queued so it never chops off the second word of the final pair.
            speak("Молодец! Все рифмы найдены!", TextToSpeech.QUEUE_ADD);
            invalidate();
        }
    }

    private void drawFinish(Canvas canvas, int w, int h) {
        canvas.drawColor(Color.rgb(248, 252, 255));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        textPaint.setColor(Color.rgb(13, 91, 163));
        textPaint.setTextSize(sp(38));
        canvas.drawText("Молодец!", w / 2f, h * 0.35f, textPaint);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT);
        textPaint.setColor(Color.rgb(40, 40, 40));
        textPaint.setTextSize(sp(22));
        canvas.drawText("Все 25 уровней пройдены", w / 2f, h * 0.42f, textPaint);

        float bw = Math.min(w * 0.72f, dp(320));
        float bh = dp(68);
        restartRect.set(w / 2f - bw / 2f, h * 0.55f, w / 2f + bw / 2f, h * 0.55f + bh);
        paint.setColor(Color.rgb(35, 174, 94));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(restartRect, dp(34), dp(34), paint);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(sp(21));
        canvas.drawText("Играть снова", restartRect.centerX(), restartRect.centerY() + dp(8), textPaint);
    }

    private void restartGame() {
        handler.removeCallbacksAndMessages(null);
        stopSpeech();
        finished = false;
        prefs.edit().putBoolean("finished", false).putInt("level", 0).apply();
        loadLevel(0);
    }

    private float dp(float v) { return v * density; }
    private float sp(float v) { return v * getResources().getDisplayMetrics().scaledDensity; }

    public void release() {
        handler.removeCallbacksAndMessages(null);
        recycleBitmaps();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}
