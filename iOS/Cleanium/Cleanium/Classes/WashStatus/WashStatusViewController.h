//
//  WashStatusViewController.h
//  Cleanium
//
//  Created by  DN-117 on 7/21/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WashStatusView.h"
#import "CostBreakdownView.h"
#import "DriverInfoView.h"
#import <Parse/Parse.h>
#import "AppDelegate.h"
#import "RatingViewController.h"

@interface WashStatusViewController : UIViewController<RatingDelegate>{
    
    IBOutlet UIScrollView *scrollView;
    IBOutlet WashStatusView *washStatusView;
    IBOutlet UILabel *lblPickupDate;
    IBOutlet UILabel *lblPickupTime;
    IBOutlet UILabel *lblPickupDay;
    IBOutlet UILabel *lblDropOffDate;
    IBOutlet UILabel *lblDropOffTime;
    IBOutlet UILabel *lblDropOffDay;
    IBOutlet UIView *statusNotesView;
    IBOutlet UIView *statusNotesContentView;
    CostBreakdownView *costBreakdownView;
    UILabel *lblNotes;
    DriverInfoView *driverInfoView;
    UIButton *btnCancel;
    
    AppDelegate *appDelegate;
    PFObject *currentOrder;
    
    NSTimer *timer;
    
    bool hasBeenToRating;
}

-(void)refreshOrder:(NSString*)orderId;

@end
