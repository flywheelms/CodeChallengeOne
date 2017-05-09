package com.flywheelms.codechallengeone;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Copyright SiliconValleyOffice GitHub owner.  All rights reserved.
 */

public class GuiHelper {

    public static void hideSoftKeyboard(Context pContext) {
        InputMethodManager inputMethodManager = (InputMethodManager) pContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = ((Activity) pContext).getCurrentFocus();
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            // inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }
}
