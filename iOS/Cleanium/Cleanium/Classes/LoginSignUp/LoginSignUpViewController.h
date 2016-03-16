//
//  LoginSignUpViewController.h
//  Cleanium
//
//  Created by  DN-117 on 7/14/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SignUpMainViewController.h"
#import "LoginViewController.h"

@interface LoginSignUpViewController : UIViewController<LogInDelegate, SignUpMainDelegate, UIGestureRecognizerDelegate>{
    
    IBOutlet NSLayoutConstraint *topLogoConstraint;
    IBOutlet NSLayoutConstraint *heightViewsConstraint;
    IBOutlet UIButton *btnLogin;
    IBOutlet UIButton *btnSignUp;
}

-(void)goToLoginFromSignUp;

@property(nonatomic, assign) bool fromTutorial;

@end
