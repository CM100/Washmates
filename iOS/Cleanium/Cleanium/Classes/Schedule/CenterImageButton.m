//
//  CenterImageButton.m
//  Cleanium
//
//  Created by  DN-117 on 7/29/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "CenterImageButton.h"

@implementation CenterImageButton

-(void)layoutSubviews{
    
    [super layoutSubviews];
    
    CGFloat imageHeight = self.frame.size.height * 0.4;
    CGFloat spacingHeight = self.frame.size.width/2 - imageHeight/2;
    CGFloat imageWidth = imageHeight * 1.06;
    CGFloat spacingWidth = self.frame.size.width/2 - imageWidth/2;
    self.imageEdgeInsets = UIEdgeInsetsMake(spacingHeight, spacingWidth * 2 - 8, spacingHeight, 8);
    
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
