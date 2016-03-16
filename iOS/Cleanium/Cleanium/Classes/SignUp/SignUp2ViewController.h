//
//  SignUp2ViewController.h
//  Cleanium
//
//  Created by  DN-117 on 7/16/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SignUpTextField.h"
#import "DLRadioButton.h"
#import <Parse/Parse.h>
#import "SignUp3ViewController.h"

@protocol SignUp2Delegate
-(void)performBackFromSignUp2;
-(void)performNextFromSignUp2:(PFObject*)objectAddress;
@end

@interface SignUp2ViewController : UIViewController<UITextFieldDelegate, UITextViewDelegate>{
    
    IBOutlet UIScrollView *scrollView;
    IBOutlet SignUpTextField *txtAddressLine1;
    IBOutlet SignUpTextField *txtAddressLine2;
    IBOutlet SignUpTextField *txtCity;
    IBOutlet SignUpTextField *txtState;
    IBOutlet SignUpTextField *txtPostCode;
    IBOutlet SignUpTextField *txtLocation;
    IBOutlet DLRadioButton *btnHome;
    IBOutlet DLRadioButton *btnWork;
    IBOutlet DLRadioButton *btnOther;
    IBOutlet UITextView *txtNotes;
    IBOutlet UIImageView *imgNotes;
    IBOutlet UIView *bottomView;
    IBOutlet UIButton *btnPrevious;
    IBOutlet UILabel *lblBtnPreviousTitle;
    IBOutlet UIButton *btnNext;
    IBOutlet UILabel *lblBtnTitle;
    
    UIView *activeView;
    
    NSString *selectedLocation;
    
    SignUp3ViewController *signUp3ViewController;
    
    bool isFirstResponder;
    bool isValidationOK;
}

@property (nonatomic, weak) id<SignUp2Delegate> delegate;

@property(nonatomic, retain) PFUser *signUpUser;
@property(nonatomic, assign) bool fromSettings;
@property(nonatomic, retain) PFObject *addressFromSettings;

-(void)saveFromSettings;

@end
