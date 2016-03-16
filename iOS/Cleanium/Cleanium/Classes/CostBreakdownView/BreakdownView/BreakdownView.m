//
//  BreakdownView.m
//  Cleanium
//
//  Created by  DN-117 on 8/26/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "BreakdownView.h"

@implementation BreakdownView

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
    self.view = [[[NSBundle mainBundle] loadNibNamed:@"BreakdownView" owner:self options:nil] lastObject];
    [self.view setFrame:CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.frame.size.width, self.frame.size.height)];
    [self addSubview:self.view];
    
}

-(void)setService:(NSDictionary*)service{
    
    lblTitle.text = [NSString stringWithFormat:@"%@ x%@", [service objectForKey:@"title"], [service objectForKey:@"quantity"]];
    lblPrice.text = [NSString stringWithFormat:@"$%@", [service objectForKey:@"price"]];
}

-(void)setServicePricing:(NSDictionary*)service{
    
    lblTitle.text = [NSString stringWithFormat:@"%@", [service objectForKey:@"title"]];
    lblPrice.text = [NSString stringWithFormat:@"%@", [service objectForKey:@"price"]];
    [lblPrice setFont:[UIFont fontWithName:@"HelveticaNeue-Bold" size:14]];
    
    topConstraint.constant = 16;
    bottomConstraint.constant = 16;
    trailingConstraint.constant = 8;
    [self.view removeConstraint:heightPriceConstraint];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
