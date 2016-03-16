//
//  RightBorderTextField.m
//  Cleanium
//
//  Created by  DN-117 on 7/22/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "RightBorderTextField.h"

@implementation RightBorderTextField

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
    
    CGContextRef ctx = UIGraphicsGetCurrentContext();
    
    CGContextSetLineWidth(ctx, 1.0f * [[UIScreen mainScreen] scale]);
    
    CGContextSetRGBStrokeColor(ctx, 199/255.0f, 199/255.0f, 205/255.0f, 1);
    
    CGContextBeginPath(ctx);
    CGContextMoveToPoint(ctx, CGRectGetMaxX(rect), CGRectGetMinY(rect));
    CGContextAddLineToPoint(ctx, CGRectGetMaxX(rect), CGRectGetMaxY(rect));
    CGContextStrokePath(ctx);
    
    [super drawRect:rect];
}


- (CGRect)rightViewRectForBounds:(CGRect)bounds{
    CGRect rightBounds = CGRectMake(bounds.size.width - 27 - 5, 25/2, 27, 25);
    return rightBounds ;
}

- (CGRect)textRectForBounds:(CGRect)bounds {
    CGRect rect = [super textRectForBounds:bounds];
    return UIEdgeInsetsInsetRect(rect, UIEdgeInsetsMake(0, 8, 0, 8));
}

- (CGRect)editingRectForBounds:(CGRect)bounds {
    CGRect rect = [super editingRectForBounds:bounds];
    return UIEdgeInsetsInsetRect(rect, UIEdgeInsetsMake(0, 8, 0, 8));
}

@end
