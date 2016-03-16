//
//  SignUpHelper.m
//  Cleanium
//
//  Created by  DN-117 on 8/12/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "SignUpHelper.h"
#import "MBProgressHUD.h"

@implementation SignUpHelper

+(void)performSignUp:(SignUp3ViewController*)viewController withUser:(PFUser*)signUpUser withAddress:(PFObject*)addressObject withCard:(STPCard*)cardObject{
    
    if ([PFUser currentUser] == signUpUser && cardObject != nil) {
        [self getStripeToken:viewController withCard:cardObject fromSettings:NO];
    }else if ([PFUser currentUser] == signUpUser) {
        [viewController signUpFinished];
    }else{
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:viewController.view animated:YES];
        hud.labelText = @"Sign Up";
        [signUpUser signUpInBackgroundWithBlock:^(BOOL succeeded, NSError *error)
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
                 if (signUpUser) {
                     [addressObject setObject:signUpUser forKey:@"user"];
                 }
                 
                 [self performInsertAddress:viewController withAddress:addressObject withCard:cardObject];
                 
             }
         }];
    }
}

+(void)performInsertAddress:(SignUp3ViewController*)viewController withAddress:(PFObject*)addressObject withCard:(STPCard*)cardObject{
    
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
             
             if (cardObject) {
                 [self getStripeToken:viewController withCard:cardObject fromSettings:NO];
             }else{
                 [viewController signUpFinished];
             }
             
         }
     }];
}

+(void)getStripeToken:(SignUp3ViewController*)viewController withCard:(STPCard *)card fromSettings:(bool)fromSettings{
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:viewController.view animated:YES];
    hud.labelText = @"Saving credit card";
    
    [[STPAPIClient sharedClient] createTokenWithCard:card
                                          completion:^(STPToken *token, NSError *error) {
                                              [hud hide:YES];
                                              if (error) {
                                                  UIAlertView *alertView =
                                                  [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil)
                                                                             message:error.localizedDescription
                                                                            delegate:self
                                                                   cancelButtonTitle:nil
                                                                   otherButtonTitles:@"Ok", nil];
                                                  [alertView show];
                                              }else {
                                                  [self createStripeCustomerObject:viewController withToken:token fromSettings:fromSettings];
                                              }
                                          }];
}

+(void)createStripeCustomerObject:(SignUp3ViewController*)viewController withToken:(STPToken*)token fromSettings:(bool)fromSettings{
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:viewController.view animated:YES];
    hud.labelText = @"Saving credit card";
    
    [PFCloud callFunctionInBackground:@"createStripeCustomer"
                       withParameters:@{@"creditCardToken": token.tokenId}
                                block:^(NSString *response, NSError *error) {
                                    [hud hide:YES];
                                    if (error) {
                                        UIAlertView *alertView =
                                        [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil)
                                                                   message:error.localizedDescription
                                                                  delegate:self
                                                         cancelButtonTitle:nil
                                                         otherButtonTitles:@"Ok", nil];
                                        [alertView show];
                                    }else {
                                        if(!fromSettings){
                                            [viewController signUpFinished];
                                        }else{
                                            [[PFUser currentUser] fetchInBackground];
                                            UIAlertView *alertView =[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Info", nil) message:@"Payment created successfully" delegate:self cancelButtonTitle:nil otherButtonTitles:@"Ok", nil];
                                            [alertView show];
                                        }
                                        
                                    }
                                }];
    
}

+(void)performUpdateUser:(SignUpViewController*)viewController withUser:(PFUser*)user{

    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:viewController.view animated:YES];
    hud.labelText = @"Update";
    [user saveInBackgroundWithBlock:^(BOOL succeeded, NSError *error)
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
             UIAlertView *alertView =[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Info", nil) message:@"Profile updated successfully" delegate:self cancelButtonTitle:nil otherButtonTitles:@"Ok", nil];
             [alertView show];
         }
     }];
}

@end
