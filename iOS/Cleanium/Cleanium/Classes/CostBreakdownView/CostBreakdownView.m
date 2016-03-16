//
//  CostBreakdownView.m
//  Cleanium
//
//  Created by  DN-117 on 8/26/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "CostBreakdownView.h"
#import "BreakdownView.h"

@implementation CostBreakdownView

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
    self.view = [[[NSBundle mainBundle] loadNibNamed:@"CostBreakdownView" owner:self options:nil] lastObject];
    [self.view setFrame:CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.frame.size.width, self.frame.size.height)];
    [self addSubview:self.view];
    
//    BreakdownView *breadownView1 = [BreakdownView new];
//    BreakdownView *breadownView2 = [BreakdownView new];
//    BreakdownView *breadownView3 = [BreakdownView new];
//    
//    breakdownViews = [[NSMutableArray alloc] initWithObjects:breadownView1, breadownView2, breadownView3, nil];
//    [self setBreakdownViews];
}

-(void)setServices:(NSArray *)services andTotalAmount:(NSNumber*)totalAmount{

    breakdownViews = [[NSMutableArray alloc] initWithCapacity:services.count];
    for (NSDictionary *service in services) {
        BreakdownView *breadownView = [BreakdownView new];
        [breadownView setService:service];
        [breakdownViews addObject:breadownView];
    }
    
    lblTotalAmount.text = [NSString stringWithFormat:@"$%i", [totalAmount intValue]];
    
    [self setBreakdownViews];
}

-(void)setBreakdownViews{
    
    UIView *prevView;
    for (int i = 0; i < breakdownViews.count; i++) {
        UIView *breakdownView = [breakdownViews objectAtIndex:i];
        [breakdownView setTranslatesAutoresizingMaskIntoConstraints:NO];
        [breakdownContentView addSubview:breakdownView];
        
        if (i == 0) {
            [breakdownContentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|[breakdownView(==40)]" options:0 metrics:nil views:NSDictionaryOfVariableBindings(breakdownView, breakdownContentView)]];
            prevView = breakdownView;
        }else{
            [breakdownContentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[prevView][breakdownView(==prevView)]" options:0 metrics:nil views:NSDictionaryOfVariableBindings(breakdownView, prevView)]];
            prevView = breakdownView;
        }
        [breakdownContentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[breakdownView(==breakdownContentView)]|" options:NSLayoutFormatAlignAllLeading metrics:nil views:NSDictionaryOfVariableBindings(breakdownView, breakdownContentView)]];
        if (i == breakdownViews.count - 1) {
            [breakdownContentView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:[prevView]|" options:0 metrics:nil views:NSDictionaryOfVariableBindings(breakdownView, prevView)]];
        }
        
    }
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
