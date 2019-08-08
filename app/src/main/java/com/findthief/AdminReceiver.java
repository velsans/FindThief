package com.findthief;

/**
 * Created by DEV 27 on 07/09/2016.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import java.util.regex.Pattern;

public class AdminReceiver extends DeviceAdminReceiver {
    boolean IsFailed = false;
    static String possibleEmail = "";
    GMailSender sender;

    public void onEnabled(Context ctxt, Intent intent) {
        ComponentName cn = new ComponentName(ctxt, AdminReceiver.class);
        DevicePolicyManager mgr =
                (DevicePolicyManager) ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mgr.setPasswordQuality(cn,
                DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
        onPasswordChanged(ctxt, intent);
    }

    public void onPasswordChanged(Context ctxt, Intent intent) {
        DevicePolicyManager mgr =
                (DevicePolicyManager) ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
        int msgId;

        if (mgr.isActivePasswordSufficient()) {
            msgId = R.string.compliant;
        } else {
            msgId = R.string.not_compliant;
        }

        Toast.makeText(ctxt, msgId, Toast.LENGTH_LONG).show();
    }

    public void onPasswordFailed(Context ctxt, Intent intent) {
        //Toast.makeText(ctxt, R.string.password_failed, Toast.LENGTH_LONG).show();
        LockUnlockActivity.attempts = LockUnlockActivity.attempts + 1;
        Log.e("attempts1", ">>>>>>" + LockUnlockActivity.attempts);
        if (LockUnlockActivity.attempts >= 2) {
            Log.e("attempts2", ">>>>>" + LockUnlockActivity.attempts);
            AccountManager accountManager = AccountManager.get(ctxt);
            Account account = getAccount(accountManager);
            if (account == null) {
                LockUnlockActivity.attempts = 0;
            } else {
                possibleEmail = account.name;
                Log.e("attempts3", ">>>>>>" + possibleEmail);
                IsFailed = true;
                return;
            }
        }

        if (IsFailed) {
            try {
                Intent i = new Intent(ctxt, CameraView.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctxt.startActivity(i);
            } catch (Exception ex) {
            }

        }

    }


    public void onPasswordSucceeded(Context ctxt, Intent intent) {
        LockUnlockActivity.attempts = 0;
        Toast.makeText(ctxt, R.string.password_success, Toast.LENGTH_LONG)
                .show();
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }

}