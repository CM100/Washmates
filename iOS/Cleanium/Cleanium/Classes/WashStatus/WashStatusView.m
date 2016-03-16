//
//  WashStatusView.m
//  Cleanium
//
//  Created by  DN-117 on 7/21/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "WashStatusView.h"

@implementation WashStatusView

#define defaultNumberOFStatuses 5;

@synthesize numberOfStatuses;

-(id)initWithCoder:(NSCoder *)aDecoder{
    
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self setUp];
    }
    
    return self;
}

-(id)initWithFrame:(CGRect)frame{
    
    self = [super initWithFrame:frame];
    if (self) {
        [self setUp];
    }
    
    return self;
}

-(void)setUp{
    [self setBackgroundColor:[UIColor clearColor]];
    
    numberOfStatuses = defaultNumberOFStatuses;
    
    lblStatus = [[UILabel alloc] init];
    lblStatus.translatesAutoresizingMaskIntoConstraints = NO;
    [lblStatus setTextAlignment:NSTextAlignmentCenter];
    [lblStatus setFont:[UIFont fontWithName:@"Helvetica" size:37]];
    lblStatus.numberOfLines = 0;
    [self addSubview: lblStatus];
    
    [self addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-20-[lblStatus]-20-|" options:0 metrics:nil views:NSDictionaryOfVariableBindings(lblStatus, self)]];
    NSLayoutConstraint *centerY = [NSLayoutConstraint constraintWithItem:lblStatus
                                 attribute:NSLayoutAttributeCenterY
                                 relatedBy:NSLayoutRelationEqual
                                    toItem:self
                                 attribute:NSLayoutAttributeCenterY
                                multiplier:1.f constant:0.f];
    [self addConstraint:centerY];
}

-(void) drawStatus:(NSInteger)statusLocal withStatusText:(NSString*)statusText{
    
    [self layoutIfNeeded];
    [self setNeedsDisplay];
    
    [self removeViews];
    
    status = statusLocal;
    //allPlayers = [[NSMutableArray alloc] init];

    int randomValue =  0;
    float angle = 0;
    if (numberOfStatuses != 0) {
        angle = 360 / numberOfStatuses;
    }
    int width = self.frame.size.width;
    for (int i = 0; i < numberOfStatuses; i++) {
        CGPoint P1 = CGPointMake((width/2 + ((width - 0 - 2) / 2) * cos((randomValue + (i * angle))*(M_PI/180)  - (M_PI/2))), (width/2 + ((width - 1 - 2) / 2) * sin((randomValue + (i * angle))*(M_PI/180)  - (M_PI/2))));
//        NSLog(@"P1 %f,%f", P1.x, P1.y);
        UIImageView *imgPom = [[UIImageView alloc] initWithFrame:CGRectMake(P1.x  - 10, P1.y  - 10 , 20, 20)];
        [imgPom setImage:[UIImage imageNamed:@"wash-status-minus.png"]];
        if (i <= status) {
            [imgPom setImage:[UIImage imageNamed:@"wash-status-correct.png"]];
        }
//        [imgPom setBackgroundColor:[UIColor whiteColor]];
//        imgPom.layer.cornerRadius = 10;
        [self addSubview:imgPom];
        
        NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
        [dic setObject:imgPom forKey:@"playerView"];
        //[allPlayers addObject:dic];
    }
    
    if (status == StatusHold) {
        [lblStatus setTextColor:[UIColor redColor]];
        [self removeViews];
    }else if(status == StatusEmpty){
        [lblStatus setTextColor:[UIColor colorWithRed:216/255.0f green:216/255.0f blue:226/255.0f alpha:1]];
    }else{
        [lblStatus setTextColor:[UIColor colorWithRed:121/255.0f green:210/255.0f blue:246/255.0f alpha:1]];
    }
    
    lblStatus.text = statusText;
//    [self setStatusText];
//    [self drawRect:self.bounds];

}

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
    
    float angle = 0;
    if (numberOfStatuses != 0) {
        angle = 360 / numberOfStatuses;
    }
    
    CGRect allRect = self.bounds;
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    CGContextSetStrokeColorWithColor(context, [UIColor colorWithRed:148/255.0f green:194/255.0f blue:41/255.0f alpha:0.7].CGColor);
    CGContextSetLineWidth(context, 1);
    
    CGPoint center = CGPointMake(allRect.size.width / 2, allRect.size.height / 2);
    CGFloat radius = (allRect.size.width) / 2 -1 ;
    
    UIColor *grayColor = [UIColor colorWithRed:216/255.0f green:216/255.0f blue:226/255.0f alpha:1];
    UIColor *color = [UIColor colorWithRed:121/255.0f green:210/255.0f blue:246/255.0f alpha:1];
    int i = 1;
    float startAngle = 270;
    while (i <= numberOfStatuses) {
        if (i <= status) {
            [self drawLine:context withCenter:center withRadius:radius withStartAngle:startAngle withStatus:i withAngle:angle withColor:color];
        }else{
            [self drawLine:context withCenter:center withRadius:radius withStartAngle:startAngle withStatus:i withAngle:angle withColor:grayColor];
        }
        startAngle = startAngle + angle;
        i++;
    }
    
    if (status == StatusHold) {
        [self drawLine:context withCenter:center withRadius:radius withStartAngle:0 withStatus:i withAngle:360 withColor:[UIColor redColor]];
//        i = 1;
//        while (i <= numberOfStatuses) {
//            [self drawLine:context withCenter:center withRadius:radius withStartAngle:startAngle withStatus:i withAngle:angle withColor:[UIColor redColor]];
//            startAngle = startAngle + angle;
//            i++;
//        }
    }
    
}

-(void)drawLine:(CGContextRef)context withCenter:(CGPoint)center withRadius:(CGFloat)radius withStartAngle:(float)startAngle withStatus:(int)statusNumber withAngle:(float)angle withColor:(UIColor*)color{
    CGContextBeginPath(context);
    CGContextSetStrokeColorWithColor(context, color.CGColor);
    CGContextAddArc(context, center.x, center.y, radius,startAngle * M_PI/180, (statusNumber * angle + 270) * M_PI/180, NO);
    CGContextStrokePath(context);
    
}

-(void)removeViews{
    for (UIView *v in self.subviews) {
        if (![v isKindOfClass:[UILabel class]]) {
            [v removeFromSuperview];
        }
    }
}

@end
