//
//  WashStatusView.h
//  Cleanium
//
//  Created by  DN-117 on 7/21/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef  enum{
    StatusHold = -2,
    StatusEmpty = -1,
    StatusOrderPlaced = 0,
    StatusWashingCollected = 1,
    StatusCleaning = 2,
    StatusAwaitingDelivery = 3,
    StatusOrderUnknown = 4,
    StatusOrderCompleted = 5,
    
}Status;

@interface WashStatusView : UIView{
    
    NSInteger status;
    
    UILabel *lblStatus;
}

@property(nonatomic, retain) UIView *view;
@property(nonatomic, assign) NSInteger numberOfStatuses;

-(void) drawStatus:(NSInteger)statusLocal withStatusText:(NSString*)statusText;

@end
