//
//  SignUp3ViewController.h
//  Cleanium
//
//  Created by  DN-117 on 7/17/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SignUpTextField.h"
#import "RightBorderButton.h"
#import <Parse/Parse.h>
#import <Stripe/Stripe.h>
#import "StringCustomPicker.h"

@protocol SignUp3Delegate
-(void)performBackFromSignUp3;
-(void)performFinishFromSignUp3:(STPCard*)card;
@end

@interface SignUp3ViewController : UIViewController<StringPickerDelegate>{
    
    IBOutlet UIScrollView *scrollView;
    IBOutlet SignUpTextField *txtCard;
    IBOutlet UILabel *lblExpirationDate;
    IBOutlet RightBorderButton *btnMonth;
    IBOutlet RightBorderButton *btnYear;
    IBOutlet UIImageView *imgExpiration;
    IBOutlet SignUpTextField *txtCVC;
    IBOutlet UIView *bottomView;
    IBOutlet UIButton *btnPrevious;
    IBOutlet UILabel *lblBtnPreviousTitle;
    IBOutlet UIButton *btnNext;
    IBOutlet UILabel *lblBtnTitle;
    IBOutlet UILabel *lblDescription;
    
    UIView *activeView;
    
    NSInteger monthSelectedIndex;
    NSMutableArray *monthArray;
    
    NSInteger yearSelectedIndex;
    NSMutableArray *yearArray;
    
    bool isValidationOK;
}

@property (nonatomic, weak) id<SignUp3Delegate> delegate;

@property(nonatomic, retain) PFUser *signUpUser;
@property(nonatomic, retain) PFObject *addressObject;
@property(nonatomic, assign) bool fromSettings;

-(void)signUpFinished;

@end
