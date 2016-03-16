//
//  CouponViewController.h
//  Cleanium
//
//  Created by marko on 9/13/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CouponViewController : UIViewController{
    
    IBOutlet NSLayoutConstraint *topLogoConstraint;
    IBOutlet NSLayoutConstraint *heightViewsConstraint;
    IBOutlet UIView *contentView;
    IBOutlet UITextField *txtCode;
    IBOutlet UIView *codeView;
    IBOutlet UIImageView *imgEmail;
    IBOutlet UILabel *lblDiscountCode;
    IBOutlet UIButton *btnSubmit;
}

@end
