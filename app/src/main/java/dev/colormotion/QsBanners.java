package dev.colormotion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/** Removes Audio Eraser before Samsung derives banner visibility and layout height. */
final class QsBanners {
    private static final String UI = "com.android.systemui.samsung.quicksetting.ui.banner.";
    private final ClassLoader cl;

    QsBanners(ClassLoader cl) { this.cl = cl; }

    void install() {
        Class<?> audioEraser = XposedHelpers.findClass(UI + "AudioEraser", cl);
        Class<?> transform = XposedHelpers.findClass(UI + "TopBannerViewModel$1$1", cl);
        XC_MethodHook.Unhook hook = XposedHelpers.findAndHookMethod(transform, "invokeSuspend", Object.class, new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam param) {
                if (param.hasThrowable() || !(param.getResult() instanceof List<?>)) return;
                List<?> original = (List<?>) param.getResult();
                int first = -1;
                for (int i = 0; i < original.size(); i++) if (audioEraser.isInstance(original.get(i))) {
                    first = i;
                    break;
                }
                if (first < 0) return;
                ArrayList<Object> filtered = new ArrayList<>(original.size() - 1);
                for (Object banner : original) if (!audioEraser.isInstance(banner)) filtered.add(banner);
                // The native collector derives isEnabled from this list. Empty means no banner
                // and zero banner height, so content reflows without a blank/clickable slot.
                param.setResult(filtered.isEmpty() ? Collections.emptyList() : filtered);
            }
        });
        if (hook == null) throw new IllegalStateException("Audio Eraser banner hook rejected");
        XposedBridge.log(Entry.TAG + ": QS Audio Eraser banner filter installed");
    }
}
