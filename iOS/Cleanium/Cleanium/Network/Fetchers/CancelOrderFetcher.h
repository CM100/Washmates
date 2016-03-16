//
//  CancelOrderFetcher.h
//  Cleanium
//
//  Created by  DN-117 on 9/9/15.
//  Copyright (c) 2015 Singularity. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface CancelOrderFetcher : NSObject

@property(nonatomic, retain) NSString *orderId;
@property(nonatomic, retain) UIView *view;

- (void)fetch:(void (^)(id resultObject, NSError *error))completionBlock;

@end
