//
//  Tutorial3View.m
//  Cleanium
//
//  Created by  DN-117 on 8/11/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "Tutorial3View.h"

@implementation Tutorial3View

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
    self.view = [[[NSBundle mainBundle] loadNibNamed:@"Tutorial3View" owner:self options:nil] lastObject];
    [self.view setFrame:CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.frame.size.width, self.frame.size.height)];
    [self addSubview:self.view];
    
    if ([UIScreen mainScreen].bounds.size.height == 480) {
        topConstraint.constant = 45;
        
        [contentView removeConstraint:imageWidthConstraint];
        [contentView addConstraint:[NSLayoutConstraint constraintWithItem:imgView
                                                                attribute:NSLayoutAttributeWidth
                                                                relatedBy:NSLayoutRelationEqual
                                                                   toItem:contentView
                                                                attribute:NSLayoutAttributeWidth
                                                               multiplier:0.3
                                                                 constant:0]];
        
        [contentView removeConstraint:lblWidthConstraint];
        [contentView addConstraint:[NSLayoutConstraint constraintWithItem:lblText
                                                                attribute:NSLayoutAttributeWidth
                                                                relatedBy:NSLayoutRelationEqual
                                                                   toItem:imgView
                                                                attribute:NSLayoutAttributeWidth
                                                               multiplier:2.0
                                                                 constant:0]];
        
        [lblText setFont:[UIFont fontWithName:@"HelveticaNeue" size:13]];
    }
    
    NSMutableParagraphStyle *paragraphStyles = [[NSMutableParagraphStyle alloc] init];
    paragraphStyles.alignment                = NSTextAlignmentJustified;
    paragraphStyles.firstLineHeadIndent      = 0.001f;
    NSString *stringTojustify                = @"Your clothes will be taken to our eco-friendly laundry facilities where they will be cleaned to the highest standard, folded, and placed back in the laundry bag for a wash mate to deliver to your door. Your account will be automatically charged, and an invoice emailed. You can keep the laundry bags, making it easier for subsequent pickups.";
    NSDictionary *attributes                 = @{NSParagraphStyleAttributeName: paragraphStyles};
    NSAttributedString *attributedString     = [[NSAttributedString alloc] initWithString:stringTojustify attributes:attributes];
    
    lblText.attributedText = attributedString;
}

-(IBAction)getStartedAction:(id)sender{
    
    [self.delegate getStartedTutorial3];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
