//
//  MenuViewController.h
//  Cleanium
//
//  Created by  DN-117 on 7/23/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ScheduleViewController.h"
#import "WashStatusViewController.h"

@protocol MenuDelegate
-(void)fromLogout;
@end

@interface MenuViewController : UIViewController{
   
    NSMutableArray *menuData;
    
    WashStatusViewController *washViewController;
    ScheduleViewController *scheduleViewController;
}

@property (nonatomic, weak) id<MenuDelegate> delegate;

@end
