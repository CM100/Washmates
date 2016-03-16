//
//  SignUpMainViewController.h
//  Cleanium
//
//  Created by  DN-117 on 7/16/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SignUpPageViewController.h"

@protocol SignUpMainDelegate
-(void)goToLogin;
@end

@interface SignUpMainViewController : UIViewController<SignUpPageDelegate>{
    
    SignUpPageViewController *pageViewController;
    
}

@property (nonatomic, weak) id<SignUpMainDelegate> delegate;

@end
