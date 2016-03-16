//
//  GetStripeCards.h
//  Cleanium
//
//  Created by  DN-117 on 9/14/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface GetStripeCards : NSObject

@property(nonatomic, retain) UIView *view;

- (void)fetch:(void (^)(id resultObject, NSError *error))completionBlock;

@end
