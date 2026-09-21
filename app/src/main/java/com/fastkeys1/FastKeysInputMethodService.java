package com.fastkeys1;
import android.inputmethodservice.InputMethodService; import android.view.View; import android.view.KeyEvent; import android.view.inputmethod.InputConnection;
public class FastKeysInputMethodService extends InputMethodService {
 public View onCreateInputView(){return new FastKeysKeyboardView(this);} 
 public void type(String s){InputConnection ic=getCurrentInputConnection(); if(ic!=null)ic.commitText(s,1);} 
 public void backspace(){InputConnection ic=getCurrentInputConnection(); if(ic!=null)ic.deleteSurroundingText(1,0);} 
 public void enter(){InputConnection ic=getCurrentInputConnection(); if(ic!=null){ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_ENTER));ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_ENTER));}}
 public void move(int code){InputConnection ic=getCurrentInputConnection(); if(ic!=null){ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,code));ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,code));}}
}
