//
//  DriverInfoView.h
//  Cleanium
//
//  Created by  DN-117 on 8/26/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RoundedImage.h"

@interface DriverInfoView : UIView

@property(nonatomic, retain) UIView *view;
@property(nonatomic, retain) IBOutlet RoundedImage *imgView;
@property(nonatomic, retain) IBOutlet UILabel *lblName;

@end
