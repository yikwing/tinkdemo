package io.flutter.plugins.bugly.upgrade;


import androidx.annotation.NonNull;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.Map;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class FlutterUpgrade implements MethodChannel.MethodCallHandler {
    private static String FlutterMethodPluginName = "Flutter-Upgrade-MethodChannel";
    private static MethodChannel methodChannel;

    static public void registerWith(@NonNull FlutterEngine flutterEngine) {
        methodChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), FlutterMethodPluginName);
        methodChannel.setMethodCallHandler(new FlutterUpgrade());
    }


    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "postException":
                String type = "";
                String error = "";
                String stackTrace = "";
                Map<String, String> extraInfo = null;
                if (call.hasArgument("type")) {
                    type = call.argument("type");
                }
                if (call.hasArgument("error")) {
                    error = call.argument("error");
                }
                if (call.hasArgument("stackTrace")) {
                    stackTrace = call.argument("stackTrace");
                }
                if (call.hasArgument("extraInfo")) {
                    extraInfo = call.argument("extraInfo");
                }
                int category = 8;
                CrashReport.postException(category, type, error, stackTrace, extraInfo);
                break;
            default:
                result.notImplemented();
                break;
        }
    }
}
