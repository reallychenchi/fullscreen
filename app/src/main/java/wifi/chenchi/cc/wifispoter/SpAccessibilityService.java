package wifi.chenchi.cc.wifispoter;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SpAccessibilityService extends AccessibilityService {

    @Override
    public int onStartCommand(Intent intent, int flags, int intentId){
        doOnPage();
        return super.onStartCommand(intent, flags, intentId);
    }

    @Override
    public void onServiceConnected(){
        super.onServiceConnected();
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        doOnPage();
    }

    private void doOnPage(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nodess = getWidgetByClass(rootNode, "android.support.v7.widget.RecyclerView");
        if (nodess.isEmpty()){
            rootNode = null;
        }else{
            rootNode = nodess.get(0);
        }
        if (rootNode != null) {
            rootNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText("强制全屏");
            for(int i = 0; i < 10 && nodes.size() == 0; ++i){
                rootNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                nodes = rootNode.findAccessibilityNodeInfosByText("强制全屏");
            }
            if (nodes.size() != 1){
                //ERROR
            }
            for (AccessibilityNodeInfo node : nodes) {
                AccessibilityNodeInfo vg = node.getParent();
                List<AccessibilityNodeInfo> nodesSwitch = getWidgetByClass(vg, "android.widget.Switch");
                if (nodesSwitch.size() != 1){
                    //ERROR
                }
                AccessibilityNodeInfo switchNode = nodesSwitch.get(0);
                if ("关闭".equals(switchNode.getText().toString())) {
                    nodesSwitch.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Toast.makeText(this, R.string.accessibility_success, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, R.string.accessibility_success, Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            //ERROR
        }

        performGlobalAction(GLOBAL_ACTION_BACK);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(GLOBAL_ACTION_BACK);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    private List<AccessibilityNodeInfo> getWidgetByClass(AccessibilityNodeInfo info, String clsName){
        List<AccessibilityNodeInfo> ret = new ArrayList<>();
        if (null == info) return ret;
        if (clsName.equals(info.getClassName().toString())){
            ret.add(info);
            return ret;
        }
        for(int i = 0; i < info.getChildCount(); ++i){
            List<AccessibilityNodeInfo> subRet = getWidgetByClass(info.getChild(i), clsName);
            if (!subRet.isEmpty()) ret.addAll(subRet);
        }

        return ret;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }
}
