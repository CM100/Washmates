//
//  BreakdownView.h
//  Cleanium
//
//  Created by  DN-117 on 8/26/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface BreakdownView : UIView{
    
    IBOutlet UILabel *lblTitle;
    IBOutlet UILabel *lblPrice;
    
    IBOutlet NSLayoutConstraint *topConstraint;
    IBOutlet NSLayoutConstraint *bottomConstraint;
    IBOutlet NSLayoutConstraint *heightPriceConstraint;
    IBOutlet NSLayoutConstraint *trailingConstraint;
}

@property(nonatomic, retain) UIView *view;
@property(nonatomic, retain) IBOutlet UIView *lineView;

-(void)setService:(NSDictionary*)service;
-(void)setServicePricing:(NSDictionary*)service;

@end
