//
//  SignUpPageViewController.h
//  Cleanium
//
//  Created by  DN-117 on 7/16/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SignUpViewController.h"
#import "SignUp2ViewController.h"
#import "SignUp3ViewController.h"
#import <Parse/Parse.h>
#import <Stripe/Stripe.h>

@protocol SignUpPageDelegate
-(void)goToLogin;
@end

@interface SignUpPageViewController : UIPageViewController<UIPageViewControllerDelegate, UIPageViewControllerDataSource, SignUp1Delegate, SignUp2Delegate, SignUp3Delegate>{
    
    PFUser *signUpUser;
    PFObject *addressObject;
    STPCard *cardObject;
}

@property(nonatomic, retain) NSArray *signUpViewControllers;
@property (nonatomic, weak) id<SignUpPageDelegate> pageDelegate;

@end
