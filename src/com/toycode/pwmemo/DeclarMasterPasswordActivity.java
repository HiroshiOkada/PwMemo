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

import android.content.Intent;

/**
 * The activity called when master password did not exist. 
 *
 * @author Hiroshi Okada
 */
public class DeclarMasterPasswordActivity extends
        AbstractMasterPasswordActivity {

    protected int getLayoutResID() {
        return R.layout.declar_master_password;
    }

    @Override
    protected void onOkBtnClick() {
        if (inputVaildateion()) {
            PasswordManager pm = PasswordManager.getInstance(this);
            pm.createMainPassword(mMasterPasswordEditText.getText().toString());
            TimeOutChecker.getInstance().onUser();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
