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
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private int level;
    private int selected = -1;
    private boolean inputLocked = false;
    private boolean finished;
    private final RectF restartRect = new RectF();

    public GameView(Context context) {
        super(context);
        setBackgroundColor(Color.WHITE);
        density = getResources().getDisplayMetrics().density;
        prefs = context.getSharedPreferences("rhymes_progress", Context.MODE_PRIVATE);
        level = Math.max(0, Math.min(GameData.LEVELS.length - 1, prefs.getInt("level", 0)));
        finished = prefs.getBoolean("finished", false);
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

    private void loadLevel(int newLevel) {
        recycleBitmaps();
        cards.clear();
        selected = -1;
        inputLocked = false;
        level = newLevel;
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

        float top = dp(18);
        textPaint.setColor(Color.rgb(13, 91, 163));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(sp(26));
        canvas.drawText("Уровень " + (level + 1) + " / " + GameData.LEVELS.length, w / 2f, top + dp(30), textPaint);

        textPaint.setColor(Color.rgb(35, 35, 35));
        textPaint.setTypeface(android.graphics.Typeface.DEFAULT);
        textPaint.setTextSize(sp(16));
        canvas.drawText("Найди 4 пары рифм", w / 2f, top + dp(57), textPaint);

        float gridTop = top + dp(72);
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
        if (finished) {
            if (restartRect.contains(event.getX(), event.getY())) restartGame();
            return true;
        }
        if (inputLocked) return true;
        int hit = findCard(event.getX(), event.getY());
        if (hit < 0) return true;
        Card card = cards.get(hit);
        speak(card.word, TextToSpeech.QUEUE_FLUSH);

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
            GameData.Pair pair = GameData.LEVELS[level][first.pairId];
            handler.postDelayed(() -> speak(pair.left + " — " + pair.right, TextToSpeech.QUEUE_ADD), 300);
            handler.postDelayed(() -> {
                cards.get(firstIndex).removed = true;
                cards.get(secondIndex).removed = true;
                cards.get(firstIndex).state = NONE;
                cards.get(secondIndex).state = NONE;
                inputLocked = false;
                invalidate();
                if (allRemoved()) handler.postDelayed(this::advanceLevel, 650);
            }, 1050);
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
            }, 520);
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
            speak("Молодец! Уровень " + (next + 1), TextToSpeech.QUEUE_FLUSH);
            loadLevel(next);
        } else {
            recycleBitmaps();
            cards.clear();
            finished = true;
            prefs.edit().putBoolean("finished", true).apply();
            speak("Молодец! Все рифмы найдены!", TextToSpeech.QUEUE_FLUSH);
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
        finished = false;
        prefs.edit().clear().apply();
        loadLevel(0);
        speak("Уровень один", TextToSpeech.QUEUE_FLUSH);
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
