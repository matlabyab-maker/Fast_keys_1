package com.fastkeys1;

import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.view.inputmethod.InputMethodInfo;
import java.util.List;

public class FastKeysInputMethodService extends InputMethodService {
    private View keyboardView;

    // Keep the IME in the normal keyboard window instead of fullscreen/extract mode.
    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Window w = getWindow().getWindow();
        if (w != null) {
            w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    private int getKeyboardHeight() {
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        return Math.round(screenHeight * 0.34f);
    }

    @Override
    public View onCreateInputView() {
        FastKeysView view = new FastKeysView(this, getCurrentInputConnection());
        keyboardView = view;
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getKeyboardHeight()));
        return view;
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype newSubtype) {
        super.onCurrentInputMethodSubtypeChanged(newSubtype);
        if (keyboardView instanceof FastKeysView && newSubtype != null) {
            String locale = newSubtype.getLocale();
            ((FastKeysView) keyboardView).setPersianLanguage(locale != null && locale.toLowerCase().startsWith("fa"));
        }
    }

    public void switchLanguageSubtype(boolean persian) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm == null || getToken() == null) return;
            List<InputMethodInfo> list = imm.getEnabledInputMethodList();
            for (InputMethodInfo info : list) {
                if (!getPackageName().equals(info.getPackageName())) continue;
                for (int i = 0; i < info.getSubtypeCount(); i++) {
                    InputMethodSubtype st = info.getSubtypeAt(i);
                    String locale = st.getLocale();
                    boolean isFa = locale != null && locale.toLowerCase().startsWith("fa");
                    if (isFa == persian) {
                        imm.setInputMethodAndSubtype(getToken(), info.getId(), st);
                        return;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void onStartInputView(android.view.inputmethod.EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        // InputMethodService does not provide getInputView(). Keep the reference
        // returned by onCreateInputView() instead.
        if (keyboardView != null) {
            ViewGroup.LayoutParams lp = keyboardView.getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, getKeyboardHeight());
            } else {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = getKeyboardHeight();
            }
            keyboardView.setLayoutParams(lp);
        }
    }
}
