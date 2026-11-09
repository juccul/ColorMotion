package dev.colormotion;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class Entry implements IXposedHookLoadPackage {
    static final String TAG = "ColorMotion";

    private static boolean installed;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        // handleLoadPackage also fires for other APKs whose resources SystemUI loads in-process.
        if (!"com.android.systemui".equals(lpparam.packageName) || !lpparam.isFirstApplication || installed) {
            return;
        }
        installed = true;
        try {
            new QsBanners(lpparam.classLoader).install();
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": QS banner filter failed");
            XposedBridge.log(t);
        }
        try {
            new ShadeMotion(lpparam.classLoader).install();
            XposedBridge.log(TAG + ": hooks installed");
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": install failed");
            XposedBridge.log(t);
        }
    }
}
