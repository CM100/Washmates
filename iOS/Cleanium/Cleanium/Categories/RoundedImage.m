//
//  RoundedImage.m
//  Cleanium
//
//  Created by marko on 9/13/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "RoundedImage.h"

@implementation RoundedImage

-(void)layoutSubviews{
    
    [super layoutSubviews];
    
    self.layer.cornerRadius = self.frame.size.width/2;
    self.layer.masksToBounds = YES;
    
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
