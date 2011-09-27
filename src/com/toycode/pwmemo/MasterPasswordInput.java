/*
 * Copyright (c) 2011 Hiroshi Okada (http://toycode.com/hiroshi/)
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *   1.The origin of this software must not be misrepresented; you must
 *     not claim that you wrote the original software. If you use this
 *     software in a product, an acknowledgment in the product
 *     documentation would be appreciated but is not required.
 *   
 *   2.Altered source versions must be plainly marked as such, and must
 *     not be misrepresented as being the original software.
 *   
 *   3.This notice may not be removed or altered from any source
 *     distribution.
 */

package com.toycode.pwmemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Show Dailog and ask Master password.
 * 
 * Uses: (inside Activity)
 * MasterPasswordInput mpi = new MasterPasswordInput (this) {
 *   public void onTureMasterPassword() 
 *   { 
 *     // Password is OK 
 *   } 
 * };
 * mpi.Ask()
 * 
 * @author hiroshi
 */
public abstract class MasterPasswordInput {

    Activity mActivity;

    public MasterPasswordInput(Activity activity) {
        mActivity = activity;
    }

    /**
     * Show Dailog and ask Master password.
     */
    public void Ask() {
        final EditText editText = new EditText(mActivity);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                .setTitle(mActivity.getString(R.string.input_masterpassword))
                .setView(editText).setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                onPassword(editText.getText().toString());
                            }
                        }).setNegativeButton(android.R.string.cancel, null)
                .create();

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog
                            .getWindow()
                            .setSoftInputMode(
                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        alertDialog.show();
    }

    /**
     * When user input password.
     */
    private void onPassword(String password) {

        PasswordManager passwordManager = PasswordManager.getInstance(mActivity);
        if (passwordManager.isMainPasswordExist()) {
            if (passwordManager.decryptMainPassword(password) == null) {
                Toast.makeText(mActivity, "Password not match", Toast.LENGTH_LONG).show();
                return;
            } else {
                onTureMasterPassword();
            }

        } else {
            passwordManager.createMainPassword(password);
            Toast.makeText(mActivity, "Set master password", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Override it.
     * 
     * @return
     */
    abstract void onTureMasterPassword();
}
