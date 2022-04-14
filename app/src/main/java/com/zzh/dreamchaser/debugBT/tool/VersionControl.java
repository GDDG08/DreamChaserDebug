package com.zzh.dreamchaser.debugBT.tool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static com.zzh.dreamchaser.debugBT.tool.myLog.logD;

import com.google.gson.Gson;
import com.zzh.dreamchaser.debugBT.MainActivity;
import com.zzh.dreamchaser.debugBT.data.jsonbean.CloudVerBean;
import com.zzh.dreamchaser.debugBT.data.jsonbean.StdAPI;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class VersionControl {
    private MainActivity mainActivity;
    private Context mContext;
    private int versionCode;
    private String versionName;

    private CloudVerBean cvb;

    public VersionControl(Context mContext) {
        this.mContext = mContext;
        initInfo();
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void initInfo() {
        PackageManager manager = mContext.getPackageManager();//获取包管理器
        try {
            //通过当前的包名获取包的信息
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);//获取包对象信息
            versionCode = info.versionCode;
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        getSeverVer();
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void getSeverVer() {
        RequestParams params = new RequestParams("http://bitrm-app.tk");
        x.http().get(params, new Callback.CommonCallback<StdAPI>() {
            @Override
            public void onSuccess(StdAPI result) {
                logD("HTTP:" + "success---->" + result.getData());
                Gson gson = new Gson();
                cvb = gson.fromJson(result.getData(), CloudVerBean.class);
                check();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                logD("HTTP:" + "error---->" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                logD("HTTP:" + "cancelled");
            }

            @Override
            public void onFinished() {
                logD("HTTP:" + "finished");
            }
        });
    }

    public void check() {
        logD("UPDATE--->" + "verCode:" + cvb.getVerCode());

        if (cvb.getVerCode() <= getVersionCode()) {
            Toast.makeText(mainActivity, "已是最新版本", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mainActivity, "检测到新版本", Toast.LENGTH_SHORT).show();

            String force_text = "暂不更新";
            DialogInterface.OnClickListener force_click = null;
            boolean force_cancelable = true;
            if (cvb.getForceUpdate()) {
                force_text = "退出";
                force_click = (dialog, which) -> mainActivity.finish();
                force_cancelable = false;
            }
            new AlertDialog.Builder(mainActivity)
                    .setTitle("检测到新版本")
                    .setMessage(cvb.getVerName() + "\n\n●更新日志：\n\n" + cvb.getChagelog() + "\n\n●点击立即更新↘")
                    .setNegativeButton(force_text, force_click)
                    .setCancelable(force_cancelable)
                    .setPositiveButton("立即更新", (dialog, which) -> {
//                        if (cvb.getType() == 0) {
                            userUpdate();
                            if (cvb.getForceUpdate())
                                mainActivity.finish();
//                        }else{
//                            final String filePath = mContext.getCacheDir().getPath() + "/DreamChaser.apk";
//
//                            File apk = new File(filePath);
//                            if (apk.exists()) {
//                                apk.delete();
//                            }
//
//
//                            RequestParams params = new RequestParams(cvb.getDirectlink());
//                            params.setAutoRename(true);//断点下载
//                            params.setSaveFilePath(filePath);
//                            x.http().get(params, new Callback.ProgressCallback<File>() {
//                                private ProgressDialog progressDialog;
//
//                                @Override
//                                public void onCancelled(CancelledException arg0) {
//                                }
//
//                                @Override
//                                public void onError(Throwable arg0, boolean arg1) {
//                                    if (progressDialog != null && progressDialog.isShowing()) {
//                                        progressDialog.dismiss();
//                                    }
//                                    Toast.makeText(mainActivity, "更新失败,请手动更新！", Toast.LENGTH_SHORT).show();
//                                    userUpdate();
//                                }
//
//                                @Override
//                                public void onFinished() {
//                                }
//
//                                @Override
//                                public void onSuccess(File arg0) {
//                                    if (progressDialog != null && progressDialog.isShowing()) {
//                                        progressDialog.dismiss();
//                                    }
//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
//                                    mainActivity.startActivity(intent);
//                                }
//
//                                @Override
//                                public void onLoading(long arg0, long arg1, boolean arg2) {
//                                    progressDialog.setMax((int) arg0);
//                                    progressDialog.setProgress((int) arg1);
//                                }
//
//                                @Override
//                                public void onStarted() {
//                                    progressDialog = new ProgressDialog(mainActivity);
//                                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置为水进行条
//                                    progressDialog.setMessage("拼命下载中...");
//                                    progressDialog.setProgress(0);
//                                    progressDialog.setCancelable(false);
//                                    progressDialog.show();
//                                }
//
//                                @Override
//                                public void onWaiting() {
//                                }
//                            });
//                        }
                    })
                    .show();
        }
    }

    private void userUpdate(){
        new AlertDialog.Builder(mainActivity)
                .setTitle("请选择下载方式")
                .setMessage("校外访问需要您先登陆webvpn后\n再次返回本软件点击更新")
                .setCancelable(true)
                .setPositiveButton("校园网访问",(view,which2)->{
                    mainActivity.web(cvb.getBitlink());
                })
                .setNegativeButton("校外访问",(view,which2)->{
                    mainActivity.web(cvb.getWeblink());
                }).show();
//        mainActivity.web(cvb.getDirectlink());
    }
}
