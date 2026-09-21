package com.fastkeys1;

import android.graphics.*;
import android.os.Handler;
import android.view.*;
import java.util.*;

public class FastKeysKeyboardView extends View {
    private final FastKeysInputMethodService service;
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Handler handler = new Handler();
    private float gap, keyH;
    private boolean caps = false;
    private Runnable repeat;
    private final String[] suggestions = new String[3];
    private static final String[][] WORDS = {
        {"سلام","سلامت","سلامتی"},{"من","منم","منطقه"},{"این","اینجا","اینجانب"},
        {"برای","برنامه","بررسی"},{"کیبورد","کیبوردی","کیبوردها"},{"است","استفاده","استان"},
        {"یک","یکی","یکم"},{"دارم","دارد","دارند"},{"می","میرم","میز"},{"خوب","خوبه","خوبی"},
        {"تایپ","تایپی","تایپ کردن"},{"کلمه","کلمات","کلمه‌های"}
    };
    private final int BG=Color.rgb(239,238,232), KEY=Color.rgb(250,249,244), BLUE=Color.rgb(20,112,235), BLACK=Color.rgb(25,29,34), GREEN=Color.rgb(45,205,55);

    public FastKeysKeyboardView(FastKeysInputMethodService s){ super(s); service=s; setBackgroundColor(BG); }
    private void txt(Canvas c,String s,float x,float y,float size,int color){ p.setTypeface(Typeface.create("sans",Typeface.NORMAL));p.setTextSize(size);p.setColor(color);p.setTextAlign(Paint.Align.CENTER);c.drawText(s,x,y-(p.ascent()+p.descent())/2,p); }
    private void key(Canvas c,float l,float t,float r,float b,String label,int color,boolean square){p.setColor(KEY);p.setStyle(Paint.Style.FILL);float rad=square?3:7;c.drawRoundRect(l,t,r,b,rad,rad,p);p.setColor(Color.rgb(205,204,199));p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1);c.drawRoundRect(l,t,r,b,rad,rad,p);p.setStyle(Paint.Style.FILL);if(label!=null&&!label.isEmpty())txt(c,label,(l+r)/2,(t+b)/2,Math.min(22,(b-t)*.42f),color);}

    @Override protected void onDraw(Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight();gap=1;int rows=7;keyH=(h-gap*(rows+1))/rows;float y=gap;
        float[] wt={.55f,1.45f,1.55f,1.05f,1.0f,1.0f,1.0f,1.25f,.55f};String[] top={"⌃","Copy All","Copy Screen","Paste","Cut","Undo","Redo","100\nHistory","⌄"};row(c,y,wt,top);
        y+=keyH+gap; String[] sug={suggestions[0],suggestions[1],suggestions[2]}; for(int i=0;i<3;i++) key(c,i*w/3f,y,(i+1)*w/3f,y+keyH,sug[i],BLUE,false);
        y+=keyH+gap;float[] w2={.55f,.9f,.9f,.9f,.9f,.9f,.9f,.9f,.9f,.9f,.9f,.9f,.9f,1.45f};String[] sy={"⌃","!\n۱","@\n۲","#\n۳","$\n۴","%\n۵","^\n۶","&\n۷","*\n۸","(\n۹",")\n۰","-\n_","=\n+","⌫"};row(c,y,w2,sy);
        y+=keyH+gap;drawArrow(c,0,y,keyH,keyH,"↑");String[] r3={"CAPS","ض","ص","ث","ق","ف","غ","ع","ه","خ","ج","{\n[","}\n]","|\n\\"};rowFrom(c,y,keyH,r3);if(caps){p.setColor(GREEN);c.drawCircle(12,y+10,4,p);}
        y+=keyH+gap;drawArrow(c,0,y,keyH,keyH,"↓");String[] r4={"ش","س","ی","ب","ل","ت","ا","ک","گ","؛","«"};rowFrom(c,y,keyH,r4);key(c,w-keyH-gap,y,w,y+2*keyH+gap,"Enter",BLACK,false);
        y+=keyH+gap;String[] r5={"،","ژ","ذ","ز","گ","چ","پ","ب","ن","م","،","/\n؟"};rowFrom(c,y,keyH,r5);
        y+=keyH+gap;float[] bw={1,1,1,3.9f,1.35f,1.35f,1.15f,1.15f};String[] b={"!#@","◎","☺","Space","←","→","↑","↓"};row(c,y,bw,b);
    }
    private void row(Canvas c,float y,float[] weights,String[] labels){float total=0;for(float q:weights)total+=q;float ww=(getWidth()-gap*(weights.length+1))/total,x=gap;for(int i=0;i<weights.length;i++){float cw=ww*weights[i];String s=labels[i];if(s.contains("\n")){key(c,x,y,x+cw,y+keyH,"",BLACK,false);String[] a=s.split("\\n");txt(c,a[0],x+cw/2,y+keyH*.35f,Math.min(20,keyH*.3f),BLACK);txt(c,a[1],x+cw/2,y+keyH*.7f,Math.min(20,keyH*.3f),BLACK);}else key(c,x,y,x+cw,y+keyH,s,BLACK,false);x+=cw+gap;}}
    private void rowFrom(Canvas c,float y,float left,String[] labels){float x=left+gap,available=getWidth()-left-gap;float ww=(available-gap*(labels.length+1))/labels.length;for(String s:labels){key(c,x,y,x+ww,y+keyH,s,BLUE,false);x+=ww+gap;}}
    private void drawArrow(Canvas c,float x,float y,float ww,float hh,String s){key(c,x,y,x+ww,y+hh,"",BLACK,true);txt(c,s,x+ww/2,y+hh/2,hh*.55f,BLACK);}

    public void refreshSuggestions(){CharSequence q=service.getCurrentInputConnection()==null?null:service.getCurrentInputConnection().getTextBeforeCursor(80,0);String word="";if(q!=null){String b=q.toString();int i=b.length()-1;while(i>=0&&!Character.isWhitespace(b.charAt(i)))i--;word=b.substring(i+1);}Arrays.fill(suggestions,"");if(word.length()==0){invalidate();return;}int n=0;for(String[] group:WORDS){for(String x:group)if(x.startsWith(word)&&!x.equals(word)&&n<3)suggestions[n++]=x;}if(n==0){for(String[] group:WORDS)for(String x:group)if(x.contains(word)&&n<3)suggestions[n++]=x;}invalidate();}

    @Override public boolean onTouchEvent(MotionEvent e){if(e.getAction()==MotionEvent.ACTION_DOWN){handle(e.getX(),e.getY());return true;}if(e.getAction()==MotionEvent.ACTION_UP||e.getAction()==MotionEvent.ACTION_CANCEL)stopRepeat();return true;}
    private void handle(float x,float y){float w=getWidth();int row=(int)((y-gap)/(keyH+gap));
        if(row==0){int i=(int)(x/(w/9f));if(i==1)service.copyAll();else if(i==3)service.paste();else if(i==4)service.cut();else if(i==5)service.undo();return;}
        if(row==1){int i=(int)(x/(w/3f));if(i>=0&&i<3&&!suggestions[i].isEmpty())service.replaceCurrentWord(suggestions[i]);return;}
        if(row==2){if(x>w-keyH*1.6f){startBackspace();return;}String[] s={"!","@","#","$","%","^","&","*","(",")","-","="};int i=Math.max(0,Math.min(11,(int)((x-keyH*.55f)/(w/13f))));service.type(s[i]);return;}
        if(row==3){if(x<keyH*1.5f){caps=!caps;invalidate();return;}String[] s={"ض","ص","ث","ق","ف","غ","ع","ه","خ","ج"};int i=Math.max(0,Math.min(9,(int)((x-keyH)/(w/12f))));service.type(s[i]);return;}
        if(row==4){if(x<keyH*1.5f){service.move(KeyEvent.KEYCODE_DPAD_DOWN);return;}if(x>w-keyH*1.5f){service.enter();return;}String[] s={"ش","س","ی","ب","ل","ت","ا","ک","گ","؛","«"};int i=Math.max(0,Math.min(10,(int)((x-keyH)/(w/12f))));service.type(s[i]);return;}
        if(row==5){String[] s={"،","ژ","ذ","ز","گ","چ","پ","ب","ن","م","،","/؟"};int i=Math.max(0,Math.min(11,(int)(x/(w/12f))));service.type(s[i]);return;}
        if(row==6){int i=(int)(x/(w/8f));if(i==0)service.type("!#@");else if(i==1)service.switchInputMethod(null);else if(i==2)service.type("🙂");else if(i==3)service.type(" ");else if(i==4)service.move(KeyEvent.KEYCODE_DPAD_LEFT);else if(i==5)service.move(KeyEvent.KEYCODE_DPAD_RIGHT);else if(i==6)service.move(KeyEvent.KEYCODE_DPAD_UP);else if(i==7)service.move(KeyEvent.KEYCODE_DPAD_DOWN);}
    }
    private void startBackspace(){service.backspace();stopRepeat();repeat=()->{service.backspace();handler.postDelayed(repeat,55);};handler.postDelayed(repeat,350);}
    private void stopRepeat(){if(repeat!=null){handler.removeCallbacks(repeat);repeat=null;}}
}
