package wifi.chenchi.cc.wifispoter;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;
import com.umeng.commonsdk.UMConfigure;

import java.util.List;

public class MainActivity extends Activity implements ServiceConnection {
    private final String TAG = "DBG";
    boolean close = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UMConfigure.init(this.getApplicationContext(), "5b57e926b27b0a060e000149", "release", UMConfigure.DEVICE_TYPE_PHONE, null);
        openAccessibility("wifi.chenchi.cc.wifispoter.SpAccessibilityService", this);
    }

    protected void onResume(){
        super.onResume();
        if (close){
            finish();
        }
    }

    private boolean checkAccessibilityEnabled(String serviceName) {
        AccessibilityManager mAccessibilityManager = (AccessibilityManager) getApplication().getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices =
                mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 该辅助功能开关是否打开了
     * @param accessibilityServiceName：指定辅助服务名字
     * @param context：上下文
     * @return
     */
    private boolean isAccessibilitySettingsOn(String accessibilityServiceName, Context context) {
        int accessibilityEnable = 0;
        String serviceName = context.getPackageName() + "/" +accessibilityServiceName;
        try {
            accessibilityEnable = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        } catch (Exception e) {
            Log.e(TAG, "get accessibility enable failed, the err:" + e.getMessage());
        }
        if (accessibilityEnable == 1) {
            TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    Log.d(TAG, "SERVICE name " + accessibilityService);
                    if (accessibilityService.equalsIgnoreCase(serviceName)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        }else {
            Log.d(TAG,"Accessibility service disable");
        }
        return false;
    }

    /**
     * 跳转到系统设置页面开启辅助功能
     * @param accessibilityServiceName：指定辅助服务名字
     * @param context：上下文
     */
    private void openAccessibility(String accessibilityServiceName, Context context){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);

        if (isAccessibilitySettingsOn(accessibilityServiceName,context)) {
            Intent intentService = new Intent(this, SpAccessibilityService.class);
            intentService.putExtra("TYPE", 0);
            startService(intentService);
        }else{
            Toast.makeText(this, R.string.accessibility_need, Toast.LENGTH_LONG).show();
        }
        close = true;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
