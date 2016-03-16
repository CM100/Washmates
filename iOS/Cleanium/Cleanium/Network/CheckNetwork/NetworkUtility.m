//
//  NetworkUtility.m
//  IF_Controller
//
//  Created by  DN-117 on 11/19/13.
//  Copyright (c) 2013 Marko Milinovic. All rights reserved.
//

#import "NetworkUtility.h"
#import <SystemConfiguration/CaptiveNetwork.h>
#include <ifaddrs.h>
#include <arpa/inet.h>
#import "Reachability.h"

@implementation NetworkUtility

+(bool)checkInternetConnection{
    
    Reachability *internetReachability = [Reachability reachabilityForInternetConnection];
	[internetReachability startNotifier];

    NetworkStatus netStatus = [internetReachability currentReachabilityStatus];
    bool isOk = NO;
    
    switch (netStatus)
    {
        case NotReachable:        {
            isOk = NO;
            /*
             Minor interface detail- connectionRequired may return YES even when the host is unreachable. We cover that up here...
             */
            break;
        }
            
        case ReachableViaWWAN:        {
            isOk = YES;
            break;
        }
        case ReachableViaWiFi:        {
            isOk = YES;
            break;
        }
    }
    
    return isOk;
}

@end
