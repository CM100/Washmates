//
//  AddressHelper.m
//  Cleanium
//
//  Created by marko on 9/12/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "AddressHelper.h"
#import "MBProgressHUD.h"
#import "AppDelegate.h"

@implementation AddressHelper

+(void)performInsertAddress:(SignUp2ViewController*)viewController withAddress:(PFObject*)addressObject isUpdate:(bool)isUpdate{
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:viewController.view animated:YES];
    hud.labelText = @"Saving address";
    
    
    [addressObject saveInBackgroundWithBlock:^(BOOL succeeded, NSError *error)
     {
         [hud hide:YES];
         if (error)
         {
             UIAlertView *alertView =
             [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil)
                                        message:error.localizedDescription
                                       delegate:self
                              cancelButtonTitle:nil
                              otherButtonTitles:@"Ok", nil];
             [alertView show];
         } else {
             NSLog(@"success");
             if (!isUpdate) {
                 AppDelegate *appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
                 [appDelegate.userAddresses addObject:addressObject];
             }
             
             [viewController saveFromSettings];
         }
     }];
}

+(void)performDeleteAddress:(SignUp2ViewController*)viewController withAddress:(PFObject*)addressObject{
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:viewController.view animated:YES];
    hud.labelText = @"Deleting address";
    
    
    [addressObject deleteInBackgroundWithBlock:^(BOOL succeeded, NSError *error)
     {
         [hud hide:YES];
         if (error)
         {
             UIAlertView *alertView =
             [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil)
                                        message:error.localizedDescription
                                       delegate:self
                              cancelButtonTitle:nil
                              otherButtonTitles:@"Ok", nil];
             [alertView show];
         } else {
             NSLog(@"success");
             AppDelegate *appDelegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
             [appDelegate.userAddresses removeObject:addressObject];
             
             [viewController saveFromSettings];
         }
     }];
}

@end
