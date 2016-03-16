//
//  GetLoadData.m
//  Cleanium
//
//  Created by  DN-117 on 7/30/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "GetLoadData.h"
#import "ServiceClient.h"
#import "AppDelegate.h"
#import "MBProgressHUD.h"
#import <Parse/Parse.h>

@implementation GetLoadData

+(void)getLoadData:(UIViewController*)viewController isFinished:(void (^)(NSNumber *))isFinished{
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:viewController.view animated:YES];
    hud.labelText = @"Loading";
    
    [self subscribeToChannel];
    
    ServiceClient *serviceClient = [ServiceClient sharedServiceClient];
    [serviceClient fetchAddressesWithCompletionBlock:^(id objects) {
        [self getOrders:serviceClient withHud:hud isFinished:^(NSNumber* isFinishedLocal){
            isFinished((NSNumber*) isFinished);
        }];
        
        AppDelegate *appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
        appDelegate.userAddresses = objects;
    } failureBlock:^(NSError *error) {
        [self getOrders:serviceClient withHud:hud isFinished:^(NSNumber* isFinishedLocal){
            isFinished((NSNumber*) isFinished);
        }];
    }];
}

+(void)getOrders:(ServiceClient*)serviceClient withHud:(MBProgressHUD*)hud isFinished:(void (^)(NSNumber *))isFinished{
    [serviceClient fetchOrdersWithCompletionBlock:^(id objects) {
        [hud hide:YES];
        
        AppDelegate *appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
        appDelegate.userOrders = objects;
        
        isFinished((NSNumber*) [NSNumber numberWithBool:YES]);
    } failureBlock:^(NSError *error) {
        [hud hide:YES];
        isFinished((NSNumber*) [NSNumber numberWithBool:YES]);
    }];
}

+(void)subscribeToChannel{
    AppDelegate *appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    
    PFInstallation *currentInstallation = [PFInstallation currentInstallation];
    [currentInstallation setDeviceTokenFromData:appDelegate.deviceTokenData];
    NSString *channel = [@"C" stringByAppendingString:[PFUser currentUser].objectId];
    currentInstallation.channels = @[ channel ];
    [currentInstallation saveInBackground];
}

@end
