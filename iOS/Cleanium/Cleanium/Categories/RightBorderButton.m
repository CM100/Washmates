//
//  RightBorderButton.m
//  Cleanium
//
//  Created by  DN-117 on 8/25/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "RightBorderButton.h"

@implementation RightBorderButton

-(void)layoutSubviews{
    
    [super layoutSubviews];
    
    CGFloat imageHeight = self.frame.size.height * 0.2;
    CGFloat spacingHeight = self.frame.size.height/2 - imageHeight/2;
    CGFloat imageWidth = imageHeight * 1.06;
    CGFloat spacingWidth = self.frame.size.width/2 - imageWidth/2;
    self.imageEdgeInsets = UIEdgeInsetsMake(spacingHeight, spacingWidth * 2 - 8, spacingHeight, 8);
    [self.titleLabel setFrame:CGRectMake(8, self.titleLabel.frame.origin.y, self.titleLabel.frame.size.width, self.titleLabel.frame.size.height)];
//    self.titleEdgeInsets = UIEdgeInsetsMake(0, 0, 0, 8 + (spacingWidth * 2 - 8));
    
}


// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {

    CGContextRef ctx = UIGraphicsGetCurrentContext();
    
    CGContextSetLineWidth(ctx, 1.0f * [[UIScreen mainScreen] scale]);
    
    CGContextSetRGBStrokeColor(ctx, 199/255.0f, 199/255.0f, 205/255.0f, 1);
    
    CGContextBeginPath(ctx);
    CGContextMoveToPoint(ctx, CGRectGetMaxX(rect), CGRectGetMinY(rect));
    CGContextAddLineToPoint(ctx, CGRectGetMaxX(rect), CGRectGetMaxY(rect));
    CGContextStrokePath(ctx);
    
    [super drawRect:rect];
}


@end
