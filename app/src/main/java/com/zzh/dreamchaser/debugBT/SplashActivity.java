package com.zzh.dreamchaser.debugBT;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.smarx.notchlib.NotchScreenManager;
import com.zzh.dreamchaser.debugBT.databinding.ActivitySplashBinding;

public class SplashActivity extends Activity
{
    int SPLASH_TIME=800;
    boolean firsttime=true;
    public static boolean sp=true;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        com.zzh.dreamchaser.debugBT.databinding.ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NotchScreenManager.getInstance().setDisplayInNotch(this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            Window.setDecorFitsSystemWindows(false);
//        } else {
//            binding.rootView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        }
		//new MainActivity().finish();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run()
//            {
//                overridePendingTransition(0, android.R.anim.fade_in);
//                Intent intent=new Intent(SplashActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent);
//                finish();
//            }
//        }, SPLASH_TIME);


        ImageView imageView2 = binding.imageView;
        RelativeLayout splashLayout = binding.splashLayout;

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1f);

//        动画的透明时间以毫秒为单位  5000ms
        alphaAnimation.setDuration(1300);

        // 动画关联到_image_logo ImageView组件上
        imageView2.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                overridePendingTransition(0, android.R.anim.fade_in);
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
