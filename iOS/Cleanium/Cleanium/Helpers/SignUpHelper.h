//
//  SignUpHelper.h
//  Cleanium
//
//  Created by  DN-117 on 8/12/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Parse/Parse.h>
#import <Stripe/Stripe.h>
#import "SignUp3ViewController.h"
#import "SignUpViewController.h"

@interface SignUpHelper : NSObject

+(void)performSignUp:(SignUp3ViewController*)viewController withUser:(PFUser*)signUpUser withAddress:(PFObject*)addressObject withCard:(STPCard*)cardObject;
+(void)getStripeToken:(SignUp3ViewController*)viewController withCard:(STPCard *)card fromSettings:(bool)fromSettings;
+(void)performUpdateUser:(SignUpViewController*)viewController withUser:(PFUser*)user;

@end
