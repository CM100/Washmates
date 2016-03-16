//
//  GetStripeCards.m
//  Cleanium
//
//  Created by  DN-117 on 9/14/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "GetStripeCards.h"
#import <Parse/Parse.h>
#import "MBProgressHUD.h"

@implementation GetStripeCards

- (void)fetch:(void (^)(id resultObject, NSError *error))completionBlock {
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Loading";
    
    [PFCloud callFunctionInBackground:@"listCards"
                       withParameters:@{}
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
