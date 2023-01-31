package com.apk.editor.utils.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.apk.editor.R;
import com.apk.editor.utils.APKData;
import com.apk.editor.utils.APKEditorUtils;

import java.io.File;

import in.sunilpaulmathew.sCommon.Utils.sExecutor;
import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by APK Explorer & Editor <apkeditor@protonmail.com> on January 28, 2023
 */
public class SaveBundletoDownloads extends sExecutor {

    private final boolean mExportOnly;
    private final Context mContext;
    private File mFile;
    private ProgressDialog mProgressDialog;
    private final String mPath;

    public SaveBundletoDownloads(String path, boolean exportOnly, Context context) {
        mPath = path;
        mExportOnly = exportOnly;
        mContext = context;
    }

    @Override
    public void onPreExecute() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage(mContext.getString(mExportOnly ? R.string.saving : R.string.preparing_bundle));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIcon(R.mipmap.ic_launcher);
        mProgressDialog.setTitle(R.string.app_name);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mFile = new File(mContext.getExternalFilesDir("APK"), new File(mPath).getName() + ".xapk");
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void doInBackground() {
        if (mFile.exists()) {
            sUtils.delete(mFile);
        }
        APKEditorUtils.zip(new File(mPath), mFile);
        if (mExportOnly) {
            APKData.saveToDownload(mFile, mFile.getName(), mContext);
        }
    }

    @Override
    public void onPostExecute() {
        try {
            mProgressDialog.dismiss();
        } catch (IllegalArgumentException ignored) {
        }
        if (!mExportOnly) {
            APKData.shareFile(mFile, "application/zip", mContext);
        }
    }

}