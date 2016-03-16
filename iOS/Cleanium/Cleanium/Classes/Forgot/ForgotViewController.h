//
//  ForgotViewController.h
//  Cleanium
//
//  Created by  DN-117 on 7/23/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ForgotViewController : UIViewController{
    
    IBOutlet NSLayoutConstraint *topLogoConstraint;
    IBOutlet NSLayoutConstraint *heightViewsConstraint;
    IBOutlet UITextField *txtEmail;
    IBOutlet UIView *emailView;
    IBOutlet UIImageView *imgEmail;
    IBOutlet UIButton *btnReset;
}

@end
