//
//  CostBreakdownPopUp.h
//  Cleanium
//
//  Created by  DN-117 on 9/10/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CostBreakdownView.h"

@interface CostBreakdownPopUp : UIView

@property(nonatomic, retain) UIView *view;
@property(nonatomic, retain) IBOutlet CostBreakdownView *costBreakdown;

@end
