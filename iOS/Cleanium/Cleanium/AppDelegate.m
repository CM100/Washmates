//
//  AppDelegate.m
//  Cleanium
//
//  Created by  DN-117 on 7/14/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "AppDelegate.h"
#import <Parse/Parse.h>
#import <Stripe/Stripe.h>

@interface AppDelegate ()

@end

@implementation AppDelegate

@synthesize stripeKey, postalCodes, userAddresses, driverInterval, orderStatuses, userOrders, deviceTokenData;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    [Parse enableLocalDatastore];
    
    // Initialize Parse.
    [Parse setApplicationId:@"cGiRSDM8unMmInG0iDO4DG3oesrWm00QGNcU6eUN"
                  clientKey:@"u8U82JgEj8CNehEA9xBL2exXTfMjsY4l348RAdz8"];
    
//    [PFConfig getConfigInBackgroundWithBlock:^(PFConfig *config, NSError *error) {
//        if (!error) {
//            NSLog(@"Yay! Config was fetched from the server.");
//        } else {
//            NSLog(@"Failed to fetch. Using Cached Config.");
//            config = [PFConfig currentConfig];
//        }
//        
//        stripeKey = config[@"stripeApiKey"];
//        [Stripe setDefaultPublishableKey:stripeKey];
//        NSLog(@"stripe key %@", stripeKey);
//        
//        postalCodes = config[@"postCodes"];
//        
//    }];
    
    // [Optional] Track statistics around application opens.
    [PFAnalytics trackAppOpenedWithLaunchOptions:launchOptions];
    
    if ([application respondsToSelector:@selector(registerUserNotificationSettings:)]) {
        UIUserNotificationType userNotificationTypes = (UIUserNotificationTypeAlert |
                                                        UIUserNotificationTypeBadge |
                                                        UIUserNotificationTypeSound);
        UIUserNotificationSettings *settings = [UIUserNotificationSettings settingsForTypes:userNotificationTypes
                                                                                 categories:nil];
        [application registerUserNotificationSettings:settings];
        [application registerForRemoteNotifications];
    }else{
        UIRemoteNotificationType myTypes = UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeSound;
        [application registerForRemoteNotificationTypes:myTypes];
    }
    
    return YES;
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    deviceTokenData = deviceToken;
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    NSLog(@"Received notification: %@", userInfo);
    
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
    
    if ( application.applicationState == UIApplicationStateActive ){
        [[NSNotificationCenter defaultCenter] postNotificationName:@"PushNotification" object:self userInfo:userInfo];
    }else{
        [[NSNotificationCenter defaultCenter] postNotificationName:@"PushNotificationBackground" object:self userInfo:userInfo];
    }
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    [UIView setAnimationsEnabled:NO];
    UINavigationController *navigationController = (UINavigationController *)self.window.rootViewController;
    [navigationController.view endEditing:YES];
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    [UIView setAnimationsEnabled:YES];
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
