//
//  AppDelegate.h
//  Cleanium
//
//  Created by  DN-117 on 7/14/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;
@property(nonatomic, retain) NSString *stripeKey;
@property(nonatomic, retain) NSArray *postalCodes;
@property(nonatomic, retain) NSMutableArray *userAddresses;
@property(nonatomic, retain) NSArray *orderStatuses;
@property(nonatomic, assign) float driverInterval;
@property(nonatomic, retain) NSMutableArray *userOrders;
@property(nonatomic, retain) NSData *deviceTokenData;


@end

