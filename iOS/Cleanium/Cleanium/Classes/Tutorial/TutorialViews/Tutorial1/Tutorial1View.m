//
//  Tutorial1View.m
//  Cleanium
//
//  Created by  DN-117 on 8/11/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "Tutorial1View.h"

@implementation Tutorial1View

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
    self.view = [[[NSBundle mainBundle] loadNibNamed:@"Tutorial1View" owner:self options:nil] lastObject];
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
                                                               multiplier:1.7
                                                                 constant:0]];
        
        [lblText setFont:[UIFont fontWithName:@"HelveticaNeue" size:13]];
    }
    
    NSMutableParagraphStyle *paragraphStyles = [[NSMutableParagraphStyle alloc] init];
    paragraphStyles.alignment                = NSTextAlignmentJustified;
    paragraphStyles.firstLineHeadIndent      = 0.001f;
    NSString *stringTojustify                = @"After creating an account, you will be able to select your location, whether you have dry cleaning and shirts to be ironed or just laundry, and choose pickup and drop off times which are convinient for you.";
    NSDictionary *attributes                 = @{NSParagraphStyleAttributeName: paragraphStyles};
    NSAttributedString *attributedString     = [[NSAttributedString alloc] initWithString:stringTojustify attributes:attributes];
    
    lblText.attributedText = attributedString;
}

-(IBAction)nextAction:(id)sender{
    
    [self.delegate nextTutorial1];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
