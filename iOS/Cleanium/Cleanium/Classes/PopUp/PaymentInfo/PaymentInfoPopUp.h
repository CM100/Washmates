//
//  PaymentInfoPopUp.h
//  Cleanium
//
//  Created by  DN-117 on 8/6/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SignUpTextField.h"
#import "RightBorderButton.h"
#import "StringCustomPicker.h"

@protocol PaymentInfoPopUpDelegate
-(void)okPaymentInfo;
@end

@interface PaymentInfoPopUp : UIView<UIGestureRecognizerDelegate, StringPickerDelegate>{
    
    IBOutlet SignUpTextField *txtCard;
    IBOutlet UILabel *lblExpirationDate;
    IBOutlet RightBorderButton *btnMonth;
    IBOutlet RightBorderButton *btnYear;
    IBOutlet UIImageView *imgExpiration;
    IBOutlet SignUpTextField *txtCVC;
    
    float viewY;
    
    NSInteger monthSelectedIndex;
    NSMutableArray *monthArray;
    
    NSInteger yearSelectedIndex;
    NSMutableArray *yearArray;
    
    bool isValidationOK;
    bool isIphone4;
}

@property(nonatomic, retain) UIView *view;

@property (nonatomic, weak) id<PaymentInfoPopUpDelegate> delegate;

@end
