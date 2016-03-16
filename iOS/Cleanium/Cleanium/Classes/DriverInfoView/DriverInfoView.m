//
//  DriverInfoView.m
//  Cleanium
//
//  Created by  DN-117 on 8/26/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "DriverInfoView.h"

@implementation DriverInfoView

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
    self.view = [[[NSBundle mainBundle] loadNibNamed:@"DriverInfoView" owner:self options:nil] lastObject];
    [self.view setFrame:CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.frame.size.width, self.frame.size.height)];
    [self addSubview:self.view];
    
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
