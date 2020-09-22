package com.example.tinker_demo

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.tencent.bugly.beta.Beta
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.plugins.bugly.upgrade.FlutterUpgrade

class MainActivity : FlutterActivity() {


    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        FlutterUpgrade.registerWith(flutterEngine);
    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        Beta.installTinker()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 必须给存储权限啊，要不然tinker的补丁无法写入本地文件夹
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001);

    }

    override fun onDestroy() {
        super.onDestroy()
        Beta.unInit()
    }

}
