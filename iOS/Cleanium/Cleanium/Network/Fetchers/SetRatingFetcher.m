//
//  SetRatingFetcher.m
//  Cleanium
//
//  Created by  DN-117 on 9/2/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "SetRatingFetcher.h"
#import "MBProgressHUD.h"

@implementation SetRatingFetcher

- (void)fetch:(void (^)(id resultObject, NSError *error))completionBlock {
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    hud.labelText = @"Rating";
    
    [self.rating saveInBackgroundWithBlock:^(BOOL succeeded, NSError *error)
     {
         [hud hide:YES];
         if (error)
         {
             UIAlertView *alertView =
             [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil)
                                        message:error.localizedDescription
                                       delegate:nil
                              cancelButtonTitle:nil
                              otherButtonTitles:@"Ok", nil];
             [alertView show];
             completionBlock(nil, error);
         } else {
             NSLog(@"success");
             
             completionBlock([NSNumber numberWithBool:succeeded] ,error);
         }
     }];
    
}

@end
