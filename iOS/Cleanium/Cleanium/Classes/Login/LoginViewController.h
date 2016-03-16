//
//  LoginViewController.h
//  Cleanium
//
//  Created by  DN-117 on 7/14/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol LogInDelegate
-(void)performLogin;
@end

@interface LoginViewController : UIViewController{
    
    IBOutlet NSLayoutConstraint *topLogoConstraint;
    IBOutlet NSLayoutConstraint *heightViewsConstraint;
    IBOutlet UITextField *txtUsername;
    IBOutlet UITextField *txtPassword;
    IBOutlet UIView *usernameView;
    IBOutlet UIView *passwordView;
    IBOutlet UIImageView *imgUser;
    IBOutlet UIImageView *imgPassword;
    IBOutlet UIButton *btnLogin;
    
    bool isFirstResponder;
    bool isValidationOK;
    
    float topConstantValue;
}

@property (nonatomic, weak) id<LogInDelegate> delegate;

@end
