//
//  CancelOrderFetcher.m
//  Cleanium
//
//  Created by  DN-117 on 9/9/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "CancelOrderFetcher.h"
#import <Parse/Parse.h>
#import "MBProgressHUD.h"

@implementation CancelOrderFetcher

- (void)fetch:(void (^)(id resultObject, NSError *error))completionBlock {
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Cancel order";
    
    [PFCloud callFunctionInBackground:@"cancelOrder"
                       withParameters:@{@"id": self.orderId}
                                block:^(PFObject *response, NSError *error) {
                                    [hud hide:YES];
                                    if (error) {
                                        UIAlertView *alertView =
                                        [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil)
                                                                   message:error.localizedDescription
                                                                  delegate:nil
                                                         cancelButtonTitle:nil
                                                         otherButtonTitles:@"Ok", nil];
                                        [alertView show];
                                        completionBlock(nil, error);
                                    }else {
                                        completionBlock(response, error);
                                    }
                                }];
    
}

@end
