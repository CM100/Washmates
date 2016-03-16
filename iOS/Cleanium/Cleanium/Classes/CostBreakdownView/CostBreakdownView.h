//
//  CostBreakdownView.h
//  Cleanium
//
//  Created by  DN-117 on 8/26/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CostBreakdownView : UIView{
    
    IBOutlet UIView *breakdownContentView;
    IBOutlet UILabel *lblTotalAmount;
    
    NSMutableArray *breakdownViews;
}

@property(nonatomic, retain) UIView *view;

-(void)setServices:(NSArray *)services andTotalAmount:(NSNumber*)totalAmount;

@end
