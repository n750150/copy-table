package com.family.rhymes;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;
import java.util.Locale;

public class GameView extends View implements TextToSpeech.OnInitListener {
  private static final int NONE=0, SELECTED=1, CORRECT=2, WRONG=3;
  private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Paint textPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Handler handler=new Handler(Looper.getMainLooper());
  private final SharedPreferences prefs;
  private final RectF[] rects=new RectF[8];
  private final boolean[] removed=new boolean[8];
  private final int[] state=new int[8];
  private final RectF restartRect=new RectF();
  private final float density;
  private TextToSpeech tts;
  private boolean ttsReady=false, inputLocked=false, finished=false;
  private int level, selected=-1;

  public GameView(Context context) {
    super(context);
    setBackgroundColor(Color.WHITE);
    density=getResources().getDisplayMetrics().density;
    prefs=context.getSharedPreferences("rhymes_progress",Context.MODE_PRIVATE);
    level=Math.max(0,Math.min(GameData.LEVELS.length-1,prefs.getInt("level",0)));
    finished=prefs.getBoolean("finished",false);
    for(int i=0;i<8;i++) rects[i]=new RectF();
    tts=new TextToSpeech(context.getApplicationContext(),this);
  }

  @Override public void onInit(int status) {
    if(status==TextToSpeech.SUCCESS) {
      int result=tts.setLanguage(new Locale("ru","RU"));
      tts.setSpeechRate(0.88f);
      tts.setPitch(1.02f);
      ttsReady=result!=TextToSpeech.LANG_MISSING_DATA && result!=TextToSpeech.LANG_NOT_SUPPORTED;
    }
  }

  private void speak(String text,int queueMode) {
    if(ttsReady && tts!=null) tts.speak(text,queueMode,null,"rhymes_"+System.nanoTime());
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int w=getWidth(), h=getHeight();
    if(w<=0||h<=0) return;
    if(finished){ drawFinish(canvas,w,h); return; }

    float top=dp(18);
    textPaint.setTextAlign(Paint.Align.CENTER);
    textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
    textPaint.setTextSize(sp(26));
    textPaint.setColor(Color.rgb(13,91,163));
    canvas.drawText("Уровень "+(level+1)+" / "+GameData.LEVELS.length,w/2f,top+dp(30),textPaint);
    textPaint.setTypeface(android.graphics.Typeface.DEFAULT);
    textPaint.setTextSize(sp(16));
    textPaint.setColor(Color.DKGRAY);
    canvas.drawText("Найди 4 пары рифм",w/2f,top+dp(58),textPaint);

    float gridTop=top+dp(76), side=dp(10), gap=dp(8), bottom=dp(10);
    float cardW=(w-2*side-gap)/2f;
    float cardH=(h-gridTop-bottom-3*gap)/4f;
    for(int i=0;i<8;i++) {
      int row=i/2, col=i%2;
      float left=side+col*(cardW+gap), y=gridTop+row*(cardH+gap);
      rects[i].set(left,y,left+cardW,y+cardH);
      if(!removed[i]) drawCard(canvas,i);
    }
  }

  private void drawCard(Canvas canvas,int i) {
    RectF r=rects[i];
    int bg=Color.rgb(250,253,255);
    if(state[i]==CORRECT) bg=Color.rgb(222,247,229);
    else if(state[i]==WRONG) bg=Color.rgb(255,231,231);
    else if(state[i]==SELECTED) bg=Color.rgb(255,247,217);
    paint.setStyle(Paint.Style.FILL); paint.setColor(bg);
    canvas.drawRoundRect(r,dp(18),dp(18),paint);

    int border=Color.rgb(117,190,239); float sw=dp(2.5f);
    if(state[i]==SELECTED){border=Color.rgb(255,183,36);sw=dp(5);} 
    else if(state[i]==CORRECT){border=Color.rgb(35,174,94);sw=dp(7);} 
    else if(state[i]==WRONG){border=Color.rgb(230,70,70);sw=dp(6);} 
    paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(sw); paint.setColor(border);
    canvas.drawRoundRect(r,dp(18),dp(18),paint);

    GameData.CardDef card=GameData.LEVELS[level][i];
    textPaint.setTextAlign(Paint.Align.CENTER);
    textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
    textPaint.setColor(Color.rgb(35,52,65));
    textPaint.setTextSize(fitText(card.word,r.width()-dp(20),sp(28),sp(17)));
    Paint.FontMetrics fm=textPaint.getFontMetrics();
    float base=r.centerY()-(fm.ascent+fm.descent)/2f;
    canvas.drawText(card.word,r.centerX(),base,textPaint);
    if(state[i]==CORRECT){textPaint.setTextSize(sp(38));textPaint.setColor(Color.rgb(24,135,70));canvas.drawText("✓",r.right-dp(28),r.top+dp(44),textPaint);}    
  }

  private float fitText(String text,float maxWidth,float start,float min){
    float s=start; textPaint.setTextSize(s);
    while(s>min && textPaint.measureText(text)>maxWidth){s-=1; textPaint.setTextSize(s);} return s;
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if(event.getAction()!=MotionEvent.ACTION_UP) return true;
    if(finished){if(restartRect.contains(event.getX(),event.getY())) restartGame(); return true;}
    if(inputLocked) return true;
    int hit=findCard(event.getX(),event.getY());
    if(hit<0) return true;
    GameData.CardDef card=GameData.LEVELS[level][hit];
    speak(card.word,TextToSpeech.QUEUE_FLUSH);
    if(selected<0){selected=hit;state[hit]=SELECTED;invalidate();return true;}
    if(selected==hit){state[hit]=NONE;selected=-1;invalidate();return true;}

    int firstIndex=selected, secondIndex=hit;
    GameData.CardDef first=GameData.LEVELS[level][firstIndex];
    selected=-1; inputLocked=true;
    if(first.pairId==card.pairId){
      state[firstIndex]=state[secondIndex]=CORRECT; invalidate();
      handler.postDelayed(()->speak(GameData.PAIR_SPEECH[level][first.pairId],TextToSpeech.QUEUE_ADD),300);
      handler.postDelayed(()->{
        removed[firstIndex]=removed[secondIndex]=true;
        state[firstIndex]=state[secondIndex]=NONE;
        inputLocked=false; invalidate();
        if(allRemoved()) handler.postDelayed(this::advanceLevel,650);
      },1100);
    } else {
      state[firstIndex]=state[secondIndex]=WRONG; invalidate();
      handler.postDelayed(()->{state[firstIndex]=state[secondIndex]=NONE;inputLocked=false;invalidate();},520);
    }
    return true;
  }

  private int findCard(float x,float y){for(int i=0;i<8;i++) if(!removed[i]&&rects[i].contains(x,y)) return i; return -1;}
  private boolean allRemoved(){for(boolean v:removed) if(!v) return false; return true;}
  private void advanceLevel(){
    if(level+1<GameData.LEVELS.length){level++;Arrays.fill(removed,false);Arrays.fill(state,NONE);prefs.edit().putInt("level",level).apply();speak("Молодец! Уровень "+(level+1),TextToSpeech.QUEUE_FLUSH);invalidate();}
    else {finished=true;prefs.edit().putBoolean("finished",true).apply();speak("Молодец! Все рифмы найдены!",TextToSpeech.QUEUE_FLUSH);invalidate();}
  }

  private void drawFinish(Canvas canvas,int w,int h){
    canvas.drawColor(Color.rgb(248,252,255));
    textPaint.setTextAlign(Paint.Align.CENTER);textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);textPaint.setTextSize(sp(38));textPaint.setColor(Color.rgb(13,91,163));canvas.drawText("Молодец!",w/2f,h*.35f,textPaint);
    textPaint.setTypeface(android.graphics.Typeface.DEFAULT);textPaint.setTextSize(sp(22));textPaint.setColor(Color.DKGRAY);canvas.drawText("Все 25 уровней пройдены",w/2f,h*.42f,textPaint);
    float bw=Math.min(w*.72f,dp(320)),bh=dp(68);restartRect.set(w/2f-bw/2f,h*.55f,w/2f+bw/2f,h*.55f+bh);paint.setStyle(Paint.Style.FILL);paint.setColor(Color.rgb(35,174,94));canvas.drawRoundRect(restartRect,dp(34),dp(34),paint);textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);textPaint.setTextSize(sp(21));textPaint.setColor(Color.WHITE);canvas.drawText("Играть снова",restartRect.centerX(),restartRect.centerY()+dp(8),textPaint);
  }

  private void restartGame(){finished=false;level=0;selected=-1;inputLocked=false;Arrays.fill(removed,false);Arrays.fill(state,NONE);prefs.edit().clear().apply();speak("Уровень один",TextToSpeech.QUEUE_FLUSH);invalidate();}
  private float dp(float v){return v*density;}
  private float sp(float v){return v*getResources().getDisplayMetrics().scaledDensity;}
  public void release(){handler.removeCallbacksAndMessages(null);if(tts!=null){tts.stop();tts.shutdown();tts=null;}}
}
