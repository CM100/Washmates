//
//  SplashViewController.h
//  Cleanium
//
//  Created by  DN-117 on 7/21/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MenuViewController.h"
#import "AppDelegate.h"
#import "TutorialViewController.h"

@interface SplashViewController : UIViewController<MenuDelegate, TutorialViewControllerDelegate>{
    
    IBOutlet NSLayoutConstraint *topLogoConstraint;
    
    AppDelegate *appDelegate;
    bool isTimerFinished;
    bool isGetConfigFinished;
    bool isGetConfigOk;
    NSTimer *timer;
}

@end
