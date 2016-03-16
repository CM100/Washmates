//
//  CustomPopUp.m
//  Cleanium
//
//  Created by  DN-117 on 8/6/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import "CustomPopUp.h"

@implementation CustomPopUp

+(void)showPaymenInfo:(id <PaymentInfoPopUpDelegate>)delegate{
    
    PaymentInfoPopUp *paymentInfoPopUp = [[PaymentInfoPopUp alloc] init];
    [paymentInfoPopUp setTranslatesAutoresizingMaskIntoConstraints:NO];
    paymentInfoPopUp.delegate = delegate;
    
    UIWindow *window = [[[UIApplication sharedApplication] delegate] window];
    
    UIView *backgroundView = [[UIView alloc] init];
    [backgroundView setTranslatesAutoresizingMaskIntoConstraints:NO];
    [backgroundView setBackgroundColor:[UIColor colorWithRed:0 green:0 blue:0 alpha:0.7]];
    backgroundView.tag = 153;
    [window addSubview:backgroundView];
    
    [window addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[backgroundView]|" options:0 metrics:nil views:NSDictionaryOfVariableBindings(backgroundView, window)]];
    [window addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|[backgroundView]|" options:NSLayoutFormatAlignAllLeading metrics:nil views:NSDictionaryOfVariableBindings(backgroundView, window)]];
    
    [backgroundView addSubview:paymentInfoPopUp];
    
    [backgroundView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-10-[paymentInfoPopUp]-10-|" options:0 metrics:nil views:NSDictionaryOfVariableBindings(paymentInfoPopUp, backgroundView)]];
    
    NSLayoutConstraint *centerY = [NSLayoutConstraint constraintWithItem:paymentInfoPopUp
                                                               attribute:NSLayoutAttributeCenterY
                                                               relatedBy:NSLayoutRelationEqual
                                                                  toItem:backgroundView
                                                               attribute:NSLayoutAttributeCenterY
                                                              multiplier:1.f constant:0.f];
    [backgroundView addConstraint:centerY];
    
    paymentInfoPopUp.transform = CGAffineTransformScale(CGAffineTransformIdentity, 0, 0);
    [UIView animateWithDuration:0.3 animations:^{
        paymentInfoPopUp.transform = CGAffineTransformScale(CGAffineTransformIdentity, 1.0, 1.0);
    }completion:^(BOOL complete){
        
    }];
}

+(void)showCostBreakdownPopUp:(NSArray*)services withTotalAmount:(NSNumber*)totalAmount{
    
    CostBreakdownPopUp *costBreakdownPopUp = [[CostBreakdownPopUp alloc] init];
    [costBreakdownPopUp setTranslatesAutoresizingMaskIntoConstraints:NO];
    [costBreakdownPopUp.costBreakdown setServices:services andTotalAmount:totalAmount];
    
    UIWindow *window = [[[UIApplication sharedApplication] delegate] window];
    
    UIView *backgroundView = [[UIView alloc] init];
    [backgroundView setTranslatesAutoresizingMaskIntoConstraints:NO];
    [backgroundView setBackgroundColor:[UIColor colorWithRed:0 green:0 blue:0 alpha:0.7]];
    backgroundView.tag = 153;
    [window addSubview:backgroundView];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(closePopUp:)];
    [backgroundView addGestureRecognizer:tap];
    
    [window addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|[backgroundView]|" options:0 metrics:nil views:NSDictionaryOfVariableBindings(backgroundView, window)]];
    [window addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"V:|[backgroundView]|" options:NSLayoutFormatAlignAllLeading metrics:nil views:NSDictionaryOfVariableBindings(backgroundView, window)]];
    
    [backgroundView addSubview:costBreakdownPopUp];
    
    [backgroundView addConstraints:[NSLayoutConstraint constraintsWithVisualFormat:@"H:|-10-[costBreakdownPopUp]-10-|" options:0 metrics:nil views:NSDictionaryOfVariableBindings(costBreakdownPopUp, backgroundView)]];
    
    NSLayoutConstraint *centerY = [NSLayoutConstraint constraintWithItem:costBreakdownPopUp
                                                               attribute:NSLayoutAttributeCenterY
                                                               relatedBy:NSLayoutRelationEqual
                                                                  toItem:backgroundView
                                                               attribute:NSLayoutAttributeCenterY
                                                              multiplier:1.f constant:0.f];
    [backgroundView addConstraint:centerY];
    
    costBreakdownPopUp.transform = CGAffineTransformScale(CGAffineTransformIdentity, 0, 0);
    [UIView animateWithDuration:0.3 animations:^{
        costBreakdownPopUp.transform = CGAffineTransformScale(CGAffineTransformIdentity, 1.0, 1.0);
    }completion:^(BOOL complete){
        
    }];
}

+(void)closePopUp:(UITapGestureRecognizer*)sender{
    [sender.view removeFromSuperview];
}

@end
