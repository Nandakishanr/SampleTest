/**
 * Copyright 2013 Maarten Pennings
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 */

package com.manvish.sampletest.CommonViews;

import android.annotation.TargetApi;
import android.app.Activity;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.manvish.sampletest.Application.AadharTimeAttendanceApplication;




/**
 * When an activity hosts a keyboardView, this class allows several EditText's
 * to register for it.
 *
 * @author Maarten Pennings
 * @date 2012 December 23
 */


public class CustomKeyboard extends InputMethodService{

    /** A link to the KeyboardView that is used to render this CustomKeyboard. */
    private KeyboardView mKeyboardView;
    /** A link to the activity that hosts the {@link #mKeyboardView}. */
    private Activity mHostActivity;

    private Keyboard mKeyBoard;
    private View mInflater;
    int touchPosition;

    int id;

    /** The key (code) handler. */
    private OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {

        public final static int CodeDelete = -5; // Keyboard.KEYCODE_DELETE
        public final static int CodeCancel = -3; // Keyboard.KEYCODE_CANCEL
        public final static int CodePrev = 55000;
        public final static int CodeAllLeft = 55001;
        public final static int CodeLeft = 55002;
        public final static int CodeRight = 55003;
        public final static int CodeAllRight = 55004;
        public final static int CodeNext = 55005;
        public final static int CodeClear = 55006;

        public final static int CodeDone = 55007;
        public final static int CodeCaps = -1;
        public final static int CodeBackSpace = -4;
        private boolean caps = false;

        @Override
        public void onKey(final int primaryCode, int[] keyCodes) {
            // NOTE We can say '<Key android:codes="49,50" ... >' in the xml
            // file; all codes come in keyCodes, the first in this list in
            // primaryCode
            // Get the EditText and its Editable

            AadharTimeAttendanceApplication.playClick();

            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
            if (focusCurrent == null
                    || focusCurrent.getClass() != EditText.class)
                return;
            final EditText edittext = (EditText) focusCurrent;

            edittext.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String searchString = s.toString();
                    int textLength = searchString.length();
//                    edittext.setSelection(textLength);
                }


                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void afterTextChanged(Editable s) {

                }

            });


            // System.out.println("print edit text id==="+edittext.getId());
            // if(edittext.getId()==R.id.ed_hack){
            //
            // // invisible the hack============fix it later
            // System.out.println("id matched");
            // //edittext.setVisibility(View.INVISIBLE);
            //
            // // make the layout visible==========
            //
            // mHostActivity.findViewById(R.id.ll_enter_id).setVisibility(View.VISIBLE);
            //
            // // Show the input field and focus on keypad
            // touched================
            // ((EditText)mHostActivity.findViewById(R.id.et_enter_id_landing)).requestFocus();
            //
            //
            // }

            final Editable editables = edittext.getText();
            final int start = edittext.getSelectionStart();
            final int end = edittext.getSelectionEnd();



            // Apply the key to the edittext
            if (primaryCode == CodeCancel) {
                hideCustomKeyboard();
            } else if (primaryCode == CodeDelete) {
                if (editables != null && start > 0)
                {
                    editables.delete(start - 1, start);
                    edittext.setSelection(start - 1);
                }
            }

            /*
			 * else if( primaryCode==CodeBackSpace ) { if( editable!=null && end
			 * >0 ) editable.delete(end - 1, end); }
			 */
            else if (primaryCode == CodeClear) {
                if (editables != null)
                    editables.clear();
            } else if (primaryCode == CodeLeft) {
                if (start > 0)
                    edittext.setSelection(start - 1);
            } else if (primaryCode == CodeRight) {
                if (start < edittext.length())
                    edittext.setSelection(start + 1);
            } else if (primaryCode == CodeAllLeft) {
                edittext.setSelection(0);
            } else if (primaryCode == CodeAllRight) {
                edittext.setSelection(edittext.length());
            } else if (primaryCode == CodePrev) {
                View focusNew = edittext.focusSearch(View.FOCUS_BACKWARD);
                if (focusNew != null)
                    focusNew.requestFocus();
            } else if (primaryCode == CodeNext) {
                View focusNew = edittext.focusSearch(View.FOCUS_FORWARD);
                if (focusNew != null)
                    focusNew.requestFocus();
            } else if (primaryCode == CodeDone) {
                hideCustomKeyboard();
            }
            // for capslock

            else if (primaryCode == CodeCaps) {
                caps = !caps;
                mKeyBoard.setShifted(caps);
                mKeyboardView.invalidateAllKeys();

                // edittext.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

                // edittext.setFilters(new InputFilter[] {new
                // InputFilter.AllCaps()});

            } else { // insert character

                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }

                editables.insert(start, Character.toString((char) code));
            }

        }


        @Override
        public void onPress(int arg0) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeUp() {
        }

    };

    /**
     * Create a custom keyboard, that uses the KeyboardView (with resource id
     * <var>viewid</var>) of the <var>host</var> activity, and load the keyboard
     * layout from xml file <var>layoutid</var> (see {@link Keyboard} for
     * description). Note that the <var>host</var> activity must have a
     * <var>KeyboardView</var> in its layout (typically aligned with the bottom
     * of the activity). Note that the keyboard layout xml file may include key
     * codes for navigation; see the constants in this class for their values.
     * Note that to enable EditText's to use this custom keyboard, call the
     * {@link #registerEditText(int)}.
     *
     * @param host
     *            The hosting activity.
     * @param viewid
     *            The id of the KeyboardView.
     * @param layoutid
     *            The id of the xml file containing the keyboard layout.
     */
    public CustomKeyboard(Activity host, int viewid, int layoutid) {
//        super(host);
        mHostActivity = host;
        mKeyboardView = (KeyboardView) mHostActivity.findViewById(viewid);

        mKeyBoard = new Keyboard(mHostActivity, layoutid);
        mKeyboardView.setKeyboard(mKeyBoard);
        mKeyboardView.setPreviewEnabled(false); // NOTE Do not show the preview
        // balloons
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        // Hide the standard keyboard initially
        mHostActivity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

//    @Override
//    protected void onSelectionChanged(int selStart, int selEnd) {
//        super.onSelectionChanged(selStart, selEnd);
//
//        Log.d("Gajanand", "onSelectionChanged: yes changed");
//    }

    public CustomKeyboard(Activity host, View inflater, int viewid, int layoutid) {
//        super(host);
        mHostActivity = host;
        mInflater = inflater;
        mKeyboardView = (KeyboardView) mInflater.findViewById(viewid);

        mKeyBoard = new Keyboard(mHostActivity, layoutid);
        mKeyboardView.setKeyboard(mKeyBoard);
        mKeyboardView.setPreviewEnabled(false); // NOTE Do not show the preview
        // balloons
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        // Hide the standard keyboard initially
        mHostActivity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /** Returns whether the CustomKeyboard is visible. */
    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /**
     * Make the CustomKeyboard visible, and hide the system keyboard for view v.
     */
    public void showCustomKeyboard(View v) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if (v != null)
            ((InputMethodManager) mHostActivity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /** Make the CustomKeyboard invisible. */
    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    /**
     * Register <var>EditText<var> with resource id <var>resid</var> (on the
     * hosting activity) for using this custom keyboard.
     *
     * @param resid
     *            The resource id of the EditText that registers to the custom
     *            keyboard.
     */
    public void registerEditText(int resid) {
        // Find the EditText 'resid'
        EditText edittext;

        edittext = (EditText) mHostActivity.findViewById(resid);
        if (edittext == null) {
            edittext = (EditText) mInflater.findViewById(resid);
        }

        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom
            // keyboard when the edit box gets focus, but also hide it when the
            // edit box loses focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus)
                    showCustomKeyboard(v);
                else
                    hideCustomKeyboard();
            }
        });
        edittext.setOnClickListener(new View.OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom
            // keyboard again, by tapping on an edit box that already had focus
            // (but that had the keyboard hidden).
            @Override
            public void onClick(View v) {
                AadharTimeAttendanceApplication.playClick();
                showCustomKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        // NOTE There is also an easy way:
        // 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a
        // cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
        edittext.setOnTouchListener(new View.OnTouchListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;

                float x = event.getX();
                float y = event.getY();
                touchPosition = edittext.getOffsetForPosition(x, y);
                if (touchPosition > 0) {
                    edittext.setSelection(touchPosition);


                }

                int inType = edittext.getInputType(); // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard
                // keyboard
                edittext.onTouchEvent(event); // Call native handler
                edittext.setInputType(inType); // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType()
                | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    public void registerAutoCompleteEditText(int resid) {
        // Find the EditText 'resid'
        System.out.println("hello ");
        AutoCompleteTextView autoCompleteTextView;

        autoCompleteTextView = (AutoCompleteTextView) mHostActivity.findViewById(resid);
        if (autoCompleteTextView == null) {
            autoCompleteTextView = (AutoCompleteTextView) mInflater.findViewById(resid);
        }

        System.out.println("hello 1");
        // Make the custom keyboard appear
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom
            // keyboard when the edit box gets focus, but also hide it when the
            // edit box loses focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                System.out.println("hello 2");
                if (hasFocus)
                    showCustomKeyboard(v);
                else
                    hideCustomKeyboard();
            }
        });
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom
            // keyboard again, by tapping on an edit box that already had focus
            // (but that had the keyboard hidden).
            @Override
            public void onClick(View v) {
                AadharTimeAttendanceApplication.playClick();
                showCustomKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        // NOTE There is also an easy way:
        // 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a
        // cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AutoCompleteTextView edittext = (AutoCompleteTextView) v;

                float x = event.getX();
                float y = event.getY();
                touchPosition = edittext.getOffsetForPosition(x, y);
                if (touchPosition > 0) {
                    edittext.setSelection(touchPosition);
                }

                int inType = edittext.getInputType(); // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard
                // keyboard
                edittext.onTouchEvent(event); // Call native handler
                edittext.setInputType(inType); // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        System.out.println("hello 5");
        autoCompleteTextView.setInputType(autoCompleteTextView.getInputType()
                | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }
}

// NOTE How can we change the background color of some keys (like the
// shift/ctrl/alt)?
// NOTE What does android:keyEdgeFlags do/mean
