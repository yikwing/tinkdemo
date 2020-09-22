package com.example.tinker_demo;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerLoadResult;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.SharePatchFileUtil;

import java.io.File;
import java.lang.reflect.Field;

import io.flutter.embedding.engine.loader.FlutterLoader;

public class FlutterPatch {

    private static final String TAG = "FlutterPatch";

    private FlutterPatch() {
    }

    public static void flutterPatchInit() {
        try {

            FlutterLoader flutterLoader = FlutterLoader.getInstance();
            String libPath = findLibraryFromTinker(ApplicationProvider.context, "lib/arm64-v8a", "libapp.so");
            Field field = FlutterLoader.class.getDeclaredField("aotSharedLibraryName");
            field.setAccessible(true);
            field.set(flutterLoader, libPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAbi() {
        if (Build.VERSION.SDK_INT >= 21) {
            for (String cpu : Build.SUPPORTED_ABIS) {
                if (!TextUtils.isEmpty(cpu)) {
                    TinkerLog.i(TAG, "cpu abi is:" + cpu);
                    return cpu;
                }
            }
        } else {
            TinkerLog.i(TAG, "cpu abi is:" + Build.CPU_ABI);
            return Build.CPU_ABI;
        }

        return "";
    }

    public static String findLibraryFromTinker(Context context, String relativePath, String libName) throws UnsatisfiedLinkError {
        final Tinker tinker = Tinker.with(context);

        libName = libName.startsWith("lib") ? libName : "lib" + libName;
        libName = libName.endsWith(".so") ? libName : libName + ".so";
        String relativeLibPath = relativePath + "/" + libName;

        TinkerLog.i(TAG, "flutterPatchInit() called   " + tinker.isTinkerLoaded() + " " + tinker.isEnabledForNativeLib());

        if (tinker.isEnabledForNativeLib() && tinker.isTinkerLoaded()) {
            TinkerLoadResult loadResult = tinker.getTinkerLoadResultIfPresent();
            if (loadResult.libs == null) {
                return libName;
            }
            for (String name : loadResult.libs.keySet()) {
                if (!name.equals(relativeLibPath)) {
                    continue;
                }
                String patchLibraryPath = loadResult.libraryDirectory + "/" + name;
                File library = new File(patchLibraryPath);
                if (!library.exists()) {
                    continue;
                }

                boolean verifyMd5 = tinker.isTinkerLoadVerify();
                if (verifyMd5 && !SharePatchFileUtil.verifyFileMd5(library, loadResult.libs.get(name))) {
                    tinker.getLoadReporter().onLoadFileMd5Mismatch(library, ShareConstants.TYPE_LIBRARY);
                } else {
                    TinkerLog.i(TAG, "findLibraryFromTinker success:" + patchLibraryPath);
                    return patchLibraryPath;
                }
            }
        }

        return libName;
    }
}