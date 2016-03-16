//
//  SignUpViewController.h
//  Cleanium
//
//  Created by  DN-117 on 7/15/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SignUpTextField.h"
#import <Parse/Parse.h>
#import "SignUp2ViewController.h"

@protocol SignUp1Delegate
-(void)performNextFromSignUp1:(PFUser*)user;
@end

@interface SignUpViewController : UIViewController{
    
    IBOutlet UIScrollView *scrollView;
    IBOutlet SignUpTextField *txtFirstName;
    IBOutlet SignUpTextField *txtLastName;
    IBOutlet SignUpTextField *txtEmail;
    IBOutlet SignUpTextField *txtPhone;
    IBOutlet SignUpTextField *txtPassword;
    IBOutlet SignUpTextField *txtConfirmPassword;
    IBOutlet UIButton *btnNext;
    IBOutlet UILabel *lblBtnTitle;
    
    UIView *activeView;
    
    SignUp2ViewController *signUp2ViewController;
    
    bool isFirstResponder;
    bool isValidationOK;
}

@property (nonatomic, weak) id<SignUp1Delegate> delegate;
@property(nonatomic, assign) bool fromSettings;

@end
