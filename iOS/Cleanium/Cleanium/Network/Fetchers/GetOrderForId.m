//
//  GetOrderForId.m
//  Cleanium
//
//  Created by  DN-117 on 9/8/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "GetOrderForId.h"
#import <Parse/Parse.h>

@implementation GetOrderForId

- (void)fetch:(void (^)(id resultObject, NSError *error))completionBlock {
    
    PFQuery *query = [PFQuery queryWithClassName:@"Order"];
    [query whereKey:@"user" equalTo:[PFUser currentUser]];
    [query whereKey:@"objectId" equalTo:self.orderId];
    [query includeKey:@"pickUpSchedule"];
    [query includeKey:@"pickUpDriver"];
    [query includeKey:@"pickUpDriver.user"];
    [query includeKey:@"deliverySchedule"];
    [query includeKey:@"deliveryDriver"];
    [query includeKey:@"deliveryDriver.user"];
    
    [query findObjectsInBackgroundWithBlock:^(NSArray *objects, NSError *error) {
        if (!error) {
            NSLog(@"Successfully retrieved %lu scores.", (unsigned long)objects.count);
            
            NSError *resultError = nil;
            completionBlock(objects ,resultError);
            
        } else {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:error.localizedDescription delegate:nil cancelButtonTitle:NSLocalizedString(@"Ok", nil) otherButtonTitles:nil];
            [alert show];
            completionBlock(nil, error);
            return;
        }
    }];
    
}

@end
