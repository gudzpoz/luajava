package com.mylua.project;

import org.robovm.apple.foundation.NSAutoreleasePool;  
import org.robovm.apple.foundation.NSDictionary;  
import org.robovm.apple.foundation.NSString;  
import org.robovm.apple.uikit.UIApplication;  
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import com.mylua.project.MyLuaProject;

public class IOSLauncher extends UIApplicationDelegateAdapter {
    @Override
    public boolean didFinishLaunching(UIApplication application, NSDictionary<NSString, ?> launchOptions) {
        new MyLuaProject();
        return true;
    }

    public static void main(String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, IOSLauncher.class);
        pool.close();
    }
}